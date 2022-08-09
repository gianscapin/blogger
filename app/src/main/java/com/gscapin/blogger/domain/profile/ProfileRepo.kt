package com.gscapin.blogger.domain.profile

import com.gscapin.blogger.data.model.User
import javax.inject.Singleton

@Singleton
interface ProfileRepo {
    suspend fun getInfoUser(): User
}