package com.mokee.imagnet.event

import okhttp3.Response

data class ResponseEvent(val type: RequestType, val response: Response)

enum class RequestType {
    NIMA,
    ALI,
    CILICAT,
    BTDB,

    NIMA_ITEM,
    CILICAT_ITEM,

    NIMA_DETAIL
}