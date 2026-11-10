package com.example.ui.screens

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.*
import com.example.ui.theme.*
import com.example.ui.viewmodel.ChallengeMeViewModel
import com.example.ui.viewmodel.ScreenTab

enum class CompetitionTab {
    DUEL,
    LEADERBOARD,
    GROUP_WAR
}

@Composable
fun CompetitionsScreen(
    viewModel: ChallengeMeViewModel,
    friendDuel: FriendDuel,
    groupWar: GroupWar,
    leaderboard: List<LeaderboardUser>,
    language: AppLanguage,
    onNavigateToMissions: () -> Unit,
    modifier: Modifier = Modifier
) {
    var activeTab by remember { mutableStateOf(CompetitionTab.DUEL) }
    var leaderboardFilter by remember { mutableStateOf("friends") }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("competitions_screen"),
        contentPadding = PaddingValues(bottom = 90.dp, top = 8.dp, start = 16.dp, end = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Banner Header
        item {
            Card(
                shape = RoundedCornerShape(22.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = if (language == AppLanguage.HEBREW)
                            "דו-קרב חברים ומלחמת קבוצות! 🥊"
                        else "Friend Duels & Squad Wars! 🥊",
                        fontWeight = FontWeight.Black,
                        fontSize = 18.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = if (language == AppLanguage.HEBREW)
                            "משחקים יחד, מרוויחים כוכבים ומנצחים בידידות! ✨"
                        else "Play together, earn stars and win with good sportsmanship! ✨",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        // Segmented Tabs (1v1 Duel / Weekly Star Trophy / Squad War)
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                CompetitionTabButton(
                    title = if (language == AppLanguage.HEBREW) "דו-קרב 1 על 1 🥊" else "1v1 Duel 🥊",
                    isSelected = activeTab == CompetitionTab.DUEL,
                    onClick = { activeTab = CompetitionTab.DUEL },
                    modifier = Modifier.weight(1f)
                )
                CompetitionTabButton(
                    title = if (language == AppLanguage.HEBREW) "גביע שבועי 🏆" else "Weekly Cup 🏆",
                    isSelected = activeTab == CompetitionTab.LEADERBOARD,
                    onClick = { activeTab = CompetitionTab.LEADERBOARD },
                    modifier = Modifier.weight(1f)
                )
                CompetitionTabButton(
                    title = if (language == AppLanguage.HEBREW) "מלחמת קבוצות 🛡️" else "Squad War 🛡️",
                    isSelected = activeTab == CompetitionTab.GROUP_WAR,
                    onClick = { activeTab = CompetitionTab.GROUP_WAR },
                    modifier = Modifier.weight(1f)
                )
            }
        }

        // Tab Content
        when (activeTab) {
            CompetitionTab.DUEL -> {
                item {
                    DuelTabContent(
                        duel = friendDuel,
                        language = language,
                        onSendCheer = { viewModel.sendCheerToOpponent() },
                        onDoMission = onNavigateToMissions
                    )
                }
            }
            CompetitionTab.LEADERBOARD -> {
                item {
                    LeaderboardTabContent(
                        leaderboard = leaderboard,
                        language = language,
                        filter = leaderboardFilter,
                        onFilterChange = { leaderboardFilter = it },
                        onEarnMoreStars = onNavigateToMissions
                    )
                }
            }
            CompetitionTab.GROUP_WAR -> {
                item {
                    GroupWarTabContent(
                        groupWar = groupWar,
                        language = language
                    )
                }
            }
        }
    }
}

@Composable
fun CompetitionTabButton(
    title: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        shape = RoundedCornerShape(14.dp),
        color = if (isSelected) NeonCyan else MaterialTheme.colorScheme.surfaceVariant,
        modifier = modifier
            .clip(RoundedCornerShape(14.dp))
            .clickable(onClick = onClick)
    ) {
        Text(
            text = title,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            color = if (isSelected) Color(0xFF001F28) else MaterialTheme.colorScheme.onSurface,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(vertical = 10.dp, horizontal = 6.dp)
        )
    }
}

