package com.ondot.navigation.base

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import com.ondot.navigation.NavRoutes

interface NavGraphContributor {
    val graphRoute: NavRoutes
    val startDestination: String
    val priority: Int get() = 100

    fun NavGraphBuilder.registerGraph(navController: NavHostController)
}