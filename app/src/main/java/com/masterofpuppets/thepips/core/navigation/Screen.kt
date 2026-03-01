package com.masterofpuppets.thepips.core.navigation

import androidx.annotation.StringRes
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.List
import androidx.compose.ui.graphics.vector.ImageVector
import com.masterofpuppets.thepips.R

sealed class Screen(
    val route: String,
    @StringRes val titleResId: Int,
    val icon: ImageVector
) {
    data object Dashboard : Screen(
        route = "dashboard",
        titleResId = R.string.title_dashboard,
        icon = Icons.Default.Home
    )

    data object Library : Screen(
        route = "library",
        titleResId = R.string.title_library,
        icon = Icons.Default.List
    )

    data object Info : Screen(
        route = "info",
        titleResId = R.string.title_info,
        icon = Icons.Default.Info
    )
}