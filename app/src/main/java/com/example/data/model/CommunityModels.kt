package com.example.data.model

import com.google.firebase.firestore.IgnoreExtraProperties

@IgnoreExtraProperties
data class CommunityPost(
    val id: String = "",
    val authorId: String = "",
    val authorName: String = "",
    val authorPhotoUrl: String = "",
    val title: String = "",
    val content: String = "",
    val category: String = "Contraptions",
    val headingStyle: String = "H1", // H1, H2, Normal, Code
    val fontFamily: String = "Default", // Default, Monospace, Serif
    val textColorHex: String = "#FFFFFF",
    val mediaUrl: String = "",
    val mediaType: String = "none", // none, image, video
    val likesCount: Int = 0,
    val dislikesCount: Int = 0,
    val likedUserIds: List<String> = emptyList(),
    val dislikedUserIds: List<String> = emptyList(),
    val commentsCount: Int = 0,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)

@IgnoreExtraProperties
data class PostComment(
    val id: String = "",
    val postId: String = "",
    val authorId: String = "",
    val authorName: String = "",
    val authorPhotoUrl: String = "",
    val text: String = "",
    val createdAt: Long = System.currentTimeMillis()
)

@IgnoreExtraProperties
data class CommunityUser(
    val uid: String = "",
    val displayName: String = "",
    val email: String = "",
    val photoUrl: String = "",
    val bio: String = "",
    val provider: String = "manual", // google, manual, apple, facebook
    val role: String = "user", // admin, user
    val isBanned: Boolean = false,
    val banReason: String = "",
    val createdAt: Long = System.currentTimeMillis()
)

enum class PostSortOption(val label: String) {
    NEWEST("Newest First"),
    MOST_LIKED("Most Liked"),
    MOST_DISCUSSED("Most Comments")
}
