package com.example.data.repository

import com.example.data.model.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.UUID

class AppRepository private constructor() {

    companion object {
        val instance: AppRepository by lazy { AppRepository() }
    }

    // Owner Profile
    val ownerProfile = User(
        id = "owner_musadiq_jatoi",
        username = "musadiq_jatoi",
        displayName = "Musadiq Hussain Jatoi",
        avatarUrl = "https://images.unsplash.com/photo-1534528741775-53994a69daeb?w=400&q=80",
        bio = "Official Founder & CEO of MR JATOI VIDEO. Watch, Create & Earn across Pakistan and Worldwide.",
        phoneNumber = "+92 300 1234567",
        email = "musadiq@mrjatoivideo.com",
        isVerified = true,
        followersCount = 1_250_000,
        followingCount = 142,
        verifiedWatchHours = 120_000,
        verifiedViews = 8_500_000,
        coins = 85_000,
        streakDays = 30,
        lastStreakClaimed = true,
        referralCode = "JATOI-VIP",
        isAdmin = true,
        isMonetized = true,
        isBlocked = false,
        communityStrikes = 0
    )

    // Current User
    private val _currentUser = MutableStateFlow(
        User(
            id = "user_current",
            username = "ali_creator",
            displayName = "Ali Raza",
            avatarUrl = "https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d?w=400&q=80",
            bio = "Digital content creator from Lahore 🇵🇰 | Tech & Pakistani Culture",
            phoneNumber = "+92 312 9876543",
            email = "ali.raza@example.pk",
            isVerified = false,
            followersCount = 425_800, // Approaching 500k monetization goal!
            followingCount = 85,
            verifiedWatchHours = 3_450, // Goal: 4,000 hrs
            verifiedViews = 92_000, // Goal: 100,000 views
            coins = 340,
            streakDays = 4,
            lastStreakClaimed = false,
            referralCode = "JATOI-ALI786",
            isAdmin = false,
            isMonetized = false,
            isBlocked = false,
            communityStrikes = 0
        )
    )
    val currentUser: StateFlow<User> = _currentUser.asStateFlow()

    // Mode Toggle (User View vs Admin Dashboard)
    private val _isAdminMode = MutableStateFlow(false)
    val isAdminMode: StateFlow<Boolean> = _isAdminMode.asStateFlow()

    // Videos
    private val _videos = MutableStateFlow(generateInitialVideos())
    val videos: StateFlow<List<Video>> = _videos.asStateFlow()

    // Comments map: videoId -> list of comments
    private val _comments = MutableStateFlow(generateInitialComments())
    val comments: StateFlow<Map<String, List<Comment>>> = _comments.asStateFlow()

    // Transactions
    private val _transactions = MutableStateFlow(generateInitialTransactions())
    val transactions: StateFlow<List<WalletTransaction>> = _transactions.asStateFlow()

    // Monetization Applications
    private val _monetizationApplications = MutableStateFlow(generateInitialMonetizationApps())
    val monetizationApplications: StateFlow<List<MonetizationApplication>> = _monetizationApplications.asStateFlow()

    // Flagged Reports
    private val _reports = MutableStateFlow(generateInitialReports())
    val reports: StateFlow<List<ReportTicket>> = _reports.asStateFlow()

    // Watch Session State for Anti-Fraud Watch-to-Earn
    private val _dailyWatchCoinsEarned = MutableStateFlow(15) // Limit: 50 coins / day
    val dailyWatchCoinsEarned: StateFlow<Int> = _dailyWatchCoinsEarned.asStateFlow()

    fun toggleAdminMode() {
        _isAdminMode.value = !_isAdminMode.value
    }

    fun setAdminMode(enabled: Boolean) {
        _isAdminMode.value = enabled
    }

