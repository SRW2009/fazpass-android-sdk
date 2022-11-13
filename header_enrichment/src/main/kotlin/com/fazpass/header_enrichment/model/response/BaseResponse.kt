package com.fazpass.header_enrichment.model.response

data class BaseResponse<D> (
    var status: Boolean,
    var message: String,
    var code: String,
    var data: D?
)