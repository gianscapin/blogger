package com.gscapin.blogger.domain.auth

import android.graphics.Bitmap
import com.google.firebase.auth.FirebaseUser
import javax.inject.Singleton

@Singleton
interface AuthRepo{
    suspend fun signIn(email: String, password: String): FirebaseUser?
    suspend fun signUp(email: String, password: String, username: String): FirebaseUser?
    suspend fun updateProfile(imageBitmap: Bitmap): Unit
}