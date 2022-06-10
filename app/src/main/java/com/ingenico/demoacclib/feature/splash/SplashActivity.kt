package com.ingenico.demoacclib.feature.splash

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.ingenico.demoacclib.MainActivity
import com.ingenico.demoacclib.data.model.SecurePaymentResult
import com.ingenico.demoacclib.databinding.ActivitySplashBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SplashActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySplashBinding
    private val viewModel: SplashViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel.setEmvConfiguration().observe(this) {
            startPaymentServices()
        }
    }

    private fun startPaymentServices() {
        viewModel.servicesConnection().observe(this) {
            when (it) {
                is SecurePaymentResult.SpsError -> {
                    Log.e("ERROR SP", it.errorCode.name)
                    //showDialogSpsError()
                }
                is SecurePaymentResult.UsdkError -> {
                    Log.e("ERROR USDK", it.errorCode.name)
                    //showDialogUsdkError()
                }
                SecurePaymentResult.Success -> {
                    //Continue process
                    val intent = Intent(this, MainActivity::class.java)
                    intent.flags =
                        Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    startActivity(intent)

                }
                is SecurePaymentResult.ExceptionError -> {
                    //dialogExceptionError(it.e)
                }
            }
        }
    }


}