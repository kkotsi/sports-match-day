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

        GlobalScope.launch(Dispatchers.Default) {

            if(memoryRepository.matches.size >= PAGE_SIZE){
                callback.onResult(memoryRepository.matches, null, memoryRepository.athletes.size)
                return@launch
            }

            memoryRepository.athletes.clear()
            firebaseRepository.getMatches {
                print(it)
            }

            callback.onResult(mutableListOf(), null, PAGE_SIZE)
        }
    }

    override fun loadBefore(params: LoadParams<Int>, callback: LoadCallback<Int, Match>) {
        //do nothing
    }

    override fun loadAfter(params: LoadParams<Int>, callback: LoadCallback<Int, Match>) {
        GlobalScope.launch(Dispatchers.Default) {

            callback.onResult(mutableListOf(), params.key + PAGE_SIZE)
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

    companion object{
        const val PAGE_SIZE = 5
    }
}
