package com.arlanallacsta.submissionstoryapp.login

import androidx.lifecycle.ViewModel
import com.arlanallacsta.submissionstoryapp.repository.Repository

class LoginViewModel constructor(private val repository: Repository): ViewModel() {

    suspend fun login(email: String, password: String) = repository.login(email, password)
}