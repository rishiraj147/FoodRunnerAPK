package com.example.foodrunnerapk.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query


@Dao
interface FoodDao {
    @Insert
    fun insertFood(foodItem:OrderEntity)

    @Delete
    fun deleteFood(foodItem: OrderEntity)

    @Query("SELECT * FROM food")
    fun getAllFood(): List<OrderEntity>

    @Query("DELETE FROM food")
    fun deleteAll()




}