package com.example.foodrunnerapk.activity

import android.app.Dialog
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.example.foodrunnerapk.R
import com.example.foodrunnerapk.activity.RestaurantMenuActivity.Companion.btnProceed
import com.example.foodrunnerapk.adapter.CartRecyclerAdapter
import com.example.foodrunnerapk.adapter.MenuRecyclerAdapter.Companion.arrayAddBtn
import com.example.foodrunnerapk.adapter.MenuRecyclerAdapter.Companion.arrayRemoveBtn
import com.example.foodrunnerapk.adapter.MenuRecyclerAdapter.Companion.isCartEmpty
import com.example.foodrunnerapk.database.ClearDBAsync
import com.example.foodrunnerapk.database.GetItemsFromDBAsync
import com.example.foodrunnerapk.model.RestaurantDish
import com.example.foodrunnerapk.util.ConnectionManager
import com.google.gson.Gson
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject

class CartActivity : AppCompatActivity() {
    lateinit var toolbar: Toolbar
    private lateinit var txtResName:TextView
    lateinit var recyclerCartView: RecyclerView
    lateinit var layoutManager: RecyclerView.LayoutManager
    lateinit var recyclerCartAdapter: CartRecyclerAdapter
    lateinit var btnPlace:Button
    var orderList= arrayListOf<RestaurantDish>()
    lateinit var sharePreferences: SharedPreferences
     var resId:String? =""
    var foodItem:String=""


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cart)

        txtResName=findViewById(R.id.txtResName)
        recyclerCartView=findViewById(R.id.recyclerViewCart)
        layoutManager=LinearLayoutManager(this@CartActivity)
        btnPlace=findViewById(R.id.btnPlaceOrder)
        toolbar=findViewById(R.id.toolbar)
        setToolBar()

        sharePreferences=getSharedPreferences(getString(R.string.preference_file_name), MODE_PRIVATE)

        val userId= sharePreferences.getString("user_id",null)
        println("UserId"+userId)

        val bundle= intent.extras
        val resName= bundle?.getString("resName")
        txtResName.text=resName
        resId= bundle?.getString("resId")


       val dbList= GetItemsFromDBAsync(this@CartActivity).execute().get()

        for (element in dbList) {
            orderList.addAll(
                Gson().fromJson(element.foodItem, Array<RestaurantDish>::class.java).asList()
            )
        }

        if (orderList.isEmpty()) {
            btnPlace.visibility = View.GONE
           // rlLoading.visibility = View.VISIBLE
        } else {
            btnPlace.visibility = View.VISIBLE
           // rlLoading.visibility = View.GONE
        }

        recyclerCartAdapter= CartRecyclerAdapter(this@CartActivity,orderList)
        recyclerCartView.adapter=recyclerCartAdapter
        recyclerCartView.layoutManager=layoutManager

     // to calculate the total cost that have to pay
        var sum=0
        for(i in 0 until orderList.size){
            sum+=orderList[i].dishPrice.toInt()
        }

        val totalCost=getString(R.string.order_place,sum)
        btnPlace.text = totalCost

        btnPlace.setOnClickListener {

            val queue=Volley.newRequestQueue(this)
            val url="http://13.235.250.119/v2/place_order/fetch_result/"

            val jsonParam= JSONObject()
            jsonParam.put("user_id",userId)
            jsonParam.put("restaurant_id",resId)
            jsonParam.put("total_cost",sum.toString())

            val foodArray=JSONArray()

            for(i in 0 until orderList.size){
                val foodId=JSONObject()
                foodId.put("food_item_id",orderList[i].dishId)
                foodArray.put(i,foodId)
            }
            jsonParam.put("food",foodArray)

            if(ConnectionManager().checkConnectivity(this@CartActivity)){

                val jsonObjectRequest=object:JsonObjectRequest(Method.POST,url,jsonParam,
                Response.Listener {
                                  try{
                                      val data=it.getJSONObject("data")
                                      val success=data.getBoolean("success")

                                      if(success){
                                          //because order has been placed then I have to clear the database
                                          val clearCart=
                                              ClearDBAsync(this@CartActivity).execute().get()
                                              isCartEmpty = true


                                          val dialog = Dialog(
                                              this@CartActivity,
                                              android.R.style.Theme_Black_NoTitleBar_Fullscreen
                                          )
                                          dialog.setContentView(R.layout.order_placed_dialog)
                                          dialog.show()
                                          dialog.setCancelable(false)
                                          val btnOk = dialog.findViewById<Button>(R.id.btnOk)
                                          btnOk.setOnClickListener {
                                             // dialog.dismiss()
                                              startActivity(Intent(this@CartActivity,HomeActivity::class.java))
                                              ActivityCompat.finishAffinity(this@CartActivity)

                                          }

                                      }
                                      else {

                                          Toast.makeText(this@CartActivity,
                                              "Some Error occurred",
                                              Toast.LENGTH_SHORT)
                                              .show()
                                      }

                                  }catch (e:JSONException){
                                      Toast.makeText(this@CartActivity,
                                          "Some Error occurred",
                                          Toast.LENGTH_SHORT)
                                          .show()
                                  }

                },
                Response.ErrorListener {
                    Toast.makeText(this@CartActivity, it.message, Toast.LENGTH_SHORT).show()
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

        }
    }

    private fun setToolBar(){
        setSupportActionBar(toolbar)
        supportActionBar?.title="My Cart"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id=item.itemId
        if(id==android.R.id.home){
            onBackPressed()
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onBackPressed() {
        val clearCart=
            ClearDBAsync(this@CartActivity).execute().get()
           isCartEmpty = true

        for(i in 0 until arrayRemoveBtn.size){
            arrayRemoveBtn[i].visibility=View.GONE
            arrayAddBtn[i].visibility=View.VISIBLE
        }
        btnProceed.visibility=View.GONE
            super.onBackPressed()
    }
}