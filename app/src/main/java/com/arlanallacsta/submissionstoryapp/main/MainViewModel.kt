package com.arlanallacsta.submissionstoryapp.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.arlanallacsta.submissionstoryapp.repository.Repository
import kotlinx.coroutines.Job
import com.arlanallacsta.submissionstoryapp.utils.Result
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class MainViewModel constructor(private val repository: Repository): ViewModel() {

    private val listStory = MutableLiveData<Result<MainResponse>>()
    private var job: Job? = null

    fun fetchListStory(authorization: String){
        job = viewModelScope.launch {
            repository.getAllStories(authorization).collectLatest {
                listStory.value = it
            }
        }
    }

    val responseListStory: LiveData<Result<MainResponse>> = listStory

    override fun onCleared() {
        super.onCleared()
        job?.cancel()
    }
}