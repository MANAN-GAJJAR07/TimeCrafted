package com.example.timecrafted.ui.main.fragment

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import com.example.timecrafted.R
import com.example.timecrafted.databinding.FragmentShopBinding
import com.example.timecrafted.ui.main.adapter.ProductAdapter
import com.example.timecrafted.ui.main.data.Product
import com.example.timecrafted.ui.product.FilterActivity
import com.example.timecrafted.ui.product.SortActivity
import com.example.timecrafted.ui.product.productDetail

class ShopFragment : Fragment() {
    private var _binding: FragmentShopBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentShopBinding.inflate(inflater, container, false)

        binding.filterBtn.setOnClickListener {
            startActivity(Intent(requireContext(), FilterActivity::class.java))
        }
        binding.sortBtn.setOnClickListener {
            startActivity(Intent(requireContext(), SortActivity::class.java))
        }

        setupRecyclerView()
        return binding.root
    }

    private fun setupRecyclerView() {
        val products = listOf(
            Product("Classic Watch", "₹2999", R.drawable.product_img),
            Product("Luxury Watch", "₹5999", R.drawable.product_img2),
            Product("Classic Watch", "₹3999", R.drawable.product_img3),
            Product("Classic Watch", "₹7999", R.drawable.product_img4),
            Product("Luxury Timepiece", "₹4999", R.drawable.men_watch),
            Product("Sport Watch", "₹1999", R.drawable.smartwatch),
            Product("Women Watch", "₹3499", R.drawable.women_watch)
        )

        val productAdapter = ProductAdapter(products) { product ->
            openProductDetail(product)
        }

        binding.productRecyclerView.layoutManager = GridLayoutManager(requireContext(), 2)
        binding.productRecyclerView.adapter = productAdapter
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
