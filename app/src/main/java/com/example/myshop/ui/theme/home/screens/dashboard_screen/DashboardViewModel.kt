package com.example.myshop.ui.theme.home.screens.dashboard_screen

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myshop.data.network.dto.Product
import com.example.myshop.data.network.repository.NetworkRepository
import com.example.myshop.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DashboardViewModel @Inject constructor(
    private val networkRepository: NetworkRepository
) : ViewModel() {

    private val _products = MutableStateFlow<Resource<List<Product>>>(Resource.Idle)
    val products: StateFlow<Resource<List<Product>>> = _products

    private val _categories = MutableStateFlow<Resource<List<String>>>(Resource.Idle)
    val categories: StateFlow<Resource<List<String>>> = _categories

    var state by mutableStateOf(DashboardScreenState())


    private var searchJob: Job? = null

    fun onEvent(event: DashboardScreenEvent) {
        when(event) {
            is DashboardScreenEvent.OnCategoryChange -> {
                state = state.copy(category = event.category)
                getProductsByCategory(categoryName = state.category)
            }

            is DashboardScreenEvent.OnProductClicked -> {
                state = state.copy(selectedProduct = event.product)
            }

            is DashboardScreenEvent.OnSearchIconClicked -> {
                state = state.copy(
                    isSearchBarVisible = true,
                    productsList = emptyList()
                )
            }

            is DashboardScreenEvent.OnCloseIconClicked -> {
                state = state.copy(isSearchBarVisible = false)
                getProductsByCategory(categoryName = state.category)
            }

            is DashboardScreenEvent.OnSearchQueryChanged -> {
                state = state.copy(searchQuery = event.searchQuery)
                searchJob?.cancel()
                searchJob = viewModelScope.launch {
                    delay(1000)
                    searchProduct(query = state.searchQuery)
                }
            }
        }
    }

    init {
        getAllCategories()
        getAllProducts()
    }

    private fun getAllProducts() = viewModelScope.launch {
        _products.value = Resource.Loading
        val result = networkRepository.getProductsListFromApi()
        _products.value = result
    }

    private fun searchProduct(query: String) = viewModelScope.launch {
        val result = networkRepository.getProductsListBySearchFromApi(query = query)
        _products.value = result
    }

    private fun getAllCategories() = viewModelScope.launch {
        val result = networkRepository.getAllCategoriesListFromApi()
        _categories.value = result
    }

    private fun getProductsByCategory(categoryName: String) = viewModelScope.launch {
        val result =
            networkRepository.getProductsListByCategoryNameFromApi(categoryName = categoryName)
        _products.value = result
    }

}