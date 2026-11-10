package com.example.data.model

import androidx.compose.ui.unit.LayoutDirection

enum class AppLanguage(val code: String, val displayName: String, val layoutDirection: LayoutDirection) {
    HEBREW("he", "עברית", LayoutDirection.Rtl),
    ENGLISH("en", "English", LayoutDirection.Ltr)
}

object Strings {
    fun appTitle(lang: AppLanguage) = if (lang == AppLanguage.HEBREW) "ChallengeMe" else "ChallengeMe"
    fun kidsWorld(lang: AppLanguage) = if (lang == AppLanguage.HEBREW) "עולם הילדים והאתגרים 🎈" else "Kids Challenge World 🎈"
    
    // Bottom Nav
    fun navHome(lang: AppLanguage) = if (lang == AppLanguage.HEBREW) "בית" else "Home"
    fun navMissions(lang: AppLanguage) = if (lang == AppLanguage.HEBREW) "משימות" else "Missions"
    fun navFriends(lang: AppLanguage) = if (lang == AppLanguage.HEBREW) "חברים" else "Friends"
    fun navGroups(lang: AppLanguage) = if (lang == AppLanguage.HEBREW) "קבוצות" else "Groups"
    fun navCompetitions(lang: AppLanguage) = if (lang == AppLanguage.HEBREW) "תחרויות" else "Arena"
    fun navAiCoach(lang: AppLanguage) = if (lang == AppLanguage.HEBREW) "רובו-חבר" else "AI Coach"
    fun navProfile(lang: AppLanguage) = if (lang == AppLanguage.HEBREW) "פרופיל" else "Profile"

    // Home Screen
    fun greeting(lang: AppLanguage, name: String) = if (lang == AppLanguage.HEBREW) "היי $name! 🌟" else "Hi $name! 🌟"
    fun levelTitle(lang: AppLanguage, level: Int, tier: String) = if (lang == AppLanguage.HEBREW) "רמה $level • $tier" else "Level $level • $tier"
    fun xpMeterTitle(lang: AppLanguage) = if (lang == AppLanguage.HEBREW) "מד הכוכבים שלי" else "My Star Meter"
    fun roadToNextLevel(lang: AppLanguage) = if (lang == AppLanguage.HEBREW) "הדרך לרמה הבאה" else "Road to Next Level"
    fun xpRemaining(lang: AppLanguage, remaining: Int, nextLevel: Int, nextTier: String) = 
        if (lang == AppLanguage.HEBREW) "עוד $remaining כוכבים בלבד כדי להגיע לרמה $nextLevel:\n$nextTier!"
        else "Only $remaining stars to reach Level $nextLevel:\n$nextTier!"
    fun streakBanner(lang: AppLanguage, days: Int) =
        if (lang == AppLanguage.HEBREW) "רצף של $days ימים ברצף! 🔥\nאתגרים, משימות ומעשים טובים"
        else "$days Days Active Streak! 🔥\nChallenges, missions and good deeds"
    fun dailyChallengeTitle(lang: AppLanguage) = if (lang == AppLanguage.HEBREW) "אתגר היום המיוחד ⭐" else "Special Daily Challenge ⭐"
    fun activeMissions(lang: AppLanguage) = if (lang == AppLanguage.HEBREW) "משימות פעילות" else "Active Missions"
    fun completedMissions(lang: AppLanguage) = if (lang == AppLanguage.HEBREW) "משימות שהושלמו" else "Completed Missions"
    fun seeAll(lang: AppLanguage) = if (lang == AppLanguage.HEBREW) "הכל >" else "See All >"
    fun quickDuel(lang: AppLanguage) = if (lang == AppLanguage.HEBREW) "דו-קרב חברים 🥊" else "Friend Duel 🥊"
    fun aiAssistant(lang: AppLanguage) = if (lang == AppLanguage.HEBREW) "המאמן החכם 🤖" else "Smart Coach 🤖"
    