    // Toggle Like on video
    fun toggleLikeVideo(videoId: String) {
        _videos.value = _videos.value.map { video ->
            if (video.id == videoId) {
                val newIsLiked = !video.isLiked
                val newLikesCount = if (newIsLiked) video.likesCount + 1 else video.likesCount - 1
                video.copy(isLiked = newIsLiked, likesCount = newLikesCount)
            } else {
                video
            }
        }
    }

    // Toggle Follow creator
    fun toggleFollowCreator(creatorId: String) {
        _videos.value = _videos.value.map { video ->
            if (video.creatorId == creatorId) {
                video.copy(isFollowing = !video.isFollowing)
            } else {
                video
            }
        }
        val user = _currentUser.value
        _currentUser.value = user.copy(followingCount = user.followingCount + 1)
    }

    // Record View and Watch Reward with anti-fraud timer
    fun recordVideoView(videoId: String, watchSeconds: Int): Boolean {
        _videos.value = _videos.value.map { video ->
            if (video.id == videoId) {
                video.copy(viewsCount = video.viewsCount + 1)
            } else {
                video
            }
        }

        // Anti-fraud criteria: minimum 10 seconds continuous watch required
        // Daily cap: 50 coins per day to prevent automated bots
        if (watchSeconds >= 10 && _dailyWatchCoinsEarned.value < 50) {
            val rewardCoins = 5L
            _dailyWatchCoinsEarned.value += 5
            val updatedUser = _currentUser.value.copy(
                coins = _currentUser.value.coins + rewardCoins
            )
            _currentUser.value = updatedUser

            val tx = WalletTransaction(
                id = UUID.randomUUID().toString().take(8),
                type = TransactionType.WATCH_REWARD,
                coins = rewardCoins,
                pkrAmount = rewardCoins * 0.50,
                status = TransactionStatus.APPROVED,
                referenceNote = "Verified watch reward (15s minimum watch rule)"
            )
            _transactions.value = listOf(tx) + _transactions.value
            return true
        }
        return false
    }

    // Add Comment
    fun addComment(videoId: String, content: String) {
        val user = _currentUser.value
        val newComment = Comment(
            id = UUID.randomUUID().toString().take(8),
            videoId = videoId,
            userId = user.id,
            userName = user.displayName,
            userAvatar = user.avatarUrl,
            content = content,
            timeAgo = "Just now",
            likesCount = 0,
            isLiked = false
        )
        val currentList = _comments.value[videoId] ?: emptyList()
        val updatedMap = _comments.value.toMutableMap()
        updatedMap[videoId] = listOf(newComment) + currentList
        _comments.value = updatedMap

        // Update video comment count
        _videos.value = _videos.value.map {
            if (it.id == videoId) it.copy(commentsCount = it.commentsCount + 1) else it
        }
    }

    // Toggle Comment Like
    fun toggleCommentLike(videoId: String, commentId: String) {
        val list = _comments.value[videoId] ?: return
        val updatedList = list.map { c ->
            if (c.id == commentId) {
                val newLiked = !c.isLiked
                c.copy(isLiked = newLiked, likesCount = if (newLiked) c.likesCount + 1 else c.likesCount - 1)
            } else c
        }
        val updatedMap = _comments.value.toMutableMap()
        updatedMap[videoId] = updatedList
        _comments.value = updatedMap
    }

    // Claim Daily Login Streak
    fun claimDailyStreak(): Boolean {
        val user = _currentUser.value
        if (user.lastStreakClaimed) return false

        val streakRewardCoins = 25L
        val updatedUser = user.copy(
            coins = user.coins + streakRewardCoins,
            streakDays = user.streakDays + 1,
            lastStreakClaimed = true
        )
        _currentUser.value = updatedUser

        val tx = WalletTransaction(
            id = UUID.randomUUID().toString().take(8),
            type = TransactionType.DAILY_STREAK,
            coins = streakRewardCoins,
            pkrAmount = streakRewardCoins * 0.50,
            status = TransactionStatus.APPROVED,
            referenceNote = "Day ${updatedUser.streakDays} Login Streak Bonus"
        )
        _transactions.value = listOf(tx) + _transactions.value
        return true
    }

