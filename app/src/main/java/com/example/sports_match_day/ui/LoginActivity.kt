package com.example.sports_match_day.ui

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ProgressBar
import androidx.appcompat.app.AppCompatActivity
import com.example.sports_match_day.R
import com.example.sports_match_day.utils.PopupManager
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.IdpResponse
import org.koin.androidx.viewmodel.ext.android.viewModel

class LoginActivity : AppCompatActivity() {

    private val viewModel: LoginViewModel by viewModel()
    private lateinit var loader: ProgressBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        setupLoader()
        setUpObservers()

        viewModel.loadData()
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

        viewModel.samplesDataLoaded.observe(this, {

            val providers: List<AuthUI.IdpConfig> = listOf(
                AuthUI.IdpConfig.GoogleBuilder().build()
            )

            startActivityForResult(
                AuthUI.getInstance()
                    .createSignInIntentBuilder()
                    .setAvailableProviders(providers)
                    .build(), SIGN_IN
            )
        })
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == SIGN_IN) {
            val response = IdpResponse.fromResultIntent(data);

            if (resultCode == RESULT_OK) {
                // Successfully signed in
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
                finishActivity(0)
            } else {
                PopupManager.simplePopupMessage(this, "Couldn't login... \n ${response?.error?.message}")
            }
        }
    }

    companion object {
        private const val SIGN_IN = 124
    }
}