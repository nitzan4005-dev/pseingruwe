package com.example.ui.screens

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
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
import androidx.compose.ui.window.Dialog
import com.example.data.model.*
import com.example.ui.theme.*
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.viewmodel.ChallengeMeViewModel

@Composable
fun ProfileScreen(
    viewModel: ChallengeMeViewModel,
    user: UserProfile,
    language: AppLanguage,
    modifier: Modifier = Modifier
) {
    val showEditDialog by viewModel.showEditProfileDialog.collectAsStateWithLifecycle()
    val showPetShopDialog by viewModel.showPetShopDialog.collectAsStateWithLifecycle()

    val progress = (user.currentXp.toFloat() / user.xpForNextLevel.toFloat()).coerceIn(0f, 1f)
    val animatedProgress by animateFloatAsState(targetValue = progress, animationSpec = tween(600), label = "profile_xp")
    val remainingXp = (user.xpForNextLevel - user.currentXp).coerceAtLeast(0)

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("profile_screen"),
        contentPadding = PaddingValues(bottom = 90.dp, top = 8.dp, start = 16.dp, end = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // 1. Top Badges & Security Pill
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = BrightBlue.copy(alpha = 0.15f)
                ) {
                    Text(
                        text = "🛡️ " + Strings.parentMode(language),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = BrightBlue,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                    )
                }

                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = SuccessGreen.copy(alpha = 0.15f)
                ) {
                    Text(
                        text = Strings.verifiedProfile(language),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = SuccessGreen,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                    )
                }
            }
        }

        // 2. Avatar & Title Center Showcase
        item {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .size(100.dp)
                        .clip(CircleShape)
                        .background(
                            Brush.sweepGradient(
                                listOf(VictoryGold, WarmGold, ElectricPurple, NeonCyan, VictoryGold)
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Box(
                        modifier = Modifier
                            .size(92.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.surface),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = user.avatarEmoji, fontSize = 52.sp)
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Avatar edit pill
                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = VictoryGold,
                    modifier = Modifier
                        .clip(RoundedCornerShape(14.dp))
                        .clickable { viewModel.openEditProfileDialog() }
                        .testTag("edit_avatar_pill")
                ) {
                    Text(
                        text = "🎨 התחפושת שלי!",
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 12.sp,
                        color = Color(0xFF2B1A00),
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 4.dp)
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = "${user.name} ⭐",
                    fontWeight = FontWeight.Black,
                    fontSize = 22.sp,
                    color = MaterialTheme.colorScheme.onSurface
                )

                Spacer(modifier = Modifier.height(4.dp))

                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant
                ) {
                    Text(
                        text = "🚀 ${user.title} • רמה ${user.level}",
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        color = VictoryGold,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.7f)
                ) {
                    Text(
                        text = "🦉 גאוות \"${user.groupName}\" (דרגה 8 במועדון)",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp)
                    )
                }
            }
        }

        // 3. Star Meter (Road to Next Level)
        item {
            Card(
                shape = RoundedCornerShape(22.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "✨ " + Strings.xpMeterTitle(language),
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                        Text(
                            text = "%,d / %,d ⭐".format(user.currentXp, user.xpForNextLevel),
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 14.sp,
                            color = VictoryGold
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(10.dp)
                            .clip(RoundedCornerShape(5.dp))
                            .background(MaterialTheme.colorScheme.background)
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth(animatedProgress)
                                .fillMaxHeight()
                                .clip(RoundedCornerShape(5.dp))
                                .background(
                                    Brush.horizontalGradient(listOf(WarmGold, EnergyOrange))
                                )
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = MaterialTheme.colorScheme.surface.copy(alpha = 0.7f),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = Strings.xpRemaining(language, remainingXp, user.level + 1, "סופר-סטאר מנצחת!"),
                            fontSize = 12.sp,
                            color = NeonCyan,
                            fontWeight = FontWeight.Medium,
                            modifier = Modifier.padding(10.dp)
                        )
                    }
                }
            }
        }

        // 4. Badges & Trophy Closet Grid
        item {
            Text(
                text = Strings.trophyCloset(language, 18, 24),
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp,
                color = MaterialTheme.colorScheme.onSurface
            )
        }

        item {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                val chunkedBadges = user.badges.chunked(2)
                chunkedBadges.forEach { rowBadges ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        rowBadges.forEach { badge ->
                            BadgeCard(
                                badge = badge,
                                language = language,
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }
            }
        }

        // 5. Personal Stats Grid
        item {
            Text(
                text = Strings.myStats(language),
                fontWeight = FontWeight.ExtraBold,
                fontSize = 16.sp,
                color = MaterialTheme.colorScheme.onSurface
            )
        }

        item {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    StatCard(
                        icon = "🎯",
                        count = user.stats.completedMissions,
                        title = "משימות שהושלמו בשמחה",
                        modifier = Modifier.weight(1f)
                    )
                    StatCard(
                        icon = "⚡",
                        count = user.stats.duelsWon,
                        title = "קרבות אתגר ידידותיים",
                        modifier = Modifier.weight(1f)
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    StatCard(
                        icon = "📚",
                        count = user.stats.booksRead,
                        title = "ספרי סיפורים שנקראו",
                        modifier = Modifier.weight(1f)
                    )
                    StatCard(
                        icon = "💖",
                        count = user.stats.kindnessActs,
                        title = "פעמים ששימחת חבר/ה",
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }

        // 6. Companion Pets Section
        item {
            Column {
                Text(
                    text = Strings.companionsTitle(language),
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 16.sp
                )
                Text(
                    text = Strings.companionsSubtitle(language),
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        item {
            LazyRow(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                items(user.companions) { pet ->
                    CompanionPetCard(
                        pet = pet,
                        language = language,
                        onEquip = { viewModel.equipPet(pet.id) }
                    )
                }
            }
        }

        // Open Star Store Button
        item {
            Button(
                onClick = { viewModel.openPetShopDialog() },
                colors = ButtonDefaults.buttonColors(containerColor = VictoryGold),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .testTag("open_pet_store_button")
            ) {
                Text(
                    text = Strings.openStarShop(language),
                    fontWeight = FontWeight.Black,
                    fontSize = 15.sp,
                    color = Color(0xFF261800)
                )
            }
        }

        // Edit Profile Button
        item {
            OutlinedButton(
                onClick = { viewModel.openEditProfileDialog() },
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .testTag("edit_profile_button")
            ) {
                Text(
                    text = Strings.editProfile(language),
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                )
            }
        }
    }

    // Edit Profile Modal
    if (showEditDialog) {
        EditProfileDialog(
            user = user,
            language = language,
            onDismiss = { viewModel.closeEditProfileDialog() },
            onSave = { name, bio, emoji, title ->
                viewModel.updateProfile(name, bio, emoji, title)
            }
        )
    }

    // Star Pet Shop Modal
    if (showPetShopDialog) {
        PetShopDialog(
            user = user,
            language = language,
            onDismiss = { viewModel.closePetShopDialog() },
            onBuy = { petId -> viewModel.buyPet(petId) },
            onEquip = { petId -> viewModel.equipPet(petId) }
        )
    }
}

@Composable
fun BadgeCard(
    badge: BadgeItem,
    language: AppLanguage,
    modifier: Modifier = Modifier
) {
    Card(
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        modifier = modifier
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(46.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.background),
                contentAlignment = Alignment.Center
            ) {
                Text(text = badge.iconEmoji, fontSize = 24.sp)
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = if (language == AppLanguage.HEBREW) badge.titleHe else badge.titleEn,
                fontWeight = FontWeight.Bold,
                fontSize = 13.sp,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = if (language == AppLanguage.HEBREW) badge.descHe else badge.descEn,
                fontSize = 11.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
                maxLines = 2
            )

            Spacer(modifier = Modifier.height(8.dp))

            Surface(
                shape = RoundedCornerShape(8.dp),
                color = if (badge.isUnlocked) SuccessGreen.copy(alpha = 0.2f) else MaterialTheme.colorScheme.background
            ) {
                Text(
                    text = if (badge.isUnlocked) "✓ הושלם!" else badge.unlockDate,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (badge.isUnlocked) SuccessGreen else MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                )
            }
        }
    }
}

@Composable
fun StatCard(
    icon: String,
    count: Int,
    title: String,
    modifier: Modifier = Modifier
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        modifier = modifier
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = icon, fontSize = 26.sp)
            Spacer(modifier = Modifier.width(10.dp))
            Column {
                Text(text = "$count", fontWeight = FontWeight.Black, fontSize = 18.sp)
                Text(text = title, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
    }
}

@Composable
fun CompanionPetCard(
    pet: PetCompanion,
    language: AppLanguage,
    onEquip: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(18.dp),
        color = MaterialTheme.colorScheme.surfaceVariant,
        modifier = Modifier.width(130.dp)
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (pet.isEquipped) {
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = VictoryGold
                ) {
                    Text(
                        text = "מלווה פעיל",
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Black,
                        color = Color(0xFF261800),
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            } else {
                Spacer(modifier = Modifier.height(16.dp))
            }

            Spacer(modifier = Modifier.height(4.dp))
            Text(text = pet.iconEmoji, fontSize = 34.sp)
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = if (language == AppLanguage.HEBREW) pet.nameHe else pet.nameEn,
                fontWeight = FontWeight.Bold,
                fontSize = 12.sp
            )
            Text(
                text = "דרגת חברות ${pet.friendshipLevel} ⭐",
                fontSize = 10.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(8.dp))

            if (!pet.isEquipped && pet.isUnlocked) {
                Button(
                    onClick = onEquip,
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                    modifier = Modifier.height(28.dp)
                ) {
                    Text(text = "בחר", fontSize = 10.sp)
                }
            }
        }
    }
}

