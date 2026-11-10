package com.example

import com.example.data.model.ChallengeCategory
import com.example.data.model.ChallengeDifficulty
import com.example.data.model.ChallengeStatus
import com.example.data.model.LevelTier
import com.example.data.repository.ChallengeRepository
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

class ChallengeRepositoryTest {

    private lateinit var repository: ChallengeRepository

    @Before
    fun setUp() {
        repository = ChallengeRepository()
    }

    @Test
    fun testLevelTierCalculation() {
        assertEquals(LevelTier.BEGINNER, LevelTier.fromLevel(1))
        assertEquals(LevelTier.BEGINNER, LevelTier.fromLevel(3))
        assertEquals(LevelTier.EXPLORER, LevelTier.fromLevel(4))
        assertEquals(LevelTier.EXPLORER, LevelTier.fromLevel(6))
        assertEquals(LevelTier.ADVANCED, LevelTier.fromLevel(7))
        assertEquals(LevelTier.EXPERT, LevelTier.fromLevel(12))
        assertEquals(LevelTier.CHAMPION, LevelTier.fromLevel(15))
        assertEquals(LevelTier.LEGEND, LevelTier.fromLevel(20))
        assertEquals(LevelTier.LEGEND, LevelTier.fromLevel(25))
    }

    @Test
    fun testCompleteChallengeAwardsXpAndStars() {
        val initialProfile = repository.userProfile.value
        val challenges = repository.challenges.value
        val available = challenges.first { it.status != ChallengeStatus.COMPLETED }

        val success = repository.completeChallenge(available.id)
        assertTrue(success)

        val updatedProfile = repository.userProfile.value
        assertEquals(initialProfile.currentXp + available.xpReward, updatedProfile.currentXp)
        assertEquals(initialProfile.stars + available.starsReward, updatedProfile.stars)
        assertEquals(initialProfile.stats.completedMissions + 1, updatedProfile.stats.completedMissions)
    }

    @Test
    fun testAddCustomChallenge() {
        val initialCount = repository.challenges.value.size
        repository.addCustomChallenge(
            titleHe = "אתגר יוגה של 5 דקות",
            descHe = "מתיחות בוקר עדינות",
            category = ChallengeCategory.SPORTS,
            difficulty = ChallengeDifficulty.EASY,
            timeMinutes = 5
        )

        val updatedChallenges = repository.challenges.value
        assertEquals(initialCount + 1, updatedChallenges.size)
        val added = updatedChallenges.first()
        assertEquals("אתגר יוגה של 5 דקות", added.titleHe)
        assertEquals(ChallengeStatus.AVAILABLE, added.status)
    }

    @Test
    fun testSendCheerToOpponentAwardsStars() {
        val initialStars = repository.userProfile.value.stars
        val initialCheers = repository.friendDuel.value.cheerCount

        repository.sendCheerToOpponent()

        assertEquals(initialCheers + 1, repository.friendDuel.value.cheerCount)
        assertEquals(initialStars + 20, repository.userProfile.value.stars)
    }

    @Test
    fun testAiPromptGeneratesResponse() {
        val initialCount = repository.aiMessages.value.size
        repository.sendAiPrompt("תן לי אתגר ספורט וריצה")

        val updatedMessages = repository.aiMessages.value
        assertTrue(updatedMessages.size >= initialCount + 2)
        val lastMsg = updatedMessages.last()
        assertNotNull(lastMsg.suggestedChallenge)
        assertEquals(ChallengeCategory.SPORTS, lastMsg.suggestedChallenge?.category)
    }
}
