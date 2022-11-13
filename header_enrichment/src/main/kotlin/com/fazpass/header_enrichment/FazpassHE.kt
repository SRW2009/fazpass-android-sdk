package com.fazpass.header_enrichment

import android.app.Activity
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.Uri
import android.os.Build
import android.telephony.TelephonyManager
import android.util.ArrayMap
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.fazpass.header_enrichment.model.request.GetAuthPageRequest
import com.fazpass.header_enrichment.model.request.CheckResultRequest
import com.fazpass.header_enrichment.model.response.GetAuthPageResponse
import com.fazpass.header_enrichment.model.response.BaseResponse
import com.fazpass.header_enrichment.model.response.CheckResultResponse
import com.fazpass.header_enrichment.model.response.LaunchAuthPageResponse
import com.google.zxing.integration.android.IntentIntegrator
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

open class FazpassHE {

    companion object {
        private val he = HE()
        private var connectivityManager: ConnectivityManager? = null
        private var telephonyManager: TelephonyManager? = null

        internal const val baseUrl: String = "https://channa.fazpas.com/v1/he/"
        internal var merchantKey: String = ""
        internal var gatewayKey: String = ""

        fun initialize(context: Context, merchantKey: String, gatewayKey: String) {
            connectivityManager = context.getSystemService(ConnectivityManager::class.java)
            telephonyManager = context.getSystemService(TelephonyManager::class.java)
            FazpassHE.merchantKey = merchantKey
            FazpassHE.gatewayKey = gatewayKey
        }

        fun authenticateWithUser(phone: String, onComplete: OnComplete<Unit?>) {
            if (phone.isBlank()) throw Exception("Phone can't be empty")

            if (!isTransportCellular()) {
                onComplete.onFailure(Throwable("Internet not connected via cellular"))
                return
            }
            if (!isCarrierMatch(phone)) {
                onComplete.onFailure(Throwable("Phone number doesn't match it's carrier"))
                return
            }

            he.getAuthPage(phone, object: OnComplete<BaseResponse<GetAuthPageResponse>> {

                override fun onSuccess(result: BaseResponse<GetAuthPageResponse>) {
                    result.data?.authpage?.let { authenticate(it, onComplete) }
                }

                override fun onFailure(err: Throwable) {
                    onComplete.onFailure(err)
                }
            })
        }

        fun authenticateWithQR(activity: AppCompatActivity, onComplete: OnComplete<Unit?>) {
            if (!isTransportCellular()) {
                onComplete.onFailure(Throwable("Internet not connected via cellular"))
                return
            }

            val intentIntegrator = IntentIntegrator(activity)
            intentIntegrator.setDesiredBarcodeFormats(listOf(IntentIntegrator.QR_CODE))
            val startForResult = activity.registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
                if (result.resultCode == Activity.RESULT_OK) {
                    val intentResult = IntentIntegrator.parseActivityResult(result.resultCode, result.data)
                    val url = intentResult.contents

                    authenticate(url, onComplete)
                    return@registerForActivityResult
                }

                onComplete.onFailure(Error("Canceled."))
            }
            startForResult.launch(intentIntegrator.createScanIntent())
        }

        private fun authenticate(url: String, onComplete: OnComplete<Unit?>) {
            he.launchAuthPage(url, object: OnComplete<BaseResponse<LaunchAuthPageResponse>> {

                override fun onSuccess(result: BaseResponse<LaunchAuthPageResponse>) {
                    onComplete.onSuccess(null)
                    /*he.checkResult(object: OnComplete<BaseResponse<CheckResultResponse>> {

                        override fun onSuccess(result: BaseResponse<CheckResultResponse>) {
                            onComplete.onSuccess(null)
                        }

                        override fun onFailure(err: Throwable) {
                            onComplete.onFailure(err)
                        }
                    })*/
                }

                override fun onFailure(err: Throwable) {
                    onComplete.onFailure(err)
                }
            })
        }

        private fun isTransportCellular(): Boolean {
            val currentNetwork = connectivityManager?.activeNetwork
            val caps = connectivityManager?.getNetworkCapabilities(currentNetwork) ?: return false

            val bannedNetworkTransports = arrayListOf(
                NetworkCapabilities.TRANSPORT_WIFI,
                NetworkCapabilities.TRANSPORT_BLUETOOTH,
                NetworkCapabilities.TRANSPORT_ETHERNET,
                NetworkCapabilities.TRANSPORT_VPN
            )
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                bannedNetworkTransports.add(NetworkCapabilities.TRANSPORT_USB)
            }

            return bannedNetworkTransports.none { caps.hasTransport(it) }
        }

        private fun isCarrierMatch(phone: String): Boolean {
            if (phone.length < 3) return false
            val carrierName = telephonyManager?.networkOperatorName ?: ""
            return DataCarrierUtility.check(phone.substring(0..3), carrierName)
        }
    }

    private class HE : BaseHE {
        override fun getAuthPage(phone: String, onComplete: OnComplete<BaseResponse<GetAuthPageResponse>>) {
            val fazpass by lazy { UseCase.start() }
            val request = GetAuthPageRequest(gatewayKey, phone)
            fazpass.auth("Bearer $merchantKey", request)
                .subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                .subscribe(onComplete::onSuccess, onComplete::onFailure)
        }

        override fun launchAuthPage(url: String, onComplete: OnComplete<BaseResponse<LaunchAuthPageResponse>>) {
            val uri = Uri.parse(url)
            val fazpass by lazy { UseCase.start("${uri.scheme}://${uri.host}/") }
            val queries = ArrayMap<String, String>()
            uri.queryParameterNames.forEach { name -> queries[name] = uri.getQueryParameter(name) }
            fazpass.redirectAuth("${uri.path}".replace("/",""), queries)
                .subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                .subscribe(onComplete::onSuccess, onComplete::onFailure)
        }

        override fun checkResult(onComplete: OnComplete<BaseResponse<CheckResultResponse>>) {
            val fazpass by lazy { UseCase.start() }
            val request = CheckResultRequest(null)
            fazpass.redirectCheckResult("Bearer $merchantKey", request)
                .subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                .subscribe(onComplete::onSuccess, onComplete::onFailure)
        }
    }
}