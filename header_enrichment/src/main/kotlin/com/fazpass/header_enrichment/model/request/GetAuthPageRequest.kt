package com.fazpass.header_enrichment.model.request

internal data class GetAuthPageRequest(
    var gateway_key: String,
    var phone_number: String
)