@Composable
fun EditProfileDialog(
    user: UserProfile,
    language: AppLanguage,
    onDismiss: () -> Unit,
    onSave: (String, String, String, String) -> Unit
) {
    var name by remember { mutableStateOf(user.name) }
    var bio by remember { mutableStateOf(user.bio) }
    var title by remember { mutableStateOf(user.title) }
    var selectedAvatar by remember { mutableStateOf(user.avatarEmoji) }

    val avatars = listOf("👧", "👦", "🧑‍🚀", "🦸", "🦊", "🐼", "🦄", "🦁", "🦉")

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(24.dp),
            color = MaterialTheme.colorScheme.surface,
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .testTag("edit_profile_dialog")
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = Strings.editProfile(language),
                    fontWeight = FontWeight.Black,
                    fontSize = 18.sp
                )

                // Avatar choices
                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(avatars) { av ->
                        Surface(
                            shape = CircleShape,
                            color = if (selectedAvatar == av) NeonCyan.copy(alpha = 0.3f) else MaterialTheme.colorScheme.surfaceVariant,
                            modifier = Modifier
                                .size(48.dp)
                                .clickable { selectedAvatar = av }
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text(text = av, fontSize = 24.sp)
                            }
                        }
                    }
                }

                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text(if (language == AppLanguage.HEBREW) "שם משתמש" else "Name") },
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text(if (language == AppLanguage.HEBREW) "תואר גיבור" else "Hero Title") },
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = bio,
                    onValueChange = { bio = it },
                    label = { Text(if (language == AppLanguage.HEBREW) "תיאור אישי" else "Bio") },
                    modifier = Modifier.fillMaxWidth(),
                    maxLines = 2
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(onClick = onDismiss) {
                        Text(if (language == AppLanguage.HEBREW) "ביטול" else "Cancel")
                    }
                    Button(
                        onClick = { onSave(name, bio, selectedAvatar, title) },
                        enabled = name.isNotBlank()
                    ) {
                        Text(if (language == AppLanguage.HEBREW) "שמור" else "Save")
                    }
                }
            }
        }
    }
}

