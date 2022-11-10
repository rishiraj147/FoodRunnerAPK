package com.example.foodrunnerapk.database

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [OrderEntity::class],version = 1)
abstract class FoodDatabase:RoomDatabase() {
    abstract fun foodDao():FoodDao
}