@Composable
fun DuelTabContent(
    duel: FriendDuel,
    language: AppLanguage,
    onSendCheer: () -> Unit,
    onDoMission: () -> Unit
) {
    val total = duel.userStars + duel.opponentStars
    val userPercent = if (total > 0) ((duel.userStars.toFloat() / total) * 100).toInt() else 50
    val oppPercent = 100 - userPercent

    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Card(
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(18.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = if (duel.userStars >= duel.opponentStars) "⭐ מאיה מובילה!" else "🔥 קרב צמוד!",
                        fontWeight = FontWeight.Black,
                        fontSize = 15.sp,
                        color = VictoryGold
                    )
                    Text(
                        text = duel.timeLeftTextHe,
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Competitors Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceAround,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // User
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Surface(
                            shape = CircleShape,
                            color = NeonCyan.copy(alpha = 0.2f),
                            modifier = Modifier.size(68.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text(text = "👧", fontSize = 36.sp)
                            }
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(text = "מאיה (את)", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        Text(text = "${duel.userStars} ⭐", fontWeight = FontWeight.Black, color = VictoryGold, fontSize = 14.sp)
                        Text(text = "${duel.userCompletedMissions} משימות", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }

                    // VS Crest
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.background),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = "VS", fontWeight = FontWeight.Black, fontSize = 16.sp, color = EnergyOrange)
                    }

                    // Opponent
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Surface(
                            shape = CircleShape,
                            color = CoralRed.copy(alpha = 0.2f),
                            modifier = Modifier.size(68.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text(text = duel.opponentAvatar, fontSize = 36.sp)
                            }
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(text = duel.opponentName, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        Text(text = "${duel.opponentStars} ⭐", fontWeight = FontWeight.Black, color = VictoryGold, fontSize = 14.sp)
                        Text(text = "${duel.opponentCompletedMissions} משימות", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Score bar percentages
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(text = "$userPercent%", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = NeonCyan)
                    Text(text = "$oppPercent%", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = CoralRed)
                }

                Spacer(modifier = Modifier.height(6.dp))

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(10.dp)
                        .clip(RoundedCornerShape(5.dp))
                        .background(CoralRed)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(userPercent / 100f)
                            .fillMaxHeight()
                            .clip(RoundedCornerShape(5.dp))
                            .background(NeonCyan)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Victory Prize description
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = MaterialTheme.colorScheme.surface.copy(alpha = 0.7f),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text(
                            text = "🎁 פרס הניצחון לשתיכן:",
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = duel.rewardDescHe,
                            fontSize = 12.sp,
                            color = VictoryGold
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = onSendCheer,
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.surface),
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier
                            .weight(1f)
                            .testTag("send_cheer_duel_button")
                    ) {
                        Text(
                            text = Strings.sendHighFive(language, duel.opponentName),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }

                    Button(
                        onClick = onDoMission,
                        colors = ButtonDefaults.buttonColors(containerColor = BrightBlue),
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier
                            .weight(1f)
                            .testTag("duel_do_mission_button")
                    ) {
                        Text(
                            text = "משימה (+120 ⭐) 🚀",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                }
            }
        }

        // Invite Friends to Duel
        Text(
            text = "✨ הזמיני חבר לדו-קרב ידידותי!",
            fontWeight = FontWeight.Bold,
            fontSize = 15.sp,
            color = MaterialTheme.colorScheme.onSurface
        )

        LazyRow(horizontalArrangement = Arrangement.spacedBy(14.dp)) {
            val friends = listOf("דן 👦", "אלי 👦", "ליאור 👦", "אמה 👧")
            items(friends) { friend ->
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    modifier = Modifier.clickable { /* invite */ }
                ) {
                    Column(
                        modifier = Modifier.padding(14.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(text = friend.split(" ").last(), fontSize = 28.sp)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(text = friend.split(" ").first(), fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        Text(text = "הזמן 🥊", fontSize = 11.sp, color = NeonCyan, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
fun LeaderboardTabContent(
    leaderboard: List<LeaderboardUser>,
    language: AppLanguage,
    filter: String,
    onFilterChange: (String) -> Unit,
    onEarnMoreStars: () -> Unit
) {
    val top1 = leaderboard.getOrNull(0)
    val top2 = leaderboard.getOrNull(1)
    val top3 = leaderboard.getOrNull(2)
    val rest = leaderboard.drop(3)

    Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
        // Filter tabs (חברים שלי / כל הילדים / קבוצות)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            FilterChip(
                selected = filter == "friends",
                onClick = { onFilterChange("friends") },
                label = { Text(Strings.tabFriends(language)) },
                modifier = Modifier.weight(1f)
            )
            FilterChip(
                selected = filter == "all",
                onClick = { onFilterChange("all") },
                label = { Text(Strings.tabAllKids(language)) },
                modifier = Modifier.weight(1f)
            )
            FilterChip(
                selected = filter == "groups",
                onClick = { onFilterChange("groups") },
                label = { Text(Strings.tabGroups(language)) },
                modifier = Modifier.weight(1f)
            )
        }

        // Podium Top 3 (Matching image_030211c9.png)
        Card(
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = Strings.weeklyCup(language),
                    fontWeight = FontWeight.Black,
                    fontSize = 17.sp,
                    color = VictoryGold
                )
                Text(
                    text = Strings.daysLeftInCup(language),
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(18.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceAround,
                    verticalAlignment = Alignment.Bottom
                ) {
                    // 2nd Place (Maya)
                    top2?.let { user ->
                        PodiumColumn(
                            user = user,
                            rank = 2,
                            badge = "🥈",
                            podiumHeight = 90.dp,
                            color = NeonCyan
                        )
                    }

                    // 1st Place (Noa)
                    top1?.let { user ->
                        PodiumColumn(
                            user = user,
                            rank = 1,
                            badge = "👑",
                            podiumHeight = 120.dp,
                            color = VictoryGold
                        )
                    }

                    // 3rd Place (Eli)
                    top3?.let { user ->
                        PodiumColumn(
                            user = user,
                            rank = 3,
                            badge = "🥉",
                            podiumHeight = 70.dp,
                            color = WarmGold
                        )
                    }
                }
            }
        }

        // Highlight card for user
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = MaterialTheme.colorScheme.surfaceVariant,
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier.padding(14.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(text = "2", fontWeight = FontWeight.Black, fontSize = 20.sp, color = NeonCyan)
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = "מאיה (את/ה) מעולה! 🌟",
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                        Text(
                            text = "רק עוד 120 כוכבים למקום הראשון! 🚀",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
                Text(text = "1,820 ⭐", fontWeight = FontWeight.Black, fontSize = 14.sp, color = VictoryGold)
            }
        }

        // Rest of leaderboard (4, 5, 6)
        rest.forEach { user ->
            Surface(
                shape = RoundedCornerShape(14.dp),
                color = MaterialTheme.colorScheme.surfaceVariant,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "${user.rank}",
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            modifier = Modifier.width(24.dp)
                        )
                        Text(text = user.avatarEmoji, fontSize = 24.sp)
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(text = user.name, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                            Text(text = user.titleHe, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                    Text(text = "${user.stars} ⭐", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = VictoryGold)
                }
            }
        }

        Button(
            onClick = onEarnMoreStars,
            colors = ButtonDefaults.buttonColors(containerColor = BrightBlue),
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
                .testTag("earn_more_stars_button")
        ) {
            Text(
                text = Strings.earnMoreStars(language),
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
                color = Color.White
            )
        }
    }
}

@Composable
fun PodiumColumn(
    user: LeaderboardUser,
    rank: Int,
    badge: String,
    podiumHeight: androidx.compose.ui.unit.Dp,
    color: Color
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.width(88.dp)
    ) {
        Text(text = badge, fontSize = 24.sp)
        Box(
            modifier = Modifier
                .size(54.dp)
                .clip(CircleShape)
                .background(color.copy(alpha = 0.25f)),
            contentAlignment = Alignment.Center
        ) {
            Text(text = user.avatarEmoji, fontSize = 28.sp)
        }

        Spacer(modifier = Modifier.height(4.dp))
        Text(text = user.name, fontWeight = FontWeight.Bold, fontSize = 12.sp, maxLines = 1)
        Text(text = "${user.stars} ⭐", fontWeight = FontWeight.ExtraBold, fontSize = 11.sp, color = VictoryGold)

        Spacer(modifier = Modifier.height(6.dp))

        Surface(
            shape = RoundedCornerShape(12.dp, 12.dp, 0.dp, 0.dp),
            color = color.copy(alpha = 0.35f),
            modifier = Modifier
                .fillMaxWidth()
                .height(podiumHeight)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Text(text = "$rank", fontWeight = FontWeight.Black, fontSize = 22.sp, color = color)
            }
        }
    }
}

@Composable
fun GroupWarTabContent(
    groupWar: GroupWar,
    language: AppLanguage
) {
    Card(
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            Text(
                text = "🛡️ מלחמת הקבוצות השבועית",
                fontWeight = FontWeight.Black,
                fontSize = 16.sp
            )
            Spacer(modifier = Modifier.height(14.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceAround,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(text = groupWar.teamAAvatar, fontSize = 36.sp)
                    Text(text = groupWar.teamAName, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    Text(text = "${groupWar.teamAScore} ⭐", fontWeight = FontWeight.Bold, color = VictoryGold)
                }

                Text(text = "VS", fontWeight = FontWeight.Black, fontSize = 18.sp, color = CoralRed)

                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(text = groupWar.teamBAvatar, fontSize = 36.sp)
                    Text(text = groupWar.teamBName, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    Text(text = "${groupWar.teamBScore} ⭐", fontWeight = FontWeight.Bold, color = VictoryGold)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "משימה משותפת: 50 משימות יחד (${groupWar.percentage}%)",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(6.dp))

            LinearProgressIndicator(
                progress = { groupWar.percentage / 100f },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .clip(RoundedCornerShape(4.dp)),
                color = ElectricPurple
            )
        }
    }
}
