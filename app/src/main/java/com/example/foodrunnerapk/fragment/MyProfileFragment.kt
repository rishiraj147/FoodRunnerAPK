package com.example.foodrunnerapk.fragment

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.foodrunnerapk.R


class MyProfileFragment : Fragment() {

    lateinit var sharePreferences: SharedPreferences
    lateinit var txtName:TextView
    lateinit var txtEmail:TextView
    lateinit var txtMobile:TextView
    lateinit var txtAddress:TextView



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view=inflater.inflate(R.layout.fragment_my_profile, container, false)

        sharePreferences=requireActivity().applicationContext.getSharedPreferences(
            getString(R.string.preference_file_name),
            Context.MODE_PRIVATE)

         txtName=view.findViewById(R.id.txtName)
         txtEmail=view.findViewById(R.id.txtEmail)
         txtMobile=view.findViewById(R.id.txtMobile)
        txtAddress=view.findViewById(R.id.txtAddress)



        val name=sharePreferences.getString("name",null)
        val email=sharePreferences.getString("email",null)
        val mobileNumber=sharePreferences.getString("mobileNo",null)
        val address=sharePreferences.getString("address",null)

        txtName.text=name
        txtAddress.text=address
        txtEmail.text=email
        txtMobile.text=mobileNumber
        return view
    }


}