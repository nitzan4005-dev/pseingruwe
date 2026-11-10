package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.model.*
import com.example.ui.theme.*
import com.example.ui.viewmodel.ChallengeMeViewModel

import androidx.lifecycle.compose.collectAsStateWithLifecycle

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChallengesScreen(
    viewModel: ChallengeMeViewModel,
    challenges: List<Challenge>,
    language: AppLanguage,
    modifier: Modifier = Modifier
) {
    val selectedCategory by viewModel.selectedCategory.collectAsStateWithLifecycle()
    val showOnlyActive by viewModel.showOnlyActive.collectAsStateWithLifecycle()
    val showAddDialog by viewModel.showAddCustomDialog.collectAsStateWithLifecycle()

    var filterStatus by remember { mutableStateOf<ChallengeStatus?>(null) }

    val filteredChallenges = remember(challenges, selectedCategory, filterStatus) {
        challenges.filter { ch ->
            (selectedCategory == null || ch.category == selectedCategory) &&
                    (filterStatus == null || ch.status == filterStatus)
        }
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        floatingActionButton = {
            FloatingActionButton(
                onClick = { viewModel.openAddCustomDialog() },
                containerColor = VictoryGold,
                contentColor = Color(0xFF261800),
                modifier = Modifier
                    .padding(bottom = 70.dp)
                    .testTag("add_custom_challenge_fab")
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(imageVector = Icons.Default.Add, contentDescription = "Add Challenge")
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = Strings.addChallenge(language),
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp
                    )
                }
            }
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .testTag("challenges_list"),
            contentPadding = PaddingValues(bottom = 120.dp, top = 8.dp, start = 16.dp, end = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Header
            item {
                Text(
                    text = if (language == AppLanguage.HEBREW) "מרכז האתגרים והמשימות ⭐" else "Challenges & Missions Hub ⭐",
                    fontWeight = FontWeight.Black,
                    fontSize = 22.sp,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = if (language == AppLanguage.HEBREW) "השלימו משימות, צברו כוכבים ועלו ברמות!" else "Complete missions, earn stars and level up!",
                    fontSize = 13.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            // Categories Filter Bar
            item {
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    item {
                        FilterChip(
                            selected = selectedCategory == null,
                            onClick = { viewModel.selectCategory(null) },
                            label = { Text(if (language == AppLanguage.HEBREW) "הכל ✨" else "All ✨") },
                            modifier = Modifier.testTag("filter_category_all")
                        )
                    }

                    items(ChallengeCategory.entries) { category ->
                        val isSelected = selectedCategory == category
                        FilterChip(
                            selected = isSelected,
                            onClick = { viewModel.selectCategory(category) },
                            label = {
                                Text("${category.emoji} ${if (language == AppLanguage.HEBREW) category.titleHe else category.titleEn}")
                            },
                            modifier = Modifier.testTag("filter_category_${category.name.lowercase()}")
                        )
                    }
                }
            }

            // Status Tabs (All / In Progress / Completed)
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    StatusChip(
                        title = if (language == AppLanguage.HEBREW) "כל המשימות" else "All",
                        isSelected = filterStatus == null,
                        onClick = { filterStatus = null }
                    )
                    StatusChip(
                        title = if (language == AppLanguage.HEBREW) "פעילות 🔥" else "Active 🔥",
                        isSelected = filterStatus == ChallengeStatus.IN_PROGRESS || filterStatus == ChallengeStatus.AVAILABLE,
                        onClick = { filterStatus = ChallengeStatus.IN_PROGRESS }
                    )
                    StatusChip(
                        title = if (language == AppLanguage.HEBREW) "הושלמו ✓" else "Completed ✓",
                        isSelected = filterStatus == ChallengeStatus.COMPLETED,
                        onClick = { filterStatus = ChallengeStatus.COMPLETED }
                    )
                }
            }

            // Challenges List
            if (filteredChallenges.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 40.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(text = "🎉", fontSize = 36.sp)
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = if (language == AppLanguage.HEBREW) "אין משימות להצגה בסינון זה" else "No missions to display",
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            } else {
                items(filteredChallenges) { challenge ->
                    ChallengeItemCard(
                        challenge = challenge,
                        language = language,
                        onComplete = { viewModel.completeChallenge(challenge.id) },
                        onStart = { viewModel.startChallenge(challenge.id) },
                        onSkip = { viewModel.skipChallenge(challenge.id) }
                    )
                }
            }
        }
    }

    // Add Custom Challenge Dialog
    if (showAddDialog) {
        AddCustomChallengeDialog(
            language = language,
            onDismiss = { viewModel.closeAddCustomDialog() },
            onConfirm = { title, desc, cat, diff, time ->
                viewModel.addCustomChallenge(title, desc, cat, diff, time)
            }
        )
    }
}

@Composable
fun StatusChip(
    title: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(12.dp),
        color = if (isSelected) NeonCyan else MaterialTheme.colorScheme.surfaceVariant,
        modifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .clickable(onClick = onClick)
    ) {
        Text(
            text = title,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            color = if (isSelected) Color(0xFF001F28) else MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp)
        )
    }
}

@Composable
fun AddCustomChallengeDialog(
    language: AppLanguage,
    onDismiss: () -> Unit,
    onConfirm: (String, String, ChallengeCategory, ChallengeDifficulty, Int) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var desc by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf(ChallengeCategory.CREATIVITY) }
    var selectedDifficulty by remember { mutableStateOf(ChallengeDifficulty.EASY) }
    var timeMinutes by remember { mutableStateOf(15) }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(24.dp),
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 8.dp,
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .testTag("add_custom_dialog")
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Text(
                    text = if (language == AppLanguage.HEBREW) "יצירת אתגר אישי חדש ✨" else "Create New Custom Challenge ✨",
                    fontWeight = FontWeight.Black,
                    fontSize = 18.sp,
                    color = MaterialTheme.colorScheme.onSurface
                )

                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text(if (language == AppLanguage.HEBREW) "שם האתגר" else "Challenge Title") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("custom_challenge_title_input"),
                    singleLine = true
                )

                OutlinedTextField(
                    value = desc,
                    onValueChange = { desc = it },
                    label = { Text(if (language == AppLanguage.HEBREW) "תיאור האתגר" else "Description") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("custom_challenge_desc_input"),
                    maxLines = 3
                )

                // Category selector row
                Text(
                    text = if (language == AppLanguage.HEBREW) "קטגוריה:" else "Category:",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )
                LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    items(ChallengeCategory.entries) { cat ->
                        FilterChip(
                            selected = selectedCategory == cat,
                            onClick = { selectedCategory = cat },
                            label = { Text("${cat.emoji} ${cat.titleHe}") }
                        )
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TextButton(onClick = onDismiss) {
                        Text(if (language == AppLanguage.HEBREW) "ביטול" else "Cancel")
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        onClick = {
                            if (title.isNotBlank()) {
                                onConfirm(title, desc.ifBlank { title }, selectedCategory, selectedDifficulty, timeMinutes)
                            }
                        },
                        enabled = title.isNotBlank(),
                        colors = ButtonDefaults.buttonColors(containerColor = BrightBlue),
                        modifier = Modifier.testTag("save_custom_challenge_button")
                    ) {
                        Text(if (language == AppLanguage.HEBREW) "שמור אתגר 🚀" else "Save Challenge 🚀")
                    }
                }
            }
        }
    }
}
