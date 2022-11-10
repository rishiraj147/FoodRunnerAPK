package com.example.foodrunnerapk.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "Restaurants")
data class RestaurantEntity (
    @PrimaryKey val resId:String,
    @ColumnInfo(name = "res_name") val resName:String?,
    @ColumnInfo(name = "res_rating") val resRating:String?,
    @ColumnInfo(name = "res_price") val resPrice:String?,
    @ColumnInfo(name = "res_image") val resImage:String?
    )