    // Upload Video with AI Moderation Scan
    fun uploadVideo(
        title: String,
        category: String,
        hashtags: List<String>,
        videoUrl: String,
        thumbnailUrl: String
    ): Pair<Boolean, String> {
        val lowerTitle = title.lowercase()
        // Prohibited Content keywords for AI safety check
        val bannedKeywords = listOf("weapon", "nude", "hack", "illegal", "scam", "gamble", "violence")
        for (kw in bannedKeywords) {
            if (lowerTitle.contains(kw)) {
                return Pair(false, "AI Safety Violation: Content contains prohibited term '$kw'. Upload blocked.")
            }
        }

        val user = _currentUser.value
        val newVideo = Video(
            id = "v_" + UUID.randomUUID().toString().take(6),
            creatorId = user.id,
            creatorName = user.displayName,
            creatorHandle = user.username,
            creatorAvatar = user.avatarUrl,
            title = title,
            description = "Uploaded by ${user.displayName}. Verified for MR JATOI VIDEO community.",
            videoUrl = videoUrl,
            thumbnailUrl = thumbnailUrl.ifEmpty { "https://images.unsplash.com/photo-1618005182384-a83a8bd57fbe?w=600&q=80" },
            category = category,
            hashtags = hashtags,
            likesCount = 1,
            commentsCount = 0,
            sharesCount = 0,
            viewsCount = 1,
            musicTitle = "Original Sound - ${user.displayName}",
            isLiked = false,
            isFollowing = false,
            isFlagged = false
        )

        _videos.value = listOf(newVideo) + _videos.value
        return Pair(true, "Video passed AI Safety checks and is now live on MR JATOI VIDEO!")
    }

    // Report video
    fun reportVideo(videoId: String, reason: String) {
        val video = _videos.value.find { it.id == videoId }
        val ticket = ReportTicket(
            id = "TKT-" + UUID.randomUUID().toString().take(6).uppercase(),
            videoId = videoId,
            videoTitle = video?.title ?: "Video #$videoId",
            creatorHandle = video?.creatorHandle ?: "@creator",
            reporterId = _currentUser.value.id,
            reason = reason,
            actionTaken = "Queued for Admin Review"
        )
        _reports.value = listOf(ticket) + _reports.value
    }

    // Apply for Monetization
    fun applyForMonetization(): Pair<Boolean, String> {
        val user = _currentUser.value
        if (user.followersCount < 500_000) {
            return Pair(
                false,
                "Eligibility not met: You have ${user.followersCount} followers. 500,000 genuine followers are strictly required by policy."
            )
        }
        if (user.verifiedViews < 100_000) {
            return Pair(
                false,
                "Eligibility not met: You need at least 100,000 verified video views."
            )
        }
        if (user.verifiedWatchHours < 4_000) {
            return Pair(
                false,
                "Eligibility not met: Minimum 4,000 watch hours required."
            )
        }
        if (user.communityStrikes > 0) {
            return Pair(
                false,
                "Disqualified: Your account has ${user.communityStrikes} community guideline strikes."
            )
        }

        val application = MonetizationApplication(
            id = "APP-" + UUID.randomUUID().toString().take(6).uppercase(),
            userId = user.id,
            userName = user.displayName,
            userHandle = "@${user.username}",
            followersCount = user.followersCount,
            verifiedViews = user.verifiedViews,
            watchHours = user.verifiedWatchHours,
            strikesCount = user.communityStrikes,
            status = ApplicationStatus.PENDING,
            adminReviewNotes = "Pending Musadiq Hussain Jatoi's official verification"
        )
        _monetizationApplications.value = listOf(application) + _monetizationApplications.value
        return Pair(true, "Application submitted successfully! Admin will audit followers and watch time.")
    }

