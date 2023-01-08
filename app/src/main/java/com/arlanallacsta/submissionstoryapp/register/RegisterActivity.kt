package com.arlanallacsta.submissionstoryapp.register

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.coroutineScope
import com.arlanallacsta.submissionstoryapp.api.ApiConfig
import com.arlanallacsta.submissionstoryapp.databinding.ActivityRegisterBinding
import com.arlanallacsta.submissionstoryapp.login.LoginActivity
import com.arlanallacsta.submissionstoryapp.repository.Repository
import com.arlanallacsta.submissionstoryapp.utils.Result
import com.arlanallacsta.submissionstoryapp.utils.UserViewModelFactory
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch


class RegisterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterBinding

    private lateinit var viewModel: RegisterViewModel
    private var registerJob: Job = Job()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.hide()

        val userRepo = Repository(ApiConfig.getApiService())
        viewModel = ViewModelProvider(this, UserViewModelFactory(userRepo))[RegisterViewModel::class.java]

        setupData()
        playAnimation()

        binding.tvLogin.setOnClickListener{
            val signIn = Intent(this, LoginActivity::class.java)
            startActivity(signIn)
            finish()
        }
    }

    private fun playAnimation() {
        ObjectAnimator.ofFloat(binding.ivRegis, View.TRANSLATION_X, -30f, 30f).apply {
            duration = 6000
            repeatCount = ObjectAnimator.INFINITE
            repeatMode = ObjectAnimator.REVERSE
        }.start()

        val title = ObjectAnimator.ofFloat(binding.titleTextView, View.ALPHA, 1f).setDuration(500)
        val message = ObjectAnimator.ofFloat(binding.messageTextView, View.ALPHA, 1f).setDuration(500)

        val tvName = ObjectAnimator.ofFloat(binding.tvNameRegis, View.ALPHA, 1f).setDuration(500)
        val etName = ObjectAnimator.ofFloat(binding.etNameRegis, View.ALPHA, 1f).setDuration(500)
        val tvEmail = ObjectAnimator.ofFloat(binding.tvEmailRegis, View.ALPHA, 1f).setDuration(500)
        val etEmail = ObjectAnimator.ofFloat(binding.etEmailRegis, View.ALPHA, 1f).setDuration(500)
        val tvPassword = ObjectAnimator.ofFloat(binding.tvPassRegis, View.ALPHA, 1f).setDuration(500)
        val etPassword = ObjectAnimator.ofFloat(binding.etPasswordRegis, View.ALPHA, 1f).setDuration(500)

        val btnRegis = ObjectAnimator.ofFloat(binding.btRegis, View.ALPHA, 1f).setDuration(500)



        val together = AnimatorSet().apply {
            playTogether(tvName,etName,tvEmail,etEmail,tvPassword,etPassword)
        }

        AnimatorSet().apply {
            playSequentially(title,message,together,btnRegis)
            start()
        }
    }

    private fun setupData() {
        binding.btRegis.setOnClickListener{
            val name = binding.etNameRegis.text.toString().trim()
            val email = binding.etEmailRegis.text.toString().trim()
            val password = binding.etPasswordRegis.text.toString().trim()
            if (TextUtils.isEmpty(name) || TextUtils.isEmpty(email) || TextUtils.isEmpty(password)){
                setMessage(this@RegisterActivity, "Incomplete data")
            }else{
                showLoading(true)
                lifecycle.coroutineScope.launchWhenResumed {
                    if (registerJob.isActive) registerJob.cancel()
                    registerJob = launch {
                        viewModel.register(name, email, password).collect{
                                result ->
                            when(result){
                                is Result.Success ->{
                                    onSuccess()
                                }
                                is Result.Error ->{

                                    onFailed()
                                }
                                is Result.Loading ->{
                                    showLoading(true)
                                }
                            }
                        }
                    }
                }

            }
        }
    }

    private fun onSuccess() {
        showLoading(false)
        startActivity(Intent(this@RegisterActivity, LoginActivity::class.java))
        setMessage(this@RegisterActivity, "account created successfully")
        finish()
    }

    private fun onFailed() {
        showLoading(false)
        setMessage(this@RegisterActivity, "account failed to create")

    }

    private fun setMessage(context: Context, text: String) {
        Toast.makeText(context, text, Toast.LENGTH_SHORT).show()

    }

    private fun showLoading(b: Boolean) {
        if (b) {
            binding.pbRegis.visibility = View.VISIBLE
        } else {
            binding.pbRegis.visibility = View.GONE
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return super.onSupportNavigateUp()
    }
}