package com.example.myshop.ui.theme.home.screens.dashboard_screen

import com.example.myshop.data.network.dto.Product

sealed class DashboardScreenEvent {
    data class OnProductClicked(var product: Product) : DashboardScreenEvent()
    data class OnCategoryChange(var category: String) : DashboardScreenEvent()
    data class OnSearchQueryChanged(var searchQuery: String) : DashboardScreenEvent()
    object OnSearchIconClicked: DashboardScreenEvent()
    object OnCloseIconClicked: DashboardScreenEvent()
}