    // Submit Payment Withdrawal Request
    fun requestWithdrawal(
        method: PaymentMethod,
        accountTitle: String,
        accountNumber: String,
        bankName: String,
        amountPkr: Double
    ): Pair<Boolean, String> {
        val user = _currentUser.value
        val requiredCoins = (amountPkr / 0.50).toLong()

        if (amountPkr < 500.0) {
            return Pair(false, "Minimum withdrawal limit is 500 PKR (1,000 Coins).")
        }
        if (user.coins < requiredCoins) {
            return Pair(false, "Insufficient balance! You have ${user.coins} Coins (${user.pkrBalance} PKR).")
        }
        if (accountTitle.isBlank() || accountNumber.isBlank()) {
            return Pair(false, "Please provide complete account holder title and account/mobile number.")
        }

        // Deduct coins from user balance immediately and record pending transaction
        _currentUser.value = user.copy(coins = user.coins - requiredCoins)

        val tx = WalletTransaction(
            id = "WD-" + UUID.randomUUID().toString().take(6).uppercase(),
            type = TransactionType.WITHDRAWAL,
            coins = requiredCoins,
            pkrAmount = amountPkr,
            status = TransactionStatus.PENDING,
            paymentMethod = method,
            accountDetails = "$accountTitle | $accountNumber ${if (bankName.isNotBlank()) "($bankName)" else ""}",
            referenceNote = "Server verification in progress. Dispatches upon admin and provider clearance."
        )
        _transactions.value = listOf(tx) + _transactions.value
        return Pair(true, "Withdrawal request of $amountPkr PKR submitted to the administrative verification queue.")
    }

    // --- Admin Actions (For Musadiq Hussain Jatoi) ---

    fun adminApproveMonetization(applicationId: String) {
        val app = _monetizationApplications.value.find { it.id == applicationId } ?: return
        _monetizationApplications.value = _monetizationApplications.value.map {
            if (it.id == applicationId) it.copy(status = ApplicationStatus.APPROVED, adminReviewNotes = "Approved by Musadiq Hussain Jatoi") else it
        }
        if (app.userId == _currentUser.value.id) {
            _currentUser.value = _currentUser.value.copy(isMonetized = true)
        }
    }

    fun adminRejectMonetization(applicationId: String, reason: String) {
        _monetizationApplications.value = _monetizationApplications.value.map {
            if (it.id == applicationId) it.copy(status = ApplicationStatus.REJECTED, adminReviewNotes = reason) else it
        }
    }

    fun adminApprovePayment(transactionId: String) {
        _transactions.value = _transactions.value.map { tx ->
            if (tx.id == transactionId) {
                tx.copy(status = TransactionStatus.PAID, referenceNote = "Dispatched via 1-Link / Bank Switch")
            } else tx
        }
    }

    fun adminRejectPayment(transactionId: String) {
        val tx = _transactions.value.find { it.id == transactionId } ?: return
        // Refund coins to user
        _currentUser.value = _currentUser.value.copy(coins = _currentUser.value.coins + tx.coins)
        _transactions.value = _transactions.value.map {
            if (it.id == transactionId) it.copy(status = TransactionStatus.REJECTED, referenceNote = "Rejected due to invalid account details. Coins refunded.") else it
        }
    }

    fun adminResolveReport(reportId: String, removeVideo: Boolean) {
        val report = _reports.value.find { it.id == reportId } ?: return
        _reports.value = _reports.value.map {
            if (it.id == reportId) it.copy(isResolved = true, actionTaken = if (removeVideo) "Video Removed & Strike Issued" else "Report Dismissed - Safe") else it
        }
        if (removeVideo) {
            _videos.value = _videos.value.filterNot { it.id == report.videoId }
        }
    }

