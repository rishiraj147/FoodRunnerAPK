package com.example.foodrunnerapk.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.foodrunnerapk.R
import com.example.foodrunnerapk.model.RestaurantDish


class MenuRecyclerAdapter
    (val context: Context, var itemList:ArrayList<RestaurantDish>,
     private val listener:OnItemClickListener)
    :RecyclerView.Adapter<MenuRecyclerAdapter.MenuViewHolder>() {


    companion object{
        var isCartEmpty=true
        val arrayRemoveBtn= arrayListOf<Button>()
        val arrayAddBtn= arrayListOf<Button>()

    }

    interface OnItemClickListener {
        fun onAddItemClick(foodItem: RestaurantDish)
        fun onRemoveItemClick(foodItem: RestaurantDish)
    }





    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MenuViewHolder {
        val view= LayoutInflater.from(parent.context).inflate(R.layout.recycler_restaurant_menu_single_row,parent,false)
        return MenuViewHolder(view)
    }

    override fun onBindViewHolder(holder: MenuViewHolder, position: Int) {
         val dish= itemList[position]
        holder.dishName.text=dish.dishName
        holder.dishPrice.text=context.getString(R.string.dish_price,dish.dishPrice)
        val count=position+1
        holder.srNo.text="$count"



        holder.btnAdd.setOnClickListener {
            arrayAddBtn.add(holder.btnAdd)
            holder.btnAdd.visibility=View.GONE
            holder.btnRemove.visibility=View.VISIBLE
            arrayRemoveBtn.add(holder.btnRemove)
            listener.onAddItemClick(dish)
        }
        holder.btnRemove.setOnClickListener {
            holder.btnAdd.visibility=View.VISIBLE
            holder.btnRemove.visibility=View.GONE
            listener.onRemoveItemClick(dish)
        }

    }

    override fun getItemCount(): Int {
        return itemList.size

    }

    class MenuViewHolder(view: View):RecyclerView.ViewHolder(view){
        val dishName: TextView =view.findViewById(R.id.txtDishName)
        val dishPrice:TextView=view.findViewById(R.id.txtDishPrice)
        val srNo:TextView=view.findViewById(R.id.txtSrNo)
        val btnAdd: Button = view.findViewById(R.id.btnAdd)
        val btnRemove:Button=view.findViewById(R.id.btnRemove)

    }
}
