package com.example.data.model

data class AiChatMessage(
    val id: String,
    val sender: MessageSender,
    val text: String,
    val suggestedChallenge: Challenge? = null,
    val timestamp: String = "עכשיו"
)

enum class MessageSender {
    USER,
    ROBOT
}

data class WeeklyAdventureDay(
    val dayId: Int, // 1 to 7
    val dayNameHe: String,
    val dayNameEn: String,
    val taskHe: String,
    val taskEn: String,
    val iconEmoji: String,
    val statusHe: String, // "הושלם", "בתהליך", "ממתין"
    val statusEn: String,
    val isCompleted: Boolean,
    val isCurrentDay: Boolean
)
