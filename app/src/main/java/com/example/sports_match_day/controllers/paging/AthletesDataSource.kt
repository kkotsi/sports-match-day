package com.example.sports_match_day.controllers.paging

import androidx.paging.DataSource
import androidx.paging.PageKeyedDataSource
import com.example.sports_match_day.controllers.DecoupleAdapter
import com.example.sports_match_day.controllers.MemoryRepository
import com.example.sports_match_day.model.Athlete
import com.example.sports_match_day.room.SportsDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

/**
 * Created by Kristo on 10-Mar-21
 */
class AthletesDataSource private constructor(
    private var decoupleAdapter: DecoupleAdapter,
    private var sportsDatabase: SportsDatabase,
    private var memoryRepository: MemoryRepository
) :
    PageKeyedDataSource<Int, Athlete>() {

    override fun loadInitial(
        params: LoadInitialParams<Int>,
        callback: LoadInitialCallback<Int, Athlete>
    ) {
        if(memoryRepository.athletes.size > 0){
            callback.onResult(memoryRepository.athletes, null, memoryRepository.athletes.size)
            return
        }

        GlobalScope.launch(Dispatchers.Default) {
            val roomAthletes = sportsDatabase.athletesDao().getAthletes(PAGE_SIZE, 0)
            val athletes = decoupleAdapter.toAthletes(roomAthletes)
            memoryRepository.athletes.addAll(athletes)
            callback.onResult(athletes, null, PAGE_SIZE)
        }
    }

    override fun loadBefore(params: LoadParams<Int>, callback: LoadCallback<Int, Athlete>) {
        //do nothing
    }

    override fun loadAfter(params: LoadParams<Int>, callback: LoadCallback<Int, Athlete>) {
        GlobalScope.launch(Dispatchers.Default) {
            val roomAthletes = sportsDatabase.athletesDao().getAthletes(PAGE_SIZE, params.key)
            val athletes = decoupleAdapter.toAthletes(roomAthletes)
            memoryRepository.athletes.addAll(athletes)
            callback.onResult(athletes, params.key + PAGE_SIZE)
        }
    }

    class Factory(
        private var decoupleAdapter: DecoupleAdapter,
        private var sportsDatabase: SportsDatabase,
        private var memoryRepository: MemoryRepository
    ) : DataSource.Factory<Int, Athlete>() {
        override fun create(): DataSource<Int, Athlete> {
            return AthletesDataSource(decoupleAdapter, sportsDatabase, memoryRepository)
        }
    }

    companion object{
        const val PAGE_SIZE = 7
    }
}
