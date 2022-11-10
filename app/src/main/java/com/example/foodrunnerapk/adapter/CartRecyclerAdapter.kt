package com.example.foodrunnerapk.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.foodrunnerapk.R
import com.example.foodrunnerapk.database.OrderEntity
import com.example.foodrunnerapk.model.RestaurantDish

class CartRecyclerAdapter(val context:Context,var cartList:ArrayList<RestaurantDish>): RecyclerView.Adapter<CartRecyclerAdapter.CartViewHolder>(){


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CartViewHolder {
        val view= LayoutInflater.from(parent.context).
        inflate(R.layout.recycler_cart_single_row,parent,false)
        return CartViewHolder(view)
    }

    override fun onBindViewHolder(holder: CartViewHolder, position: Int) {
        val cartObject = cartList[position]

        holder.dishName.text=cartObject.dishName
        holder.dishPrice.text=cartObject.dishPrice

      //  holder.dishPrice.text=cartObject.foodItem



    }

    override fun getItemCount(): Int {
       return cartList.size
    }



    class CartViewHolder(view: View):RecyclerView.ViewHolder(view){
        val dishName: TextView =view.findViewById(R.id.txtDishName)
        val dishPrice: TextView =view.findViewById(R.id.txtDishPrice)

    }

}