package com.example.foodrunnerapk.database

import android.content.Context
import android.os.AsyncTask
import androidx.room.Room

class ItemsOfCart(val context: Context, private val resId:String, private val foodItem:String, private val mode:Int): AsyncTask<Void, Void, Boolean>(){
    /*
    * mode1--> save the food item into DB
    * mode3--> Remove the favorite book
    * */

    val db = Room.databaseBuilder(context,FoodDatabase::class.java,"food-db").build()

    override fun doInBackground(vararg p0: Void?): Boolean {

        when(mode){

            1->{
                //save the book into DB as favorite
                db.foodDao().insertFood(OrderEntity(resId,foodItem))
                db.close()
                return true
            }
//            2->{
//
//                db.foodDao().deleteFood(OrderEntity(resId,foodItem))
//                db.close()
//                return true
//            }
        }
        return false
    }

}
//to Extract all foodItems from dataBase
class GetItemsFromDBAsync(context: Context) : AsyncTask<Void, Void, List<OrderEntity>>() {
    val db = Room.databaseBuilder(context,FoodDatabase::class.java, "food-db").build()
    override fun doInBackground(vararg params: Void?): List<OrderEntity> {
        return db.foodDao().getAllFood()
    }


}

//to Clear the all Entries from Database

class ClearDBAsync(context: Context) : AsyncTask<Void, Void, Boolean>() {
    val db = Room.databaseBuilder(context, FoodDatabase::class.java, "food-db").build()
    override fun doInBackground(vararg params: Void?): Boolean {
        db.foodDao().deleteAll()
        db.close()
        return true
    }


}

// to add favorite Restaurants in Database

class DBFavorite(context: Context, private val resEntity:RestaurantEntity, private val mode:Int): AsyncTask<Void,Void,Boolean>(){

    val db = Room.databaseBuilder(context, RestaurantDatabase::class.java,"res-db").build()



    override fun doInBackground(vararg p0: Void?): Boolean {
        when(mode){

            1-> {
                //check DB if the restaurant is favorite or not
                val restaurant: RestaurantEntity?=db.resDao().getResById(resEntity.resId)
                db.close()
                return restaurant!=null
            }

            2->{
                //save the restaurant into DB as favorite
                db.resDao().insertDish(resEntity)
                db.close()
                return true
            }

            3->{
                //Remove the favorite restaurant
                db.resDao().deleteDish(resEntity)
                db.close()
                return true
            }

        }
        return false
    }


}

class RetrieveFavourites(val context:Context): AsyncTask<Void, Void, List<RestaurantEntity>>(){

    override fun doInBackground(vararg p0: Void?): List<RestaurantEntity> {
        val db= Room.databaseBuilder(context,RestaurantDatabase::class.java,"res-db").build()
        return db.resDao().getAllRestaurants()
    }
}