    // Actions
    fun start(lang: AppLanguage) = if (lang == AppLanguage.HEBREW) "התחל" else "Start"
    fun complete(lang: AppLanguage) = if (lang == AppLanguage.HEBREW) "הושלם! 🎉" else "Complete! 🎉"
    fun markComplete(lang: AppLanguage) = if (lang == AppLanguage.HEBREW) "השלמתי!" else "I Did It!"
    fun skip(lang: AppLanguage) = if (lang == AppLanguage.HEBREW) "דלג" else "Skip"
    fun acceptChallenge(lang: AppLanguage) = if (lang == AppLanguage.HEBREW) "אני מקבל את האתגר! 🚀" else "I Accept Challenge! 🚀"
    fun switchChallenge(lang: AppLanguage) = if (lang == AppLanguage.HEBREW) "החלף 🔄" else "Switch 🔄"
    fun cheer(lang: AppLanguage) = if (lang == AppLanguage.HEBREW) "עודד 👏" else "Cheer 👏"
    fun sendHighFive(lang: AppLanguage, name: String) = if (lang == AppLanguage.HEBREW) "שלחי כיף ל$name! ✋" else "Send High-Five to $name! ✋"
    fun addChallenge(lang: AppLanguage) = if (lang == AppLanguage.HEBREW) "הוסף אתגר אישי +" else "Add Custom Challenge +"

    // Profile & Trophies
    fun trophyCloset(lang: AppLanguage, unlocked: Int, total: Int) =
        if (lang == AppLanguage.HEBREW) "ארון הגביעים והתגים 🏆\nנאספו $unlocked מתוך $total תגים מגניבים"
        else "Trophy & Badge Showcase 🏆\nCollected $unlocked out of $total awesome badges"
    fun myStats(lang: AppLanguage) = if (lang == AppLanguage.HEBREW) "הסטטיסטיקות המשמחות שלי 📊" else "My Cheerful Stats 📊"
    fun companionsTitle(lang: AppLanguage) = if (lang == AppLanguage.HEBREW) "חיות המחמד והתלבושות שלי 🐾" else "My Companions & Pets 🐾"
    fun companionsSubtitle(lang: AppLanguage) = if (lang == AppLanguage.HEBREW) "החברים הקסומים שמלווים אותי" else "Magical friends that accompany my journey"
    fun openStarShop(lang: AppLanguage) = if (lang == AppLanguage.HEBREW) "פתח עוד בחנות הכוכבים ⭐" else "Open Star Pet Shop ⭐"
    fun editProfile(lang: AppLanguage) = if (lang == AppLanguage.HEBREW) "ערוך פרופיל ✏️" else "Edit Profile ✏️"
    fun parentMode(lang: AppLanguage) = if (lang == AppLanguage.HEBREW) "מצב הורים 🛡️" else "Parental Mode 🛡️"
    fun verifiedProfile(lang: AppLanguage) = if (lang == AppLanguage.HEBREW) "פרופיל מאומת ✓" else "Verified Profile ✓"

    // AI Coach Screen
    fun aiScreenTitle(lang: AppLanguage) = if (lang == AppLanguage.HEBREW) "החבר החכם שלי 🤖" else "My Smart AI Friend 🤖"
    fun aiScreenSubtitle(lang: AppLanguage) = if (lang == AppLanguage.HEBREW) "המאמן האישי שלך להרפתקאות וכוכבים!" else "Your personal adventure & stars coach!"
    fun weeklyAdventure(lang: AppLanguage, completed: Int, total: Int) =
        if (lang == AppLanguage.HEBREW) "הרפתקת השבוע שלי 📅 ($completed מתוך $total הושלמו!)"
        else "My Weekly Adventure 📅 ($completed of $total Completed!)"
    fun askAiPlaceholder(lang: AppLanguage) = if (lang == AppLanguage.HEBREW) "בקשו משימה חדשה מהרובוט..." else "Ask robot for a new mission..."

