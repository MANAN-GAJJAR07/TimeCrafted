package com.example.timecrafted.ui.main.fragment

import android.content.Context.MODE_PRIVATE
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.timecrafted.R
import com.example.timecrafted.databinding.FragmentHomeBinding
import com.example.timecrafted.ui.auth.loginScreen
import com.example.timecrafted.ui.main.adapter.ProductAdapter
import com.example.timecrafted.ui.main.adaptor.CategoriesAdapter
import com.example.timecrafted.ui.main.data.Categories
import com.example.timecrafted.ui.main.data.Product
import com.example.timecrafted.ui.product.CartActivity
import com.example.timecrafted.ui.product.productDetail

class HomeFragment : Fragment() {
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)

        setupRecyclerView()

        binding.cartBtn.setOnClickListener {
            val sharedPref = requireActivity().getSharedPreferences("LoginPref", MODE_PRIVATE)
            val savedEmail = sharedPref.getString("email", null)

            if (savedEmail != null) {
                startActivity(Intent(requireContext(), CartActivity::class.java))
            } else {
                startActivity(Intent(requireContext(), loginScreen::class.java))
            }
        }

        return binding.root
    }

    private fun setupRecyclerView() {
        val newArrivalsList = listOf(
            Product("Classic Watch", "₹2999", R.drawable.product_img),
            Product("Luxury Timepiece", "₹4999", R.drawable.product_img2),
            Product("Sport Watch", "₹1999", R.drawable.product_img3),
            Product("Modern Design", "₹3499", R.drawable.product_img4)
        )

        val newArrivalsAdapter = ProductAdapter(newArrivalsList) { product ->
            openProductDetail(product)
        }

        binding.newArrivalsRecyclerView.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        binding.newArrivalsRecyclerView.adapter = newArrivalsAdapter

        val bestsellers = listOf(
            Product("Classic Watch", "₹299", R.drawable.product_img4),
            Product("Luxury Timepiece", "₹499", R.drawable.product_img3),
            Product("Sport Watch", "₹199", R.drawable.product_img2),
            Product("Modern Design", "₹349", R.drawable.product_img)
        )

        val bestsellersAdapter = ProductAdapter(bestsellers) { product ->
            openProductDetail(product)
        }

        binding.bestsellersRecyclerView.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        binding.bestsellersRecyclerView.adapter = bestsellersAdapter

        val categories = listOf(
            Categories("Men's Watches", R.drawable.men_watch),
            Categories("Women's Watches", R.drawable.women_watch),
            Categories("Smart Watches", R.drawable.smartwatch)
        )

        val categoriesAdapter = CategoriesAdapter(categories)
        binding.categoriesRecyclerView.layoutManager = GridLayoutManager(requireContext(), 2)
        binding.categoriesRecyclerView.adapter = categoriesAdapter
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
