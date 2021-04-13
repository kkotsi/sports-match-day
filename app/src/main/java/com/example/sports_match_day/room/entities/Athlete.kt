package com.example.sports_match_day.room.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Created by Kristo on 07-Mar-21
 */

@Entity(tableName = "athletes")
/*@Entity(tableName = "athletes", foreignKeys = [ForeignKey(entity = Sport::class,
    parentColumns = arrayOf("id"),
    childColumns = arrayOf("sportId"),
    onDelete = ForeignKey.CASCADE)]
)*/
data class Athlete (
    @PrimaryKey(autoGenerate = true)
    var id: Int,
    @ColumnInfo
    var name: String,
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
    var matches: MutableList<Int>?
)