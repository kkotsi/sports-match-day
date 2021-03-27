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
) {

    override fun equals(other: Any?): Boolean {
        if (other is Sport)
            return other.id == this.id

        return false
    }

    override fun hashCode(): Int {
        var result = id
        result = 31 * result + name.hashCode()
        result = 31 * result + type.hashCode()
        result = 31 * result + gender.hashCode()
        return result
    }

}

/**
 * This enum is a responsible to separate the type of a sport.
 * SOLO: A sport that an individual can participate.
 * TEAM: A sport that a squad can participate.
 */
enum class SportType() {
    SOLO, TEAM
}