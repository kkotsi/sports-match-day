package com.example.sports_match_day.room.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Created by Kristo on 07-Mar-21
 */

@Entity
data class Squad (
    @PrimaryKey
    var id: String,
    @ColumnInfo
    var name: String
)