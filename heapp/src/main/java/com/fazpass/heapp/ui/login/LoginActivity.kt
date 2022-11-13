package com.fazpass.heapp.ui.login

import android.app.Activity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.EditText
import android.widget.Toast
import com.fazpass.heapp.databinding.ActivityLoginBinding

import com.fazpass.heapp.R

class LoginActivity : AppCompatActivity() {
    private companion object {
        const val MERCHANT_KEY = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpZGVudGlmaWVyIjoxfQ.HiMpQwtuAotxoI3pencdEBn5jYXLgbQMCiO0dZz78Ho"
        const val GATEWAY_KEY = "gatewaykey"
    }

    private lateinit var loginViewModel: LoginViewModel
    private lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val phone = binding.phone
        val login = binding.login
        val loading = binding.loading

        loginViewModel = ViewModelProvider(this, LoginViewModelFactory())
            .get(LoginViewModel::class.java)

        loginViewModel.initialize(baseContext, MERCHANT_KEY, GATEWAY_KEY)

        loginViewModel.loginFormState.observe(this@LoginActivity, Observer {
            val loginState = it ?: return@Observer

            // disable login button unless phone is valid
            login.isEnabled = loginState.isDataValid

            if (loginState.phoneError != null) {
                phone.error = getString(loginState.phoneError)
            }
        })

        loginViewModel.loginResult.observe(this@LoginActivity, Observer {
            val loginResult = it ?: return@Observer

            phone.isEnabled = true
            login.isEnabled = true
            loading.visibility = View.GONE
            if (loginResult.error != null) {
                showLoginFailed(loginResult.error)
            }
            if (loginResult.success != null) {
                updateUiWithUser(loginResult.success)
            }
            setResult(Activity.RESULT_OK)

            //Complete and destroy login activity once successful
            //finish()
        })

        phone.afterTextChanged {
            loginViewModel.loginDataChanged(
                phone.text.toString()
            )
        }

        login.setOnClickListener {
            phone.isEnabled = false
            login.isEnabled = false
            loading.visibility = View.VISIBLE
            loginViewModel.login(phone.text.toString())
        }
    }

    private fun updateUiWithUser(model: LoggedInUserView) {
        val welcome = getString(R.string.welcome)
        val displayName = model.displayName
        // TODO : initiate successful logged in experience
        Toast.makeText(
            applicationContext,
            "$welcome $displayName",
            Toast.LENGTH_LONG
        ).show()
    }

    private fun showLoginFailed(errMessage: String) {
        Toast.makeText(applicationContext, errMessage, Toast.LENGTH_SHORT).show()
    }
}

fun EditText.afterTextChanged(afterTextChanged: (String) -> Unit) {
    this.addTextChangedListener(object : TextWatcher {
        override fun afterTextChanged(editable: Editable?) {
            afterTextChanged.invoke(editable.toString())
        }

        override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}

        override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
    })
}