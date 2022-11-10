package com.example.foodrunnerapk.fragment

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.RelativeLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.foodrunnerapk.R
import com.example.foodrunnerapk.adapter.FavoriteRecyclerAdapter
import com.example.foodrunnerapk.database.DBFavorite
import com.example.foodrunnerapk.database.RestaurantEntity
import com.example.foodrunnerapk.database.RetrieveFavourites
import com.example.foodrunnerapk.model.Restaurant


class FavoriteFragment : Fragment() {
    lateinit var recyclerFavorite: RecyclerView
    lateinit var layoutManager: RecyclerView.LayoutManager
    var resList= listOf<RestaurantEntity>()
    lateinit var recyclerAdapter: FavoriteRecyclerAdapter
    lateinit var progressLayout: RelativeLayout
    lateinit var progressBar: ProgressBar


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view=inflater.inflate(R.layout.fragment_favorite, container, false)


        recyclerFavorite=view.findViewById(R.id.recyclerFavorite)
        layoutManager=LinearLayoutManager(activity as Context)


        resList= RetrieveFavourites(activity as Context,).execute().get()

        if(activity!=null){
           // progressLayout.visibility=View.GONE
            recyclerAdapter= FavoriteRecyclerAdapter(activity as Context,resList)
            recyclerFavorite.adapter=recyclerAdapter
            recyclerFavorite.layoutManager=layoutManager
        }


        return view
    }


}