package com.example.sports_match_day.controllers.paging

import androidx.paging.DataSource
import androidx.paging.PageKeyedDataSource
import com.example.sports_match_day.controllers.DecoupleAdapter
import com.example.sports_match_day.controllers.MemoryRepository
import com.example.sports_match_day.firebase.FirebaseRepository
import com.example.sports_match_day.model.Match
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

/**
 * Created by Kristo on 16-Mar-21
 */
class MatchesDataSource private constructor(
    private var decoupleAdapter: DecoupleAdapter,
    private var firebaseRepository: FirebaseRepository,
    private var memoryRepository: MemoryRepository
) :
    PageKeyedDataSource<Int, Match>() {

    override fun loadInitial(
        params: LoadInitialParams<Int>,
        callback: LoadInitialCallback<Int, Match>
    ) {

        if (memoryRepository.matches.size >= PAGE_SIZE) {
            callback.onResult(memoryRepository.matches, null, memoryRepository.athletes.size)
            return
        }

        memoryRepository.matches.clear()
        firebaseRepository.getMatchesInitial(PAGE_SIZE) { list ->
            GlobalScope.launch(Dispatchers.Default) {
                list?.let {
                    val matches = decoupleAdapter.toMatches(it)
                    memoryRepository.matches = matches
                    callback.onResult(matches, null, PAGE_SIZE)
                }
            }
        }
    }

    override fun loadBefore(params: LoadParams<Int>, callback: LoadCallback<Int, Match>) {
        //do nothing
    }

    override fun loadAfter(params: LoadParams<Int>, callback: LoadCallback<Int, Match>) {
        firebaseRepository.getMatches(PAGE_SIZE) { list ->
            GlobalScope.launch(Dispatchers.Default) {
                list?.let {
                    val matches = decoupleAdapter.toMatches(it)
                    callback.onResult(matches, params.key + PAGE_SIZE)
                }
            }
        }
    }

    class Factory(
        private var decoupleAdapter: DecoupleAdapter,
        private var firebaseRepository: FirebaseRepository,
        private var memoryRepository: MemoryRepository
    ) : DataSource.Factory<Int, Match>() {
        override fun create(): DataSource<Int, Match> {
            return MatchesDataSource(decoupleAdapter, firebaseRepository, memoryRepository)
        }
    }

    companion object {
        const val PAGE_SIZE = 5
    }
}
