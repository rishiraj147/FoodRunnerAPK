package com.example.foodrunnerapk.fragment

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.text.Editable
import android.text.TextWatcher
import android.view.*
import android.widget.EditText
import android.widget.RelativeLayout
import androidx.fragment.app.Fragment
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.example.foodrunnerapk.R
import com.example.foodrunnerapk.adapter.RestaurantRecyclerAdapter
import com.example.foodrunnerapk.model.Restaurant
import com.example.foodrunnerapk.util.ConnectionManager
import org.json.JSONException
import java.util.*
import kotlin.Comparator
import kotlin.collections.HashMap

class RestaurantsFragment : Fragment() {
      lateinit var recyclerHome: RecyclerView
      lateinit var layoutManager:RecyclerView.LayoutManager
      var restaurantList= arrayListOf<Restaurant>()
      lateinit var recyclerAdapter:RestaurantRecyclerAdapter
      lateinit var etSearch:EditText
      lateinit var rlProgress:RelativeLayout

    //sort according to ratings
    var ratingComparator = Comparator<Restaurant> { rest1, rest2 ->

        if (rest1.restaurantRating.compareTo(rest2.restaurantRating, true) == 0) {
            rest1.restaurantName.compareTo(rest2.restaurantName, true)
        } else {
            rest1.restaurantRating.compareTo(rest2.restaurantRating, true)
        }
    }

    //sort according to cost(decreasing)
    var costComparator = Comparator<Restaurant> { rest1, rest2 ->
        rest1.restaurantPrice.compareTo(rest2.restaurantPrice, true)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view= inflater.inflate(R.layout.fragment_restaurants, container, false)

        rlProgress=view.findViewById(R.id.rlProgress)
        recyclerHome=view.findViewById(R.id.recyclerHome)
        layoutManager=LinearLayoutManager(activity)
        setHasOptionsMenu(true)


        //to search restaurants
        etSearch=view.findViewById(R.id.etSearch)
        etSearch.addTextChangedListener(object: TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun afterTextChanged(p0: Editable?) {
                recyclerAdapter.filter.filter(p0.toString())
            }
        })



        val queue= Volley.newRequestQueue(activity as Context)
        val url="http://13.235.250.119/v2/restaurants/fetch_result/"
   if(ConnectionManager().checkConnectivity(activity as Context)) {
         rlProgress.visibility=View.VISIBLE
       val jsonObjectRequest = object : JsonObjectRequest(Method.GET, url, null,
           Response.Listener {
               try{
                   val mainObject = it.getJSONObject("data")
                   val success = mainObject.getBoolean("success")

                   if (success) {
                       rlProgress.visibility=View.GONE
                       val data = mainObject.getJSONArray("data")
                       for (i in 0 until data.length()) {
                           val dishJasonObject = data.getJSONObject(i)
                           val restaurantObject = Restaurant(
                               dishJasonObject.getString("id"),
                               dishJasonObject.getString("name"),
                               dishJasonObject.getString("rating"),
                               dishJasonObject.getString("cost_for_one"),
                               dishJasonObject.getString("image_url")
                           )
                           restaurantList.add(restaurantObject)
                       }
                       recyclerAdapter =
                           RestaurantRecyclerAdapter(activity as Context, restaurantList)
                       recyclerHome.adapter = recyclerAdapter
                       recyclerHome.layoutManager = layoutManager
                   } else {
                       Toast.makeText(
                           activity as Context,
                           "Some error occurred",
                           Toast.LENGTH_SHORT
                       )
                           .show()
                   }

               }catch(e: JSONException){
                   Toast.makeText(activity as Context,"Some unexpected error occur!!!",Toast.LENGTH_SHORT ).show()
               } },
           Response.ErrorListener {
               if(activity!=null){
                   Toast.makeText(activity as Context,"Volley error Occurred",Toast.LENGTH_SHORT ).show()
               }

           }) {
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

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_restaurant_fragment, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {

            //to sort by cost low to high
            R.id.sortByCostLToH->{
                Collections.sort(restaurantList,costComparator)
            }

            //to sort by cost high to low
            R.id.sortByCostHToL->{
                Collections.sort(restaurantList,costComparator)
                restaurantList.reverse()
            }

            //to sort by rating
            R.id.sortByRating->{
                Collections.sort(restaurantList,ratingComparator)
                restaurantList.reverse()
            }
        }
        recyclerAdapter.notifyDataSetChanged()

        return super.onOptionsItemSelected(item)
    }





}