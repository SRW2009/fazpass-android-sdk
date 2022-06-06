package com.fazpass.otp

import com.fazpass.otp.model.*
import com.fazpass.otp.usecase.MerchantUseCase
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

open class Merchant {

    companion object{
        internal var merchantKey: String = ""
        internal var gatewayKey: String = ""
        internal var baseUrl: String = ""
    }

    fun setGateway(gateway: String){
        Merchant.gatewayKey = gateway
    }

    fun generateOtp(target:String, onComplete:(Response)->Unit){
         val fazpass by lazy { MerchantUseCase.start() }
        var response = Response(false,"","generate", "",target,null)
         if(android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches()) {
             fazpass.generateOtpByEmail(
                 "Bearer $merchantKey",
                 RequestOtpByEmail(gatewayKey, target)
             ).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                 .subscribe(
                     { result ->
                         response = result
                         response.target = target
                         onComplete(response)
                     },
                     { error ->
                         response.error = error.message
                         onComplete(response)
                     }
                 )
//        }else if(android.util.Patterns.PHONE.matcher(target).matches()){
            }else{
            fazpass.generateOtpByPhone("Bearer $merchantKey",RequestOtpByPhone(gatewayKey, target)).
            subscribeOn(Schedulers.io()).
            observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    { result ->
                        response = result
                        response.target = target
                        onComplete(response)
                    },
                    { error ->
                        response.error = error.message
                        onComplete(response)
                    }
                )
        }
    }

    fun verifyOtp(otpId:String, otp:String, onComplete: (Boolean) -> Unit){
        val fazpass by lazy { MerchantUseCase.start() }
        fazpass.verifyOtp("Bearer $merchantKey",VerifyOtpRequest(otpId, otp)).
        subscribeOn(Schedulers.io()).
        observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                {
                    onComplete(true)
                },
                {
                    onComplete(false)
                }
            )
    }

    fun sendOtp(target:String, otp:String, onComplete: (Response) -> Unit){
        val fazpass by lazy { MerchantUseCase.start() }
        var response = Response(false,"","send", "",target,Data("",otp,"","","","",""))
        if(android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches()) {
            fazpass.sendOtpByEmail(
                "Bearer $merchantKey",
                SendOtpRequestByEmail(gatewayKey, target, otp)).
            subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    { result ->
                        response = result
                        response.target = target
                        onComplete(response)
                    },
                    { error ->
                        response.error = error.message
                        onComplete(response)
                    }
                )
        }else{
            fazpass.sendOtpByPhone("Bearer $merchantKey",
                SendOtpRequestByPhone(gatewayKey, target, otp)).
            subscribeOn(Schedulers.io()).
            observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    { result ->
                        response = result
                        response.target = target
                        onComplete(response)
                    },
                    { error ->
                        response.error = error.message
                        onComplete(response)
                    }
                )
        }
    }

    fun requestOtp(target:String, onComplete:(Response)->Unit){
        val fazpass by lazy { MerchantUseCase.start() }
        var response = Response(false,"","request", "",target,null)
        if(android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches()) {
            fazpass.requestOtpByEmail(
                "Bearer $merchantKey",
                RequestOtpByEmail(gatewayKey, target)
            ).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    { result ->
                        response = result
                        response.target = target
                        onComplete(response)
                    },
                    { error ->
                        response.error = error.message
                        onComplete(response)
                    }
                )
        }else{
            fazpass.requestOtpByPhone("Bearer $merchantKey",RequestOtpByPhone(gatewayKey, target)).
            subscribeOn(Schedulers.io()).
            observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    { result ->
                        response = result
                        response.target = target
                        onComplete(response)
                    },
                    { error ->
                        response.error = error.message
                        onComplete(response)
                    }
                )
        }
    }
}