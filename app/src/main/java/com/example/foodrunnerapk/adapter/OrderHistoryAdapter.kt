package com.example.foodrunnerapk.adapter

import android.content.Context
import android.content.SharedPreferences
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.example.foodrunnerapk.R
import com.example.foodrunnerapk.fragment.OrderHistoryFragment
import com.example.foodrunnerapk.model.FoodItem
import com.example.foodrunnerapk.model.OrderedHistory
import org.json.JSONException

class OrderHistoryAdapter(val context: Context, private var orderedRes:ArrayList<OrderedHistory>):
    RecyclerView.Adapter<OrderHistoryAdapter.OrderedViewHolder>() {




    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderedViewHolder {
        val view= LayoutInflater.from(parent.context).
        inflate(R.layout.recycler_order_history_single_row,parent,false)
        return OrderedViewHolder(view)
    }

    override fun onBindViewHolder(holder: OrderedViewHolder, position: Int) {
        val data=orderedRes[position]
        holder.txtResName.text=data.resName
        holder.txtOrderDate.text=data.orderedDate

        var sharedPreferences: SharedPreferences = context.getSharedPreferences(context.getString(R.string.preference_file_name),
            Context.MODE_PRIVATE
        )
        val userId=sharedPreferences.getString("user_id",null)

        val queue= Volley.newRequestQueue(context)
        val url ="http://13.235.250.119/v2/orders/fetch_result/${userId}"

        val jsonObjectRequest=object: JsonObjectRequest(Method.GET,url,null,
            Response.Listener {
                    try{
                        val foodOrdered=arrayListOf<FoodItem>()

                        val data=it.getJSONObject("data")
                        val success=data.getBoolean("success")
                        if(success){
                            val data1=data.getJSONArray("data")
                            val mainData=data1.getJSONObject(position)
                            val foodItemArray=mainData.getJSONArray("food_items")

                            for(j in 0 until foodItemArray.length()){
                                    val data2=foodItemArray.getJSONObject(j)
                                    val foodName=data2.getString("name")
                                    val foodPrice=data2.getString("cost")
                                    foodOrdered.add(FoodItem(foodName,foodPrice))
                                }
                            holder.recyclerOrderedFoodItems.adapter=FoodItemAdapter(context,foodOrdered)
                            holder.recyclerOrderedFoodItems.layoutManager=LinearLayoutManager(context)

                        }

                    }catch (e:JSONException){
                        Toast.makeText(context,
                            "Some unexpected error occur!!!",
                            Toast.LENGTH_SHORT ).
                        show()
                    }

            },
            Response.ErrorListener {
                Toast.makeText(context,"Volley error Occurred", Toast.LENGTH_SHORT).show()

            }){

            override fun getHeaders(): MutableMap<String, String> {
                val headers = HashMap<String, String>()
                headers["Content-type"] = "application/json"
                headers["token"] = "9bf534118365f1"
                return headers
            }


        }
        queue.add(jsonObjectRequest)

    }

    override fun getItemCount(): Int {
      return orderedRes.size
    }


    class OrderedViewHolder(view: View):RecyclerView.ViewHolder(view){
        val txtResName:TextView=view.findViewById(R.id.txtResName)
        val txtOrderDate:TextView=view.findViewById(R.id.txtorderDate)
        val recyclerOrderedFoodItems:RecyclerView=view.findViewById(R.id.recyclerOrderedFoodItems)

    }


}