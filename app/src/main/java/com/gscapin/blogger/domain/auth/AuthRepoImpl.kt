package com.gscapin.blogger.domain.auth

import android.graphics.Bitmap
import com.google.firebase.auth.FirebaseUser
import com.gscapin.blogger.data.remote.auth.AuthDataSource
import javax.inject.Inject

class AuthRepoImpl @Inject constructor(private val dataSource: AuthDataSource): AuthRepo{
    override suspend fun signIn(email: String, password: String): FirebaseUser? = dataSource.signIn(email, password)

    override suspend fun signUp(email: String, password: String, username: String): FirebaseUser? = dataSource.signUp(email, password, username)

    override suspend fun updateProfile(imageBitmap: Bitmap) = dataSource.updateProfile(imageBitmap)

}