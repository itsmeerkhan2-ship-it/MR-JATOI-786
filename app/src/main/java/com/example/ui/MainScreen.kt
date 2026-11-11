package com.example.ui

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.repository.AppRepository
import com.example.localization.AppLanguage
import com.example.localization.AppStrings
import com.example.ui.screens.admin.AdminDashboardScreen
import com.example.ui.screens.auth.AuthScreen
import com.example.ui.screens.discover.DiscoverScreen
import com.example.ui.screens.home.HomeScreen
import com.example.ui.screens.monetization.MonetizationPortalScreen
import com.example.ui.screens.profile.ProfileScreen
import com.example.ui.screens.upload.UploadScreen
import com.example.ui.screens.wallet.WalletScreen
import com.example.ui.theme.*

enum class AppDestination {
    HOME,
    DISCOVER,
    UPLOAD,
    WALLET,
    PROFILE
}

@Composable
fun MainScreen(
    repository: AppRepository = AppRepository.instance
) {
    var currentTab by remember { mutableStateOf(AppDestination.HOME) }
    var language by remember { mutableStateOf(AppLanguage.ENGLISH) }

    // Secondary Screen Overlays
    var showMonetizationPortal by remember { mutableStateOf(false) }
    var showAdminDashboard by remember { mutableStateOf(false) }
    var showAuthModal by remember { mutableStateOf(false) }

    // Android Hardware / Gesture BackHandler
    BackHandler(enabled = showAdminDashboard || showMonetizationPortal || showAuthModal || currentTab != AppDestination.HOME) {
        when {
            showAdminDashboard -> showAdminDashboard = false
            showMonetizationPortal -> showMonetizationPortal = false
            showAuthModal -> showAuthModal = false
            currentTab != AppDestination.HOME -> currentTab = AppDestination.HOME
        }
    }

    Scaffold(
        bottomBar = {
            if (!showAdminDashboard && !showMonetizationPortal && !showAuthModal) {
                MrJatoiBottomNavBar(
                    selectedTab = currentTab,
                    language = language,
                    onTabSelected = { tab ->
                        currentTab = tab
                    }
                )
            }
        },
        containerColor = LuxuryBlack
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = if (!showAdminDashboard && !showMonetizationPortal && !showAuthModal) innerPadding.calculateBottomPadding() else 0.dp)
        ) {
            // Main Tab View
            when (currentTab) {
                AppDestination.HOME -> {
                    HomeScreen(
                        repository = repository,
                        language = language,
                        onNavigateToWallet = { currentTab = AppDestination.WALLET },
                        onNavigateToCreator = { currentTab = AppDestination.PROFILE }
                    )
                }
                AppDestination.DISCOVER -> {
                    DiscoverScreen(
                        repository = repository,
                        language = language,
                        onVideoSelected = { currentTab = AppDestination.HOME },
                        onCreatorSelected = { currentTab = AppDestination.PROFILE }
                    )
                }
                AppDestination.UPLOAD -> {
                    UploadScreen(
                        repository = repository,
                        language = language,
                        onUploadSuccess = { currentTab = AppDestination.HOME }
                    )
                }
                AppDestination.WALLET -> {
                    WalletScreen(
                        repository = repository,
                        language = language,
                        onNavigateToMonetization = { showMonetizationPortal = true }
                    )
                }
                AppDestination.PROFILE -> {
                    ProfileScreen(
                        repository = repository,
                        language = language,
                        onLanguageChange = { language = it },
                        onNavigateToMonetization = { showMonetizationPortal = true },
                        onNavigateToAdmin = { showAdminDashboard = true },
                        onVideoSelected = { currentTab = AppDestination.HOME }
                    )
                }
            }

            // Monetization Screen Overlay
            if (showMonetizationPortal) {
                MonetizationPortalScreen(
                    repository = repository,
                    language = language,
                    onBack = { showMonetizationPortal = false }
                )
            }

            // Administrator Dashboard Overlay
            if (showAdminDashboard) {
                AdminDashboardScreen(
                    repository = repository,
                    language = language,
                    onBack = { showAdminDashboard = false }
                )
            }

            // Auth Modal Overlay
            if (showAuthModal) {
                AuthScreen(
                    repository = repository,
                    language = language,
                    onAuthSuccess = { showAuthModal = false },
                    onDismiss = { showAuthModal = false }
                )
            }
        }
    }
}

@Composable
fun MrJatoiBottomNavBar(
    selectedTab: AppDestination,
    language: AppLanguage,
    onTabSelected: (AppDestination) -> Unit
) {
    Surface(
        color = LuxuryBlack,
        tonalElevation = 8.dp,
        border = androidx.compose.foundation.BorderStroke(1.dp, LuxuryBorder),
        modifier = Modifier
            .fillMaxWidth()
            .windowInsetsPadding(WindowInsets.navigationBars)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 6.dp),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Home
            NavTabItem(
                label = AppStrings.get("home", language),
                icon = if (selectedTab == AppDestination.HOME) Icons.Filled.Home else Icons.Outlined.Home,
                isSelected = selectedTab == AppDestination.HOME,
                onClick = { onTabSelected(AppDestination.HOME) },
                testTag = "nav_home_tab"
            )

            // Discover
            NavTabItem(
                label = AppStrings.get("discover", language),
                icon = if (selectedTab == AppDestination.DISCOVER) Icons.Filled.Explore else Icons.Outlined.Explore,
                isSelected = selectedTab == AppDestination.DISCOVER,
                onClick = { onTabSelected(AppDestination.DISCOVER) },
                testTag = "nav_discover_tab"
            )

            // Upload (Special Golden Pill FAB in center)
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .clip(RoundedCornerShape(14.dp))
                    .background(GoldPrimary)
                    .clickable { onTabSelected(AppDestination.UPLOAD) }
                    .padding(horizontal = 14.dp, vertical = 6.dp)
                    .testTag("nav_upload_tab")
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = AppStrings.get("upload", language),
                    tint = LuxuryBlack,
                    modifier = Modifier.size(24.dp)
                )
            }

            // Wallet
            NavTabItem(
                label = AppStrings.get("wallet", language),
                icon = if (selectedTab == AppDestination.WALLET) Icons.Filled.AccountBalanceWallet else Icons.Outlined.AccountBalanceWallet,
                isSelected = selectedTab == AppDestination.WALLET,
                onClick = { onTabSelected(AppDestination.WALLET) },
                testTag = "nav_wallet_tab"
            )

            // Profile
            NavTabItem(
                label = AppStrings.get("profile", language),
                icon = if (selectedTab == AppDestination.PROFILE) Icons.Filled.Person else Icons.Outlined.Person,
                isSelected = selectedTab == AppDestination.PROFILE,
                onClick = { onTabSelected(AppDestination.PROFILE) },
                testTag = "nav_profile_tab"
            )
        }
    }
}

@Composable
private fun NavTabItem(
    label: String,
    icon: ImageVector,
    isSelected: Boolean,
    onClick: () -> Unit,
    testTag: String
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier
            .clickable { onClick() }
            .padding(horizontal = 12.dp, vertical = 4.dp)
            .testTag(testTag)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            tint = if (isSelected) GoldPrimary else TextMuted,
            modifier = Modifier.size(22.dp)
        )
        Spacer(modifier = Modifier.height(3.dp))
        Text(
            text = label,
            color = if (isSelected) GoldPrimary else TextMuted,
            fontSize = 10.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
        )
    }
}
