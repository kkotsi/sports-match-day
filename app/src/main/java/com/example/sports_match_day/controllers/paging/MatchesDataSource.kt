package com.example.sports_match_day.controllers.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.example.sports_match_day.controllers.DecoupleAdapter
import com.example.sports_match_day.controllers.MemoryRepository
import com.example.sports_match_day.firebase.FirebaseRepository
import com.example.sports_match_day.model.Match

/**
 * Created by Kristo on 16-Mar-21
 */
class MatchesDataSource constructor(
    private var decoupleAdapter: DecoupleAdapter,
    private var firebaseRepository: FirebaseRepository,
    private var memoryRepository: MemoryRepository
) : PagingSource<Int, Match>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Match> {

        try {
            val offset = params.key ?: 0

            val matches = mutableListOf<Match>()
            val cacheSize = memoryRepository.matches.size

            if (offset + PAGE_SIZE <= cacheSize) {

                //get all data from memory
                val nextKey = offset + PAGE_SIZE
                val prevKey = if (offset >= PAGE_SIZE) offset - PAGE_SIZE else null
                val returned = mutableListOf<Match>()
                returned.addAll(memoryRepository.matches.subList(offset, offset + PAGE_SIZE))
                return LoadResult.Page(
                    data = returned,
                    prevKey = prevKey,
                    nextKey = nextKey
                )
            } else if (offset < cacheSize) {
                //Get as many cached data as possible.
                matches.addAll(memoryRepository.matches.subList(offset, cacheSize))
            }

            val firebaseMatches = firebaseRepository.getMatches(PAGE_SIZE, getLastId())

            firebaseMatches?.let {
                val newMatches = decoupleAdapter.toMatches(firebaseMatches)
                matches.addAll(newMatches)
                memoryRepository.matches.addAll(newMatches)
            }

            val nextKey = if (matches.size >= PAGE_SIZE) offset + PAGE_SIZE else null
            val prevKey = if (offset >= PAGE_SIZE) offset - PAGE_SIZE else null

            return LoadResult.Page(
                data = matches,
                prevKey = prevKey,
                nextKey = nextKey
            )
        } catch (t: Throwable) {
            return LoadResult.Error(t)
        }
    }

    private fun getLastId(): Int? {
        if (memoryRepository.matches.isNotEmpty()) {
            return memoryRepository.matches.last().id
        }
        return null
    }

    override val keyReuseSupported: Boolean
        get() = true

    override fun getRefreshKey(state: PagingState<Int, Match>): Int? {
        //Find the start of the closest page.
        return state.anchorPosition?.let { anchorPosition ->
            anchorPosition - (anchorPosition % PAGE_SIZE)
        }
    }

    companion object {
        const val PAGE_SIZE = 10
    }
}