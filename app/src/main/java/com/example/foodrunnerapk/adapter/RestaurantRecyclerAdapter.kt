package com.example.foodrunnerapk.adapter


import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.recyclerview.widget.RecyclerView
import com.example.foodrunnerapk.R
import com.example.foodrunnerapk.activity.RestaurantMenuActivity
import com.example.foodrunnerapk.database.DBFavorite
import com.example.foodrunnerapk.database.RestaurantEntity
import com.example.foodrunnerapk.model.Restaurant
import com.squareup.picasso.Picasso

class RestaurantRecyclerAdapter(val context: Context, var itemList:ArrayList<Restaurant>):RecyclerView.Adapter<RestaurantRecyclerAdapter.RestaurantViewHolder>(),Filterable {

    var backup= arrayListOf<Restaurant>()
    init {
        backup.addAll(itemList)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RestaurantViewHolder {
        val view= LayoutInflater.from(parent.context).inflate(R.layout.recycler_home_single_row,parent,false)
        return RestaurantViewHolder(view)
    }
    override fun onBindViewHolder(holder: RestaurantViewHolder, position: Int) {
        val restaurant=itemList[position]
        holder.txtRestaurantName.text=restaurant.restaurantName
        val moddedString= context.getString(R.string.per_person,restaurant.restaurantPrice)
        holder.txtPrice.text=moddedString
        holder.txtRating.text=restaurant.restaurantRating
        Picasso.get().load(restaurant.restaurantImage).error(R.drawable.ic_launcher_background).into(holder.imageView)

        val resEntity=RestaurantEntity(restaurant.restaurantId,restaurant.restaurantName,
            restaurant.restaurantRating,restaurant.restaurantPrice,restaurant.restaurantImage)


        val checkFav= DBFavorite(context,resEntity,1).execute()
        val isFav=checkFav.get()

        if(isFav){
            holder.btnFavorite.setBackgroundResource(R.drawable.ic_baseline_favorite_24)
        }
        else{
            holder.btnFavorite.setBackgroundResource(R.drawable.ic_favorite)
        }

        holder.btnFavorite.setOnClickListener {

            if(!DBFavorite(context,resEntity, 1).execute().get()){
                val async= DBFavorite(context,resEntity, 2).execute()
                val result=async.get()
                if(result){
                    holder.btnFavorite.setBackgroundResource(R.drawable.ic_baseline_favorite_24)
                }
            }else{
                val async= DBFavorite(context,resEntity, 3).execute()
                val result=async.get()
                if(result) {
                    holder.btnFavorite.setBackgroundResource(R.drawable.ic_favorite)
                }
            }
        }

        holder.llContent.setOnClickListener {
            val intent= Intent(context,RestaurantMenuActivity::class.java)
            intent.putExtra("restaurant_id",restaurant.restaurantId)
            intent.putExtra("resName",restaurant.restaurantName)
            context.startActivity(intent)
        }



    }

    override fun getItemCount(): Int {
        return itemList.size
    }



    class RestaurantViewHolder(view: View):RecyclerView.ViewHolder(view){
        val imageView: ImageView =view.findViewById(R.id.imgRestaurantImage)
        val txtRestaurantName: TextView =view.findViewById(R.id.txtRestaurantName)
        val txtRating: TextView =view.findViewById(R.id.txtRating)
        val txtPrice: TextView =view.findViewById(R.id.txtPrice)
        val btnFavorite: TextView =view.findViewById(R.id.txtFavoriteIcon)
        val llContent:LinearLayout=view.findViewById(R.id.llContent)
    }

    override fun getFilter(): Filter {
        return filter
    }

    private val filter:Filter=object:Filter(){
        //background Thread
        override fun performFiltering(keyword: CharSequence?): FilterResults {
            val filteredData=ArrayList<Restaurant>()
            if(keyword.toString().isEmpty()){
                filteredData.addAll(backup)
            }
            else{
                for(obj:Restaurant in backup){
                    if(obj.restaurantName.lowercase().contains(keyword.toString().lowercase())){
                        filteredData.add(obj)
                    }
                }
            }
            val result= FilterResults()
            result.values=filteredData
            return result
        }

        //main UI Thread
        @SuppressLint("NotifyDataSetChanged")
        override fun publishResults(p0: CharSequence?, p1: FilterResults?) {
            itemList.clear()
            if (p1 != null) {
                itemList.addAll(p1.values as ArrayList<Restaurant>)
            }
            notifyDataSetChanged()
        }

    }

}