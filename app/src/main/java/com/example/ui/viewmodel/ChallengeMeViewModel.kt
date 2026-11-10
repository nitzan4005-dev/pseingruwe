package com.example.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.model.*
import com.example.data.repository.ChallengeRepository
import com.example.data.repository.LevelUpData
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

enum class ScreenTab {
    HOME,
    MISSIONS,
    GROUPS,
    COMPETITIONS,
    AI_COACH,
    PROFILE,
    SETTINGS
}

class ChallengeMeViewModel(
    private val repository: ChallengeRepository = ChallengeRepository()
) : ViewModel() {

    // Current Navigation Screen
    private val _currentScreen = MutableStateFlow(ScreenTab.HOME)
    val currentScreen: StateFlow<ScreenTab> = _currentScreen.asStateFlow()

    // Screen Backstack for proper BackHandler support
    private val screenStack = mutableListOf(ScreenTab.HOME)

    // Language & Theme State
    private val _language = MutableStateFlow(AppLanguage.HEBREW)
    val language: StateFlow<AppLanguage> = _language.asStateFlow()

    private val _isDarkTheme = MutableStateFlow(true)
    val isDarkTheme: StateFlow<Boolean> = _isDarkTheme.asStateFlow()

    private val _hapticsEnabled = MutableStateFlow(true)
    val hapticsEnabled: StateFlow<Boolean> = _hapticsEnabled.asStateFlow()

    // Repository Flows
    val userProfile: StateFlow<UserProfile> = repository.userProfile
    val challenges: StateFlow<List<Challenge>> = repository.challenges
    val friendDuel: StateFlow<FriendDuel> = repository.friendDuel
    val groupWar: StateFlow<GroupWar> = repository.groupWar
    val activeGroup: StateFlow<GroupItem> = repository.activeGroup
    val leaderboard: StateFlow<List<LeaderboardUser>> = repository.leaderboard
    val aiMessages: StateFlow<List<AiChatMessage>> = repository.aiMessages
    val weeklyAdventure: StateFlow<List<WeeklyAdventureDay>> = repository.weeklyAdventure
    val friends: StateFlow<List<FriendItem>> = repository.friends
    val levelUpEvent: StateFlow<LevelUpData?> = repository.levelUpEvent

    // UI Dialog & Filter States
    private val _selectedCategory = MutableStateFlow<ChallengeCategory?>(null)
    val selectedCategory: StateFlow<ChallengeCategory?> = _selectedCategory.asStateFlow()

    private val _showOnlyActive = MutableStateFlow(false)
    val showOnlyActive: StateFlow<Boolean> = _showOnlyActive.asStateFlow()

    private val _showAddCustomDialog = MutableStateFlow(false)
    val showAddCustomDialog: StateFlow<Boolean> = _showAddCustomDialog.asStateFlow()

    private val _showEditProfileDialog = MutableStateFlow(false)
    val showEditProfileDialog: StateFlow<Boolean> = _showEditProfileDialog.asStateFlow()

    private val _showPetShopDialog = MutableStateFlow(false)
    val showPetShopDialog: StateFlow<Boolean> = _showPetShopDialog.asStateFlow()

    private val _showCelebrationOverlay = MutableStateFlow(false)
    val showCelebrationOverlay: StateFlow<Boolean> = _showCelebrationOverlay.asStateFlow()

    private val _toastMessage = MutableStateFlow<String?>(null)
    val toastMessage: StateFlow<String?> = _toastMessage.asStateFlow()

    fun navigateTo(tab: ScreenTab) {
        if (_currentScreen.value != tab) {
            screenStack.add(tab)
            _currentScreen.value = tab
        }
    }

    fun handleBack(): Boolean {
        if (screenStack.size > 1) {
            screenStack.removeAt(screenStack.lastIndex)
            _currentScreen.value = screenStack.last()
            return true
        }
        return false
    }

    fun toggleLanguage() {
        _language.value = if (_language.value == AppLanguage.HEBREW) AppLanguage.ENGLISH else AppLanguage.HEBREW
    }

    fun setLanguage(lang: AppLanguage) {
        _language.value = lang
    }

    fun toggleTheme() {
        _isDarkTheme.value = !_isDarkTheme.value
    }

    fun toggleHaptics() {
        _hapticsEnabled.value = !_hapticsEnabled.value
    }

    fun selectCategory(category: ChallengeCategory?) {
        _selectedCategory.value = if (_selectedCategory.value == category) null else category
    }

    fun toggleShowOnlyActive() {
        _showOnlyActive.value = !_showOnlyActive.value
    }

    fun openAddCustomDialog() {
        _showAddCustomDialog.value = true
    }

    fun closeAddCustomDialog() {
        _showAddCustomDialog.value = false
    }

    fun openEditProfileDialog() {
        _showEditProfileDialog.value = true
    }

    fun closeEditProfileDialog() {
        _showEditProfileDialog.value = false
    }

    fun openPetShopDialog() {
        _showPetShopDialog.value = true
    }

    fun closePetShopDialog() {
        _showPetShopDialog.value = false
    }

    fun clearToast() {
        _toastMessage.value = null
    }

    // Business Logic Actions
    fun completeChallenge(challengeId: String) {
        val success = repository.completeChallenge(challengeId)
        if (success) {
            _showCelebrationOverlay.value = true
            val lang = _language.value
            _toastMessage.value = if (lang == AppLanguage.HEBREW) "משימה הושלמה! +כוכבים ו-XP נצברו! 🎉" else "Mission completed! +Stars & XP earned! 🎉"
        }
    }

    fun dismissCelebration() {
        _showCelebrationOverlay.value = false
    }

    fun dismissLevelUp() {
        repository.dismissLevelUpDialog()
    }

    fun startChallenge(challengeId: String) {
        repository.startChallenge(challengeId)
        val lang = _language.value
        _toastMessage.value = if (lang == AppLanguage.HEBREW) "המשימה התחילה! בהצלחה! 🚀" else "Mission started! Good luck! 🚀"
    }

    fun skipChallenge(challengeId: String) {
        repository.skipChallenge(challengeId)
    }

    fun addCustomChallenge(
        titleHe: String,
        descHe: String,
        category: ChallengeCategory,
        difficulty: ChallengeDifficulty,
        timeMinutes: Int
    ) {
        repository.addCustomChallenge(titleHe, descHe, category, difficulty, timeMinutes)
        _showAddCustomDialog.value = false
        val lang = _language.value
        _toastMessage.value = if (lang == AppLanguage.HEBREW) "אתגר אישי נוסף בהצלחה! ⭐" else "Custom challenge added successfully! ⭐"
    }

    fun acceptAiChallenge(challenge: Challenge) {
        repository.addAiGeneratedChallenge(challenge)
        navigateTo(ScreenTab.MISSIONS)
        val lang = _language.value
        _toastMessage.value = if (lang == AppLanguage.HEBREW) "האתגר נוסף למשימות שלך! 🚀" else "Challenge added to your missions! 🚀"
    }

    fun toggleWeeklyDay(dayId: Int) {
        repository.toggleWeeklyDay(dayId)
    }

    fun sendCheerToOpponent() {
        repository.sendCheerToOpponent()
        val lang = _language.value
        val name = friendDuel.value.opponentName
        _toastMessage.value = if (lang == AppLanguage.HEBREW) "שלחת כיף ל$name! קיבלת 20 כוכבים! ✋⭐" else "Sent high-five to $name! Earned 20 stars! ✋⭐"
    }

    fun sendGroupChatMessage(text: String, isCheer: Boolean = false) {
        repository.sendGroupChatMessage(text, isCheer)
        if (isCheer) {
            val lang = _language.value
            _toastMessage.value = if (lang == AppLanguage.HEBREW) "שלחת עידוד לקבוצה! +30 כוכבים לקופה! 🦉" else "Cheered the squad! +30 stars to vault! 🦉"
        }
    }

    fun contributeToTeamMission(missionId: String) {
        repository.contributeToTeamMission(missionId, 1000)
        val lang = _language.value
        _toastMessage.value = if (lang == AppLanguage.HEBREW) "תרמת 1,000 צעדים למשימת הקבוצה! 👟" else "Contributed 1,000 steps to squad mission! 👟"
    }

    fun equipPet(petId: String) {
        repository.equipPet(petId)
        val lang = _language.value
        _toastMessage.value = if (lang == AppLanguage.HEBREW) "חיית המחמד לובשת עכשיו! 🐾" else "Pet companion equipped! 🐾"
    }

    fun buyPet(petId: String) {
        repository.buyPet(petId)
        val lang = _language.value
        _toastMessage.value = if (lang == AppLanguage.HEBREW) "החבר החדש נפתח בהצלחה! 🎁" else "New pet unlocked successfully! 🎁"
    }

    fun updateProfile(name: String, bio: String, avatarEmoji: String, title: String) {
        repository.updateProfile(name, bio, avatarEmoji, title)
        _showEditProfileDialog.value = false
        val lang = _language.value
        _toastMessage.value = if (lang == AppLanguage.HEBREW) "הפרופיל עודכן בהצלחה! ✨" else "Profile updated successfully! ✨"
    }

    fun sendAiPrompt(prompt: String) {
        if (prompt.isBlank()) return
        viewModelScope.launch {
            repository.sendAiPrompt(prompt)
        }
    }
}
