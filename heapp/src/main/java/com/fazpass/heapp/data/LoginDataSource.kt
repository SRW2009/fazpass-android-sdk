package com.fazpass.heapp.data

import com.fazpass.header_enrichment.FazpassHE
import com.fazpass.header_enrichment.OnComplete
import com.fazpass.heapp.data.model.LoggedInUser

/**
 * Class that handles authentication w/ login credentials and retrieves user information.
 */
class LoginDataSource {

    fun login(phone: String, onComplete: OnComplete<Result.Success<LoggedInUser>>) {
        FazpassHE.authenticateWithUser(phone, object: OnComplete<Unit?> {
            override fun onSuccess(result: Unit?) {
                val fakeUser = LoggedInUser(java.util.UUID.randomUUID().toString(), "Jane Doe")
                onComplete.onSuccess(Result.Success(fakeUser))
            }

            override fun onFailure(err: Throwable) {
                onComplete.onFailure(err)
            }
        })
    }

    fun logout() {
        // TODO: revoke authentication
    }
}