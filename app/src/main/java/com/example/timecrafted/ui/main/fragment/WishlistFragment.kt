package com.example.timecrafted.ui.main.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import com.example.timecrafted.R
import com.example.timecrafted.data.auth.AuthRepository
import com.example.timecrafted.data.firebase.FirebaseListener
import com.example.timecrafted.data.repository.WishlistRepository
import com.example.timecrafted.databinding.FragmentWishlistBinding
import com.example.timecrafted.ui.main.adapter.ProductAdapter
import com.example.timecrafted.ui.main.data.Product
import com.example.timecrafted.ui.product.productDetail
import com.google.android.material.snackbar.Snackbar

class WishlistFragment : Fragment() {
    private var _binding: FragmentWishlistBinding? = null
    private val binding get() = _binding!!

    private val wishlistRepository by lazy { WishlistRepository() }
    private val authRepository by lazy { AuthRepository() }
    private var wishlistListener: FirebaseListener? = null
    private lateinit var wishlistAdapter: ProductAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentWishlistBinding.inflate(inflater, container, false)
        setupRecyclerView()
        observeWishlist()
        return binding.root
    }

    private fun setupRecyclerView() {
        wishlistAdapter = ProductAdapter(::openProductDetail)
        binding.wishlistRecyclerView.apply {
            layoutManager = GridLayoutManager(requireContext(), 2)
            adapter = wishlistAdapter
        }
    }

    private fun observeWishlist() {
        val userId = authRepository.getCurrentUserId()
        if (userId == null) {
            binding.wishlistProgress.isVisible = false
            binding.wishlistEmptyState.isVisible = true
            binding.wishlistEmptyState.text = "Please login to view your wishlist"
            return
        }

        wishlistListener = wishlistRepository.listenForWishlist(
            userId = userId,
            onResult = { items ->
                binding.wishlistProgress.isVisible = false
                wishlistAdapter.submitList(items)
                binding.wishlistEmptyState.isVisible = items.isEmpty()
            },
            onError = { error ->
                binding.wishlistProgress.isVisible = false
                handleError(error)
            }
        )
    }

    private fun openProductDetail(product: Product) {
        startActivity(productDetail.createIntent(requireContext(), product))
    }

    private fun handleError(throwable: Throwable) {
        Snackbar.make(
            binding.root,
            throwable.message ?: getString(R.string.generic_error_message),
            Snackbar.LENGTH_LONG
        ).show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        wishlistListener?.let { listener ->
            listener.reference.removeEventListener(listener.listener)
        }
        _binding = null
    }
}
