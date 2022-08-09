package com.gscapin.blogger.data.remote.auth

import android.graphics.Bitmap
import android.net.Uri
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.userProfileChangeRequest
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.gscapin.blogger.data.model.User
import kotlinx.coroutines.tasks.await
import java.io.ByteArrayOutputStream
import javax.inject.Inject

class AuthDataSource @Inject constructor() {

    suspend fun signIn(email: String, password: String): FirebaseUser? {
        val authResult =
            FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password).await()
        return authResult.user
    }

    suspend fun signUp(email: String, password: String, user: String): FirebaseUser? {
        val authResult =
            FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password).await()

        authResult.user?.let {
            FirebaseFirestore.getInstance().collection("users").document(it.uid).set(
                User(username = user, email = email, userPhotoUrl = "")
            ).await()
        }
        return authResult.user
    }

    suspend fun getUsername(): MutableMap<String, Any>? {
        val currentUser = FirebaseAuth.getInstance().currentUser
        val userRef =
            FirebaseFirestore.getInstance().collection("users").document(currentUser!!.uid).get()
                .await()
        return userRef.data
    }

    suspend fun updateProfile(imageBitMap: Bitmap) {
        val user = FirebaseAuth.getInstance().currentUser

        val username =
            user?.uid?.let {
                FirebaseFirestore.getInstance().collection("users").document(it).get()
                    .await().data?.get("username").toString()
            }

        val imageRef = FirebaseStorage.getInstance().reference.child("${user?.uid}/profile_picture")
        val baos = ByteArrayOutputStream()

        imageBitMap.compress(Bitmap.CompressFormat.JPEG, 100, baos)

        val downloadUrl =
            imageRef.putBytes(baos.toByteArray()).await().storage.downloadUrl.await().toString()

        val profileUpdates = userProfileChangeRequest {
            displayName = username
            photoUri = Uri.parse(downloadUrl)
        }

        user?.updateProfile(profileUpdates)?.await()

        var userDb = FirebaseFirestore.getInstance().collection("users").document(user!!.uid).get().await()
        userDb.toObject(User::class.java).let { userFirebase ->
            userFirebase?.apply {
                userPhotoUrl = downloadUrl
            }
        }
        // falta saber si se guarda, sino set(userDb, SetOptions.merge())
    }
}