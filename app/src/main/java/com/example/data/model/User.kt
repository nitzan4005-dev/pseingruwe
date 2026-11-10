package com.example.data.model

enum class LevelTier(val titleHe: String, val titleEn: String, val minLevel: Int, val maxLevel: Int) {
    BEGINNER("מתחיל", "Beginner", 1, 3),
    EXPLORER("מגלה", "Explorer", 4, 6),
    ADVANCED("מתקדם", "Advanced", 7, 9),
    EXPERT("מומחה", "Expert", 10, 14),
    CHAMPION("אלוף", "Champion", 15, 19),
    LEGEND("אגדה", "Legend", 20, 99);

    companion object {
        fun fromLevel(level: Int): LevelTier {
            return entries.find { level in it.minLevel..it.maxLevel } ?: if (level >= 20) LEGEND else BEGINNER
        }
    }
}

data class UserStats(
    val completedMissions: Int = 68,
    val duelsWon: Int = 12,
    val booksRead: Int = 14,
    val kindnessActs: Int = 42,
    val groupScoreContribution: Int = 2340
)

data class BadgeItem(
    val id: String,
    val titleHe: String,
    val titleEn: String,
    val descHe: String,
    val descEn: String,
    val iconEmoji: String,
    val isUnlocked: Boolean = true,
    val unlockDate: String = "השבוע"
)

data class PetCompanion(
    val id: String,
    val nameHe: String,
    val nameEn: String,
    val friendshipLevel: Int,
    val iconEmoji: String,
    val isEquipped: Boolean = false,
    val costStars: Int = 0,
    val isUnlocked: Boolean = true
)

data class XpTransaction(
    val id: String,
    val title: String,
    val amount: Int,
    val timestamp: String,
    val category: String
)

data class UserProfile(
    val id: String = "user_maya",
    val name: String = "מאיה כוכב-עולה",
    val username: String = "@maya_star",
    val title: String = "חוקרת כוכבים נועזת",
    val bio: String = "אוהבת אתגרים, ציור, טיולים עם חברים ולצבור כוכבים!",
    val avatarEmoji: String = "👧",
    val level: Int = 12,
    val currentXp: Int = 2340,
    val xpForNextLevel: Int = 3000,
    val stars: Int = 1820,
    val streakDays: Int = 9,
    val leagueName: String = "ליגת הזהב",
    val leagueProgressPercent: Int = 74,
    val groupName: String = "מועדון ינשופי הכוכבים",
    val isVerified: Boolean = true,
    val stats: UserStats = UserStats(),
    val badges: List<BadgeItem> = emptyList(),
    val companions: List<PetCompanion> = emptyList(),
    val xpHistory: List<XpTransaction> = emptyList()
)
