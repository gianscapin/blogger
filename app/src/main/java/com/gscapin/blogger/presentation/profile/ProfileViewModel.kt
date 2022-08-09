package com.gscapin.blogger.presentation.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import com.gscapin.blogger.core.Result
import com.gscapin.blogger.domain.profile.ProfileRepoImpl
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(private val repository: ProfileRepoImpl): ViewModel() {

    fun getUserInfo() = liveData(Dispatchers.IO){
        emit(Result.Loading())

        try {
            emit(Result.Success(repository.getInfoUser()))
        }catch (e: Exception){
            emit(Result.Failure(e))
        }
    }
}