@Composable
fun PetShopDialog(
    user: UserProfile,
    language: AppLanguage,
    onDismiss: () -> Unit,
    onBuy: (String) -> Unit,
    onEquip: (String) -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(24.dp),
            color = MaterialTheme.colorScheme.surface,
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
                .testTag("pet_shop_dialog")
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "🐾 חנות החיות הקסומות",
                        fontWeight = FontWeight.Black,
                        fontSize = 17.sp
                    )
                    Text(
                        text = "⭐ ${user.stars}",
                        fontWeight = FontWeight.Bold,
                        color = VictoryGold
                    )
                }

                HorizontalDivider()

                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(300.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(user.companions) { pet ->
                        Surface(
                            shape = RoundedCornerShape(14.dp),
                            color = MaterialTheme.colorScheme.surfaceVariant,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(12.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(text = pet.iconEmoji, fontSize = 28.sp)
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Column {
                                        Text(text = pet.nameHe, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                        Text(
                                            text = if (pet.isUnlocked) "פתוח בחשבונך ✓" else "עלות: ${pet.costStars} ⭐",
                                            fontSize = 11.sp,
                                            color = if (pet.isUnlocked) SuccessGreen else VictoryGold
                                        )
                                    }
                                }

                                if (pet.isEquipped) {
                                    Text(text = "נבחר ✓", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = NeonCyan)
                                } else if (pet.isUnlocked) {
                                    Button(
                                        onClick = { onEquip(pet.id) },
                                        shape = RoundedCornerShape(10.dp),
                                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                                        modifier = Modifier.height(32.dp)
                                    ) {
                                        Text(text = "הלבש", fontSize = 11.sp)
                                    }
                                } else {
                                    Button(
                                        onClick = { onBuy(pet.id) },
                                        enabled = user.stars >= pet.costStars,
                                        shape = RoundedCornerShape(10.dp),
                                        colors = ButtonDefaults.buttonColors(containerColor = VictoryGold),
                                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                                        modifier = Modifier.height(32.dp)
                                    ) {
                                        Text(text = "קנה", fontSize = 11.sp, color = Color(0xFF261800))
                                    }
                                }
                            }
                        }
                    }
                }

                Button(
                    onClick = onDismiss,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(text = if (language == AppLanguage.HEBREW) "סגור" else "Close")
                }
            }
        }
    }
}
