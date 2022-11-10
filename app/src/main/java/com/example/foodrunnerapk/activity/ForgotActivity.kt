package com.example.foodrunnerapk.activity

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.RelativeLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.example.foodrunnerapk.R
import com.example.foodrunnerapk.util.ConnectionManager
import org.json.JSONException
import org.json.JSONObject

class ForgotActivity : AppCompatActivity() {
    lateinit var etMobileNo: EditText
    lateinit var etEmail: EditText
    lateinit var btnNext: Button
    lateinit var toolbar: Toolbar
    lateinit var rlProgress:RelativeLayout
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forgot)

        etMobileNo = findViewById(R.id.etMobileNo)
        etEmail = findViewById(R.id.etEmailAdd)
        btnNext = findViewById(R.id.btnNext)
        toolbar=findViewById(R.id.toolbar)
        rlProgress=findViewById(R.id.rlProgress)
        setToolBar()

        val queue = Volley.newRequestQueue(this@ForgotActivity)
        val url = "http://13.235.250.119/v2/forgot_password/fetch_result"
        val jsonParams = JSONObject()

        btnNext.setOnClickListener {
            val mobile = etMobileNo.text.toString()
            val email = etEmail.text.toString()
            jsonParams.put("mobile_number", mobile)
            jsonParams.put("email", email)

            if (ConnectionManager().checkConnectivity(this@ForgotActivity)) {
                rlProgress.visibility=View.VISIBLE
                val jsonObjectRequest = object : JsonObjectRequest(Method.POST, url, jsonParams,
                    Response.Listener {
                                try{
                                    val data=it.getJSONObject("data")
                                    val success=data.getBoolean("success")

                                    if(success){
                                        rlProgress.visibility=View.GONE
                                        val intent= Intent(this@ForgotActivity,ResetActivity::class.java)
                                        val bundle= Bundle()
                                        bundle.putString("mobile_number",mobile)
                                        intent.putExtras(bundle)
                                        startActivity(intent)
                                        finish()
                                    }else{
                                        rlProgress.visibility=View.GONE
                                        Toast.makeText(
                                            this@ForgotActivity,
                                            "No user found!",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }


                                }catch(e:JSONException){
                                    Toast.makeText(this@ForgotActivity,
                                        "Some unexpected error occur!!!",
                                        Toast.LENGTH_SHORT ).
                                    show()
                                }


                    },
                    Response.ErrorListener {
                        Toast.makeText(this@ForgotActivity,
                            "Volley error Occurred",
                            Toast.LENGTH_SHORT )
                            .show()
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
                val dialog= AlertDialog.Builder(this)
                dialog.setTitle("Error")
                dialog.setMessage("Internet Connection is not Found")
                dialog.setPositiveButton("Open Settings"){ text,listener->
                    val settingsIntent= Intent(Settings.ACTION_WIRELESS_SETTINGS)
                    startActivity(settingsIntent)
                    this.finish()
                }

                dialog.setNegativeButton("Exit"){text,listener->
                    ActivityCompat.finishAffinity(this)
                }
                dialog.create()
                dialog.show()
            }


        }
    }


    private fun setToolBar(){
        setSupportActionBar(toolbar)
        supportActionBar?.title="Forgot Password"
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


}