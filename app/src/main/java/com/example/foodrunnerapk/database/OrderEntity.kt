package com.example.foodrunnerapk.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.foodrunnerapk.model.RestaurantDish

@Entity(tableName = "food")
data class OrderEntity (
    @PrimaryKey val resId:String,
    @ColumnInfo(name="food_item")  val foodItem:String,

    )

