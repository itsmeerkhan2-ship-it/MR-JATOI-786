package com.example.data.model

data class User(
    val id: String,
    val username: String,
    val displayName: String,
    val avatarUrl: String,
    val bio: String,
    val phoneNumber: String = "",
    val email: String = "",
    val isVerified: Boolean = false,
    val followersCount: Long = 0,
    val followingCount: Long = 0,
    val verifiedWatchHours: Long = 0,
    val verifiedViews: Long = 0,
    val coins: Long = 120, // Initial welcome reward
    val streakDays: Int = 1,
    val lastStreakClaimed: Boolean = false,
    val referralCode: String = "JATOI-786",
    val isAdmin: Boolean = false,
    val isMonetized: Boolean = false,
    val isBlocked: Boolean = false,
    val communityStrikes: Int = 0
) {
    val pkrBalance: Double
        get() = coins * 0.50
}

data class Video(
    val id: String,
    val creatorId: String,
    val creatorName: String,
    val creatorHandle: String,
    val creatorAvatar: String,
    val title: String,
    val description: String,
    val videoUrl: String,
    val thumbnailUrl: String,
    val category: String,
    val hashtags: List<String>,
    val likesCount: Long,
    val commentsCount: Long,
    val sharesCount: Long,
    val viewsCount: Long,
    val musicTitle: String = "Original Audio - Mr Jatoi Hits",
    val isLiked: Boolean = false,
    val isSaved: Boolean = false,
    val isFollowing: Boolean = false,
    val isFlagged: Boolean = false,
    val uploadTimestamp: Long = System.currentTimeMillis()
)

data class Comment(
    val id: String,
    val videoId: String,
    val userId: String,
    val userName: String,
    val userAvatar: String,
    val content: String,
    val timeAgo: String,
    val likesCount: Int = 0,
    val isLiked: Boolean = false
)

enum class PaymentMethod(val title: String, val badge: String) {
    JAZZCASH("JazzCash", "Mobile Account"),
    EASYPAISA("Easypaisa", "Mobile Account"),
    BANK_ACCOUNT("Pakistani Bank", "IBAN / Raast"),
    INTERNATIONAL("International Wire", "Payoneer / USDT")
}

enum class TransactionStatus(val label: String) {
    PENDING("Under Review"),
    APPROVED("Approved"),
    PAID("Paid & Dispatched"),
    REJECTED("Rejected")
}

enum class TransactionType(val label: String) {
    WATCH_REWARD("Video Watch Reward"),
    DAILY_STREAK("Daily Login Bonus"),
    REFERRAL_BONUS("Referral Bonus"),
    CREATOR_EARNING("Creator Monetization"),
    WITHDRAWAL("Payout Withdrawal")
}

data class WalletTransaction(
    val id: String,
    val type: TransactionType,
    val coins: Long,
    val pkrAmount: Double,
    val timestamp: Long = System.currentTimeMillis(),
    val status: TransactionStatus = TransactionStatus.APPROVED,
    val paymentMethod: PaymentMethod? = null,
    val accountDetails: String = "",
    val referenceNote: String = ""
)

enum class ApplicationStatus(val title: String) {
    PENDING("Under Admin Verification"),
    APPROVED("Monetized Creator"),
    REJECTED("Not Approved")
}

data class MonetizationApplication(
    val id: String,
    val userId: String,
    val userName: String,
    val userHandle: String,
    val followersCount: Long,
    val verifiedViews: Long,
    val watchHours: Long,
    val strikesCount: Int,
    val appliedDate: Long = System.currentTimeMillis(),
    val status: ApplicationStatus = ApplicationStatus.PENDING,
    val adminReviewNotes: String = ""
)

data class ReportTicket(
    val id: String,
    val videoId: String,
    val videoTitle: String,
    val creatorHandle: String,
    val reporterId: String,
    val reason: String,
    val timestamp: Long = System.currentTimeMillis(),
    val isResolved: Boolean = false,
    val actionTaken: String = "Pending Review"
)
