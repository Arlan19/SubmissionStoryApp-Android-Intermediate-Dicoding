package com.arlanallacsta.submissionstoryapp.story

import androidx.lifecycle.ViewModel
import com.arlanallacsta.submissionstoryapp.repository.Repository
import java.io.File

class StoryViewModel constructor(private val repository: Repository): ViewModel() {

    suspend fun uploadStories(authorization: String, description: String, file: File) = repository.uploadStory(authorization, description, file)
}