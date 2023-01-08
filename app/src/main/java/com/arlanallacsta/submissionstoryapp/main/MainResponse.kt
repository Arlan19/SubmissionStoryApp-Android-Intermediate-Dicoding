package com.arlanallacsta.submissionstoryapp.main


import com.google.gson.annotations.SerializedName

data class MainResponse(
    @field:SerializedName("error")
    val error: Boolean,

    @field:SerializedName("message")
    val message: String,

    @field:SerializedName("listStory")
    val listStory: List<ListStory>
)
