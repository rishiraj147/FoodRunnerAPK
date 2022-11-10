package com.example.foodrunnerapk.activity

import android.app.AlertDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.core.app.ActivityCompat
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.example.foodrunnerapk.R
import com.example.foodrunnerapk.util.ConnectionManager
import org.json.JSONException
import org.json.JSONObject

class ResetActivity : AppCompatActivity() {
    lateinit var etOTP: EditText
    lateinit var etNewPassword: EditText
    lateinit var etConfirmPassword: EditText
    lateinit var btnSubmit: Button
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reset)

         btnSubmit=findViewById(R.id.btnSubmit)
         etOTP=findViewById(R.id.etOTP)
         etNewPassword=findViewById(R.id.etNewPassword)
        etConfirmPassword=findViewById(R.id.etConfirmPassword)

        val bundle= intent.extras
        val mobile= bundle?.getString("mobile_number")





        val queue = Volley.newRequestQueue(this@ResetActivity)
        val url = "http://13.235.250.119/v2/reset_password/fetch_result"
        val jsonParams = JSONObject()


        btnSubmit.setOnClickListener {

            val otp = etOTP.text.toString()
            val password = etNewPassword.text.toString()
            jsonParams.put("mobile_number", mobile)
            jsonParams.put("password", password)
            jsonParams.put("otp", otp)

            if (password.length >= 4 && password == etConfirmPassword.text.toString()){
                if (ConnectionManager().checkConnectivity(this@ResetActivity)) {
                    val jsonObjectRequest = object : JsonObjectRequest(Method.POST, url, jsonParams,
                        Response.Listener {
                            try {
                                val data = it.getJSONObject("data")
                                val success = data.getBoolean("success")
                                if (success) {
                                    Toast.makeText(
                                        this@ResetActivity,
                                        data.getString("successMessage"),
                                        Toast.LENGTH_SHORT
                                    ).show()
                                    startActivity(Intent(this, LogInActivity::class.java))
                                    finish()
                                } else {
                                    Toast.makeText(
                                        this@ResetActivity,
                                        data.getString("errorMessage"),
                                        Toast.LENGTH_SHORT
                                    ).show()

                                }

                            } catch (e: JSONException) {
                                Toast.makeText(
                                    this@ResetActivity,
                                    "Some unexpected error occur!!!",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }


                        }, Response.ErrorListener {
                            Toast.makeText(
                                this@ResetActivity,
                                "Volley error Occurred",
                                Toast.LENGTH_SHORT
                            )
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
                } else {
                    val dialog = AlertDialog.Builder(this)
                    dialog.setTitle("Error")
                    dialog.setMessage("Internet Connection is not Found")
                    dialog.setPositiveButton("Open Settings") { text, listener ->
                        val settingsIntent = Intent(Settings.ACTION_WIRELESS_SETTINGS)
                        startActivity(settingsIntent)
                        this.finish()
                    }

                    dialog.setNegativeButton("Exit") { text, listener ->
                        ActivityCompat.finishAffinity(this)
                    }
                    dialog.create()
                    dialog.show()
                }
        }else{

                Toast.makeText(
                    this@ResetActivity,
                    "Password Mismatched",
                    Toast.LENGTH_SHORT
                )
                    .show()

        }


        }
    }
}