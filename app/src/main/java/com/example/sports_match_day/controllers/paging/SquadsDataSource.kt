package com.example.sports_match_day.controllers.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.example.sports_match_day.controllers.DecoupleAdapter
import com.example.sports_match_day.controllers.MemoryRepository
import com.example.sports_match_day.model.Squad
import com.example.sports_match_day.room.SportsDatabase

/**
 * Created by Kristo on 11-Mar-21
 */
class SquadsDataSource(
    private var decoupleAdapter: DecoupleAdapter,
    private var sportsDatabase: SportsDatabase,
    private var memoryRepository: MemoryRepository
) :
    PagingSource<Int, Squad>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Squad> {
        val offset = params.key ?: 0

        val squads = mutableListOf<Squad>()
        val cacheSize = memoryRepository.squads.size

        if (offset + PAGE_SIZE <= cacheSize) {
            //get all data from memory
            val nextKey = offset + PAGE_SIZE
            val prevKey = if (offset >= PAGE_SIZE) offset - PAGE_SIZE else null
            val returned = mutableListOf<Squad>()
            returned.addAll(memoryRepository.squads.subList(offset, offset + PAGE_SIZE))
            return LoadResult.Page(
                data = returned,
                prevKey = prevKey,
                nextKey = nextKey
            )
        } else if (offset < cacheSize) {
            //Get as many cached data as possible.
            squads.addAll(memoryRepository.squads.subList(offset, cacheSize))
        }

        val roomSquads = sportsDatabase.squadsDao().getSquads(PAGE_SIZE, offset + squads.size)
        val newSquads = decoupleAdapter.toSquads(roomSquads)
        squads.addAll(newSquads)

        val nextKey = if (squads.size >= PAGE_SIZE) offset + PAGE_SIZE else null
        val prevKey = if (offset >= PAGE_SIZE) offset - PAGE_SIZE else null

        memoryRepository.squads.addAll(newSquads)

        return LoadResult.Page(
            data = squads,
            prevKey = prevKey,
            nextKey = nextKey
        )
    }

    override val keyReuseSupported: Boolean
        get() = true

    override fun getRefreshKey(state: PagingState<Int, Squad>): Int? {
        //Find the start of the closest page.
        return state.anchorPosition?.let { anchorPosition ->
            anchorPosition - (anchorPosition % PAGE_SIZE)
        }
    }

    companion object {
        const val PAGE_SIZE = 10
    }
}