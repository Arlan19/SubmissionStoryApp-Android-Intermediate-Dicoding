package com.arlanallacsta.submissionstoryapp.repository

import com.arlanallacsta.submissionstoryapp.api.ApiService
import com.arlanallacsta.submissionstoryapp.login.LoginResponse
import com.arlanallacsta.submissionstoryapp.main.MainResponse
import com.arlanallacsta.submissionstoryapp.register.RegisterResponse
import com.arlanallacsta.submissionstoryapp.story.StoryResponse
import com.arlanallacsta.submissionstoryapp.utils.Result
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.HttpException
import java.io.File

class Repository constructor(private val apiService: ApiService) {

    suspend fun register(
        name: String,
        email: String,
        password: String
    ): Flow<Result<RegisterResponse>> = flow {
        try {
            val response = apiService.register(name, email, password)
            emit(Result.Success(response))
        } catch (e: Exception) {
            e.printStackTrace()
            emit(Result.Error(e.toString()))
        }
    }.flowOn(Dispatchers.IO)

    suspend fun login(email: String, password: String): Flow<Result<LoginResponse>> = flow {
        try {
            val response = apiService.login(email, password)
            emit(Result.Success(response))
        } catch (e: Exception) {
            e.printStackTrace()
            emit(Result.Error(e.toString()))
        }
    }.flowOn(Dispatchers.IO)

    suspend fun getAllStories(authorization: String): Flow<Result<MainResponse>> = flow {
        try {
            val token = generateToken(authorization)
            val response = apiService.getAllStories(token)
            emit(Result.Success(response))
        }catch (e : java.lang.Exception){
            val ex = (e as? HttpException)?.response()?.errorBody()?.string()
            emit(Result.Error(ex.toString()))
        }
    }.flowOn(Dispatchers.IO)

    suspend fun uploadStory(authorization: String, description: String, file: File) : Flow<Result<StoryResponse>> = flow {
        try {
            val token = generateToken(authorization)
            val requestImage = file.asRequestBody("image/jpg".toMediaTypeOrNull())
            val imageMultipart = MultipartBody.Part.createFormData(
                "photo", file.name, requestImage
            )
            val desc = description.toRequestBody("text/plain".toMediaType())

            val response = apiService.uploadStories(token, imageMultipart, desc)
            emit(Result.Success(response))
        }catch (e : java.lang.Exception){
            val ex = (e as? HttpException)?.response()?.errorBody()?.string()
            emit(Result.Error(ex.toString()))
        }
    }.flowOn(Dispatchers.IO)

    private fun generateToken(token: String): String {
        return "Bearer $token"
    }
}