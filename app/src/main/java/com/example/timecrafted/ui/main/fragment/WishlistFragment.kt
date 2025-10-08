package com.example.timecrafted.ui.main.fragment

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import com.example.timecrafted.R
import com.example.timecrafted.databinding.FragmentWishlistBinding
import com.example.timecrafted.ui.main.adapter.ProductAdapter
import com.example.timecrafted.ui.main.data.Product
import com.example.timecrafted.ui.product.productDetail

class WishlistFragment : Fragment() {
    private var _binding: FragmentWishlistBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentWishlistBinding.inflate(inflater, container, false)

        setupRecyclerView()
        return binding.root
    }

    private fun setupRecyclerView() {
        val products = listOf(
            Product("Luxury Timepiece", "₹4999", R.drawable.men_watch),
            Product("Luxury Watch", "₹5999", R.drawable.product_img2),
            Product("Classic Watch", "₹7999", R.drawable.product_img4),
            Product("Sport Watch", "₹1999", R.drawable.smartwatch)
        )

        val wishlistAdapter = ProductAdapter(products) { product ->
            openProductDetail(product)
        }

        binding.wishlistRecyclerView.layoutManager = GridLayoutManager(requireContext(), 2)
        binding.wishlistRecyclerView.adapter = wishlistAdapter
    }

    private fun openProductDetail(product: Product) {
        val intent = Intent(requireContext(), productDetail::class.java).apply {
            putExtra("name", product.name)
            putExtra("price", product.price)
            putExtra("imageRes", product.imageResId)
        }
        startActivity(intent)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
