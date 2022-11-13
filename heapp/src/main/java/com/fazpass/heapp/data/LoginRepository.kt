package com.fazpass.heapp.data

import com.fazpass.header_enrichment.OnComplete
import com.fazpass.heapp.data.model.LoggedInUser

/**
 * Class that requests authentication and user information from the remote data source and
 * maintains an in-memory cache of login status and user credentials information.
 */

class LoginRepository(val dataSource: LoginDataSource) {

    // in-memory cache of the loggedInUser object
    var user: LoggedInUser? = null
        private set

    val isLoggedIn: Boolean
        get() = user != null

    init {
        // If user credentials will be cached in local storage, it is recommended it be encrypted
        // @see https://developer.android.com/training/articles/keystore
        user = null
    }

    fun logout() {
        user = null
        dataSource.logout()
    }

    fun login(phone: String, onComplete: OnComplete<Result.Success<LoggedInUser>>) {

        dataSource.login(phone, object: OnComplete<Result.Success<LoggedInUser>> {
            override fun onSuccess(result: Result.Success<LoggedInUser>) {
                setLoggedInUser(result.data)
                onComplete.onSuccess(result)
            }

            override fun onFailure(err: Throwable) {
                onComplete.onFailure(err)
            }
        })
    }

    private fun setLoggedInUser(loggedInUser: LoggedInUser) {
        this.user = loggedInUser
        // If user credentials will be cached in local storage, it is recommended it be encrypted
        // @see https://developer.android.com/training/articles/keystore
    }
}