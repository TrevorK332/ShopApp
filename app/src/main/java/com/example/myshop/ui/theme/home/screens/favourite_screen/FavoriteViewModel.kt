package com.example.myshop.ui.theme.home.screens.favourite_screen

import android.content.SharedPreferences
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myshop.data.local.models.FavoriteProduct
import com.example.myshop.data.local.repositories.LocalRepository
import com.example.myshop.util.Resource
import com.example.myshop.util.getUserIdFromSharedPref
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FavoriteViewModel @Inject constructor(
    private val localRepository: LocalRepository,
    private val sharedPreferences: SharedPreferences
) : ViewModel() {
    private val _favoriteCarts = MutableStateFlow<Resource<List<FavoriteProduct>>>(Resource.Idle)
    val favoriteCarts: StateFlow<Resource<List<FavoriteProduct>>> = _favoriteCarts

    init {
        getFavoriteProducts()
    }

    private fun getFavoriteProducts() = viewModelScope.launch {
        val result =
            localRepository.getFavoriteProductsFromDb(getUserIdFromSharedPref(sharedPreferences))
        _favoriteCarts.value = result
    }

    fun deleteFavoriteItem(favoriteProduct: FavoriteProduct) = viewModelScope.launch {
        localRepository.deleteFavoriteItemFromDb(favoriteProduct = favoriteProduct)
        getFavoriteProducts()
    }
}