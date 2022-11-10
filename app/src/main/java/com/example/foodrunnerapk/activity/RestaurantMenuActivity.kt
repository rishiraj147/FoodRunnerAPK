package com.example.foodrunnerapk.activity

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.example.foodrunnerapk.R
import com.example.foodrunnerapk.adapter.MenuRecyclerAdapter
import com.example.foodrunnerapk.adapter.MenuRecyclerAdapter.Companion.isCartEmpty
import com.example.foodrunnerapk.database.DBFavorite
import com.example.foodrunnerapk.database.ItemsOfCart
import com.example.foodrunnerapk.database.RestaurantEntity
import com.example.foodrunnerapk.model.RestaurantDish
import com.example.foodrunnerapk.util.ConnectionManager
import com.google.gson.Gson
import org.json.JSONException

class RestaurantMenuActivity : AppCompatActivity() {
    lateinit var toolbar:Toolbar
    lateinit var favoriteIcon: TextView
    lateinit var recyclerView: RecyclerView
    lateinit var rlProgress: RelativeLayout
    lateinit var layoutManager: RecyclerView.LayoutManager
    lateinit var recyclerAdapter: MenuRecyclerAdapter
    var dishList= arrayListOf<RestaurantDish>()
    var orderList= arrayListOf<RestaurantDish>()

    private var resId:String = ""
    private var resName:String = ""

    companion object{
        @SuppressLint("StaticFieldLeak")
        lateinit var btnProceed:Button
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_restaurant_menu)

        btnProceed=findViewById(R.id.btnProceed)
        favoriteIcon=findViewById(R.id.txtFavoriteIcon)
        toolbar=findViewById(R.id.toolbar)
        rlProgress=findViewById(R.id.rlProgress)


        recyclerView=findViewById(R.id.recyclerMenu)
        layoutManager= LinearLayoutManager(this@RestaurantMenuActivity)


        resId= intent.getStringExtra("restaurant_id").toString()
        resName= intent.getStringExtra("resName").toString()
        setToolBar()
        btnProceed.visibility=View.GONE

        val resEntity= RestaurantEntity(resId,null,null,null,null)
        val checkFav= DBFavorite(this,resEntity,1).execute()
        val isFav=checkFav.get()

        if(isFav){
            favoriteIcon.setBackgroundResource(R.drawable.ic_baseline_favorite_24)
        }
        else{
            favoriteIcon.setBackgroundResource(R.drawable.ic_favorite)
        }



        btnProceed.setOnClickListener {

            proceedToCart()
        }

        val queue= Volley.newRequestQueue(this@RestaurantMenuActivity )
        val url=getString(R.string.restaurant_menu_fetch_link,resId)

        if(ConnectionManager().checkConnectivity(this@RestaurantMenuActivity)) {
            rlProgress.visibility=View.VISIBLE
            val jsonObjectRequest=object:JsonObjectRequest(Method.GET,url,null,
                Response.Listener {
                        try{
                            val data=it.getJSONObject("data")
                            val success=data.getBoolean("success")

                            if(success){
                                rlProgress.visibility=View.GONE
                                val menuData=data.getJSONArray("data")

                                for(i in 0 until menuData.length()){
                                    val dishJsonObject= menuData.getJSONObject(i)
                                    val dishObject=RestaurantDish(
                                        dishJsonObject.getString("id"),
                                        dishJsonObject.getString("name") ,
                                        dishJsonObject.getString("cost_for_one"),
                                    )
                                   dishList.add(dishObject)
                                }
                                recyclerAdapter=MenuRecyclerAdapter(this@RestaurantMenuActivity,dishList,object:
                                MenuRecyclerAdapter.OnItemClickListener{
                                    override fun onAddItemClick(foodItem: RestaurantDish) {
                                        orderList.add(foodItem)
                                        if(orderList.size>0){
                                            btnProceed.visibility= View.VISIBLE
                                            isCartEmpty=false
                                        }
                                    }
                                    override fun onRemoveItemClick(foodItem: RestaurantDish) {
                                        orderList.remove(foodItem)
                                        if (orderList.isEmpty()) {
                                            btnProceed.visibility =View.GONE
                                            isCartEmpty = true
                                        }
                                    }
                                })
                                recyclerView.adapter=recyclerAdapter
                                recyclerView.layoutManager=layoutManager

                            }else{
                                Toast.makeText(
                                    this,
                                    "Some error occurred",
                                    Toast.LENGTH_SHORT
                                )
                                    .show()
                            }

                        }catch (e:JSONException){
                            Toast.makeText(this,"Some unexpected error occur!!!",
                                Toast.LENGTH_SHORT ).show()
                        }

                },

                Response.ErrorListener {
                    Toast.makeText(this,"Volley error Occurred"
                        ,Toast.LENGTH_SHORT )
                        .show()
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
            //open DialogBox Code
            val dialog= AlertDialog.Builder(this)
            dialog.setTitle("Error")
            dialog.setMessage("Internet Connection is not Found")
            dialog.setPositiveButton("Open Settings"){ text,listener->
                val settingsIntent= Intent(Settings.ACTION_WIRELESS_SETTINGS)
                startActivity(settingsIntent)
                this?.finish()
            }

            dialog.setNegativeButton("Exit"){text,listener->
                ActivityCompat.finishAffinity(this)
            }
            dialog.create()
            dialog.show()

        }
    }

    private fun setToolBar(){
        setSupportActionBar(toolbar)
        supportActionBar?.title=resName
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

    private fun proceedToCart(){
        //here we supposed to add orderList in dataBase Using Gson()

        val gson=Gson()

        val foodItems=gson.toJson(orderList)
        val async=ItemsOfCart(this@RestaurantMenuActivity,resId,foodItems,1).execute()
        val result=async.get()

        if(result){

            val data=Bundle()

            data.putString("resId",resId)
            data.putString("resName",resName)
            val intent=Intent(this@RestaurantMenuActivity,CartActivity::class.java)
            intent.putExtras(data)
            orderList.clear()
            startActivity(intent)

        }else{
            Toast.makeText(this, " BOOM ITEMS OF CART", Toast.LENGTH_SHORT).show()
        }

    }

}