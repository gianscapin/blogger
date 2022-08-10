package com.gscapin.blogger.domain.post

import android.graphics.Bitmap
import com.gscapin.blogger.data.remote.post.PostDataSource
import javax.inject.Inject

class PostRepoImpl @Inject constructor(private val dataSource: PostDataSource): PostRepo {
    override suspend fun uploadPost(description: String, image: Bitmap) = dataSource.uploadPost(description, image)
}