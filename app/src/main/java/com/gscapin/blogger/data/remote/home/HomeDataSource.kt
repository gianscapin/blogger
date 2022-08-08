package com.gscapin.blogger.data.remote.home

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.*
import com.gscapin.blogger.core.Result
import com.gscapin.blogger.data.model.Post
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import javax.inject.Inject
import kotlin.Exception

class HomeDataSource @Inject constructor() {


    suspend fun getLastestPost(): Result<List<Post>> {
        val postList = mutableListOf<Post>()
        withContext(Dispatchers.IO) {
            val querySnapshot = FirebaseFirestore.getInstance().collection("posts")
                .orderBy("createdAt", Query.Direction.DESCENDING).get().await()

            for (post in querySnapshot.documents) {

                post.toObject(Post::class.java).let { postFromFirebase ->

                    val isLikedByMe = FirebaseAuth.getInstance().currentUser?.let {
                        isPostLikedByMe(post.id, it.uid)
                    }

                    postFromFirebase?.apply {
                        createdAt = post.getTimestamp(
                            "createdAt",
                            DocumentSnapshot.ServerTimestampBehavior.ESTIMATE
                        )?.toDate()
                        id = post.id
                        if (isLikedByMe != null) {
                            liked = isLikedByMe
                        }
                        postList.add(postFromFirebase)
                    }
                }


            }
        }
        return Result.Success(postList)
    }

    private suspend fun isPostLikedByMe(postId: String, uid: String): Boolean {
        val posts =
            FirebaseFirestore.getInstance().collection("postsLikes").document(postId).get().await()

        if (!posts.exists()) {
            return false
        }

        val likeArray: List<String> = posts.get("likes") as List<String>

        return likeArray.contains(uid)
    }

    fun registerLike(postId: String, liked: Boolean) {
        val increment = FieldValue.increment(1)
        val decrement = FieldValue.increment(-1)

        val uid = FirebaseAuth.getInstance().currentUser?.uid

        val postRef = FirebaseFirestore.getInstance().collection("posts").document(postId)
        val postLikesRef = FirebaseFirestore.getInstance().collection("postsLikes").document(postId)



        val db = FirebaseFirestore.getInstance()

        db.runTransaction { transaction ->
            val snapshot = transaction.get(postRef)
            val likeCount = snapshot.getLong("likes")
            if(likeCount != null){
                if(liked){
                    if(transaction.get(postLikesRef).exists()){
                        transaction.update(postLikesRef, "likes", FieldValue.arrayUnion(uid))
                    }else{
                        transaction.set(postLikesRef, hashMapOf("likes" to arrayListOf(uid)), SetOptions.merge())
                    }
                    transaction.update(postRef, "likes", increment)
                }else{
                    transaction.update(postRef, "likes", decrement)
                    transaction.update(postLikesRef, "likes", FieldValue.arrayRemove(uid))
                }
            }
        }.addOnFailureListener{
            throw Exception(it.message)
        }
    }
}