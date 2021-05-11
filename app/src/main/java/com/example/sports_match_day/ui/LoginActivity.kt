package com.example.sports_match_day.ui

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ProgressBar
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import com.example.sports_match_day.R
import com.example.sports_match_day.utils.PopupManager
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.IdpResponse
import org.koin.androidx.viewmodel.ext.android.viewModel

class LoginActivity : AppCompatActivity() {

    private val viewModel: LoginViewModel by viewModel()
    private lateinit var loader: ProgressBar
    private lateinit var retry: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        setupRetry()
        setupLoader()
        setUpObservers()

        viewModel.loadData()
    }

    private fun setupRetry(){
        retry = findViewById(R.id.button_retry)
        retry.setOnClickListener {
            login()
        }
    }
    private fun login(){
        loader.visibility = View.VISIBLE
        val providers: List<AuthUI.IdpConfig> = listOf(
            AuthUI.IdpConfig.GoogleBuilder().build()
        )

        startActivityForResult(
            AuthUI.getInstance()
                .createSignInIntentBuilder()
                .setAvailableProviders(providers)
                .build(), SIGN_IN)
    }

    private fun setupLoader() {
        loader = findViewById(R.id.progress_loading)
    }

    private fun setUpObservers() {
        viewModel.isDataLoading.observe(this, {
            if (it) {
                loader.visibility = View.VISIBLE
            } else {
                loader.visibility = View.INVISIBLE
            }
        })

        viewModel.apiErrorMessage.observe(this, {
            retry.isVisible = true
            PopupManager.simplePopupMessage(this, "Couldn't login... \n ${it.message}")
        })

        viewModel.samplesDataLoaded.observe(this, {
            login()
        })

        viewModel.signUp.observe(this, {
            if(it) {
                // Successfully signed up
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
                this@LoginActivity.finish()
            }
        })
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        loader.visibility = View.INVISIBLE
        if (requestCode == SIGN_IN) {
            val response = IdpResponse.fromResultIntent(data)
            if (resultCode == RESULT_OK) {
                if(response?.isNewUser == true){
                    viewModel.signUpUser()
                }else {
                    // Successfully signed in
                    val intent = Intent(this, MainActivity::class.java)
                    startActivity(intent)
                    this@LoginActivity.finish()
                }
            } else {
                retry.isVisible = true
                PopupManager.simplePopupMessage(this, "Couldn't login... \n ${response?.error?.message}")
            }
        }
    }

    companion object {
        private const val SIGN_IN = 124
    }
}