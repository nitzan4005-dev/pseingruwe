package com.example.data.model

data class FriendDuel(
    val id: String,
    val opponentName: String,
    val opponentAvatar: String,
    val opponentRoleHe: String = "חברה",
    val opponentRoleEn: String = "Friend",
    val opponentStars: Int = 1140,
    val opponentCompletedMissions: Int = 3,
    val userStars: Int = 1420,
    val userCompletedMissions: Int = 4,
    val timeLeftTextHe: String = "נותרו עוד 3 שעות",
    val timeLeftTextEn: String = "3 hours left",
    val rewardDescHe: String = "+350 כוכבים ⭐, גביע זהב 🏆 ותג אומץ 🦁",
    val rewardDescEn: String = "+350 Stars ⭐, Gold Trophy 🏆 & Courage Badge 🦁",
    val nextMissionRewardStars: Int = 120,
    val canSendCheer: Boolean = true,
    val cheerCount: Int = 3
)

data class GroupWar(
    val id: String,
    val teamAName: String = "ינשופי הלילה",
    val teamAAvatar: String = "🦉",
    val teamAScore: Int = 8420,
    val teamBName: String = "טיטאנים מהירים",
    val teamBAvatar: String = "⚡",
    val teamBScore: Int = 8850,
    val rank: Int = 4,
    val jointMissionsCurrent: Int = 38,
    val jointMissionsTarget: Int = 50,
    val percentage: Int = 76
)

data class LeaderboardUser(
    val rank: Int,
    val name: String,
    val titleHe: String,
    val titleEn: String,
    val avatarEmoji: String,
    val stars: Int,
    val isCurrentUser: Boolean = false,
    val badgeEmoji: String = "⭐"
)

data class FriendItem(
    val id: String,
    val name: String,
    val username: String,
    val avatarEmoji: String,
    val level: Int,
    val stars: Int,
    val isOnline: Boolean,
    val statusTextHe: String,
    val statusTextEn: String,
    val friendStatus: FriendRequestStatus = FriendRequestStatus.FRIENDS
)

enum class FriendRequestStatus {
    FRIENDS,
    PENDING_SENT,
    PENDING_RECEIVED,
    NONE
}
