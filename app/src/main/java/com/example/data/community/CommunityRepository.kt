package com.example.data.community

import android.content.Context
import com.example.R
import com.example.data.model.CommunityPost
import com.example.data.model.CommunityUser
import com.example.data.model.PostComment
import com.example.data.model.PostSortOption
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.SetOptions
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

class CommunityRepository(private val db: FirebaseFirestore) {

    constructor(context: Context) : this(
        FirebaseFirestore.getInstance(
            context.applicationContext.getString(R.string.firestore_database_id)
        )
    )

    private val postsCollection = db.collection("posts")
    private val usersCollection = db.collection("users")

    /**
     * Real-time flow of posts with optional category filter and sorting
     */
    fun getPostsFlow(categoryFilter: String? = null, sortOption: PostSortOption = PostSortOption.NEWEST): Flow<List<CommunityPost>> = callbackFlow {
        var query: Query = postsCollection

        if (!categoryFilter.isNullOrBlank() && categoryFilter != "All") {
            query = query.whereEqualTo("category", categoryFilter)
        }

        query = when (sortOption) {
            PostSortOption.NEWEST -> query.orderBy("createdAt", Query.Direction.DESCENDING)
            PostSortOption.MOST_LIKED -> query.orderBy("likesCount", Query.Direction.DESCENDING)
            PostSortOption.MOST_DISCUSSED -> query.orderBy("commentsCount", Query.Direction.DESCENDING)
        }

        val listener = query.addSnapshotListener { snapshot, error ->
            if (error != null) {
                // If index or query error, fall back to simple snapshot
                trySend(emptyList())
                return@addSnapshotListener
            }
            if (snapshot != null) {
                val posts = snapshot.documents.mapNotNull { doc ->
                    try {
                        doc.toObject(CommunityPost::class.java)?.copy(id = doc.id)
                    } catch (e: Exception) {
                        null
                    }
                }
                trySend(posts)
            }
        }

        awaitClose { listener.remove() }
    }

    /**
     * Real-time flow of posts authored by a specific user
     */
    fun getUserPostsFlow(userId: String): Flow<List<CommunityPost>> = callbackFlow {
        val query = postsCollection.whereEqualTo("authorId", userId)
        val listener = query.addSnapshotListener { snapshot, error ->
            if (error != null) {
                trySend(emptyList())
                return@addSnapshotListener
            }
            if (snapshot != null) {
                val posts = snapshot.documents.mapNotNull { doc ->
                    try {
                        doc.toObject(CommunityPost::class.java)?.copy(id = doc.id)
                    } catch (e: Exception) {
                        null
                    }
                }.sortedByDescending { it.createdAt }
                trySend(posts)
            }
        }
        awaitClose { listener.remove() }
    }

