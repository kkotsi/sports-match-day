package com.example.sports_match_day.controllers

import android.content.Context
import com.example.sports_match_day.utils.RawManager

/**
 * Created by Kristo on 15-Apr-21
 */
class LoginControllerImpl(private val context: Context, private val remoteRepository: RemoteRepository): LoginController {

    override suspend fun signUp(): Boolean {
        val data = RawManager.getMatchesRaw(context)
        remoteRepository.signUp(data)
        return true
    }
}

interface LoginController {
    suspend fun signUp(): Boolean
}