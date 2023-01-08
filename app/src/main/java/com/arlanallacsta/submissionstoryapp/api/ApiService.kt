package com.arlanallacsta.submissionstoryapp.api

import com.arlanallacsta.submissionstoryapp.login.LoginResponse
import com.arlanallacsta.submissionstoryapp.main.MainResponse
import com.arlanallacsta.submissionstoryapp.register.RegisterResponse
import com.arlanallacsta.submissionstoryapp.story.StoryResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.http.*

interface ApiService {
    @POST("register")
    @FormUrlEncoded
    suspend fun register(
        @Field("name") name: String,
        @Field("email") email: String,
        @Field("password") password: String
    ): RegisterResponse

    @POST("login")
    @FormUrlEncoded
    suspend fun login(
        @Field("email") email: String,
        @Field("password") password: String
    ): LoginResponse

    @GET("stories")
    suspend fun getAllStories(
        @Header("Authorization") auth: String
    ): MainResponse

    @Multipart
    @POST("stories")
    suspend fun uploadStories(
        @Header("Authorization") auth: String,
        @Part file: MultipartBody.Part,
        @Part("description") description: RequestBody
    ): StoryResponse
}