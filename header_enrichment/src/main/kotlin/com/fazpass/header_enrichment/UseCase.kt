package com.fazpass.header_enrichment

import com.fazpass.header_enrichment.model.request.CheckResultRequest
import com.fazpass.header_enrichment.model.request.GetAuthPageRequest
import com.fazpass.header_enrichment.model.response.BaseResponse
import com.fazpass.header_enrichment.model.response.CheckResultResponse
import com.fazpass.header_enrichment.model.response.GetAuthPageResponse
import io.reactivex.Observable
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.*

internal interface UseCase {
    @POST("request/auth-page") fun auth(@Header("Authorization") token: String, @Body requestBody: GetAuthPageRequest) : Observable<BaseResponse<GetAuthPageResponse>>
    @GET("{path}") fun redirectAuth(@Path("path") path: String, @QueryMap queries: Map<String, String>) : Observable<BaseResponse<Unit?>>
    @POST("check/result") fun redirectCheckResult(@Header("Authorization") token: String, @Body requestBody: CheckResultRequest) : Observable<BaseResponse<CheckResultResponse>>

    companion object{
        fun start(baseUrl: String = FazpassHE.baseUrl): UseCase {
            val clientBuilder = OkHttpClient.Builder()
            val loggingInterceptor = HttpLoggingInterceptor()
            loggingInterceptor.level = HttpLoggingInterceptor.Level.BODY
            clientBuilder.addInterceptor(loggingInterceptor)
            val retrofit = Retrofit.Builder()
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl(baseUrl)
                .client(clientBuilder.build())
                .build()

            return retrofit.create(UseCase::class.java)
        }
    }
}