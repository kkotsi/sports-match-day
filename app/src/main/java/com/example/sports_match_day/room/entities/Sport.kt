package com.example.sports_match_day.room.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Created by Kristo on 07-Mar-21
 */

@Entity(tableName = "sports")
data class Sport (
    @PrimaryKey
    var id: Int,
    @ColumnInfo
    var name: String,
    @ColumnInfo
    var type: Boolean,
    @ColumnInfo
    var gender: Boolean
)