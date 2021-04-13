package com.example.sports_match_day.model

/**
 * Created by Kristo on 09-Mar-21
 */
abstract class Contestant(var name: String, val id: Int, var matches: MutableList<Int>){

    override fun equals(other: Any?): Boolean {
        if(other is Contestant){
            return other.id == this.id
        }
        return false
    }

    override fun hashCode(): Int {
        var result = name.hashCode()
        result = 31 * result + id
        return result
    }
}