    /**
     * Real-time flow of comments on a specific post
     */
    fun getCommentsFlow(postId: String): Flow<List<PostComment>> = callbackFlow {
        val commentsCol = postsCollection.document(postId).collection("comments")
        val listener = commentsCol.orderBy("createdAt", Query.Direction.ASCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    trySend(emptyList())
                    return@addSnapshotListener
                }
                if (snapshot != null) {
                    val comments = snapshot.documents.mapNotNull { doc ->
                        try {
                            doc.toObject(PostComment::class.java)?.copy(id = doc.id)
                        } catch (e: Exception) {
                            null
                        }
                    }
                    trySend(comments)
                }
            }
        awaitClose { listener.remove() }
    }

    /**
     * Create a new post in Firestore
     */
    suspend fun createPost(post: CommunityPost): Result<String> = runCatching {
        val docRef = if (post.id.isNotBlank()) {
            postsCollection.document(post.id)
        } else {
            postsCollection.document()
        }
        val dataMap = hashMapOf<String, Any?>(
            "id" to docRef.id,
            "authorId" to post.authorId,
            "authorName" to post.authorName,
            "authorPhotoUrl" to post.authorPhotoUrl,
            "title" to post.title,
            "content" to post.content,
            "category" to post.category,
            "headingStyle" to post.headingStyle,
            "fontFamily" to post.fontFamily,
            "textColorHex" to post.textColorHex,
            "mediaUrl" to post.mediaUrl,
            "mediaType" to post.mediaType,
            "likesCount" to post.likesCount,
            "dislikesCount" to post.dislikesCount,
            "likedUserIds" to post.likedUserIds,
            "dislikedUserIds" to post.dislikedUserIds,
            "commentsCount" to post.commentsCount,
            "createdAt" to post.createdAt,
            "updatedAt" to post.updatedAt
        )
        docRef.set(dataMap).await()
        docRef.id
    }

    /**
     * Update an existing post
     */
    suspend fun updatePost(post: CommunityPost): Result<Unit> = runCatching {
        require(post.id.isNotBlank()) { "Post ID must not be blank" }
        val dataMap = hashMapOf<String, Any?>(
            "id" to post.id,
            "authorId" to post.authorId,
            "authorName" to post.authorName,
            "authorPhotoUrl" to post.authorPhotoUrl,
            "title" to post.title,
            "content" to post.content,
            "category" to post.category,
            "headingStyle" to post.headingStyle,
            "fontFamily" to post.fontFamily,
            "textColorHex" to post.textColorHex,
            "mediaUrl" to post.mediaUrl,
            "mediaType" to post.mediaType,
            "likesCount" to post.likesCount,
            "dislikesCount" to post.dislikesCount,
            "likedUserIds" to post.likedUserIds,
            "dislikedUserIds" to post.dislikedUserIds,
            "commentsCount" to post.commentsCount,
            "createdAt" to post.createdAt,
            "updatedAt" to post.updatedAt
        )
        postsCollection.document(post.id).set(dataMap).await()
    }

    /**
     * Delete a post and its comments
     */
    suspend fun deletePost(postId: String): Result<Unit> = runCatching {
        postsCollection.document(postId).delete().await()
    }

    /**
     * Toggle like on a post for a given user
     */
    suspend fun toggleLike(postId: String, userId: String): Result<Unit> = runCatching {
        val docRef = postsCollection.document(postId)
        db.runTransaction { transaction ->
            val snapshot = transaction.get(docRef)
            val post = snapshot.toObject(CommunityPost::class.java) ?: return@runTransaction
            val currentLikes = post.likedUserIds.toMutableList()
            val currentDislikes = post.dislikedUserIds.toMutableList()

            if (currentLikes.contains(userId)) {
                currentLikes.remove(userId)
            } else {
                currentLikes.add(userId)
                currentDislikes.remove(userId)
            }

            transaction.update(
                docRef,
                mapOf(
                    "likedUserIds" to currentLikes,
                    "likesCount" to currentLikes.size,
                    "dislikedUserIds" to currentDislikes,
                    "dislikesCount" to currentDislikes.size
                )
            )
        }.await()
    }

    /**
     * Toggle dislike on a post for a given user
     */
    suspend fun toggleDislike(postId: String, userId: String): Result<Unit> = runCatching {
        val docRef = postsCollection.document(postId)
        db.runTransaction { transaction ->
            val snapshot = transaction.get(docRef)
            val post = snapshot.toObject(CommunityPost::class.java) ?: return@runTransaction
            val currentLikes = post.likedUserIds.toMutableList()
            val currentDislikes = post.dislikedUserIds.toMutableList()

            if (currentDislikes.contains(userId)) {
                currentDislikes.remove(userId)
            } else {
                currentDislikes.add(userId)
                currentLikes.remove(userId)
            }

            transaction.update(
                docRef,
                mapOf(
                    "likedUserIds" to currentLikes,
                    "likesCount" to currentLikes.size,
                    "dislikedUserIds" to currentDislikes,
                    "dislikesCount" to currentDislikes.size
                )
            )
        }.await()
    }

    /**
     * Add a comment to a post
     */
    suspend fun addComment(postId: String, comment: PostComment): Result<Unit> = runCatching {
        val postDoc = postsCollection.document(postId)
        val commentDoc = postDoc.collection("comments").document()
        val commentMap = hashMapOf<String, Any?>(
            "id" to commentDoc.id,
            "postId" to postId,
            "authorId" to comment.authorId,
            "authorName" to comment.authorName,
            "authorPhotoUrl" to comment.authorPhotoUrl,
            "text" to comment.text,
            "createdAt" to comment.createdAt
        )

        db.runBatch { batch ->
            batch.set(commentDoc, commentMap)
            batch.update(postDoc, "commentsCount", FieldValue.increment(1))
        }.await()
    }

    /**
     * Delete a comment from a post
     */
    suspend fun deleteComment(postId: String, commentId: String): Result<Unit> = runCatching {
        val postDoc = postsCollection.document(postId)
        val commentDoc = postDoc.collection("comments").document(commentId)

        db.runBatch { batch ->
            batch.delete(commentDoc)
            batch.update(postDoc, "commentsCount", FieldValue.increment(-1))
        }.await()
    }

    /**
     * Get or save user profile in Firestore
     */
    suspend fun getUserProfile(userId: String): CommunityUser? = runCatching {
        val doc = usersCollection.document(userId).get().await()
        doc.toObject(CommunityUser::class.java)
    }.getOrNull()

    suspend fun saveUserProfile(user: CommunityUser): Result<Unit> = runCatching {
        val userMap = hashMapOf<String, Any?>(
            "uid" to user.uid,
            "displayName" to user.displayName,
            "email" to user.email,
            "photoUrl" to user.photoUrl,
            "bio" to user.bio,
            "provider" to user.provider,
            "role" to user.role,
            "isBanned" to user.isBanned,
            "banReason" to user.banReason,
            "createdAt" to user.createdAt
        )
        usersCollection.document(user.uid).set(userMap, SetOptions.merge()).await()
    }

    /**
     * Admin: Real-time flow of all registered community users
     */
    fun getAllUsersFlow(): Flow<List<CommunityUser>> = callbackFlow {
        val listener = usersCollection.addSnapshotListener { snapshot, error ->
            if (error != null) {
                trySend(emptyList())
                return@addSnapshotListener
            }
            if (snapshot != null) {
                val users = snapshot.documents.mapNotNull { doc ->
                    try {
                        doc.toObject(CommunityUser::class.java)?.copy(uid = doc.id)
                    } catch (e: Exception) {
                        null
                    }
                }.sortedByDescending { it.createdAt }
                trySend(users)
            }
        }
        awaitClose { listener.remove() }
    }

    /**
     * Admin: Ban user account
     */
    suspend fun banUser(userId: String, reason: String = "Policy violation"): Result<Unit> = runCatching {
        usersCollection.document(userId).update(
            mapOf(
                "isBanned" to true,
                "banReason" to reason
            )
        ).await()
    }

    /**
     * Admin: Unban user account
     */
    suspend fun unbanUser(userId: String): Result<Unit> = runCatching {
        usersCollection.document(userId).update(
            mapOf(
                "isBanned" to false,
                "banReason" to ""
            )
        ).await()
    }

    /**
     * Admin: Delete user profile from cloud
     */
    suspend fun deleteUser(userId: String): Result<Unit> = runCatching {
        usersCollection.document(userId).delete().await()
    }

    /**
     * Admin: Purge all posts authored by a user
     */
    suspend fun deleteUserPosts(userId: String): Result<Int> = runCatching {
        val snapshot = postsCollection.whereEqualTo("authorId", userId).get().await()
        var count = 0
        db.runBatch { batch ->
            snapshot.documents.forEach { doc ->
                batch.delete(doc.reference)
                count++
            }
        }.await()
        count
    }
}
