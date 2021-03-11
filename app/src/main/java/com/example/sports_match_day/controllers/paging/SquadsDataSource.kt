package com.example.sports_match_day.controllers.paging

import androidx.paging.DataSource
import androidx.paging.PageKeyedDataSource
import com.example.sports_match_day.controllers.DecoupleAdapter
import com.example.sports_match_day.model.Squad
import com.example.sports_match_day.room.SportsDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

/**
 * Created by Kristo on 11-Mar-21
 */
class SquadsDataSource private constructor(
    private var decoupleAdapter: DecoupleAdapter,
    private var sportsDatabase: SportsDatabase
) :
    PageKeyedDataSource<Int, Squad>() {

    override fun loadInitial(
        params: LoadInitialParams<Int>,
        callback: LoadInitialCallback<Int, Squad>
    ) {

        GlobalScope.launch(Dispatchers.Default) {

            val roomSquads = sportsDatabase.squadsDao().getSquads(PAGE_SIZE, 0)
            val squads = decoupleAdapter.toSquads(roomSquads)
            callback.onResult(squads, null, PAGE_SIZE)
        }
    }

    override fun loadBefore(params: LoadParams<Int>, callback: LoadCallback<Int, Squad>) {
        //do nothing
    }

    override fun loadAfter(params: LoadParams<Int>, callback: LoadCallback<Int, Squad>) {
        GlobalScope.launch(Dispatchers.Default) {

            val roomSquads = sportsDatabase.squadsDao().getSquads(5, params.key)
            val squads = decoupleAdapter.toSquads(roomSquads)
            callback.onResult(squads, params.key + PAGE_SIZE)
        }
    }

    class Factory(
        private var decoupleAdapter: DecoupleAdapter,
        private var sportsDatabase: SportsDatabase
    ) : DataSource.Factory<Int, Squad>() {
        override fun create(): DataSource<Int, Squad> {
            return SquadsDataSource(decoupleAdapter, sportsDatabase)
        }
    }

    companion object{
        const val PAGE_SIZE = 7
    }
}