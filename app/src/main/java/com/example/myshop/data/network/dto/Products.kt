package com.example.myshop.data.network.dto

data class Products(
    val limit: Int,
    val products: List<Product>,
    val skip: Int,
    val total: Int
)