package com.example.data.repository

import com.example.data.model.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class ChallengeRepository {

    private val _userProfile = MutableStateFlow(createInitialProfile())
    val userProfile: StateFlow<UserProfile> = _userProfile.asStateFlow()

    private val _challenges = MutableStateFlow(createInitialChallenges())
    val challenges: StateFlow<List<Challenge>> = _challenges.asStateFlow()

    private val _friendDuel = MutableStateFlow(createInitialDuel())
    val friendDuel: StateFlow<FriendDuel> = _friendDuel.asStateFlow()

    private val _groupWar = MutableStateFlow(GroupWar("war_1"))
    val groupWar: StateFlow<GroupWar> = _groupWar.asStateFlow()

    private val _activeGroup = MutableStateFlow(createInitialGroup())
    val activeGroup: StateFlow<GroupItem> = _activeGroup.asStateFlow()

    private val _leaderboard = MutableStateFlow(createInitialLeaderboard())
    val leaderboard: StateFlow<List<LeaderboardUser>> = _leaderboard.asStateFlow()

    private val _aiMessages = MutableStateFlow(createInitialAiChat())
    val aiMessages: StateFlow<List<AiChatMessage>> = _aiMessages.asStateFlow()

    private val _weeklyAdventure = MutableStateFlow(createInitialWeeklyAdventure())
    val weeklyAdventure: StateFlow<List<WeeklyAdventureDay>> = _weeklyAdventure.asStateFlow()

    private val _friends = MutableStateFlow(createInitialFriends())
    val friends: StateFlow<List<FriendItem>> = _friends.asStateFlow()

    // Level up event channel
    private val _levelUpEvent = MutableStateFlow<LevelUpData?>(null)
    val levelUpEvent: StateFlow<LevelUpData?> = _levelUpEvent.asStateFlow()

    fun dismissLevelUpDialog() {
        _levelUpEvent.value = null
    }

    fun completeChallenge(challengeId: String): Boolean {
        var completedChallenge: Challenge? = null
        _challenges.update { list ->
            list.map { ch ->
                if (ch.id == challengeId && ch.status != ChallengeStatus.COMPLETED) {
                    completedChallenge = ch
                    ch.copy(status = ChallengeStatus.COMPLETED, completedAt = System.currentTimeMillis())
                } else ch
            }
        }

        val ch = completedChallenge ?: return false
        val earnedXp = ch.xpReward
        val earnedStars = ch.starsReward

        _userProfile.update { current ->
            val newXp = current.currentXp + earnedXp
            val newStars = current.stars + earnedStars
            var currentLevel = current.level
            var requiredForNext = current.xpForNextLevel
            var leveledUp = false

            if (newXp >= requiredForNext) {
                currentLevel += 1
                requiredForNext = currentLevel * 250 + 500
                leveledUp = true
            }

            val newStats = current.stats.copy(
                completedMissions = current.stats.completedMissions + 1,
                kindnessActs = if (ch.category == ChallengeCategory.GOOD_DEEDS || ch.category == ChallengeCategory.SOCIAL)
                    current.stats.kindnessActs + 1 else current.stats.kindnessActs,
                booksRead = if (ch.category == ChallengeCategory.LEARNING)
                    current.stats.booksRead + 1 else current.stats.booksRead
            )

            val newHistoryItem = XpTransaction(
                id = "tx_${System.currentTimeMillis()}",
                title = ch.titleHe,
                amount = earnedXp,
                timestamp = "היום",
                category = ch.category.titleHe
            )

            if (leveledUp) {
                _levelUpEvent.value = LevelUpData(
                    newLevel = currentLevel,
                    tier = LevelTier.fromLevel(currentLevel),
                    bonusStars = 100,
                    unlockedRewardHe = "תואר חדש: ${LevelTier.fromLevel(currentLevel).titleHe} מנצח! 🏆",
                    unlockedRewardEn = "New Title: Victorious ${LevelTier.fromLevel(currentLevel).titleEn}! 🏆"
                )
            }

            current.copy(
                level = currentLevel,
                currentXp = newXp,
                xpForNextLevel = requiredForNext,
                stars = newStars,
                stats = newStats,
                xpHistory = listOf(newHistoryItem) + current.xpHistory
            )
        }

        // Also update duel score and leaderboard
        _friendDuel.update { duel ->
            val updatedUserStars = duel.userStars + earnedStars
            val updatedUserMissions = duel.userCompletedMissions + 1
            duel.copy(
                userStars = updatedUserStars,
                userCompletedMissions = updatedUserMissions
            )
        }

        _leaderboard.update { list ->
            list.map { entry ->
                if (entry.isCurrentUser) {
                    entry.copy(stars = entry.stars + earnedStars)
                } else entry
            }.sortedByDescending { it.stars }
        }

        // Update group progress as well
        _activeGroup.update { group ->
            group.copy(
                totalStars = group.totalStars + earnedStars
            )
        }

        return true
    }

    fun startChallenge(challengeId: String) {
        _challenges.update { list ->
            list.map { ch ->
                if (ch.id == challengeId && ch.status == ChallengeStatus.AVAILABLE) {
                    ch.copy(status = ChallengeStatus.IN_PROGRESS)
                } else ch
            }
        }
    }

    fun skipChallenge(challengeId: String) {
        _challenges.update { list ->
            list.map { ch ->
                if (ch.id == challengeId) {
                    ch.copy(status = ChallengeStatus.SKIPPED)
                } else ch
            }
        }
    }

    fun addCustomChallenge(
        titleHe: String,
        descHe: String,
        category: ChallengeCategory,
        difficulty: ChallengeDifficulty,
        timeMinutes: Int
    ) {
        val xp = when (difficulty) {
            ChallengeDifficulty.EASY -> 60
            ChallengeDifficulty.MEDIUM -> 120
            ChallengeDifficulty.HARD -> 220
        }
        val newChallenge = Challenge(
            id = "custom_${System.currentTimeMillis()}",
            titleHe = titleHe,
            titleEn = titleHe,
            descHe = descHe,
            descEn = descHe,
            category = category,
            difficulty = difficulty,
            xpReward = xp,
            timeMinutes = timeMinutes,
            status = ChallengeStatus.AVAILABLE,
            isAiGenerated = false
        )
        _challenges.update { listOf(newChallenge) + it }
    }

    fun addAiGeneratedChallenge(challenge: Challenge) {
        _challenges.update { list ->
            listOf(challenge.copy(status = ChallengeStatus.IN_PROGRESS)) + list
        }
    }

    fun toggleWeeklyDay(dayId: Int) {
        _weeklyAdventure.update { days ->
            days.map { day ->
                if (day.dayId == dayId) {
                    val toggled = !day.isCompleted
                    if (toggled) {
                        _userProfile.update { it.copy(stars = it.stars + 50, currentXp = it.currentXp + 70) }
                    }
                    day.copy(
                        isCompleted = toggled,
                        statusHe = if (toggled) "הושלם" else "ממתין",
                        statusEn = if (toggled) "Completed" else "Pending"
                    )
                } else day
            }
        }
    }

    fun sendCheerToOpponent() {
        _friendDuel.update { duel ->
            duel.copy(
                cheerCount = duel.cheerCount + 1,
                userStars = duel.userStars + 20
            )
        }
        _userProfile.update { it.copy(stars = it.stars + 20) }
    }

    fun sendGroupChatMessage(text: String, isCheer: Boolean = false) {
        val newMsg = GroupChatMessage(
            id = "msg_${System.currentTimeMillis()}",
            senderName = _userProfile.value.name,
            senderAvatar = _userProfile.value.avatarEmoji,
            message = text,
            timestamp = "עכשיו",
            isCheer = isCheer
        )
        _activeGroup.update { group ->
            group.copy(
                chatMessages = group.chatMessages + newMsg,
                totalStars = if (isCheer) group.totalStars + 30 else group.totalStars
            )
        }
    }

    fun contributeToTeamMission(missionId: String, amount: Int = 1000) {
        _activeGroup.update { group ->
            val updatedMissions = group.missions.map { m ->
                if (m.id == missionId) {
                    val nextProg = (m.currentProgress + amount).coerceAtMost(m.targetProgress)
                    m.copy(
                        currentProgress = nextProg,
                        isCompleted = nextProg >= m.targetProgress
                    )
                } else m
            }
            group.copy(
                missions = updatedMissions,
                totalStars = group.totalStars + (amount / 10)
            )
        }
        _userProfile.update { it.copy(stars = it.stars + (amount / 20)) }
    }

    fun equipPet(petId: String) {
        _userProfile.update { profile ->
            val updated = profile.companions.map { pet ->
                pet.copy(isEquipped = pet.id == petId)
            }
            profile.copy(companions = updated)
        }
    }

    fun buyPet(petId: String) {
        val pet = _userProfile.value.companions.find { it.id == petId } ?: return
        if (_userProfile.value.stars >= pet.costStars) {
            _userProfile.update { profile ->
                val updated = profile.companions.map {
                    if (it.id == petId) it.copy(isUnlocked = true, isEquipped = true)
                    else it.copy(isEquipped = false)
                }
                profile.copy(
                    stars = profile.stars - pet.costStars,
                    companions = updated
                )
            }
        }
    }

    fun updateProfile(name: String, bio: String, avatarEmoji: String, title: String) {
        _userProfile.update {
            it.copy(
                name = name,
                bio = bio,
                avatarEmoji = avatarEmoji,
                title = title
            )
        }
    }

    fun sendAiPrompt(prompt: String) {
        val userMsg = AiChatMessage(
            id = "user_${System.currentTimeMillis()}",
            sender = MessageSender.USER,
            text = prompt,
            timestamp = "עכשיו"
        )
        _aiMessages.update { it + userMsg }

        // Generate tailored smart response
        val response = generateAiResponse(prompt, _userProfile.value.level)
        _aiMessages.update { it + response }
    }

    private fun generateAiResponse(prompt: String, userLevel: Int): AiChatMessage {
        val p = prompt.lowercase()
        val category: ChallengeCategory
        val titleHe: String
        val titleEn: String
        val descHe: String
        val descEn: String
        val timeMin: Int
        val xp: Int

        when {
            p.contains("ספורט") || p.contains("כושר") || p.contains("ריצה") || p.contains("sport") || p.contains("run") -> {
                category = ChallengeCategory.SPORTS
                titleHe = "אימון אנרגיית ברק ⚡"
                titleEn = "Lightning Energy Workout ⚡"
                descHe = "בצעו 20 קפיצות כוכב, 10 מתיחות ידיים ושתו כוס מים צוננת!"
                descEn = "Do 20 jumping jacks, 10 arm stretches and drink a glass of fresh water!"
                timeMin = 10
                xp = 80
            }
            p.contains("ציור") || p.contains("יצירה") || p.contains("draw") || p.contains("art") || p.contains("craft") -> {
                category = ChallengeCategory.CREATIVITY
                titleHe = "ציירו את חיית החלומות שלכם! 🦄"
                titleEn = "Draw Your Dream Companion! 🦄"
                descHe = "השתמשו בצבעים הכי שמחים שלכם והמציאו חבר קסום חדש עם כוחות על!"
                descEn = "Use your most cheerful colors and invent a new magical creature with super powers!"
                timeMin = 15
                xp = 70
            }
            p.contains("ספר") || p.contains("לימוד") || p.contains("שפה") || p.contains("read") || p.contains("learn") -> {
                category = ChallengeCategory.LEARNING
                titleHe = "מסע אל עולם המדע והסיפורים 🪐"
                titleEn = "Voyage into Science & Stories 🪐"
                descHe = "קראו פרק אחד בספר מרתק ולמדו עובדה חדשה שלא ידעתם!"
                descEn = "Read one chapter in an exciting book and learn a new fun fact!"
                timeMin = 15
                xp = 90
            }
            p.contains("חבר") || p.contains("טוב") || p.contains("עזרה") || p.contains("friend") || p.contains("help") -> {
                category = ChallengeCategory.GOOD_DEEDS
                titleHe = "קרן של אור וחיוך 💖"
                titleEn = "Ray of Light & Smile 💖"
                descHe = "שלחו הודעה חמה או מחמאה לחבר טוב או הציעו עזרה להורים במטבח!"
                descEn = "Send a warm compliment to a friend or help your parents prepare dinner!"
                timeMin = 10
                xp = 85
            }
            else -> {
                category = ChallengeCategory.COURAGE
                titleHe = "אתגר האומץ וההרפתקה! 🦁"
                titleEn = "Courage & Adventure Challenge! 🦁"
                descHe = "עשו משהו חדש היום: נסו מאכל בריא שמעולם לא טעמתם או למדו משפט בשפה זרה!"
                descEn = "Try something new today: taste a healthy food you never tried or learn a foreign sentence!"
                timeMin = 12
                xp = 75
            }
        }

        val suggested = Challenge(
            id = "ai_${System.currentTimeMillis()}",
            titleHe = titleHe,
            titleEn = titleEn,
            descHe = descHe,
            descEn = descEn,
            category = category,
            difficulty = ChallengeDifficulty.EASY,
            xpReward = xp,
            timeMinutes = timeMin,
            status = ChallengeStatus.AVAILABLE,
            isAiGenerated = true
        )

        return AiChatMessage(
            id = "robot_${System.currentTimeMillis()}",
            sender = MessageSender.ROBOT,
            text = "בכיף מאיה! הנה אתגר מתוק במיוחד שמתאים לרמה $userLevel שלך:\n$descHe",
            suggestedChallenge = suggested,
            timestamp = "עכשיו"
        )
    }

    // Initial Seed Data
    private fun createInitialProfile(): UserProfile {
        return UserProfile(
            badges = listOf(
                BadgeItem("b1", "אומץ לב זהב", "Golden Courage", "השלמת 10 אתגרי ביטחון עצמי", "Completed 10 confidence challenges", "🦁"),
                BadgeItem("b2", "חבר/ת אמת", "True Friend", "עזרת ל-5 חברים במשימות", "Helped 5 friends with challenges", "🤝"),
                BadgeItem("b3", "סילון של אנרגיה", "Energy Jet", "15,000 צעדים ביום אחד", "15,000 steps in one single day", "🏃"),
                BadgeItem("b4", "אמן היצירה", "Art Master", "7 משימות ציור עם מאמן ה-AI", "7 art missions with AI coach", "🎨"),
                BadgeItem("b5", "מנצח דו-קרב", "Duel Champion", "3 ניצחונות בקרב ידידותי", "3 friendly duel victories", "👑"),
                BadgeItem("b6", "מאסטר השבוע", "Master of Week", "השלמת כל אתגרי השבוע", "Completed all weekly challenges", "🎁", isUnlocked = false, unlockDate = "עוד יומיים לפתיחה!")
            ),
            companions = listOf(
                PetCompanion("p1", "במבו הפנדה", "Bambu Panda", 5, "🐼", isEquipped = true, isUnlocked = true),
                PetCompanion("p2", "הוטי הינשוף", "Hooty Owl", 3, "🦉", isEquipped = false, isUnlocked = true),
                PetCompanion("p3", "שועלי הזוהר", "Fox Glow", 1, "🦊", isEquipped = false, costStars = 400, isUnlocked = false),
                PetCompanion("p4", "חד קרן הקסם", "Magic Unicorn", 1, "🦄", isEquipped = false, costStars = 800, isUnlocked = false)
            ),
            xpHistory = listOf(
                XpTransaction("tx_1", "צעדת הבוקר הגדולה", 120, "היום", "ספורט וכושר"),
                XpTransaction("tx_2", "עזרה לחבר בשיעורים", 90, "אתמול", "חברה וביטחון"),
                XpTransaction("tx_3", "ציור חיית החלומות עם AI", 70, "שלשום", "יצירתיות"),
                XpTransaction("tx_4", "ניצחון בדו-קרב ידידותי", 200, "לפני 3 ימים", "תחרויות")
            )
        )
    }

    private fun createInitialChallenges(): List<Challenge> {
        return listOf(
            Challenge(
                id = "c1",
                titleHe = "צעדת 3,000 צעדים באוויר הפתוח",
                titleEn = "Walk 3,000 Steps in Fresh Air",
                descHe = "צאו להליכה נמרצת בגינה או ברחוב עם המשפחה או החברים!",
                descEn = "Take a brisk walk in the park or street with family or friends!",
                category = ChallengeCategory.SPORTS,
                difficulty = ChallengeDifficulty.MEDIUM,
                xpReward = 120,
                timeMinutes = 25,
                status = ChallengeStatus.IN_PROGRESS
            ),
            Challenge(
                id = "c2",
                titleHe = "אמרו מילה טובה ומחמאה לחבר",
                titleEn = "Give a Warm Compliment to a Friend",
                descHe = "שמחו חבר בכיתה או מורה עם מילה טובה אמיתית שמחממת את הלב.",
                descEn = "Brighten someone's day at school with an honest, heartwarming compliment.",
                category = ChallengeCategory.SOCIAL,
                difficulty = ChallengeDifficulty.EASY,
                xpReward = 80,
                timeMinutes = 5,
                status = ChallengeStatus.AVAILABLE
            ),
            Challenge(
                id = "c3",
                titleHe = "קראו 15 דקות בספר אהוב",
                titleEn = "Read 15 Minutes in Favorite Book",
                descHe = "פתחו ספר מרתק, שקעו בסיפור והרחיבו את הדמיון שלכם!",
                descEn = "Dive into an engaging story, relax and fuel your imagination!",
                category = ChallengeCategory.LEARNING,
                difficulty = ChallengeDifficulty.EASY,
                xpReward = 75,
                timeMinutes = 15,
                status = ChallengeStatus.AVAILABLE
            ),
            Challenge(
                id = "c4",
                titleHe = "עזרו בהכנת ארוחה בריאה בבית",
                titleEn = "Help Prepare a Healthy Meal",
                descHe = "הכינו יחד סלט ירקות צבעוני או חטיף פירות טרי וטעים.",
                descEn = "Help cut a colorful fresh veggie salad or yummy fruit snack.",
                category = ChallengeCategory.GOOD_DEEDS,
                difficulty = ChallengeDifficulty.MEDIUM,
                xpReward = 110,
                timeMinutes = 20,
                status = ChallengeStatus.AVAILABLE
            ),
            Challenge(
                id = "c5",
                titleHe = "ציירו את חיית החלומות שלכם! 🦄",
                titleEn = "Draw Your Dream Magical Pet! 🦄",
                descHe = "ציירו עם צבעים שמחים והמציאו דמות קסומה חדשה עם כוחות מיוחדים.",
                descEn = "Draw with bright cheerful colors and invent a magical pet with powers.",
                category = ChallengeCategory.CREATIVITY,
                difficulty = ChallengeDifficulty.EASY,
                xpReward = 70,
                timeMinutes = 15,
                status = ChallengeStatus.AVAILABLE,
                isAiGenerated = true
            ),
            Challenge(
                id = "c6",
                titleHe = "סדרו את שולחן העבודה והילקוט",
                titleEn = "Organize Study Desk & Backpack",
                descHe = "ארגנו את המחברות והעטים כדי לפתוח את מחר בשיא הרוגע והסדר!",
                descEn = "Arrange your books and pencil case so tomorrow starts cleanly!",
                category = ChallengeCategory.PRODUCTIVITY,
                difficulty = ChallengeDifficulty.EASY,
                xpReward = 65,
                timeMinutes = 10,
                status = ChallengeStatus.COMPLETED
            ),
            Challenge(
                id = "c7",
                titleHe = "אתגר האומץ: הציעו עזרה ראשונים",
                titleEn = "Courage Challenge: Volunteer First",
                descHe = "הרימו יד ראשונים בכיתה או הציעו עזרה לשכן או חבר מבלי שהתבקשתם.",
                descEn = "Raise your hand first or offer voluntary help without being asked.",
                category = ChallengeCategory.COURAGE,
                difficulty = ChallengeDifficulty.HARD,
                xpReward = 180,
                timeMinutes = 10,
                status = ChallengeStatus.AVAILABLE
            )
        )
    }

    private fun createInitialDuel(): FriendDuel {
        return FriendDuel(
            id = "duel_maya_noa",
            opponentName = "נועה",
            opponentAvatar = "👧",
            opponentRoleHe = "חברה קרובה",
            opponentRoleEn = "Close Friend",
            opponentStars = 1140,
            opponentCompletedMissions = 3,
            userStars = 1420,
            userCompletedMissions = 4,
            timeLeftTextHe = "נותרו עוד 3 שעות ⏰",
            timeLeftTextEn = "3 hours left ⏰",
            rewardDescHe = "+350 כוכבים ⭐, גביע זהב 🏆 ותג אומץ 🦁",
            rewardDescEn = "+350 Stars ⭐, Gold Trophy 🏆 & Courage Badge 🦁"
        )
    }

    private fun createInitialGroup(): GroupItem {
        return GroupItem(
            id = "grp_owls",
            name = "מועדון ינשופי הכוכבים",
            description = "קבוצת האלופים של בית הספר! עובדים יחד, צוברים כוכבים ומנצחים!",
            avatarEmoji = "🦉",
            leaderName = "מאיה",
            memberCount = 18,
            onlineCount = 5,
            totalStars = 7200,
            starsGoal = 10000,
            rankInLeague = 4,
            teammates = listOf(
                Teammate("t1", "את/ה", "👧", isOnline = true, isCurrentUser = true, starsContributed = 650),
                Teammate("t2", "נועה", "👧", isOnline = true, starsContributed = 580),
                Teammate("t3", "דן", "👦", isOnline = true, starsContributed = 510),
                Teammate("t4", "ליאור", "👦", isOnline = true, starsContributed = 480),
                Teammate("t5", "אמה", "👧", isOnline = false, starsContributed = 420),
                Teammate("t6", "אלי", "👦", isOnline = true, starsContributed = 390)
            ),
            missions = listOf(
                TeamMission(
                    id = "tm1",
                    titleHe = "צעדת הבוקר הגדולה - צועדים יחד לפסגת ההר! 👟",
                    titleEn = "Big Morning Hike - Stepping Together to the Peak! 👟",
                    currentProgress = 15000,
                    targetProgress = 20000,
                    unitHe = "צעדים",
                    unitEn = "steps",
                    rewardDescHe = "תיבת זהב קבוצתית 🎁",
                    rewardDescEn = "Team Gold Chest 🎁",
                    iconEmoji = "👟"
                ),
                TeamMission(
                    id = "tm2",
                    titleHe = "משימת עזרה ולמידה - עזרו לחבר או קראו סיפור יחד 📖",
                    titleEn = "Help & Learn - Read a Story or Assist a Friend 📖",
                    currentProgress = 14,
                    targetProgress = 20,
                    unitHe = "קריאות",
                    unitEn = "sessions",
                    rewardDescHe = "+450 כוכבים לכולם! ⭐",
                    rewardDescEn = "+450 Stars to All! ⭐",
                    iconEmoji = "📖"
                )
            ),
            chatMessages = listOf(
                GroupChatMessage("m1", "דן", "👦", "סיימתי את משימת ההליכה! הוסיף 500 צעדים לקבוצה! 🚀", "10:15"),
                GroupChatMessage("m2", "נועה", "👧", "כל הכבוד דן! עוד מעט מגיעים ליעד השבועי! ✨", "10:20"),
                GroupChatMessage("m3", "ליאור", "👦", "שלחתי כיף לכולם! יאללה ינשופים! 🦉", "10:24", isCheer = true)
            )
        )
    }

    private fun createInitialLeaderboard(): List<LeaderboardUser> {
        return listOf(
            LeaderboardUser(1, "נועה", "אלופת הכוכבים", "Star Champion", "👧", 1940, false, "👑"),
            LeaderboardUser(2, "מאיה (את/ה)", "חוקרת כוכבים נועזת", "Daring Explorer", "👧", 1820, true, "🥈"),
            LeaderboardUser(3, "אלי", "מגלה העולמות", "Cosmic Voyager", "👦", 1610, false, "🥉"),
            LeaderboardUser(4, "דן", "אלוף האומץ", "Courage Master", "👦", 1480, false, "🦁"),
            LeaderboardUser(5, "ליאור", "חוקר סקרן", "Curious Scholar", "👦", 1320, false, "🔍"),
            LeaderboardUser(6, "אמה", "חברה טובה", "Kind Friend", "👧", 1150, false, "🌸")
        )
    }

    private fun createInitialAiChat(): List<AiChatMessage> {
        return listOf(
            AiChatMessage(
                id = "ai_welcome",
                sender = MessageSender.ROBOT,
                text = "שלום מאיה! 🌟 אני המאמן החכם שלך. בקשי ממני כל אתגר שתרצי — ציור, ספורט, קריאה, או מעשה טוב!"
            ),
            AiChatMessage(
                id = "ai_user_sample",
                sender = MessageSender.USER,
                text = "תן לי משימה כיפית של 15 דקות לציור או יצירה! 🎨"
            ),
            AiChatMessage(
                id = "ai_robot_sample",
                sender = MessageSender.ROBOT,
                text = "בכיף מאיה! הנה אתגר מתוק במיוחד שמתאים לרמה 12 שלך:\nציירו את חיית החלומות שלכם! 🦄",
                suggestedChallenge = Challenge(
                    id = "c5",
                    titleHe = "ציירו את חיית החלומות שלכם! 🦄",
                    titleEn = "Draw Your Dream Magical Pet! 🦄",
                    descHe = "השתמשו בצבעים הכי שמחים שלכם והמציאו חבר קסום חדש.",
                    descEn = "Use your brightest colors and invent a magical new pet companion.",
                    category = ChallengeCategory.CREATIVITY,
                    difficulty = ChallengeDifficulty.EASY,
                    xpReward = 70,
                    timeMinutes = 15,
                    status = ChallengeStatus.AVAILABLE,
                    isAiGenerated = true
                )
            )
        )
    }

    private fun createInitialWeeklyAdventure(): List<WeeklyAdventureDay> {
        return listOf(
            WeeklyAdventureDay(1, "יום ראשון", "Sunday", "צעדו 3,000 צעדים בפארק 👟", "Walk 3,000 steps in park 👟", "🏆", "הושלם", "Completed", isCompleted = true, isCurrentDay = false),
            WeeklyAdventureDay(2, "יום שני", "Monday", "אמרו מילה טובה לחבר או מורה 💬", "Give kind words to friend 💬", "💖", "הושלם", "Completed", isCompleted = true, isCurrentDay = false),
            WeeklyAdventureDay(3, "יום שלישי", "Tuesday", "קראו 15 דקות בספר אהוב 📚", "Read 15 min favorite book 📚", "📖", "הושלם", "Completed", isCompleted = true, isCurrentDay = false),
            WeeklyAdventureDay(4, "יום רביעי", "Wednesday", "עזרו בהכנת ארוחה בריאה 🥗", "Help prepare healthy dinner 🥗", "🥗", "בתהליך", "In Progress", isCompleted = false, isCurrentDay = true),
            WeeklyAdventureDay(5, "יום חמישי", "Thursday", "למדו 3 מילים חדשות בשפה אחרת 🌍", "Learn 3 words in another language 🌍", "✨", "ממתין", "Pending", isCompleted = false, isCurrentDay = false),
            WeeklyAdventureDay(6, "יום שישי", "Friday", "שיעור יצירה או תחביב אהוב 🎨", "Craft or favorite hobby 🎨", "🎨", "ממתין", "Pending", isCompleted = false, isCurrentDay = false),
            WeeklyAdventureDay(7, "יום שבת", "Saturday", "10 דקות של מנוחה ורוגע 🧘", "10 min relaxation & mindfulness 🧘", "🌈", "ממתין", "Pending", isCompleted = false, isCurrentDay = false)
        )
    }

    private fun createInitialFriends(): List<FriendItem> {
        return listOf(
            FriendItem("f1", "נועה", "@noa_star", "👧", 13, 1940, true, "פעילה עכשיו", "Active now"),
            FriendItem("f2", "דן", "@dan_brave", "👦", 11, 1480, true, "במשימת כושר", "On sports mission"),
            FriendItem("f3", "אלי", "@eli_cosmo", "👦", 12, 1610, true, "בקרב ידידותי", "In friendly duel"),
            FriendItem("f4", "ליאור", "@lior_find", "👦", 10, 1320, false, "התחבר לפני שעה", "Active 1h ago"),
            FriendItem("f5", "אמה", "@emma_kind", "👧", 9, 1150, false, "התחברה אתמול", "Active yesterday")
        )
    }
}

data class LevelUpData(
    val newLevel: Int,
    val tier: LevelTier,
    val bonusStars: Int,
    val unlockedRewardHe: String,
    val unlockedRewardEn: String
)
