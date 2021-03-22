package com.example.sports_match_day.controllers.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.example.sports_match_day.controllers.DecoupleAdapter
import com.example.sports_match_day.controllers.MemoryRepository
import com.example.sports_match_day.model.Athlete
import com.example.sports_match_day.room.SportsDatabase

/**
 * Created by Kristo on 10-Mar-21
 */
class AthletesDataSource constructor(
    private var decoupleAdapter: DecoupleAdapter,
    private var sportsDatabase: SportsDatabase,
    private var memoryRepository: MemoryRepository
) :
    PagingSource<Int, Athlete>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Athlete> {
        try {
            val offset = params.key ?: 0

            val athletes = mutableListOf<Athlete>()
            val cacheSize = memoryRepository.athletes.size

            if (offset + PAGE_SIZE <= cacheSize) {
                //get all data from memory
                val nextKey = offset + PAGE_SIZE
                val prevKey = if (offset >= PAGE_SIZE) offset - PAGE_SIZE else null
                val returned = mutableListOf<Athlete>()
                returned.addAll(memoryRepository.athletes.subList(offset, offset + PAGE_SIZE))
                return LoadResult.Page(
                    data = returned,
                    prevKey = prevKey,
                    nextKey = nextKey
                )
            } else if (offset < cacheSize) {
                //Get as many cached data as possible.
                athletes.addAll(memoryRepository.athletes.subList(offset, cacheSize))
            }

            val roomAthletes =
                sportsDatabase.athletesDao().getAthletes(PAGE_SIZE, offset + athletes.size)
            val newAthletes = decoupleAdapter.toAthletes(roomAthletes)
            athletes.addAll(newAthletes)
            memoryRepository.athletes.addAll(newAthletes)

            val nextKey = if (athletes.size >= PAGE_SIZE) offset + PAGE_SIZE else null
            val prevKey = if (offset >= PAGE_SIZE) offset - PAGE_SIZE else null

            return LoadResult.Page(
                data = athletes,
                prevKey = prevKey,
                nextKey = nextKey
            )
        } catch (t: Throwable) {
            return LoadResult.Error(t)
        }
    }

    override val keyReuseSupported: Boolean
        get() = true

    override fun getRefreshKey(state: PagingState<Int, Athlete>): Int? {
        //Find the start of the closest page.
        return state.anchorPosition?.let { anchorPosition ->
            anchorPosition - (anchorPosition % PAGE_SIZE)
        }
    }

    companion object {
        const val PAGE_SIZE = 10
    }
}