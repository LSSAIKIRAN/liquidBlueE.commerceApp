package com.kiran.liquidblue.Activities

import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.kiran.liquidblue.MainActivity
import com.kiran.liquidblue.R
import com.kiran.liquidblue.databinding.ActivityAddressBinding

class AddressActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddressBinding

    private lateinit var preferences : SharedPreferences

    private lateinit var totalCost : String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddressBinding.inflate(layoutInflater)
        setContentView(binding.root)
        preferences = this.getSharedPreferences("user", MODE_PRIVATE)

        totalCost = intent.getStringExtra("totalCost")!!
        loadUserInfo()

        binding.proceed.setOnClickListener {
            validateData(
                binding.userNumber.text.toString(),
                binding.userName.text.toString(),
                binding.userPinCode.text.toString(),
                binding.userCity.text.toString(),
                binding.userState.text.toString(),
                binding.userVillage.text.toString()

            )
        }
    }

    private fun validateData(number: String, name: String, pinCode: String, city: String, state: String, village: String) {

        if (number.isEmpty() || name.isEmpty() || pinCode.isEmpty() || city.isEmpty() || state.isEmpty() || village.isEmpty()){
            Toast.makeText(this, "Enter Your Details", Toast.LENGTH_SHORT).show()
        }
        else{
            storeData(pinCode,city,state,village)
        }
    }

    private fun storeData( pinCode: String, city: String, state: String, village: String) {

        val map = hashMapOf<String, Any>()
        map["village"] = village
        map["state"] = state
        map["city"] = city
        map["pinCode"] = pinCode

        Firebase.firestore.collection("users")
            .document(preferences.getString("number", "")!!)
            .update(map).addOnSuccessListener {
                val b = Bundle()
                b.putStringArrayList("productIds",intent.getStringArrayListExtra("productIds"))
                b.putString("totalCost", totalCost)
                val intent = Intent(this, checkoutActivity::class.java)
                intent.putExtras(b)
                startActivity(intent)
            }
            .addOnFailureListener {
                Toast.makeText(this, "Something went wrong", Toast.LENGTH_SHORT).show()
            }
    }


    private fun loadUserInfo() {

        Firebase.firestore.collection("users")
            .document(preferences.getString("number", "")!!)
            .get().addOnSuccessListener {
                binding.userName.setText(it.getString("userName"))
                binding.userNumber.setText(it.getString("userPhoneNumber"))
                binding.userVillage.setText(it.getString("village"))
                binding.userCity.setText(it.getString("city"))
                binding.userPinCode.setText(it.getString("pinCode"))
                binding.userState.setText(it.getString("state"))
            }
    }
}