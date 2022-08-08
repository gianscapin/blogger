package com.gscapin.blogger.domain.home

import com.gscapin.blogger.core.Result
import com.gscapin.blogger.data.model.Post
import javax.inject.Singleton

@Singleton
interface HomeRepo {
    suspend fun getLastestPosts(): Result<List<Post>>
    suspend fun registerLike(postId: String, liked: Boolean): Unit
}