package com.example.foodrunnerapk.activity

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.hardware.camera2.params.MultiResolutionStreamConfigurationMap
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.example.foodrunnerapk.R
import com.example.foodrunnerapk.util.ConnectionManager
import org.json.JSONException
import org.json.JSONObject

class LogInActivity : AppCompatActivity() {
    lateinit var etMobileNo:EditText
    lateinit var etPassword:EditText
    private lateinit var btnRegister: Button
    lateinit var txtForgot:TextView
    lateinit var btnLogIn:Button
    lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_log_in)


        etMobileNo=findViewById(R.id.etMobileNo)
        etPassword=findViewById(R.id.etPassword)
        btnRegister=findViewById(R.id.btnRegister)
        txtForgot=findViewById(R.id.txtForgot)
        btnLogIn=findViewById(R.id.btnLogIn)
        sharedPreferences=getSharedPreferences(getString(R.string.preference_file_name), MODE_PRIVATE)



        val isLogIn=sharedPreferences.getBoolean("isLogIn",false)

        if(isLogIn){
            val intent=Intent(this@LogInActivity,HomeActivity::class.java)
            startActivity(intent)
        }




        val queue = Volley.newRequestQueue(this@LogInActivity)
        val url = "http://13.235.250.119/v2/login/fetch_result"



        btnLogIn.setOnClickListener {

            val mobileNo=etMobileNo.text.toString()
            val password=etPassword.text.toString()
            val jasonParams=JSONObject(getMap(mobileNo,password).toMap())

            if (ConnectionManager().checkConnectivity(this@LogInActivity)) {
                val jasonObjectRequest= object: JsonObjectRequest(Method.POST,url,jasonParams,
                Response.Listener {
                           try{
                               val mainObject = it.getJSONObject("data")
                               val success = mainObject.getBoolean("success")

                               if(success){
                                   val data=mainObject.getJSONObject("data")

                                   sharedPreferences.edit().putBoolean("isLogIn", true).apply()
                                   sharedPreferences.edit().putString("user_id",data.getString("user_id")).apply()
                                   sharedPreferences.edit().putString("name",data.getString("name")).apply()
                                   sharedPreferences.edit().putString("mobileNo",data.getString("mobile_number")).apply()
                                   sharedPreferences.edit().putString("email", data.getString("email")).apply()
                                   sharedPreferences.edit().putString("address", data.getString("address")).apply()

                                   val intent=Intent(this@LogInActivity,HomeActivity::class.java)
                                   startActivity(intent)
                               }else{
                                   Toast.makeText(
                                       this@LogInActivity,
                                       "Mobile Number not registered",
                                       Toast.LENGTH_SHORT
                                   ).show()
                               }
                           }catch(e:JSONException){
                               Toast.makeText(this@LogInActivity,
                                   "Some unexpected error occur!!!",
                                   Toast.LENGTH_SHORT ).
                               show()
                           }
                },
                Response.ErrorListener {
                    Toast.makeText(this@LogInActivity,
                        "Volley error Occurred",
                        Toast.LENGTH_SHORT )
                        .show()
                }){

                    override fun getHeaders(): MutableMap<String, String> {
                        val headers = HashMap<String, String>()
                        headers["Content-type"] = "application/json"
                        headers["token"] = "9bf534118365f1"
                        return headers

                    }
            }
                queue.add(jasonObjectRequest)

           }else{
                val dialog= AlertDialog.Builder(this@LogInActivity)
                dialog.setTitle("Error")
                dialog.setMessage("Internet Connection is not Found")
                dialog.setPositiveButton("Open Settings"){ text,listener->
                    val settingsIntent= Intent(Settings.ACTION_WIRELESS_SETTINGS)
                    startActivity(settingsIntent)
                    this@LogInActivity.finish()
                }

                dialog.setNegativeButton("Exit"){text,listener->
                    ActivityCompat.finishAffinity(this@LogInActivity)
                }
                dialog.create()
                dialog.show()
           }
        }

        txtForgot.setOnClickListener {
            val intent=Intent(this@LogInActivity,ForgotActivity::class.java)
            startActivity(intent)

        }

        btnRegister.setOnClickListener {
            val intent=Intent(this@LogInActivity,RegistrationActivity::class.java)
            startActivity(intent)

        }

    }

    fun getMap(mobile:String,password:String): MutableMap<String,String>{
        val  param = HashMap<String,String>()
        param["mobile_number"]=mobile
        param["password"]=password
      return param
    }

}