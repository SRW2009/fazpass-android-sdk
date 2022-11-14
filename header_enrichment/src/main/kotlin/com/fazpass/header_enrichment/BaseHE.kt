package com.fazpass.header_enrichment

import com.fazpass.header_enrichment.model.response.BaseResponse
import com.fazpass.header_enrichment.model.response.CheckResultResponse
import com.fazpass.header_enrichment.model.response.GetAuthPageResponse

internal interface BaseHE {
    fun getAuthPage(phone: String, onComplete: OnComplete<BaseResponse<GetAuthPageResponse>>)
    fun launchAuthPage(url: String, onComplete: OnComplete<BaseResponse<Unit?>>)
    fun checkResult(onComplete: OnComplete<BaseResponse<CheckResultResponse>>)
}