    // Groups Screen
    fun myGroup(lang: AppLanguage) = if (lang == AppLanguage.HEBREW) "הקבוצה שלי 🦉" else "My Group 🦉"
    fun starTreasury(lang: AppLanguage) = if (lang == AppLanguage.HEBREW) "קופת הכוכבים" else "Star Vault"
    fun weeklyGroupGoal(lang: AppLanguage, bonus: Int) =
        if (lang == AppLanguage.HEBREW) "יעד שבועי: +$bonus כוכבי בונוס לכל חבר בהגעה ליעד! 🎁"
        else "Weekly Goal: +$bonus bonus stars for each teammate! 🎁"
    fun teamSquad(lang: AppLanguage, online: Int) =
        if (lang == AppLanguage.HEBREW) "חברי הנבחרת ($online מחוברים)"
        else "Team Squad ($online online)"
    fun safeChat(lang: AppLanguage) = if (lang == AppLanguage.HEBREW) "צ'אט קבוצתי בטוח 🛡️" else "Safe Kid Team Chat 🛡️"
    fun coopMissions(lang: AppLanguage) = if (lang == AppLanguage.HEBREW) "משימות קבוצתיות שיתופיות 🤝" else "Cooperative Team Missions 🤝"
    fun createGroup(lang: AppLanguage) = if (lang == AppLanguage.HEBREW) "צור קבוצה 🎈" else "Create Group 🎈"
    fun searchGroup(lang: AppLanguage) = if (lang == AppLanguage.HEBREW) "חיפוש חברים או קבוצות..." else "Search friends or groups..."

    // Competitions & Leaderboards
    fun weeklyCup(lang: AppLanguage) = if (lang == AppLanguage.HEBREW) "גביע הכוכבים השבועי 🏆" else "Weekly Star Trophy 🏆"
    fun daysLeftInCup(lang: AppLanguage) = if (lang == AppLanguage.HEBREW) "עוד יומיים לסיום האתגר השבועי!" else "2 days left in this weekly challenge!"
    fun tabFriends(lang: AppLanguage) = if (lang == AppLanguage.HEBREW) "חברים שלי 🧑‍🤝‍🧑" else "My Friends 🧑‍🤝‍🧑"
    fun tabAllKids(lang: AppLanguage) = if (lang == AppLanguage.HEBREW) "כל הילדים 🌍" else "All Kids 🌍"
    fun tabGroups(lang: AppLanguage) = if (lang == AppLanguage.HEBREW) "קבוצות 🦉" else "Groups 🦉"
    fun earnMoreStars(lang: AppLanguage) = if (lang == AppLanguage.HEBREW) "הרווח עוד כוכבים באתגרים! 🚀" else "Earn More Stars in Challenges! 🚀"
    fun leagueProgress(lang: AppLanguage, league: String, percent: Int) =
        if (lang == AppLanguage.HEBREW) "התקדמות ל$league: $percent%" else "Progress to $league: $percent%"

    // Level up dialog
    fun levelUpCelebration(lang: AppLanguage) = if (lang == AppLanguage.HEBREW) "עליית רמה! 🚀🎉" else "Level Up! 🚀🎉"
    fun congratulations(lang: AppLanguage, name: String, level: Int, tier: String) =
        if (lang == AppLanguage.HEBREW) "כל הכבוד $name!\nהגעת לרמה $level: $tier!"
        else "Congratulations $name!\nYou reached Level $level: $tier!"
    fun collectReward(lang: AppLanguage) = if (lang == AppLanguage.HEBREW) "אסוף תגמול! 🎁" else "Claim Reward! 🎁"

    // Settings
    fun settings(lang: AppLanguage) = if (lang == AppLanguage.HEBREW) "הגדרות" else "Settings"
    fun language(lang: AppLanguage) = if (lang == AppLanguage.HEBREW) "שפה (Language)" else "Language"
    fun theme(lang: AppLanguage) = if (lang == AppLanguage.HEBREW) "עיצוב (מצב כהה / בהיר)" else "Theme (Dark / Light)"
    fun soundVibe(lang: AppLanguage) = if (lang == AppLanguage.HEBREW) "רטט ואפקטים קוליים" else "Sound & Haptic Effects"
}