    // Seed Data Generators
    private fun generateInitialVideos(): List<Video> {
        return listOf(
            Video(
                id = "v_aistudio_tutorial",
                creatorId = "creator_1",
                creatorName = "Musadiq Hussain Jatoi",
                creatorHandle = "musadiq_jatoi",
                creatorAvatar = "https://images.unsplash.com/photo-1534528741775-53994a69daeb?w=400&q=80",
                title = "Build Your Own Mobile App Without Coding Using Google AI Studio! 🚀📱 Step-by-Step Tutorial",
                description = "Have you ever wanted to create your own mobile application but don't know how to code? Now, with the power of AI, you can turn your app ideas into reality without learning programming! Learn how to use AI prompts to create an app, customize its design, add features, and test on mobile.",
                videoUrl = "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/BigBuckBunny.mp4",
                thumbnailUrl = "https://images.unsplash.com/photo-1551650975-87deedd944c3?w=600&q=80",
                category = "Technology",
                hashtags = listOf("#GoogleAIStudio", "#NoCodeApp", "#AIAppBuilder", "#MusadiqJatoi", "#MRJATOI", "#BuildAppsWithoutCoding"),
                likesCount = 142800,
                commentsCount = 5420,
                sharesCount = 28900,
                viewsCount = 1450000,
                musicTitle = "Tech Inspiration - Google AI Beats",
                isLiked = true,
                isFollowing = true
            ),
            Video(
                id = "v1",
                creatorId = "creator_1",
                creatorName = "Musadiq Hussain Jatoi",
                creatorHandle = "musadiq_jatoi",
                creatorAvatar = "https://images.unsplash.com/photo-1534528741775-53994a69daeb?w=400&q=80",
                title = "Welcome to MR JATOI VIDEO! Watch videos, share high quality reels and earn genuine rewards in Pakistan 🇵🇰",
                description = "MR JATOI VIDEO official announcement. Supporting local Pakistani creators and global audiences.",
                videoUrl = "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ForBiggerBlazes.mp4",
                thumbnailUrl = "https://images.unsplash.com/photo-1574717024653-61fd2cf4d44d?w=600&q=80",
                category = "Announcement",
                hashtags = listOf("#mrjatoivideo", "#earnwithvideos", "#pakistancreators", "#musadiqjatoi"),
                likesCount = 89420,
                commentsCount = 2814,
                sharesCount = 12500,
                viewsCount = 745000,
                musicTitle = "Mr Jatoi Anthem - Official Soundtrack",
                isLiked = true,
                isFollowing = true
            ),
            Video(
                id = "v2",
                creatorId = "creator_2",
                creatorName = "Zainab Tech & Reviews",
                creatorHandle = "zainab_tech",
                creatorAvatar = "https://images.unsplash.com/photo-1544005313-94ddf0286df2?w=400&q=80",
                title = "Top 5 smartphones under 40k PKR in Pakistan! Best camera and battery comparison 📱⚡",
                description = "Complete camera test in Islamabad daylight. Which smartphone gives best performance for content creators?",
                videoUrl = "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ForBiggerEscapes.mp4",
                thumbnailUrl = "https://images.unsplash.com/photo-1511707171634-5f897ff02560?w=600&q=80",
                category = "Technology",
                hashtags = listOf("#techpakistan", "#smartphone", "#budgetphone", "#urdureviews"),
                likesCount = 34500,
                commentsCount = 890,
                sharesCount = 3100,
                viewsCount = 189000,
                musicTitle = "Tech Ambient Beat - Lo-Fi Chill",
                isLiked = false,
                isFollowing = false
            ),
            Video(
                id = "v3",
                creatorId = "creator_3",
                creatorName = "Karachi Food Street",
                creatorHandle = "street_flavors_pk",
                creatorAvatar = "https://images.unsplash.com/photo-1506794778202-cad84cf45f1d?w=400&q=80",
                title = "Famous Burns Road Nihari & crispy Roghani Naan at midnight! The aroma is unbelievable 🍲🔥",
                description = "Karachi midnight food street vlog. Pure heritage food culture.",
                videoUrl = "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ForBiggerFun.mp4",
                thumbnailUrl = "https://images.unsplash.com/photo-1589302168068-964664d93dc0?w=600&q=80",
                category = "Food & Travel",
                hashtags = listOf("#burnsroad", "#karachifood", "#nihari", "#pakistanicuisine"),
                likesCount = 112000,
                commentsCount = 4520,
                sharesCount = 18400,
                viewsCount = 980000,
                musicTitle = "Pakistani Sufi Instrumental Flute",
                isLiked = true,
                isFollowing = false
            ),
            Video(
                id = "v4",
                creatorId = "creator_4",
                creatorName = "Northern Wonders 🏔️",
                creatorHandle = "hunza_explorer",
                creatorAvatar = "https://images.unsplash.com/photo-1522075469751-3a6694fb2f61?w=400&q=80",
                title = "Autumn in Hunza Valley! Golden apricot trees and majestic Rakaposhi peak snow 🍂❄️",
                description = "Spectacular 4K drone view of Passu Cones and Attabad Lake in Gilgit-Baltistan.",
                videoUrl = "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ForBiggerJoyBlazes.mp4",
                thumbnailUrl = "https://images.unsplash.com/photo-1506744038136-46273834b3fb?w=600&q=80",
                category = "Nature & Travel",
                hashtags = listOf("#hunzavalley", "#pakistantour", "#gilgit", "#rakaposhi"),
                likesCount = 67800,
                commentsCount = 1940,
                sharesCount = 9200,
                viewsCount = 412000,
                musicTitle = "Melodic Rubab & Acoustic Strings",
                isLiked = false,
                isFollowing = true
            ),
            Video(
                id = "v5",
                creatorId = "creator_5",
                creatorName = "Earning Mastery Pakistan",
                creatorHandle = "digital_earning_pk",
                creatorAvatar = "https://images.unsplash.com/photo-1535713875002-d1d0cf377fde?w=400&q=80",
                title = "How to reach 500,000 followers and unlock Creator Monetization on MR JATOI VIDEO 📈💰",
                description = "Proven tips for high watch-time, verified authentic views and community compliance.",
                videoUrl = "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/TearsOfSteel.mp4",
                thumbnailUrl = "https://images.unsplash.com/photo-1553729459-efe14ef6055d?w=600&q=80",
                category = "Earning Guide",
                hashtags = listOf("#creatormonetization", "#earnfromhome", "#mrjatoi", "#jazzcashpayout"),
                likesCount = 95400,
                commentsCount = 3780,
                sharesCount = 16200,
                viewsCount = 630000,
                musicTitle = "Inspirational Golden Waves - Background Score",
                isLiked = false,
                isFollowing = false
            )
        )
    }

