package com.example.sports_match_day.room.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Created by Kristo on 07-Mar-21
 */

@Entity(tableName = "squads")
data class Squad (
    @PrimaryKey
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
    var birthday: Long
)