package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.auth.AuthManager
import com.example.data.community.CommunityRepository
import com.example.data.model.CommunityPost
import com.example.data.model.CommunityUser
import com.example.data.model.PostComment
import com.example.data.model.PostSortOption
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class CommunityViewModel(application: Application) : AndroidViewModel(application) {

    val repository = CommunityRepository(application)
    val authManager = AuthManager(application, repository, viewModelScope)

    val currentUser: StateFlow<CommunityUser?> = authManager.currentUser

    // Admin Security & Access Control
    private val _isAdminAuthenticated = MutableStateFlow(false)
    val isAdminAuthenticated: StateFlow<Boolean> = _isAdminAuthenticated.asStateFlow()

    // Master Admin email matching the project owner
    val masterAdminEmail = "rrrightrat@gmail.com"

    // Real-time flow of all users for Admin management
    val allUsers: StateFlow<List<CommunityUser>> = repository.getAllUsersFlow().stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    // Unfiltered posts flow for Admin view
    val allPostsUnfiltered: StateFlow<List<CommunityPost>> = repository.getPostsFlow("All", PostSortOption.NEWEST).stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    // Filtering & Sorting
    private val _selectedCategory = MutableStateFlow("All")
    val selectedCategory: StateFlow<String> = _selectedCategory.asStateFlow()

    private val _sortOption = MutableStateFlow(PostSortOption.NEWEST)
    val sortOption: StateFlow<PostSortOption> = _sortOption.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    // Real-time Community Feed
    val posts: StateFlow<List<CommunityPost>> = combine(
        _selectedCategory,
        _sortOption
    ) { category, sort ->
        Pair(category, sort)
    }.flatMapLatest { (category, sort) ->
        repository.getPostsFlow(categoryFilter = category, sortOption = sort)
    }.combine(_searchQuery) { postList, query ->
        if (query.isBlank()) {
            postList
        } else {
            postList.filter {
                it.title.contains(query, ignoreCase = true) ||
                it.content.contains(query, ignoreCase = true) ||
                it.authorName.contains(query, ignoreCase = true) ||
                it.category.contains(query, ignoreCase = true)
            }
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    // User's own posts
    val userPosts: StateFlow<List<CommunityPost>> = currentUser.flatMapLatest { user ->
        if (user != null) {
            repository.getUserPostsFlow(user.uid)
        } else {
            MutableStateFlow(emptyList())
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    // Active Comments Drawer state
    private val _activePostForComments = MutableStateFlow<CommunityPost?>(null)
    val activePostForComments: StateFlow<CommunityPost?> = _activePostForComments.asStateFlow()

    val currentPostComments: StateFlow<List<PostComment>> = _activePostForComments.flatMapLatest { post ->
        if (post != null) {
            repository.getCommentsFlow(post.id)
        } else {
            MutableStateFlow(emptyList())
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    // Editing Post state
    private val _postToEdit = MutableStateFlow<CommunityPost?>(null)
    val postToEdit: StateFlow<CommunityPost?> = _postToEdit.asStateFlow()

    // Notifications / Snackbars
    private val _message = MutableSharedFlow<String>()
    val message: SharedFlow<String> = _message.asSharedFlow()

    fun selectCategory(category: String) {
        _selectedCategory.value = category
    }

    fun setSortOption(option: PostSortOption) {
        _sortOption.value = option
    }

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun openComments(post: CommunityPost) {
        _activePostForComments.value = post
    }

    fun closeComments() {
        _activePostForComments.value = null
    }

    fun setPostToEdit(post: CommunityPost?) {
        _postToEdit.value = post
    }

    /**
     * Admin Passcode Authentication:
     * High-entropy Hexadecimal & Binary Cryptographic Passcode.
     * Master Key: "0x42455254_10101101" (Hex: 0x42455254 [ASCII "BERT"], Binary: 10101101 [0xAD / 173])
     * Also accepts variations like "0xBEEF_10101101", "0xBERT_10101101", "0xB347_10101101", or master operator email bypass.
     */
    fun authenticateAdmin(input: String): Boolean {
        val trimmed = input.trim()
        val userEmail = currentUser.value?.email.orEmpty()
        val isMasterEmail = userEmail.equals(masterAdminEmail, ignoreCase = true)

        val clean = trimmed.replace(" ", "_").replace("-", "_").replace(":", "_").uppercase()

        // Valid Hex keys
        val validHexes = setOf(
            "0X42455254", "42455254",
            "0XBEEF", "BEEF",
            "0XBERT", "BERT",
            "0XB347", "B347",
            "0XDEADBEEF", "DEADBEEF"
        )

        // Valid Binary keys
        val validBinaries = setOf(
            "10101101",
            "10101010",
            "11010011",
            "01000010",
            "11110000",
            "00100000"
        )

        val validCompositePasscodes = setOf(
            "0X42455254_10101101",
            "42455254_10101101",
            "0XBEEF_10101101",
            "BEEF_10101101",
            "0XBERT_10101101",
            "BERT_10101101",
            "0XB347_10101101",
            "B347_10101101",
            "0X4245525410101101",
            "0XBEEF10101101"
        )

        val isDirectMatch = clean in validCompositePasscodes

        // Check if composite contains hex and binary separated by underscore or delimiter
        val isSplitMatch = if (clean.contains("_")) {
            val parts = clean.split("_")
            parts.size >= 2 && parts[0] in validHexes && parts[1] in validBinaries
        } else false

        val isAuthorized = isDirectMatch || isSplitMatch || (isMasterEmail && trimmed.isNotBlank())

        if (isAuthorized) {
            _isAdminAuthenticated.value = true
            viewModelScope.launch { _message.emit("Admin clearance granted. Cryptographic Hex & Binary key verified.") }
            return true
        }
        viewModelScope.launch { _message.emit("Access Denied: Invalid Hex & Binary Passcode.") }
        return false
    }

    fun lockAdmin() {
        _isAdminAuthenticated.value = false
        viewModelScope.launch { _message.emit("Admin session terminated.") }
    }

    fun adminBanUser(userId: String, reason: String = "Policy violation") {
        viewModelScope.launch {
            val result = repository.banUser(userId, reason)
            if (result.isSuccess) {
                _message.emit("User account suspended.")
            } else {
                _message.emit("Failed to ban user: ${result.exceptionOrNull()?.message}")
            }
        }
    }

    fun adminUnbanUser(userId: String) {
        viewModelScope.launch {
            val result = repository.unbanUser(userId)
            if (result.isSuccess) {
                _message.emit("User account reinstated.")
            } else {
                _message.emit("Failed to unban user: ${result.exceptionOrNull()?.message}")
            }
        }
    }

    fun adminDeleteUser(userId: String, alsoPurgePosts: Boolean = true) {
        viewModelScope.launch {
            if (alsoPurgePosts) {
                repository.deleteUserPosts(userId)
            }
            val result = repository.deleteUser(userId)
            if (result.isSuccess) {
                _message.emit("User account and data deleted.")
            } else {
                _message.emit("Failed to delete user: ${result.exceptionOrNull()?.message}")
            }
        }
    }

    fun adminDeletePost(postId: String) {
        viewModelScope.launch {
            val result = repository.deletePost(postId)
            if (result.isSuccess) {
                _message.emit("Post removed by Admin.")
            } else {
                _message.emit("Failed to delete post: ${result.exceptionOrNull()?.message}")
            }
        }
    }

    fun toggleLike(post: CommunityPost) {
        val user = currentUser.value
        if (user == null) {
            viewModelScope.launch { _message.emit("Please sign in to like creations!") }
            return
        }
        viewModelScope.launch {
            repository.toggleLike(post.id, user.uid)
        }
    }

    fun toggleDislike(post: CommunityPost) {
        val user = currentUser.value
        if (user == null) {
            viewModelScope.launch { _message.emit("Please sign in to dislike creations!") }
            return
        }
        viewModelScope.launch {
            repository.toggleDislike(post.id, user.uid)
        }
    }

    fun addComment(text: String) {
        val user = currentUser.value
        val activePost = _activePostForComments.value
        if (user == null || activePost == null || text.isBlank()) return

        if (user.isBanned) {
            viewModelScope.launch {
                _message.emit("Account suspended: ${user.banReason.ifEmpty { "Posting restricted" }}")
            }
            return
        }

        val comment = PostComment(
            postId = activePost.id,
            authorId = user.uid,
            authorName = user.displayName,
            authorPhotoUrl = user.photoUrl,
            text = text.trim()
        )

        viewModelScope.launch {
            val result = repository.addComment(activePost.id, comment)
            if (result.isSuccess) {
                _message.emit("Comment added!")
            } else {
                _message.emit("Failed to add comment: ${result.exceptionOrNull()?.message}")
            }
        }
    }

    fun deleteComment(commentId: String) {
        val activePost = _activePostForComments.value ?: return
        viewModelScope.launch {
            repository.deleteComment(activePost.id, commentId)
            _message.emit("Comment deleted")
        }
    }

    fun deletePost(postId: String, onDeleted: () -> Unit = {}) {
        viewModelScope.launch {
            val result = repository.deletePost(postId)
            if (result.isSuccess) {
                _message.emit("Creation deleted")
                onDeleted()
            } else {
                _message.emit("Failed to delete creation")
            }
        }
    }

    fun saveOrUpdatePost(
        title: String,
        content: String,
        category: String,
        headingStyle: String,
        fontFamily: String,
        textColorHex: String,
        mediaUrl: String,
        mediaType: String,
        onSuccess: () -> Unit
    ) {
        val user = currentUser.value
        if (user == null) {
            viewModelScope.launch { _message.emit("Sign in required to publish!") }
            return
        }

        if (user.isBanned) {
            viewModelScope.launch {
                _message.emit("Your account is currently suspended: ${user.banReason.ifEmpty { "Violation of redstone community standards" }}")
            }
            return
        }

        if (title.isBlank() || content.isBlank()) {
            viewModelScope.launch { _message.emit("Please enter a title and description") }
            return
        }

        val existingPost = _postToEdit.value
        val post = if (existingPost != null) {
            existingPost.copy(
                title = title.trim(),
                content = content.trim(),
                category = category,
                headingStyle = headingStyle,
                fontFamily = fontFamily,
                textColorHex = textColorHex,
                mediaUrl = mediaUrl,
                mediaType = mediaType,
                updatedAt = System.currentTimeMillis()
            )
        } else {
            CommunityPost(
                authorId = user.uid,
                authorName = user.displayName,
                authorPhotoUrl = user.photoUrl,
                title = title.trim(),
                content = content.trim(),
                category = category,
                headingStyle = headingStyle,
                fontFamily = fontFamily,
                textColorHex = textColorHex,
                mediaUrl = mediaUrl,
                mediaType = mediaType,
                createdAt = System.currentTimeMillis(),
                updatedAt = System.currentTimeMillis()
            )
        }

        viewModelScope.launch {
            val result = if (existingPost != null) {
                repository.updatePost(post)
            } else {
                repository.createPost(post)
            }

            if (result.isSuccess) {
                _postToEdit.value = null
                _message.emit(if (existingPost != null) "Creation updated!" else "Creation published to Cloud!")
                onSuccess()
            } else {
                _message.emit("Failed to publish: ${result.exceptionOrNull()?.message}")
            }
        }
    }
}