    private fun generateInitialComments(): Map<String, List<Comment>> {
        return mapOf(
            "v_aistudio_tutorial" to listOf(
                Comment("c_ai1", "v_aistudio_tutorial", "u_creator1", "Danish Ali", "https://images.unsplash.com/photo-1535713875002-d1d0cf377fde?w=100&q=80", "Brilliant video Jatoi Bhai! I built my first Android app in just 15 minutes using Google AI Studio without writing a single line of code! 🔥", "5m ago", 128, true),
                Comment("c_ai2", "v_aistudio_tutorial", "u_creator2", "Fatima Noor", "https://images.unsplash.com/photo-1494790108377-be9c29b29330?w=100&q=80", "The prompt engineering tips are golden. So helpful for beginners and YouTubers in Pakistan.", "18m ago", 76, true),
                Comment("c_ai3", "v_aistudio_tutorial", "u_creator3", "Kamran Tech", "https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d?w=100&q=80", "Testing on mobile using prompt refinement is a game changer! Thanks for explaining free limits too.", "42m ago", 49, false)
            ),
            "v1" to listOf(
                Comment("c1", "v1", "u1", "Hamza Khan", "https://images.unsplash.com/photo-1500648767791-00dcc994a43e?w=100&q=80", "Zabardast initiative by Musadiq Jatoi! Pakistan creators really needed this platform.", "10m ago", 84, true),
                Comment("c2", "v1", "u2", "Ayesha Malik", "https://images.unsplash.com/photo-1494790108377-be9c29b29330?w=100&q=80", "The video player is so smooth and black-gold theme looks very luxurious! 🌟", "25m ago", 42, false),
                Comment("c3", "v1", "u3", "Bilal Ahmed", "https://images.unsplash.com/photo-1472099645785-5658abf4ff4e?w=100&q=80", "Just received 25 coins for my daily streak! Easy to withdraw to JazzCash.", "1h ago", 19, false)
            ),
            "v2" to listOf(
                Comment("c4", "v2", "u4", "Usman Tariq", "https://images.unsplash.com/photo-1519085360753-af0119f7cbe7?w=100&q=80", "Best honest review! Waiting for the camera night test comparison.", "2h ago", 15, false)
            )
        )
    }

