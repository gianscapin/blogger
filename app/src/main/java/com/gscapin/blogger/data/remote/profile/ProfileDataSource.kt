package com.gscapin.blogger.data.remote.profile

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.gscapin.blogger.core.Result
import com.gscapin.blogger.data.model.User
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class ProfileDataSource @Inject constructor() {

    suspend fun getInfoUser(): User {
        val userId = FirebaseAuth.getInstance().currentUser!!.uid

        val user =
            FirebaseFirestore.getInstance().collection("users").document(userId).get().await()

        return user.toObject(User::class.java)!!

    }
}