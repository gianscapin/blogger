package com.gscapin.blogger.domain.home

import com.gscapin.blogger.core.Result
import com.gscapin.blogger.data.model.Post
import com.gscapin.blogger.data.remote.home.HomeDataSource
import javax.inject.Inject

class HomeRepoImpl @Inject constructor(private val dataSource: HomeDataSource): HomeRepo {
    override suspend fun getLastestPosts(): Result<List<Post>> = dataSource.getLastestPost()

    override suspend fun registerLike(postId: String, liked: Boolean) = dataSource.registerLike(postId, liked)
}