package com.example.blooddonationapp.screens

import ProfileScreen
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.annotation.StringRes
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.blooddonationapp.R

enum class BloodDonorScreen(@StringRes val title: Int) {
    Start(R.string.app_name),
    Home(R.string.home),
    Appointment(R.string.appointment),
    Profile(R.string.profile)
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun BloodDonorApp(
    navController: NavHostController = rememberNavController(),
) {
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentScreen = BloodDonorScreen.valueOf(
        backStackEntry?.destination?.route ?: BloodDonorScreen.Start.name
    )

    Scaffold(
        bottomBar = {
            if (currentScreen != BloodDonorScreen.Start) {
                BottomNavigationBar(navController)
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = BloodDonorScreen.Start.name,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(route = BloodDonorScreen.Start.name) {
                StartScreen(
                    onNextButtonClicked = {
                        navController.navigate(BloodDonorScreen.Home.name) {
                            popUpTo(BloodDonorScreen.Start.name) { inclusive = true }
                        }
                    },
                    modifier = Modifier.fillMaxSize()
                )
            }
            composable(route = BloodDonorScreen.Home.name) {
                HomeScreen(
                    onNextButtonClicked = { /* Not needed for bottom nav */ },
                    modifier = Modifier.fillMaxSize()
                )
            }
            composable(route = BloodDonorScreen.Appointment.name) {
                val context = LocalContext.current

                AppointmentScreen(
                    onNextButtonClicked = {},
                    onCancelButtonClicked = {},
                    onShareButtonClicked = { subject, message ->
                        shareDetails(context, subject, message)
                    },
                    modifier = Modifier.fillMaxSize()
                )
            }
            composable(route = BloodDonorScreen.Profile.name) {
                ProfileScreen(
                    onNextButtonClicked = { /* Not needed for bottom nav */ },
                )
            }
        }
    }
}

@Composable
fun BottomNavigationBar(navController: NavHostController) {
    NavigationBar {
        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentDestination = navBackStackEntry?.destination

        val items = listOf(
            BloodDonorScreen.Home to Icons.Filled.Home,
            BloodDonorScreen.Appointment to Icons.Filled.DateRange,
            BloodDonorScreen.Profile to Icons.Filled.Person
        )

        items.forEach { (screen, icon) ->
            NavigationBarItem(
                icon = { Icon(icon, contentDescription = null) },
                label = { Text(stringResource(screen.title)) },
                selected = currentDestination?.hierarchy?.any { it.route == screen.name } == true,
                onClick = {
                    navController.navigate(screen.name) {
                        popUpTo(navController.graph.findStartDestination().id) {
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                }
            )
        }
    }
}