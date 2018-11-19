package com.mokee.imagnet.model

import okhttp3.Response

data class ResponseEvent(val type: RequestType, val response: Response)

enum class RequestType {
    NIMA,
    ALI,
    CILICAT,
    BTDB_ME,

    NIMA_ITEM,
    CILICAT_ITEM,
    ALI_ITEM,
    BTDB_ME_ITEM,

    NIMA_DETAIL,
    CILICAT_DETAIL,
    CILICAT_SEARCH_DETAIL,
    ALI_DETAIL,
    BTDB_ME_DETAIL,

    NIMA_SEARCH,
    CILICAT_SEARCH
}