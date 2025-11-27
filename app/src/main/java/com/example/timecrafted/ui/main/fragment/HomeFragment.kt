package com.example.timecrafted.ui.main.fragment

import android.content.Context.MODE_PRIVATE
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.timecrafted.data.firebase.FirebaseKeys
import com.example.timecrafted.data.firebase.FirebaseListener
import com.example.timecrafted.data.firebase.FirebaseRepository
import com.example.timecrafted.databinding.FragmentHomeBinding
import com.example.timecrafted.ui.auth.loginScreen
import com.example.timecrafted.ui.main.adaptor.CategoriesAdapter
import com.example.timecrafted.ui.main.adapter.ProductAdapter
import com.example.timecrafted.ui.main.data.Product
import com.example.timecrafted.ui.product.CartActivity
import com.example.timecrafted.ui.product.productDetail
import com.google.android.material.snackbar.Snackbar

class HomeFragment : Fragment() {
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private val repository by lazy { FirebaseRepository() }
    private val activeListeners = mutableListOf<FirebaseListener>()

    private lateinit var newArrivalsAdapter: ProductAdapter
    private lateinit var bestsellersAdapter: ProductAdapter
    private lateinit var categoriesAdapter: CategoriesAdapter

    private var productsLoaded = false
    private var categoriesLoaded = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)

        setupRecyclerViews()
        observeCollections()

        binding.cartBtn.setOnClickListener { handleCartClick() }

        return binding.root
    }

    private fun setupRecyclerViews() {
        newArrivalsAdapter = ProductAdapter(::openProductDetail)
        binding.newArrivalsRecyclerView.apply {
            layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
            adapter = newArrivalsAdapter
        }

        bestsellersAdapter = ProductAdapter(::openProductDetail)
        binding.bestsellersRecyclerView.apply {
            layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
            adapter = bestsellersAdapter
        }

        categoriesAdapter = CategoriesAdapter()
        binding.categoriesRecyclerView.apply {
            layoutManager = GridLayoutManager(requireContext(), 2)
            adapter = categoriesAdapter
        }
    }

    private fun observeCollections() {
        activeListeners.forEach(repository::removeListener)
        activeListeners.clear()
        productsLoaded = false
        categoriesLoaded = false
        updateLoadingState()

        activeListeners += repository.listenForProducts(
            FirebaseKeys.Collections.SHOP,
            onResult = { products ->
                productsLoaded = true
                val newArrivals = products
                    .sortedByDescending { it.createdAt }
                    .take(HOME_SECTION_LIMIT)
                val bestsellers = products
                    .sortedByDescending { it.salesCount }
                    .take(HOME_SECTION_LIMIT)

                newArrivalsAdapter.submitList(newArrivals)
                bestsellersAdapter.submitList(bestsellers)
                updateLoadingState()
            },
            onError = {
                productsLoaded = true
                updateLoadingState()
                handleError(it)
            }
        )

        activeListeners += repository.listenForCategories(
            onResult = {
                categoriesLoaded = true
                categoriesAdapter.submitList(it)
                updateLoadingState()
            },
            onError = {
                categoriesLoaded = true
                updateLoadingState()
                handleError(it)
            }
        )
    }

    private fun handleCartClick() {
        val authRepository = com.example.timecrafted.data.auth.AuthRepository()
        val destination = if (authRepository.isUserLoggedIn()) CartActivity::class.java else loginScreen::class.java
        startActivity(Intent(requireContext(), destination))
    }

    private fun openProductDetail(product: Product) {
        startActivity(productDetail.createIntent(requireContext(), product))
    }

    private fun updateLoadingState() {
        binding.homeProgress.isVisible = !(productsLoaded && categoriesLoaded)
    }

    private fun handleError(throwable: Throwable) {
        Snackbar.make(binding.root, throwable.message ?: "Something went wrong", Snackbar.LENGTH_LONG)
            .show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        activeListeners.forEach(repository::removeListener)
        activeListeners.clear()
        _binding = null
    }

    companion object {
        private const val HOME_SECTION_LIMIT = 4
    }
}
