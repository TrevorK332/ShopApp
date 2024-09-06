package com.example.myshop.ui.theme.home.screens.detail_screen

import android.content.SharedPreferences
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myshop.data.local.models.FavoriteProduct
import com.example.myshop.data.local.models.UserCart
import com.example.myshop.data.local.repositories.LocalRepository
import com.example.myshop.data.network.dto.Product
import com.example.myshop.data.network.repository.NetworkRepository
import com.example.myshop.util.Resource
import com.example.myshop.util.getUserIdFromSharedPref
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DetailViewModel @Inject constructor(
    private val networkRepository: NetworkRepository,
    private val localRepository: LocalRepository,
    private val sharedPreferences: SharedPreferences,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _details = MutableStateFlow<Resource<Product>>(Resource.Idle)
    val details: StateFlow<Resource<Product>> = _details

    init {
        getDetail()
    }

    private fun getDetail() = viewModelScope.launch {
        savedStateHandle.get<Int>("id")?.let { id ->
            val result = networkRepository.getSingleProductByIdFromApi(productId = id)
            _details.value = result
        }
    }


    fun addToCart(userCart: UserCart) = viewModelScope.launch {
        localRepository.insertUserCartToDb(
            userCart.copy(
                userId = getUserIdFromSharedPref(sharedPreferences)
            )
        )
    }

    fun addToFavorite(favoriteProduct: FavoriteProduct) = viewModelScope.launch {
        localRepository.insertFavoriteItemToDb(
            favoriteProduct.copy(
                userId = getUserIdFromSharedPref(sharedPreferences)
            )
        )
    }
}