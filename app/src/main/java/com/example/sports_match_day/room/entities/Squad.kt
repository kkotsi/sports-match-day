package com.example.sports_match_day.room.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Created by Kristo on 07-Mar-21
 */

@Entity(tableName = "squads")
/*@Entity(tableName = "squads", foreignKeys = [ForeignKey(entity = Sport::class,
    parentColumns = arrayOf("id"),
    childColumns = arrayOf("sportId"),
    onDelete = ForeignKey.CASCADE)]
)*/
data class Squad(
    @PrimaryKey(autoGenerate = true)
    var id: Int,
    @ColumnInfo
    var name: String,
    @ColumnInfo
    var stadium: String,
    @ColumnInfo
    var city: String,
    @ColumnInfo
    var country: String,
    @ColumnInfo
    var sportId: Int,
    @ColumnInfo
    var birthday: Long,
    @ColumnInfo
    var gender: Boolean,
    @ColumnInfo
    val matches: MutableList<Int>?,
    @ColumnInfo
    var stadiumLocation: String? = null
)