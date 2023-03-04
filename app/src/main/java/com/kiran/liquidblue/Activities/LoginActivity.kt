package com.kiran.liquidblue.Activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.google.firebase.FirebaseException
import com.google.firebase.FirebaseTooManyRequestsException
import com.google.firebase.auth.*
import com.kiran.liquidblue.R
import com.kiran.liquidblue.databinding.ActivityLoginBinding
import java.util.concurrent.TimeUnit

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.button4.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
            finish()
        }
        binding.button3.setOnClickListener {
            if (binding.userNumber.text!!.isEmpty()) {
                Toast.makeText(this, "Enter your number", Toast.LENGTH_SHORT).show()
            } else {
                sendOtp(binding.userNumber.text.toString())
            }
        }
    }

    private lateinit var builder: AlertDialog
    private fun sendOtp(number: String) {
        builder = AlertDialog.Builder(this)
            .setTitle("Loading...")
            .setMessage("Wait...")
            .setCancelable(false)
            .create()
        builder.show()

        val options = PhoneAuthOptions.newBuilder(FirebaseAuth.getInstance())
            .setPhoneNumber("+91$number")
            .setTimeout(60L, TimeUnit.SECONDS)
            .setActivity(this)
            .setCallbacks(callbacks)
            .build()
        PhoneAuthProvider.verifyPhoneNumber(options)
    }

    private val callbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

        override fun onVerificationCompleted(credential: PhoneAuthCredential) {

        }

        override fun onVerificationFailed(e: FirebaseException) {

        }

        override fun onCodeSent(
            verificationId: String,
            token: PhoneAuthProvider.ForceResendingToken
        ) {
            builder.dismiss()
            val intent = Intent(this@LoginActivity, OTPActivity::class.java)
            intent.putExtra("verificationId", verificationId)
            intent.putExtra("number", binding.userNumber.text.toString())
            startActivity(intent)

        }
    }
}