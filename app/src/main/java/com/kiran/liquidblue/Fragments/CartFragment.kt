package com.kiran.liquidblue.Fragments

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import com.kiran.liquidblue.Activities.AddressActivity
import com.kiran.liquidblue.Activities.CategoryActivity
import com.kiran.liquidblue.Activities.productDetailActivity
import com.kiran.liquidblue.Adapter.CartAdapter
import com.kiran.liquidblue.R
import com.kiran.liquidblue.databinding.FragmentCartBinding
import com.kiran.liquidblue.roomdb.AppDatabase
import com.kiran.liquidblue.roomdb.ProductModel


class CartFragment : Fragment() {

    private lateinit var binding: FragmentCartBinding
    //private lateinit var list : ArrayList<String>

    private val list = arrayListOf<String>()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
      binding = FragmentCartBinding.inflate(layoutInflater)

        val preference = requireContext().getSharedPreferences("info", AppCompatActivity.MODE_PRIVATE)
        val editor = preference.edit()
        editor.putBoolean("isCart", false)
        editor.apply()

        val dao = AppDatabase.getInstance(requireContext()).productDao()

        dao.getAllProduct().observe(requireActivity()){
            binding.cartRecycler.adapter = CartAdapter(requireContext(), it)

            list.clear()
            for (data in it){
                list.add(data.productId)
            }
            
            totalCost(it)
        }

        return binding.root
    }

    private fun totalCost(data: List<ProductModel>?) {
       var total = 0

        for (item in data!!){
            total += item.productSp!!.toInt()
        }

        binding.textView12.text = "Total Items ${data.size}"
        binding.textView13.text = "Total Cost : $total"

        binding.checkout.setOnClickListener {
            val intent = Intent(context, AddressActivity::class.java)
            val b = Bundle()
            b.putStringArrayList("productIds",list)
            b.putString("totalCost", total.toString())
            intent.putExtras(b)
            startActivity(intent)
        }
    }

}