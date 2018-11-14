package com.mokee.imagnet.model

import okhttp3.Response

data class ResponseEvent(val type: RequestType, val response: Response)

enum class RequestType {
    NIMA,
    ALI,
    CILICAT,
    BTDB,

    NIMA_ITEM,
    CILICAT_ITEM,
    ALI_ITEM,

    NIMA_DETAIL,
    CILICAT_DETAIL,
    CILICAT_SEARCH_DETAIL,

    NIMA_SEARCH,
    CILICAT_SEARCH
}