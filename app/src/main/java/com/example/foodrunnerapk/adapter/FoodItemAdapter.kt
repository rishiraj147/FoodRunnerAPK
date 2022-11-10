package com.example.foodrunnerapk.adapter

import android.content.Context
import android.icu.text.Transliterator
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.foodrunnerapk.R
import com.example.foodrunnerapk.model.FoodItem

class FoodItemAdapter(val context: Context, var orderedItemList:ArrayList<FoodItem>):
    RecyclerView.Adapter<FoodItemAdapter.FoodItemViewHolder>() {



    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FoodItemViewHolder {
        val view= LayoutInflater.from(parent.context).
        inflate(R.layout.recycler_cart_single_row,parent,false)
        return FoodItemViewHolder(view)
    }

    override fun onBindViewHolder(holder: FoodItemViewHolder, position: Int) {
        val data=orderedItemList[position]
        holder.dishName.text=data.foodName
        holder.dishPrice.text=data.foodCost

    }

    override fun getItemCount(): Int {
       return orderedItemList.size
    }

    class FoodItemViewHolder(view: View):RecyclerView.ViewHolder(view){
        val dishName: TextView =view.findViewById(R.id.txtDishName)
        val dishPrice: TextView =view.findViewById(R.id.txtDishPrice)

    }


}