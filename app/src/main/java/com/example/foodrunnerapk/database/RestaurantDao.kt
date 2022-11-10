package com.example.foodrunnerapk.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query

@Dao
interface RestaurantDao {
    @Insert
    fun insertDish(dishEntity:RestaurantEntity)

    @Delete
    fun deleteDish(dishEntity:RestaurantEntity)

    @Query("SELECT * FROM restaurants")
    fun getAllRestaurants(): List<RestaurantEntity>

    @Query("SELECT * FROM restaurants WHERE resId= :resId")
    fun getResById(resId:String): RestaurantEntity

}