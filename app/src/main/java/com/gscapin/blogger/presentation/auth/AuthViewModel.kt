package com.gscapin.blogger.presentation.auth

import android.graphics.Bitmap
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import com.gscapin.blogger.core.Result
import com.gscapin.blogger.domain.auth.AuthRepoImpl
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(private val repository: AuthRepoImpl): ViewModel() {

    private var username = MutableLiveData<String>()

    fun signIn(email: String, password: String) = liveData(Dispatchers.IO){
        emit(Result.Loading())
        try {
            emit(Result.Success(repository.signIn(email, password)))
        }catch (e: Exception){
            emit(Result.Failure(e))
        }
    }

    fun signUp(email: String, password: String, username: String) = liveData(Dispatchers.IO) {
        emit(Result.Loading())
        try {
            emit(Result.Success(repository.signUp(email, password, username)))
        }catch (e: Exception){
            emit(Result.Failure(e))
        }
    }

    fun updateProfile(imageBitmap: Bitmap) = liveData(Dispatchers.IO) {
        emit(Result.Loading())
        try {
            emit(Result.Success(repository.updateProfile(imageBitmap)))
        }catch (e: Exception){
            emit(Result.Failure(e))
        }
    }

    fun setUsername(name: String){
        username.value = name
    }

    fun getUsername(): MutableLiveData<String>{
        return username
    }

}