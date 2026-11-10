package com.example

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.Crossfade
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.data.model.AppLanguage
import com.example.ui.components.BottomNavBar
import com.example.ui.components.LevelUpCelebrationDialog
import com.example.ui.components.TopHeaderBar
import com.example.ui.screens.*
import com.example.ui.theme.ChallengeMeTheme
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.VictoryGold
import com.example.ui.viewmodel.ChallengeMeViewModel
import com.example.ui.viewmodel.ScreenTab

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            val viewModel: ChallengeMeViewModel = viewModel()
            val isDarkTheme by viewModel.isDarkTheme.collectAsStateWithLifecycle()
            val language by viewModel.language.collectAsStateWithLifecycle()
            val currentScreen by viewModel.currentScreen.collectAsStateWithLifecycle()
            val userProfile by viewModel.userProfile.collectAsStateWithLifecycle()
            val challenges by viewModel.challenges.collectAsStateWithLifecycle()
            val friendDuel by viewModel.friendDuel.collectAsStateWithLifecycle()
            val groupWar by viewModel.groupWar.collectAsStateWithLifecycle()
            val activeGroup by viewModel.activeGroup.collectAsStateWithLifecycle()
            val leaderboard by viewModel.leaderboard.collectAsStateWithLifecycle()
            val aiMessages by viewModel.aiMessages.collectAsStateWithLifecycle()
            val weeklyAdventure by viewModel.weeklyAdventure.collectAsStateWithLifecycle()
            val levelUpData by viewModel.levelUpEvent.collectAsStateWithLifecycle()
            val toastMessage by viewModel.toastMessage.collectAsStateWithLifecycle()

            val context = LocalContext.current

            // Toast feedback
            LaunchedEffect(toastMessage) {
                toastMessage?.let { msg ->
                    Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
                    viewModel.clearToast()
                }
            }

            // BackHandler: Return to Home if on secondary screen
            BackHandler(enabled = currentScreen != ScreenTab.HOME) {
                viewModel.handleBack()
            }

            // Language Direction: RTL for Hebrew, LTR for English
            CompositionLocalProvider(LocalLayoutDirection provides language.layoutDirection) {
                ChallengeMeTheme(darkTheme = isDarkTheme) {
                    Scaffold(
                        modifier = Modifier.fillMaxSize(),
                        containerColor = MaterialTheme.colorScheme.background,
                        topBar = {
                            if (currentScreen != ScreenTab.SETTINGS) {
                                TopHeaderBar(
                                    stars = userProfile.stars,
                                    language = language,
                                    onLanguageToggle = { viewModel.toggleLanguage() },
                                    onSettingsClick = { viewModel.navigateTo(ScreenTab.SETTINGS) },
                                    onNotificationsClick = {
                                        Toast.makeText(
                                            context,
                                            if (language == AppLanguage.HEBREW) "אין התראות חדשות כרגע ✨" else "No new notifications ✨",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }
                                )
                            }
                        },
                        bottomBar = {
                            if (currentScreen != ScreenTab.SETTINGS) {
                                BottomNavBar(
                                    currentTab = currentScreen,
                                    onTabSelected = { tab -> viewModel.navigateTo(tab) },
                                    language = language
                                )
                            }
                        }
                    ) { innerPadding ->
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(innerPadding)
                        ) {
                            Crossfade(
                                targetState = currentScreen,
                                label = "screen_crossfade"
                            ) { tab ->
                                when (tab) {
                                    ScreenTab.HOME -> HomeScreen(
                                        viewModel = viewModel,
                                        user = userProfile,
                                        challenges = challenges,
                                        language = language,
                                        onNavigate = { dest -> viewModel.navigateTo(dest) }
                                    )
                                    ScreenTab.MISSIONS -> ChallengesScreen(
                                        viewModel = viewModel,
                                        challenges = challenges,
                                        language = language
                                    )
                                    ScreenTab.AI_COACH -> AiCoachScreen(
                                        viewModel = viewModel,
                                        messages = aiMessages,
                                        weeklyAdventure = weeklyAdventure,
                                        language = language
                                    )
                                    ScreenTab.GROUPS -> GroupsScreen(
                                        viewModel = viewModel,
                                        group = activeGroup,
                                        language = language
                                    )
                                    ScreenTab.COMPETITIONS -> CompetitionsScreen(
                                        viewModel = viewModel,
                                        friendDuel = friendDuel,
                                        groupWar = groupWar,
                                        leaderboard = leaderboard,
                                        language = language,
                                        onNavigateToMissions = { viewModel.navigateTo(ScreenTab.MISSIONS) }
                                    )
                                    ScreenTab.PROFILE -> ProfileScreen(
                                        viewModel = viewModel,
                                        user = userProfile,
                                        language = language
                                    )
                                    ScreenTab.SETTINGS -> SettingsScreen(
                                        viewModel = viewModel,
                                        language = language,
                                        isDarkTheme = isDarkTheme,
                                        hapticsEnabled = viewModel.hapticsEnabled.collectAsStateWithLifecycle().value,
                                        onBack = { viewModel.handleBack() }
                                    )
                                }
                            }

                            // Level Up Celebration Dialog
                            levelUpData?.let { data ->
                                LevelUpCelebrationDialog(
                                    data = data,
                                    language = language,
                                    onDismiss = { viewModel.dismissLevelUp() }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
