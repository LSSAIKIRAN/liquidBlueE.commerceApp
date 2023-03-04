package com.kiran.liquidblue.Activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.content.ContentProviderCompat.requireContext
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.kiran.liquidblue.Adapter.CategoryProductAdapter
import com.kiran.liquidblue.Adapter.ProductAdapter
import com.kiran.liquidblue.Model.AddProductModel
import com.kiran.liquidblue.R
import com.kiran.liquidblue.databinding.ActivityCategoryBinding
import java.util.ArrayList

class CategoryActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCategoryBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCategoryBinding.inflate(layoutInflater)
        setContentView(binding.root)


        getProducts(intent.getStringExtra("cat"))
    }

    private fun getProducts(category: String?) {
        val list = ArrayList<AddProductModel>()
        Firebase.firestore.collection("products").whereEqualTo("productCategory", category)
            .get().addOnSuccessListener {
                list.clear()
                for (doc in it.documents) {
                    val data = doc.toObject(AddProductModel::class.java)
                    list.add(data!!)
                }
                binding.recyclerView.adapter = CategoryProductAdapter(this, list)
            }
    }
}