    private fun generateInitialTransactions(): List<WalletTransaction> {
        return listOf(
            WalletTransaction(
                id = "TX-8921",
                type = TransactionType.DAILY_STREAK,
                coins = 25,
                pkrAmount = 12.50,
                status = TransactionStatus.APPROVED,
                referenceNote = "Day 4 Daily Streak Bonus"
            ),
            WalletTransaction(
                id = "TX-8804",
                type = TransactionType.WATCH_REWARD,
                coins = 20,
                pkrAmount = 10.00,
                status = TransactionStatus.APPROVED,
                referenceNote = "Watch-to-Earn verified session"
            ),
            WalletTransaction(
                id = "TX-8742",
                type = TransactionType.REFERRAL_BONUS,
                coins = 50,
                pkrAmount = 25.00,
                status = TransactionStatus.APPROVED,
                referenceNote = "Referral code activation by @usman_lahore"
            ),
            WalletTransaction(
                id = "WD-1029",
                type = TransactionType.WITHDRAWAL,
                coins = 1000,
                pkrAmount = 500.00,
                status = TransactionStatus.PENDING,
                paymentMethod = PaymentMethod.JAZZCASH,
                accountDetails = "Ali Raza | 0312-9876543",
                referenceNote = "Server verification in progress. Awaiting admin approval."
            )
        )
    }

    private fun generateInitialMonetizationApps(): List<MonetizationApplication> {
        return listOf(
            MonetizationApplication(
                id = "APP-9821",
                userId = "user_current",
                userName = "Ali Raza",
                userHandle = "@ali_creator",
                followersCount = 512_000, // Eligible!
                verifiedViews = 135_000,
                watchHours = 4_250,
                strikesCount = 0,
                status = ApplicationStatus.PENDING,
                adminReviewNotes = "Verification queued: checking subscriber retention and authentic views."
            ),
            MonetizationApplication(
                id = "APP-9750",
                userId = "creator_hunza",
                userName = "Hunza Explorer",
                userHandle = "@hunza_explorer",
                followersCount = 680_000,
                verifiedViews = 490_000,
                watchHours = 12_800,
                strikesCount = 0,
                status = ApplicationStatus.APPROVED,
                adminReviewNotes = "Approved by Musadiq Hussain Jatoi on 10-Nov-2026."
            )
        )
    }

    private fun generateInitialReports(): List<ReportTicket> {
        return listOf(
            ReportTicket(
                id = "TKT-1084",
                videoId = "v_spam_sample",
                videoTitle = "Suspicious financial bot reel",
                creatorHandle = "@crypto_bot_99",
                reporterId = "user_491",
                reason = "Spam or Scam",
                isResolved = false,
                actionTaken = "Flagged by AI Scanner & pending Admin action"
            )
        )
    }
}
