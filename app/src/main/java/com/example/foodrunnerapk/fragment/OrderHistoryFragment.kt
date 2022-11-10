package com.example.foodrunnerapk.fragment

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.example.foodrunnerapk.R
import com.example.foodrunnerapk.adapter.FoodItemAdapter
import com.example.foodrunnerapk.adapter.OrderHistoryAdapter
import com.example.foodrunnerapk.model.FoodItem
import com.example.foodrunnerapk.model.OrderedHistory
import com.example.foodrunnerapk.util.ConnectionManager
import org.json.JSONException


class OrderHistoryFragment : Fragment() {

    lateinit var recyclerOrderHistory:RecyclerView
    lateinit var layoutManager: RecyclerView.LayoutManager
    lateinit var recyclerAdapter: OrderHistoryAdapter
    private lateinit var sharedPreferences:SharedPreferences
    lateinit var rlProgress: RelativeLayout
    var placedOrder= arrayListOf<OrderedHistory>()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view=inflater.inflate(R.layout.fragment_order_history, container, false)

        sharedPreferences=requireActivity().applicationContext.getSharedPreferences(getString(R.string.preference_file_name),MODE_PRIVATE)

        val userId=sharedPreferences.getString("user_id",null)

        recyclerOrderHistory=view.findViewById(R.id.recyclerOrderHistory)
        rlProgress=view.findViewById(R.id.rlProgress)

        layoutManager=LinearLayoutManager(activity as Context)

        val queue=Volley.newRequestQueue(activity as Context)
        val url ="http://13.235.250.119/v2/orders/fetch_result/${userId}"

        if(ConnectionManager().checkConnectivity(activity as Context)){
            rlProgress.visibility=View.VISIBLE
            val jsonObjectRequest=object: JsonObjectRequest(Method.GET,url,null,
                Response.Listener {
                    try{
                        val data=it.getJSONObject("data")
                        val success=data.getBoolean("success")

                        if(success){
                            rlProgress.visibility=View.GONE
                            val data=data.getJSONArray("data")

                            for(i in 0 until data.length()){
                                val mainData=data.getJSONObject(i)
                                val resName=mainData.getString("restaurant_name")
                                var date=mainData.getString("order_placed_at")
                                val d1=date.substring(0,6)+"20"+date.substring(6,8)
                                date=d1.replace('-','/')


                                placedOrder.add(OrderedHistory(resName,date))
                            }
                            recyclerAdapter= OrderHistoryAdapter(activity as Context,placedOrder)
                            recyclerOrderHistory.adapter=recyclerAdapter
                            recyclerOrderHistory.layoutManager=layoutManager

                        }else{
                            Toast.makeText(
                                activity as Context,
                                "Some Expected Error occured",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }catch (e:JSONException){
                        Toast.makeText(activity as Context,
                            "Some unexpected error occur!!!",
                            Toast.LENGTH_SHORT ).
                        show()
                    }
                },

                Response.ErrorListener {
                    Toast.makeText(activity as Context,"Volley error Occurred", Toast.LENGTH_SHORT).show()
                }){
                override fun getHeaders(): MutableMap<String, String> {
                    val headers = HashMap<String, String>()
                    headers["Content-type"] = "application/json"
                    headers["token"] = "9bf534118365f1"
                    return headers
                }

            }
             queue.add(jsonObjectRequest)

        }else{
            val dialog= AlertDialog.Builder(activity as Context)
            dialog.setTitle("Error")
            dialog.setMessage("Internet Connection is not Found")
            dialog.setPositiveButton("Open Settings"){ text,listener->
                val settingsIntent= Intent(Settings.ACTION_WIRELESS_SETTINGS)
                startActivity(settingsIntent)
                activity?.finish()
            }

            dialog.setNegativeButton("Exit"){text,listener->
                ActivityCompat.finishAffinity(activity as Activity)
            }
            dialog.create()
            dialog.show()
        }
        return view
    }


}