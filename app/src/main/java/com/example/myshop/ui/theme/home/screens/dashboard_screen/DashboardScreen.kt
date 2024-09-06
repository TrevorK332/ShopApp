package com.example.myshop.ui.theme.home.screens.dashboard_screen

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.myshop.data.network.dto.Product
import com.example.myshop.ui.theme.home.component.CategoryTabRow
import com.example.myshop.ui.theme.home.component.Error
import com.example.myshop.ui.theme.home.component.HomeScreenTopBar
import com.example.myshop.ui.theme.home.component.Loading
import com.example.myshop.ui.theme.home.component.ProductList
import com.example.myshop.ui.theme.home.component.SearchAppBar
import com.example.myshop.util.Resource
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.rememberPagerState
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun DashboardScreen(
    state: DashboardScreenState,
    onEvent: (DashboardScreenEvent) -> Unit,
    onProductClicked: (Product) -> Unit,
    viewModel: DashboardViewModel = hiltViewModel(),
) {
    val productState by viewModel.products.collectAsState()
    val categoryState by viewModel.categories.collectAsState()

    DashboardContent(
        productState = productState,
        categoryState = categoryState,
        onProductClicked = onProductClicked,
        state = state,
        onEvent = onEvent
    )

}


@Composable
fun DashboardContent(
    productState: Resource<List<Product>>?,
    categoryState: Resource<List<String>>,
    onProductClicked: (Product) -> Unit,
    state: DashboardScreenState,
    onEvent: (DashboardScreenEvent) -> Unit
) {
    Box(modifier = Modifier.fillMaxSize()) {
        when {
            productState is Resource.Success && categoryState is Resource.Success -> {
                SuccessScreen(
                    productUiData = productState.data,
                    categoryUiData = categoryState.data,
                    onProductClicked = onProductClicked,
                    state = state,
                    onEvent = onEvent
                )
            }

            productState is Resource.Failure<*> || categoryState is Resource.Failure<*> -> {
                Error(message = "Error")
            }

            productState is Resource.Loading || categoryState is Resource.Loading -> {
                Loading()
            }

            else -> {
                Error(message = "Error")
            }
        }
    }
}


@OptIn(
    ExperimentalComposeUiApi::class, ExperimentalMaterial3Api::class,
    ExperimentalPagerApi::class
)
@Composable
fun SuccessScreen(
    productUiData: List<Product>,
    categoryUiData: List<String>,
    onProductClicked: (Product) -> Unit,
    state: DashboardScreenState,
    onEvent: (DashboardScreenEvent) -> Unit
) {

    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()
    val pagerState = rememberPagerState()
    val coroutineScope = rememberCoroutineScope()

    val focusRequester = remember { FocusRequester() }
    val focusManager = LocalFocusManager.current
    val keyboardController = LocalSoftwareKeyboardController.current

    LaunchedEffect(key1 = pagerState) {
        snapshotFlow { pagerState.currentPage }.collect { page ->
            onEvent(DashboardScreenEvent.OnCategoryChange(category = categoryUiData[page]))
        }
    }

    LaunchedEffect(key1 = Unit) {
        if (state.searchQuery.isNotEmpty()) {
            onEvent(DashboardScreenEvent.OnSearchQueryChanged(searchQuery = state.searchQuery))
        }
    }

    Column(modifier = Modifier.fillMaxSize()) {
        Crossfade(targetState = state.isSearchBarVisible, label = "") { isVisible ->
            if (isVisible) {
                Column {
                    SearchAppBar(
                        modifier = Modifier
                            .focusRequester(focusRequester),
                        value = state.searchQuery,
                        onInputValueChange = { newValue ->
                            onEvent(DashboardScreenEvent.OnSearchQueryChanged(newValue))
                        },
                        onCloseIconClicked = { onEvent(DashboardScreenEvent.OnCloseIconClicked) },
                        onSearchIconClicked = {
                            keyboardController?.hide()
                            focusManager.clearFocus()
                        }
                    )
                    ProductList(
                        products = productUiData,
                        onProductClicked = onProductClicked
                    )
                }
            } else {
                Scaffold(
                    modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
                    topBar = {
                        HomeScreenTopBar(
                            scrollBehavior = scrollBehavior,
                            onSearchIconClicked = {
                                coroutineScope.launch {
                                    delay(500)
                                    focusRequester.requestFocus()
                                }
                                onEvent(DashboardScreenEvent.OnSearchIconClicked)
                            }
                        )
                    }
                ) { padding ->
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(padding)
                    ) {
                        CategoryTabRow(
                            pagerState = pagerState,
                            categories = categoryUiData,
                            onTabSelected = { index ->
                                coroutineScope.launch { pagerState.animateScrollToPage(index) }
                            }
                        )

                        HorizontalPager(
                            count = categoryUiData.size,
                            state = pagerState
                        ) {
                            ProductList(
                                products = productUiData,
                                onProductClicked = onProductClicked
                            )
                        }
                    }
                }
            }
        }
    }
}