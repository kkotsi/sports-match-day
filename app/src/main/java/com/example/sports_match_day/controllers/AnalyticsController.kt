package com.example.sports_match_day.controllers

import com.example.sports_match_day.model.Participant
import com.example.sports_match_day.model.Sport
import com.example.sports_match_day.model.SportType
import com.example.sports_match_day.model.Squad

/**
 * Created by Kristo on 15-Apr-21
 */
class AnalyticsControllerImpl
    (private val remoteRepository: RemoteRepository) : AnalyticsController {

    override suspend fun getSquadsMostWins(): Map<Squad, Int> {
        val matches = remoteRepository.getAllMatches()
        val teamsWins = mutableMapOf<Squad, Int>()
        matches.forEach { match ->
            if (match.participants.size > 1) {
                match.sport?.let {
                    if (it.type == SportType.TEAM) {
                        var winner: Participant? = match.participants[0]
                        for (i in 1 until match.participants.size) {
                            val participant = match.participants[i]
                            if (winner!!.score < participant.score) {
                                winner = participant
                            } else if (winner.score == participant.score) {
                                winner = null
                                break
                            }
                        }
                        winner?.contestant?.let { contestant ->
                            val squad = contestant as Squad
                            if (squad.id == 1)
                                print("ok")
                            if (teamsWins.containsKey(squad)) {
                                teamsWins[squad] = teamsWins[squad]!!.plus(1)
                            } else {
                                teamsWins[squad] = 1
                            }
                        }
                    }
                }
            }
        }
        return getBest4(teamsWins) as MutableMap<Squad,Int>
    }

    private fun getBest4(anyMap: MutableMap<*, Int>): Map<*, Int> {
        val map = anyMap.toMutableMap()
        val best4: MutableMap<Any, Int> = mutableMapOf()
        var i = 0
        while (i < 4) {
            var bestScore: Map.Entry<*, Int>? = null
            map.forEach {
                if (it.value > bestScore?.value ?: -1) {
                    bestScore = it
                }
            }
            if (bestScore == null) return best4
            map.remove(bestScore!!.key)
            best4[bestScore!!.key as Any] = bestScore!!.value
            i++
        }

        return best4
    }

    override suspend fun getMatchesCountPerSport(): Map<Sport, Int> {
        val matches = remoteRepository.getAllMatches()
        val matchesPerSport = mutableMapOf<Sport, Int>()
        matches.forEach {
            it.sport?.let {
                if (matchesPerSport.containsKey(it)) {
                    matchesPerSport[it] = matchesPerSport[it]!!.plus(1)
                } else {
                    matchesPerSport[it] = 1
                }
            }
        }
        return getBest4(matchesPerSport)  as MutableMap<Sport,Int>
    }
}

interface AnalyticsController {
    suspend fun getSquadsMostWins(): Map<Squad, Int>
    suspend fun getMatchesCountPerSport(): Map<Sport, Int>
}