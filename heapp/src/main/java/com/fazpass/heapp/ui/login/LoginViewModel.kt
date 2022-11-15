package com.fazpass.heapp.ui.login

import android.content.Context
import androidx.core.text.isDigitsOnly
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.fazpass.header_enrichment.FazpassHE
import com.fazpass.header_enrichment.OnComplete
import com.fazpass.heapp.data.LoginRepository
import com.fazpass.heapp.data.Result

import com.fazpass.heapp.R
import com.fazpass.heapp.data.model.LoggedInUser
import java.util.*

class LoginViewModel(private val loginRepository: LoginRepository) : ViewModel() {

    private val _loginForm = MutableLiveData<LoginFormState>()
    val loginFormState: LiveData<LoginFormState> = _loginForm

    private val _loginResult = MutableLiveData<LoginResult>()
    val loginResult: LiveData<LoginResult> = _loginResult

    fun initialize(context: Context, MERCHANT_KEY: String, GATEWAY_KEY: String) {
        FazpassHE.initialize(context, MERCHANT_KEY, GATEWAY_KEY)
    }

    fun login(phone: String) {
        // can be launched in a separate asynchronous job
        loginRepository.login(phone, object: OnComplete<Result.Success<LoggedInUser>> {
            override fun onSuccess(result: Result.Success<LoggedInUser>) {
                _loginResult.value =
                    LoginResult(success = LoggedInUserView(displayName = result.data.displayName))
            }

            override fun onFailure(err: Throwable) {
                _loginResult.value = LoginResult(error = err.message)
            }
        })
    }

    fun loginDataChanged(phone: String) {
        if (!isPhoneValid(phone)) {
            _loginForm.value = LoginFormState(phoneError = R.string.invalid_phone)
        } else {
            _loginForm.value = LoginFormState(isDataValid = true)
        }
    }

    // A placeholder phone validation check
    private fun isPhoneValid(phone: String): Boolean {
        return phone.isNotBlank()
                && phone.isDigitsOnly()
                && phone.length > 9
    }
}