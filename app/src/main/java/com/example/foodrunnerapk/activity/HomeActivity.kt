package com.example.foodrunnerapk.activity

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.provider.Settings
import android.view.MenuItem
import android.view.View
import android.widget.FrameLayout
import android.widget.TextView
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.app.ActivityCompat
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import com.example.foodrunnerapk.R
import com.example.foodrunnerapk.fragment.*
import com.google.android.material.navigation.NavigationView

class HomeActivity : AppCompatActivity() {
    lateinit var drawerLayout: DrawerLayout
    private lateinit var coordinatorLayout: CoordinatorLayout
    lateinit var toolbar: Toolbar
    lateinit var frameLayout: FrameLayout
    private lateinit var navigationView: NavigationView
    var previousMenuItem: MenuItem? =null
    lateinit var txtName:TextView
    private lateinit var txtMobile:TextView
    lateinit var sharePreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        drawerLayout=findViewById(R.id.drawerLayout)
        coordinatorLayout=findViewById(R.id.coordinatorLayout)
        toolbar=findViewById(R.id.toolbar)
        frameLayout=findViewById(R.id.frameLayout)
        navigationView=findViewById(R.id.navigationView)

       val  headerView: View =navigationView.getHeaderView(0)

        txtName=headerView.findViewById(R.id.txtName)
        txtMobile=headerView.findViewById(R.id.txtMobile)
        sharePreferences=getSharedPreferences(getString(R.string.preference_file_name), MODE_PRIVATE)

        txtName.text=sharePreferences.getString("name",null)

        txtMobile.text=getString(R.string.mobileNo,sharePreferences.getString("mobileNo",null))

        setToolBar()
        val view= RestaurantsFragment()
        val title="All Restaurants"
        openFrag(title,view)
        navigationView.setCheckedItem(R.id.home)

        val actionBarDrawerToggle= ActionBarDrawerToggle(this@HomeActivity,
            drawerLayout,
            R.string.open_drawer,
            R.string.close_drawer)
        drawerLayout.addDrawerListener(actionBarDrawerToggle)
        actionBarDrawerToggle.syncState()

        navigationView.setNavigationItemSelectedListener {

            if(previousMenuItem !=null){
                previousMenuItem?.isChecked =false
            }
            it.isCheckable=true
            it.isChecked=true
            previousMenuItem=it



            when(it.itemId){
                R.id.home->{
                    openFrag("All Restaurants",RestaurantsFragment())

                }
                R.id.myProfile->{
                    openFrag("My Profile", MyProfileFragment())
                }

                R.id.favRestaurant->{
                    openFrag("Favorite restaurants",FavoriteFragment())
                }

                R.id.orderHistory->{
                     openFrag("My Previous Orders",OrderHistoryFragment())
                }

                R.id.FAQs->{
                    openFrag("Frequently Asked Questions", FaqsFragment())
                }
                R.id.logOut->{
                    drawerLayout.closeDrawers()

                    val dialog= AlertDialog.Builder(this)
                    dialog.setTitle("Log out")
                    dialog.setMessage("Are you sure? ")
                    dialog.setPositiveButton("Yes"){ _,_->
                        sharePreferences.edit().clear().apply()
                        startActivity(Intent(this,LogInActivity::class.java))
                        ActivityCompat.finishAffinity(this)
                    }
                    dialog.setNegativeButton("No"){_,_->
                        navigationView.setCheckedItem(R.id.home)
                        openFrag("All Restaurants",RestaurantsFragment())
                    }
                    dialog.create()
                    dialog.show()
                }
            }
            return@setNavigationItemSelectedListener true
        }

    }
    private fun openFrag(title:String, view:Fragment){
        supportFragmentManager.beginTransaction()
            .replace(R.id.frameLayout,view)
            .commit()
        supportActionBar?.title=title
        drawerLayout.closeDrawers()
    }

    private fun setToolBar(){
        setSupportActionBar(toolbar)
        supportActionBar?.title="All Restaurants"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)


    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id=item.itemId
        if(id==android.R.id.home){
            drawerLayout.openDrawer(GravityCompat.START)
        }

        return super.onOptionsItemSelected(item)
    }

    override fun onBackPressed() {
        val frag= supportFragmentManager.findFragmentById(R.id.frameLayout)

        when(frag){
            !is RestaurantsFragment ->{
                navigationView.setCheckedItem(R.id.home)
                openFrag("All Restaurants",RestaurantsFragment())
            }

            else ->super.onBackPressed()
        }
    }

}