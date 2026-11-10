package com.example.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.AppLanguage
import com.example.data.model.Strings
import com.example.ui.theme.*
import com.example.ui.viewmodel.ScreenTab

data class NavItem(
    val tab: ScreenTab,
    val titleHe: String,
    val titleEn: String,
    val iconFilled: ImageVector,
    val iconOutlined: ImageVector,
    val isHighlighted: Boolean = false
)

@Composable
fun BottomNavBar(
    currentTab: ScreenTab,
    onTabSelected: (ScreenTab) -> Unit,
    language: AppLanguage,
    modifier: Modifier = Modifier
) {
    val items = listOf(
        NavItem(ScreenTab.HOME, Strings.navHome(AppLanguage.HEBREW), Strings.navHome(AppLanguage.ENGLISH), Icons.Filled.Home, Icons.Outlined.Home),
        NavItem(ScreenTab.MISSIONS, Strings.navMissions(AppLanguage.HEBREW), Strings.navMissions(AppLanguage.ENGLISH), Icons.Filled.Flag, Icons.Outlined.Flag),
        NavItem(ScreenTab.AI_COACH, Strings.navAiCoach(AppLanguage.HEBREW), Strings.navAiCoach(AppLanguage.ENGLISH), Icons.Filled.SmartToy, Icons.Outlined.SmartToy, isHighlighted = true),
        NavItem(ScreenTab.GROUPS, Strings.navGroups(AppLanguage.HEBREW), Strings.navGroups(AppLanguage.ENGLISH), Icons.Filled.Groups, Icons.Outlined.Groups),
        NavItem(ScreenTab.COMPETITIONS, Strings.navCompetitions(AppLanguage.HEBREW), Strings.navCompetitions(AppLanguage.ENGLISH), Icons.Filled.EmojiEvents, Icons.Outlined.EmojiEvents),
        NavItem(ScreenTab.PROFILE, Strings.navProfile(AppLanguage.HEBREW), Strings.navProfile(AppLanguage.ENGLISH), Icons.Filled.AccountCircle, Icons.Outlined.AccountCircle)
    )

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .navigationBarsPadding(),
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = 8.dp,
        shadowElevation = 12.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 4.dp, vertical = 6.dp),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            items.forEach { item ->
                val selected = currentTab == item.tab
                val title = if (language == AppLanguage.HEBREW) item.titleHe else item.titleEn
                val activeColor by animateColorAsState(
                    targetValue = if (selected) {
                        if (item.isHighlighted) ElectricPurple else NeonCyan
                    } else MaterialTheme.colorScheme.onSurfaceVariant,
                    label = "nav_color"
                )

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null
                        ) { onTabSelected(item.tab) }
                        .padding(horizontal = 6.dp, vertical = 6.dp)
                        .testTag("nav_tab_${item.tab.name.lowercase()}")
                ) {
                    if (item.isHighlighted) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(
                                    Brush.linearGradient(
                                        listOf(ElectricPurple, NeonCyan)
                                    )
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = item.iconFilled,
                                contentDescription = title,
                                tint = Color.White,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    } else {
                        Icon(
                            imageVector = if (selected) item.iconFilled else item.iconOutlined,
                            contentDescription = title,
                            tint = activeColor,
                            modifier = Modifier.size(22.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(2.dp))

                    Text(
                        text = title,
                        fontSize = 10.sp,
                        fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium,
                        color = activeColor
                    )
                }
            }
        }
    }
}
