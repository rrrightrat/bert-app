package com.example.data.auth

import android.content.Context
import android.content.SharedPreferences
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.credentials.exceptions.GetCredentialException
import com.example.R
import com.example.data.community.CommunityRepository
import com.example.data.model.CommunityUser
import com.google.android.libraries.identity.googleid.GetSignInWithGoogleOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.util.UUID

class AuthManager(
    private val context: Context,
    private val repository: CommunityRepository,
    private val scope: CoroutineScope
) {
    private val prefs: SharedPreferences =
        context.getSharedPreferences("bert_community_auth", Context.MODE_PRIVATE)
    private val credentialManager = CredentialManager.create(context)
    private val firebaseAuth: FirebaseAuth = FirebaseAuth.getInstance()

    private val _currentUser = MutableStateFlow<CommunityUser?>(null)
    val currentUser: StateFlow<CommunityUser?> = _currentUser.asStateFlow()

    private val _authError = MutableStateFlow<String?>(null)
    val authError: StateFlow<String?> = _authError.asStateFlow()

    init {
        // Restore existing user from prefs or Firebase Auth
        loadSavedUser()
    }

    private fun loadSavedUser() {
        val fbUser = firebaseAuth.currentUser
        if (fbUser != null) {
            val user = CommunityUser(
                uid = fbUser.uid,
                displayName = fbUser.displayName ?: "Redstoner_${fbUser.uid.take(4)}",
                email = fbUser.email ?: "",
                photoUrl = fbUser.photoUrl?.toString() ?: "",
                bio = "Bedrock redstone engineer",
                provider = "google"
            )
            _currentUser.value = user
            scope.launch(Dispatchers.IO) {
                repository.saveUserProfile(user)
            }
            return
        }

        // Check local saved user
        val uid = prefs.getString("uid", null)
        val displayName = prefs.getString("displayName", null)
        if (uid != null && displayName != null) {
            val user = CommunityUser(
                uid = uid,
                displayName = displayName,
                email = prefs.getString("email", "") ?: "",
                photoUrl = prefs.getString("photoUrl", "") ?: "",
                bio = prefs.getString("bio", "Bedrock redstone enthusiast") ?: "",
                provider = prefs.getString("provider", "manual") ?: "manual",
                createdAt = prefs.getLong("createdAt", System.currentTimeMillis())
            )
            _currentUser.value = user
        }
    }

    /**
     * Sign In with Google via Credential Manager
     */
    fun signInWithGoogle(activityContext: Context, onComplete: (Boolean) -> Unit) {
        scope.launch {
            try {
                val serverClientId = context.getString(R.string.default_web_client_id)
                val googleIdOption = GetSignInWithGoogleOption.Builder(serverClientId)
                    .build()

                val request = GetCredentialRequest.Builder()
                    .addCredentialOption(googleIdOption)
                    .build()

                val result = credentialManager.getCredential(
                    request = request,
                    context = activityContext
                )

                val credential = result.credential
                if (credential is CustomCredential &&
                    credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL
                ) {
                    val googleIdTokenCredential = GoogleIdTokenCredential.createFrom(credential.data)
                    val idToken = googleIdTokenCredential.idToken

                    val authCredential = GoogleAuthProvider.getCredential(idToken, null)
                    val authResult = firebaseAuth.signInWithCredential(authCredential).await()
                    val fbUser = authResult.user

                    if (fbUser != null) {
                        val communityUser = CommunityUser(
                            uid = fbUser.uid,
                            displayName = fbUser.displayName ?: googleIdTokenCredential.displayName ?: "Redstoner",
                            email = fbUser.email ?: googleIdTokenCredential.id,
                            photoUrl = fbUser.photoUrl?.toString() ?: googleIdTokenCredential.profilePictureUri?.toString() ?: "",
                            bio = "Bedrock redstone engineer",
                            provider = "google"
                        )
                        saveUserSession(communityUser)
                        repository.saveUserProfile(communityUser)
                        onComplete(true)
                        return@launch
                    }
                }
                onComplete(false)
            } catch (e: GetCredentialException) {
                // If Google Play Services is unavailable in this environment, fallback to demo/manual
                _authError.value = "Google Sign-In canceled or unavailable: ${e.message}"
                onComplete(false)
            } catch (e: Exception) {
                _authError.value = "Sign in failed: ${e.message}"
                onComplete(false)
            }
        }
    }

    /**
     * Manual Sign-Up / Sign-In with username, email, avatar and bio
     */
    fun manualSignUp(
        username: String,
        email: String,
        bio: String,
        avatarEmoji: String,
        onComplete: (Boolean) -> Unit
    ) {
        scope.launch {
            try {
                val existingUid = prefs.getString("uid", null) ?: "user_${UUID.randomUUID().toString().take(8)}"
                val user = CommunityUser(
                    uid = existingUid,
                    displayName = username.trim().ifEmpty { "RedstoneCrafter" },
                    email = email.trim(),
                    photoUrl = avatarEmoji,
                    bio = bio.trim().ifEmpty { "MCBE redstone circuit builder" },
                    provider = "manual",
                    createdAt = System.currentTimeMillis()
                )
                saveUserSession(user)
                repository.saveUserProfile(user)
                onComplete(true)
            } catch (e: Exception) {
                _authError.value = e.message
                onComplete(false)
            }
        }
    }

    /**
     * Social login handler (Apple / Facebook / Demo)
     */
    fun socialLogin(provider: String, onComplete: (Boolean) -> Unit) {
        scope.launch {
            try {
                val providerName = provider.replaceFirstChar { it.uppercase() }
                val uid = "${provider.lowercase()}_${UUID.randomUUID().toString().take(8)}"
                val user = CommunityUser(
                    uid = uid,
                    displayName = "$providerName User",
                    email = "user@$provider.com",
                    photoUrl = when (provider.lowercase()) {
                        "apple" -> "🍎"
                        "facebook" -> "📘"
                        else -> "⭐"
                    },
                    bio = "Logged in with $providerName",
                    provider = provider.lowercase()
                )
                saveUserSession(user)
                repository.saveUserProfile(user)
                onComplete(true)
            } catch (e: Exception) {
                _authError.value = e.message
                onComplete(false)
            }
        }
    }

    /**
     * Update user profile info
     */
    fun updateProfile(displayName: String, bio: String, photoUrl: String, onComplete: (Boolean) -> Unit) {
        val current = _currentUser.value ?: return
        val updated = current.copy(
            displayName = displayName.trim().ifEmpty { current.displayName },
            bio = bio.trim(),
            photoUrl = photoUrl
        )
        scope.launch {
            saveUserSession(updated)
            repository.saveUserProfile(updated)
            onComplete(true)
        }
    }

    fun signOut() {
        firebaseAuth.signOut()
        prefs.edit().clear().apply()
        _currentUser.value = null
    }

    private fun saveUserSession(user: CommunityUser) {
        prefs.edit()
            .putString("uid", user.uid)
            .putString("displayName", user.displayName)
            .putString("email", user.email)
            .putString("photoUrl", user.photoUrl)
            .putString("bio", user.bio)
            .putString("provider", user.provider)
            .putLong("createdAt", user.createdAt)
            .apply()
        _currentUser.value = user
    }
}
