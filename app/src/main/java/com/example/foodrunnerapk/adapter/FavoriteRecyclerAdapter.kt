package com.example.foodrunnerapk.adapter

import android.content.Context
import android.content.RestrictionEntry
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.foodrunnerapk.R
import com.example.foodrunnerapk.database.DBFavorite
import com.example.foodrunnerapk.database.RestaurantEntity
import com.squareup.picasso.Picasso

class FavoriteRecyclerAdapter(val context: Context, private val itemList:List<RestaurantEntity>):RecyclerView.Adapter<FavoriteRecyclerAdapter.FavoriteViewHolder>() {



    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FavoriteViewHolder {
        val view= LayoutInflater.from(parent.context).inflate(R.layout.recycler_home_single_row,parent,false)
        return FavoriteViewHolder(view)

    }

    override fun onBindViewHolder(holder: FavoriteViewHolder, position: Int) {
        val res=itemList[position]

        holder.txtResName.text=res.resName
        val moddedString= context.getString(R.string.per_person,res.resPrice)
        holder.txtPrice.text=moddedString
        holder.txtRating.text=res.resRating
        Picasso.get().load(res.resImage).error(R.drawable.ic_launcher_background).into(holder.imageView)

        val checkFav= DBFavorite(context,itemList[position],1).execute()
        val isFav=checkFav.get()

        if(isFav){
            holder.btnFavorite.setBackgroundResource(R.drawable.ic_baseline_favorite_24)
        }
        else{
            holder.btnFavorite.setBackgroundResource(R.drawable.ic_favorite)
        }

    }

    override fun getItemCount(): Int {
        return itemList.size
    }

    class FavoriteViewHolder(view: View): RecyclerView.ViewHolder(view){
        val imageView: ImageView =view.findViewById(R.id.imgRestaurantImage)
        val txtResName: TextView =view.findViewById(R.id.txtRestaurantName)
        val txtRating: TextView =view.findViewById(R.id.txtRating)
        val txtPrice: TextView =view.findViewById(R.id.txtPrice)
        val btnFavorite: TextView =view.findViewById(R.id.txtFavoriteIcon)

    }


}