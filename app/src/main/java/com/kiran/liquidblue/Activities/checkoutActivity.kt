package com.kiran.liquidblue.Activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.kiran.liquidblue.MainActivity
import com.kiran.liquidblue.R
import com.kiran.liquidblue.roomdb.AppDatabase
import com.kiran.liquidblue.roomdb.ProductModel
import com.razorpay.Checkout
import com.razorpay.PaymentResultListener
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.json.JSONObject

class checkoutActivity : AppCompatActivity(), PaymentResultListener {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_checkout)

        val checkout = Checkout()
        checkout.setKeyID("rzp_test_deTONJhwSZzH53")


        val price = intent.getStringExtra("totalCost")
        try {
            val options = JSONObject()
            options.put("name", "liquidBlue")
            options.put("description", "Best e-Commerce App")
            options.put(
                "image",
                "https://timesnext.com/content/images/size/w1304/wp-content/uploads/2022/05/The-Ecommerce-Business-Model-Explained.jpg"
            )
            options.put("order_id", "order_DBJOWzybf0sJbb")
            options.put("theme.color", "#2196F3")
            options.put("currency", "INR")
            options.put("amount", (price!!.toInt()*100).toString())
            options.put("prefill.email", "saikiran49321@gmail.com")
            options.put("prefill.contact", "6309260799")
            checkout.open(this, options)

        } catch (e: Exception) {
            Toast.makeText(this, "Something went wrong", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onPaymentSuccess(p0: String?) {
        Toast.makeText(this, "Payment Success", Toast.LENGTH_SHORT).show()

        uploadData()
    }

    private fun uploadData() {
        val id = intent.getStringArrayListExtra("productIds")
        for (currentId in id!!) {
            fetchData(currentId)
        }
    }

    private fun fetchData(productId: String?) {

        val dao = AppDatabase.getInstance(this).productDao()

        Firebase.firestore.collection("products")
            .document(productId!!).get().addOnSuccessListener {

                lifecycleScope.launch(Dispatchers.IO){
                    dao.deleteProduct(ProductModel(productId))
                }

                saveData(
                    it.getString("productName"),
                    it.getString("productSp"),
                    productId
                )
            }
    }

    private fun saveData(name: String?, price: String?, productId: String) {
        val preferences = this.getSharedPreferences("user", MODE_PRIVATE)
        val data = hashMapOf<String, Any>()
        data["name"] = name!!
        data["price"] = price!!
        data["productId"] = productId
        data["status"] = "Ordered"
        data["userId"] = preferences.getString("number", "")!!

        val firestore = Firebase.firestore.collection("allOrders")
        val key = firestore.document().id
        data["orderId"] = key

        firestore.document(key).set(data).addOnSuccessListener {
            Toast.makeText(this, "Your Order Placed", Toast.LENGTH_SHORT).show()
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }
            .addOnFailureListener {
                Toast.makeText(this, "Something went wrong", Toast.LENGTH_SHORT).show()
            }
    }


    override fun onPaymentError(p0: Int, p1: String?) {
        Toast.makeText(this, "Payment Failed", Toast.LENGTH_SHORT).show()
    }
}