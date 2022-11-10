package com.example.foodrunnerapk.activity

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.provider.Settings
import android.view.MenuItem
import android.widget.Button
import android.widget.EditText
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

class RegistrationActivity : AppCompatActivity() {
    lateinit var etName: EditText
    lateinit var etEmail: EditText
    lateinit var etMobileNumber: EditText
    lateinit var etDelAddress: EditText
    private lateinit var etPassword: EditText
    lateinit var etConfirmPassword: EditText
    lateinit var btnRegister: Button
    lateinit var sharedPreferences: SharedPreferences
    lateinit var toolbar:Toolbar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registration)

        toolbar=findViewById(R.id.toolbar)
        etName = findViewById(R.id.etName)
        etEmail = findViewById(R.id.etEmail)
        etMobileNumber = findViewById(R.id.etMobileNo)
        etDelAddress = findViewById(R.id.etDelAddress)
        etPassword = findViewById(R.id.etRegPassword)
        etConfirmPassword = findViewById(R.id.etConfirmPassword)
        btnRegister = findViewById(R.id.btnRegister)
        sharedPreferences =
            getSharedPreferences(getString(R.string.preference_file_name), Context.MODE_PRIVATE)
        setToolBar()


        val queue = Volley.newRequestQueue(this@RegistrationActivity)
        val url = "http://13.235.250.119/v2/register/fetch_result"



        btnRegister.setOnClickListener {
            val confirmPass = etConfirmPassword.text.toString()
            val jasonParam = JSONObject(getMap().toMap())

            if (etName.text.toString().length >= 3 &&etPassword.text.toString().length>=4&& etPassword.text.toString() == confirmPass){


                if (ConnectionManager().checkConnectivity(this@RegistrationActivity)) {
                    val jsonRequest = object : JsonObjectRequest(Method.POST, url, jasonParam,
                        Response.Listener {

                            try {
                                val mainObject = it.getJSONObject("data")
                                val success = mainObject.getBoolean("success")


                                if (success) {
                                    val jasonObject = mainObject.getJSONObject("data")
                                    sharedPreferences.edit().putBoolean("isLogIn", true).apply()
                                    sharedPreferences.edit()
                                        .putString("user_id", jasonObject.getString("user_id"))
                                        .apply()
                                    sharedPreferences.edit()
                                        .putString("name", jasonObject.getString("name")).apply()
                                    sharedPreferences.edit().putString(
                                        "mobileNo",
                                        jasonObject.getString("mobile_number")
                                    ).apply()
                                    sharedPreferences.edit()
                                        .putString("email", jasonObject.getString("email")).apply()
                                    sharedPreferences.edit()
                                        .putString("address", jasonObject.getString("address"))
                                        .apply()
                                    val intent = Intent(
                                        this@RegistrationActivity,
                                        HomeActivity::class.java
                                    )
                                    startActivity(intent)
                                } else {
                                    Toast.makeText(
                                        this@RegistrationActivity,
                                        "Mobile number OR Email Id is already registered",
                                        Toast.LENGTH_SHORT
                                    ).show()

                                }

                            } catch (e: JSONException) {
                                Toast.makeText(
                                    this@RegistrationActivity,
                                    "Some unexpected error occur!!!",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        },
                        Response.ErrorListener {

                            Toast.makeText(
                                this@RegistrationActivity,
                                "Volley error Occurred",
                                Toast.LENGTH_SHORT
                            ).show()

                        }) {
                        override fun getHeaders(): MutableMap<String, String> {
                            val headers = HashMap<String, String>()
                            headers["Content-type"] = "application/json"
                            headers["token"] = "9bf534118365f1"
                            return headers

                        }
                    }
                    queue.add(jsonRequest)

                } else {
                    val dialog = AlertDialog.Builder(this@RegistrationActivity)
                    dialog.setTitle("Error")
                    dialog.setMessage("Internet Connection is not Found")
                    dialog.setPositiveButton("Open Settings") { text, listener ->
                        val settingsIntent = Intent(Settings.ACTION_WIRELESS_SETTINGS)
                        startActivity(settingsIntent)
                        this@RegistrationActivity.finish()
                    }

                    dialog.setNegativeButton("Exit") { text, listener ->
                        ActivityCompat.finishAffinity(this@RegistrationActivity)
                    }
                    dialog.create()
                    dialog.show()
                }
        }else{
                Toast.makeText(
                    this@RegistrationActivity,
                    "Please Type Correct Credential may your password not matched",
                    Toast.LENGTH_SHORT
                ).show()
        }
      }
    }

    private fun setToolBar(){
        setSupportActionBar(toolbar)
        supportActionBar?.title="Register Yourself"
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






   private fun getMap(): MutableMap<String,String>{
       val name = etName.text.toString()
       val email = etEmail.text.toString()
       val mobileNo = etMobileNumber.text.toString()
       val delAdd = etDelAddress.text.toString()
       val password = etPassword.text.toString()
        val  param = HashMap<String,String>()
        param["name"]=name
        param["mobile_number"]=mobileNo
        param["password"]=password
        param["address"]=delAdd
        param["email"]=email
        return param
    }


}