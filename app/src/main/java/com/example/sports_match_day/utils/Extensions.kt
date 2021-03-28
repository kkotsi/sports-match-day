package com.example.sports_match_day.utils

/**
 * Created by Kristo on 28-Mar-21
 */

inline fun <T>MutableList<T>.findAll(predicate: (T) -> Boolean) : List<T>{
    val subList = mutableListOf<T>()
    for (element in this) if (predicate(element)) subList.add(element)
    return subList
}

inline fun <T>MutableList<T>.doToAll(predicate: (T) -> Boolean, action: (T) -> Unit){
    for (element in this) if (predicate(element)) action(element)
}