package com.example.sports_match_day.controllers

import android.content.Context
import androidx.lifecycle.MutableLiveData
import com.example.sports_match_day.firebase.FirebaseRepository
import com.example.sports_match_day.model.Match
import com.example.sports_match_day.utils.RawManager

/**
 * Created by Kristo on 08-Mar-21
 */
class CoreControllerImpl(
    private var context: Context,
    private var firebaseRepository: FirebaseRepository,
    private var memoryRepository: MemoryRepository,
    private var decoupleAdapter: DecoupleAdapter
) : CoreController {

    override fun loadMatches(matches: MutableLiveData<List<Match>>) {
        val mMatches = mutableListOf<Match>()
        val netMatches: List<com.example.sports_match_day.model.network.Match> =
            RawManager.getMatchesRaw(context)
        netMatches.forEach {
            mMatches.add(decoupleAdapter.toMatch(it))
        }
        memoryRepository.matches = mMatches
        matches.value = mMatches
    }

}

interface CoreController {
    fun loadMatches(matches: MutableLiveData<List<Match>>)
}