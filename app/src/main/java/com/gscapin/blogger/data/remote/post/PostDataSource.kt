package com.gscapin.blogger.data.remote.post

import android.graphics.Bitmap
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.gscapin.blogger.data.model.Post
import com.gscapin.blogger.data.model.Poster
import com.gscapin.blogger.data.model.User
import kotlinx.coroutines.tasks.await
import java.io.ByteArrayOutputStream
import java.util.*
import javax.inject.Inject

class PostDataSource @Inject constructor() {

    suspend fun uploadPost(description: String, image: Bitmap) {
        val user = FirebaseAuth.getInstance().currentUser
        val randomNamePost = UUID.randomUUID().toString()

        val username = user?.uid?.let {
            FirebaseFirestore.getInstance().collection("users").document(it).get()
                .await().toObject(User::class.java)
        }

        val imageRef =
            FirebaseStorage.getInstance().reference.child("${user?.uid}/posts/$randomNamePost")

        val baos = ByteArrayOutputStream()

        image.compress(Bitmap.CompressFormat.JPEG, 100, baos)

        val downloadUrl =
            imageRef.putBytes(baos.toByteArray()).await().storage.downloadUrl.await().toString()

        FirebaseFirestore.getInstance().collection("posts").add(
            Post(
                postImage = downloadUrl,
                postDescription = description,
                poster = username?.username?.let {
                    Poster(
                        username = it,
                        uid = user.uid,
                        profilePicture = user.photoUrl.toString()
                    )
                }
            )
        )
    }
}