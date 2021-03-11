package com.example.sports_match_day.controllers.paging

import androidx.paging.DataSource
import androidx.paging.PageKeyedDataSource
import com.example.sports_match_day.controllers.DecoupleAdapter
import com.example.sports_match_day.model.Sport
import com.example.sports_match_day.room.SportsDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

/**
 * Created by Kristo on 11-Mar-21
 */
class SportsDataSource private constructor(
    private var decoupleAdapter: DecoupleAdapter,
    private var sportsDatabase: SportsDatabase
) :
    PageKeyedDataSource<Int, Sport>() {

    override fun loadInitial(
        params: LoadInitialParams<Int>,
        callback: LoadInitialCallback<Int, Sport>
    ) {

        GlobalScope.launch(Dispatchers.Default) {

            val roomSports = sportsDatabase.sportsDao().getSports(PAGE_SIZE, 0)
            val sports = decoupleAdapter.toSports(roomSports)
            callback.onResult(sports, null, PAGE_SIZE)
        }
    }

    override fun loadBefore(params: LoadParams<Int>, callback: LoadCallback<Int, Sport>) {
        //do nothing
    }

    override fun loadAfter(params: LoadParams<Int>, callback: LoadCallback<Int, Sport>) {
        GlobalScope.launch(Dispatchers.Default) {

            val roomSports = sportsDatabase.sportsDao().getSports(5, params.key)
            val sports = decoupleAdapter.toSports(roomSports)
            callback.onResult(sports, params.key + PAGE_SIZE)
        }
    }

    class Factory(
        private var decoupleAdapter: DecoupleAdapter,
        private var sportsDatabase: SportsDatabase
    ) : DataSource.Factory<Int, Sport>() {
        override fun create(): DataSource<Int, Sport> {
            return SportsDataSource(decoupleAdapter, sportsDatabase)
        }
    }

    companion object{
        const val PAGE_SIZE = 15
    }
}