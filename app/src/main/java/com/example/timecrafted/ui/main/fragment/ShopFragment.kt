package com.example.timecrafted.ui.main.fragment

import android.app.Activity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import com.example.timecrafted.R
import com.example.timecrafted.data.firebase.FirebaseKeys
import com.example.timecrafted.data.firebase.FirebaseListener
import com.example.timecrafted.data.firebase.FirebaseRepository
import com.example.timecrafted.databinding.FragmentShopBinding
import com.example.timecrafted.ui.main.adapter.ProductAdapter
import com.example.timecrafted.ui.main.data.Product
import com.example.timecrafted.ui.product.FilterActivity
import com.example.timecrafted.ui.product.SortActivity
import com.example.timecrafted.ui.product.model.ProductFilterOptions
import com.example.timecrafted.ui.product.model.ProductSortOption
import com.example.timecrafted.ui.product.productDetail
import com.google.android.material.snackbar.Snackbar

class ShopFragment : Fragment() {
    private var _binding: FragmentShopBinding? = null
    private val binding get() = _binding!!

    private val repository by lazy { FirebaseRepository() }
    private var productsListener: FirebaseListener? = null
    private lateinit var productAdapter: ProductAdapter

    private var allProducts: List<Product> = emptyList()
    private var appliedFilter: ProductFilterOptions? = null
    private var appliedSort: ProductSortOption = ProductSortOption.NONE

    private val filterLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val filter =
                    result.data?.getSerializableExtra(FilterActivity.EXTRA_FILTER_RESULT) as? ProductFilterOptions
                appliedFilter = filter?.takeUnless { it.isEmpty() }
                renderProducts()
            }
        }

    private val sortLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val sortOption =
                    result.data?.getSerializableExtra(SortActivity.EXTRA_SORT_RESULT) as? ProductSortOption
                        ?: ProductSortOption.NONE
                appliedSort = sortOption
                renderProducts()
            }
        }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentShopBinding.inflate(inflater, container, false)

        binding.filterBtn.setOnClickListener {
            filterLauncher.launch(FilterActivity.createIntent(requireContext(), appliedFilter))
        }
        binding.sortBtn.setOnClickListener {
            sortLauncher.launch(SortActivity.createIntent(requireContext(), appliedSort))
        }

        setupRecyclerView()
        observeProducts()
        return binding.root
    }

    private fun setupRecyclerView() {
        productAdapter = ProductAdapter(::openProductDetail)
        binding.productRecyclerView.apply {
            layoutManager = GridLayoutManager(requireContext(), 2)
            adapter = productAdapter
        }
    }

    private fun observeProducts() {
        repository.removeListener(productsListener)
        binding.shopProgress.isVisible = true
        productsListener = repository.listenForProducts(
            FirebaseKeys.Collections.SHOP,
            onResult = { items ->
                binding.shopProgress.isVisible = false
                allProducts = items
                renderProducts()
            },
            onError = { error ->
                binding.shopProgress.isVisible = false
                handleError(error)
            }
        )
    }

    private fun renderProducts() {
        val currentBinding = _binding ?: return
        val filtered = appliedFilter?.let { filter ->
            allProducts.filter { filter.matches(it) }
        } ?: allProducts

        val sorted = when (appliedSort) {
            ProductSortOption.PRICE_LOW_HIGH -> filtered.sortedBy { it.price }
            ProductSortOption.PRICE_HIGH_LOW -> filtered.sortedByDescending { it.price }
            ProductSortOption.NEWEST -> filtered.sortedByDescending { it.createdAt }
            ProductSortOption.POPULARITY -> filtered.sortedByDescending { it.salesCount }
            ProductSortOption.RATING -> filtered.sortedByDescending { it.rating }
            ProductSortOption.NONE -> filtered
        }

        currentBinding.ro3mz3pf0wak.text =
            getString(R.string.shop_results_count, sorted.size)
        productAdapter.submitList(sorted)
    }

    private fun openProductDetail(product: Product) {
        startActivity(productDetail.createIntent(requireContext(), product))
    }

    private fun handleError(throwable: Throwable) {
        val currentBinding = _binding ?: return
        Snackbar.make(
            currentBinding.root,
            throwable.message ?: getString(R.string.generic_error_message),
            Snackbar.LENGTH_LONG
        ).show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        repository.removeListener(productsListener)
        _binding = null
    }
}
