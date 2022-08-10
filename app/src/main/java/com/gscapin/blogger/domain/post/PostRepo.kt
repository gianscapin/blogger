package com.gscapin.blogger.domain.post

import android.graphics.Bitmap
import javax.inject.Singleton

@Singleton
interface PostRepo {
    suspend fun uploadPost(description: String, image: Bitmap)
}