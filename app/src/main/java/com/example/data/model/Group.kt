package com.example.data.model

data class Teammate(
    val id: String,
    val name: String,
    val avatarEmoji: String,
    val isOnline: Boolean = true,
    val isCurrentUser: Boolean = false,
    val starsContributed: Int = 450
)

data class TeamMission(
    val id: String,
    val titleHe: String,
    val titleEn: String,
    val currentProgress: Int,
    val targetProgress: Int,
    val unitHe: String,
    val unitEn: String,
    val rewardDescHe: String,
    val rewardDescEn: String,
    val iconEmoji: String = "👟",
    val isCompleted: Boolean = false
)

data class GroupChatMessage(
    val id: String,
    val senderName: String,
    val senderAvatar: String,
    val message: String,
    val timestamp: String,
    val isCheer: Boolean = false
)

data class GroupItem(
    val id: String,
    val name: String,
    val description: String,
    val avatarEmoji: String = "🦉",
    val leaderName: String = "מאיה",
    val memberCount: Int = 18,
    val onlineCount: Int = 5,
    val totalStars: Int = 7200,
    val starsGoal: Int = 10000,
    val rankInLeague: Int = 4,
    val leagueName: String = "ליגת הילדים",
    val weeklyBonusStars: Int = 500,
    val isUserMember: Boolean = true,
    val teammates: List<Teammate> = emptyList(),
    val missions: List<TeamMission> = emptyList(),
    val chatMessages: List<GroupChatMessage> = emptyList()
)
