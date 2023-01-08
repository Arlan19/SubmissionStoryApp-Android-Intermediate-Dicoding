package com.arlanallacsta.submissionstoryapp.login

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
import com.arlanallacsta.submissionstoryapp.MainActivity
import com.arlanallacsta.submissionstoryapp.api.ApiConfig
import com.arlanallacsta.submissionstoryapp.databinding.ActivityLoginBinding
import com.arlanallacsta.submissionstoryapp.datastore.UserPreference
import com.arlanallacsta.submissionstoryapp.register.RegisterActivity
import com.arlanallacsta.submissionstoryapp.repository.Repository
import com.arlanallacsta.submissionstoryapp.utils.Result
import com.arlanallacsta.submissionstoryapp.utils.UserViewModelFactory
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding

    private lateinit var viewModel: LoginViewModel
    private lateinit var userPreference: UserPreference
    private var loginJob: Job = Job()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar!!.hide()

        userPreference = UserPreference(this)

        val repository = Repository(ApiConfig.getApiService())
        viewModel =
            ViewModelProvider(this, UserViewModelFactory(repository))[LoginViewModel::class.java]
        setupLogin()

        playAnimation()
        binding.tvRegister.setOnClickListener {
            val signUp = Intent(this@LoginActivity, RegisterActivity::class.java)
            startActivity(signUp)
        }

    }

    private fun playAnimation() {
        ObjectAnimator.ofFloat(binding.ivLogin, View.TRANSLATION_X, -30f, 30f).apply {
            duration = 6000
            repeatCount = ObjectAnimator.INFINITE
            repeatMode = ObjectAnimator.REVERSE
        }.start()

        val title = ObjectAnimator.ofFloat(binding.titleTextView, View.ALPHA, 1f).setDuration(500)
        val message = ObjectAnimator.ofFloat(binding.messageTextView, View.ALPHA, 1f).setDuration(500)

        val tvEmail = ObjectAnimator.ofFloat(binding.tvEmailLogin, View.ALPHA, 1f).setDuration(500)
        val etEmail = ObjectAnimator.ofFloat(binding.etEmailLogin, View.ALPHA, 1f).setDuration(500)
        val tvPassword = ObjectAnimator.ofFloat(binding.tvPassLogin, View.ALPHA, 1f).setDuration(500)
        val etPassword = ObjectAnimator.ofFloat(binding.etPasswordLogin, View.ALPHA, 1f).setDuration(500)

        val btnRegis = ObjectAnimator.ofFloat(binding.btLogin, View.ALPHA, 1f).setDuration(500)



        AnimatorSet().apply {
            playSequentially(title,message,tvEmail,etEmail,tvPassword,etPassword,btnRegis)
            start()
        }
    }

    private fun setupLogin() {
        binding.btLogin.setOnClickListener {
            val email = binding.etEmailLogin.text.toString().trim()
            val password = binding.etPasswordLogin.text.toString().trim()
            if (TextUtils.isEmpty(email) || TextUtils.isEmpty(password)) {
                setMessage(this@LoginActivity, "Incomplete data")
            } else {
                showLoading(true)
                lifecycle.coroutineScope.launchWhenResumed {
                    if (loginJob.isActive) loginJob.cancel()
                    loginJob = launch {
                        viewModel.login(email, password).collect{ result ->
                            when(result){
                                is Result.Success ->{
                                    userPreference.isLogin = !result.data?.error!!
                                    userPreference.token = result.data.loginResult.token
                                    onSuccess()
                                }
                                is Result.Loading ->{
                                    showLoading(true)
                                }
                                is Result.Error ->{
                                    onFailed()
                                }

                            }
                        }
                    }
                }
            }
        }
    }

    private fun onFailed() {
        showLoading(false)
        setMessage(this@LoginActivity, "your email and password are wrong")
    }

    private fun onSuccess() {
        startActivity(Intent(this@LoginActivity, MainActivity::class.java))
        finish()

        showLoading(false)
    }

    private fun setMessage(context: Context, text: String) {
        Toast.makeText(context, text, Toast.LENGTH_SHORT).show()
    }

    private fun showLoading(b: Boolean) {
        if (b) {
            binding.pbLogin.visibility = View.VISIBLE
        } else {
            binding.pbLogin.visibility = View.GONE
        }
    }
}