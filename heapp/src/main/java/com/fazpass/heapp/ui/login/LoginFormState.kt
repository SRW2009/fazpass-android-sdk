package com.fazpass.heapp.ui.login

/**
 * Data validation state of the login form.
 */
data class LoginFormState(
    val phoneError: Int? = null,
    val isDataValid: Boolean = false
)