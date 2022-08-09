package com.gscapin.blogger.domain.profile

import com.gscapin.blogger.data.model.User
import com.gscapin.blogger.data.remote.profile.ProfileDataSource
import javax.inject.Inject

class ProfileRepoImpl @Inject constructor(private val dataSource: ProfileDataSource) : ProfileRepo {
    override suspend fun getInfoUser(): User = dataSource.getInfoUser()
}