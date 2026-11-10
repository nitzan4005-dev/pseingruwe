package com.example.data.model

enum class ChallengeCategory(
    val titleHe: String,
    val titleEn: String,
    val emoji: String
) {
    SPORTS("ספורט וכושר", "Sports & Fitness", "🏃"),
    LEARNING("לימודים וידע", "Learning & Knowledge", "📚"),
    SOCIAL("חברה וביטחון", "Social & Confidence", "🤝"),
    COURAGE("אומץ ואזור הנוחות", "Courage", "🦁"),
    CREATIVITY("יצירתיות", "Creativity", "🎨"),
    PRODUCTIVITY("פרודוקטיביות", "Productivity", "⚡"),
    GOOD_DEEDS("מעשים טובים", "Good Deeds", "💖")
}

enum class ChallengeDifficulty(
    val titleHe: String,
    val titleEn: String,
    val baseMultiplier: Int
) {
    EASY("קל", "Easy", 1),
    MEDIUM("בינוני", "Medium", 2),
    HARD("מאתגר", "Challenging", 3)
}

enum class ChallengeStatus {
    AVAILABLE,
    IN_PROGRESS,
    COMPLETED,
    SKIPPED
}

data class Challenge(
    val id: String,
    val titleHe: String,
    val titleEn: String,
    val descHe: String,
    val descEn: String,
    val category: ChallengeCategory,
    val difficulty: ChallengeDifficulty,
    val xpReward: Int,
    val starsReward: Int = xpReward / 2,
    val timeMinutes: Int,
    val status: ChallengeStatus = ChallengeStatus.AVAILABLE,
    val completedAt: Long? = null,
    val isAiGenerated: Boolean = false,
    val isDaily: Boolean = true
)
