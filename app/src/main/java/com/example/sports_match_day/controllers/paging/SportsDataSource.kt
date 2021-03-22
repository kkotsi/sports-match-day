package com.example.sports_match_day.controllers.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.example.sports_match_day.controllers.DecoupleAdapter
import com.example.sports_match_day.controllers.MemoryRepository
import com.example.sports_match_day.model.Sport
import com.example.sports_match_day.room.SportsDatabase

/**
 * Created by Kristo on 11-Mar-21
 */
class SportsDataSource constructor(
    private var decoupleAdapter: DecoupleAdapter,
    private var sportsDatabase: SportsDatabase,
    private var memoryRepository: MemoryRepository
) :
    PagingSource<Int, Sport>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Sport> {
        try {
            val offset = params.key ?: 0

            val sports = mutableListOf<Sport>()
            val cacheSize = memoryRepository.sports.size

            if (offset + PAGE_SIZE <= cacheSize) {
                //get all data from memory
                val nextKey = offset + PAGE_SIZE
                val prevKey = if (offset >= PAGE_SIZE) offset - PAGE_SIZE else null
                val returned = mutableListOf<Sport>()
                returned.addAll(memoryRepository.sports.subList(offset, offset + PAGE_SIZE))
                return LoadResult.Page(
                    data = returned,
                    prevKey = prevKey,
                    nextKey = nextKey
                )
            } else if (offset < cacheSize) {
                //Get as many cached data as possible.
                sports.addAll(memoryRepository.sports.subList(offset, cacheSize))
            }

            val roomSports = sportsDatabase.sportsDao().getSports(PAGE_SIZE, offset + sports.size)
            val newSports = decoupleAdapter.toSports(roomSports)
            sports.addAll(newSports)
            memoryRepository.sports.addAll(newSports)

            val nextKey = if (sports.size >= PAGE_SIZE) offset + PAGE_SIZE else null
            val prevKey = if (offset >= PAGE_SIZE) offset - PAGE_SIZE else null

            return LoadResult.Page(
                data = sports,
                prevKey = prevKey,
                nextKey = nextKey
            )
        } catch (t: Throwable) {
            return LoadResult.Error(t)
        }
    }

    override val keyReuseSupported: Boolean
        get() = true

    override fun getRefreshKey(state: PagingState<Int, Sport>): Int? {
        //Find the start of the closest page.
        return state.anchorPosition?.let { anchorPosition ->
            anchorPosition - (anchorPosition % PAGE_SIZE)
        }
    }

    companion object {
        const val PAGE_SIZE = 15
    }
}