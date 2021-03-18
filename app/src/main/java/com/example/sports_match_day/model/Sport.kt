package com.example.sports_match_day.model

/**
 * Created by Kristo on 07-Mar-21
 * A class that represents a Sport
 */
class Sport(
  var id: Int,
  var name: String,
  var type: SportType,
  var gender: Gender,
  var participantCount: Int
)

/**
 * This enum is a responsible to separate the type of a sport.
 * SOLO: A sport that an individual can participate.
 * TEAM: A sport that a squad can participate.
 */
enum class SportType(){
    SOLO, TEAM
}