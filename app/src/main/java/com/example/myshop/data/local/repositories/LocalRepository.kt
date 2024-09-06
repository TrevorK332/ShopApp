package com.example.myshop.data.local.repositories

import com.example.myshop.data.local.models.FavoriteProduct
import com.example.myshop.data.local.models.UserCart
import com.example.myshop.util.Resource

interface LocalRepository {

    suspend fun getUserCartByUserIdFromDb(userId: String): Resource<List<UserCart>>

    suspend fun insertUserCartToDb(userCart: UserCart)

    suspend fun deleteUserCartFromDb(userCart: UserCart)

    suspend fun updateUserCartFromDb(userCart: UserCart)

    suspend fun getFavoriteProductsFromDb(userId: String): Resource<List<FavoriteProduct>>

    suspend fun insertFavoriteItemToDb(favoriteProduct: FavoriteProduct)

    suspend fun deleteFavoriteItemFromDb(favoriteProduct: FavoriteProduct)

    suspend fun getBadgeCountFromDb(userId: String): Int
}