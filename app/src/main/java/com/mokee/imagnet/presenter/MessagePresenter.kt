package com.mokee.imagnet.presenter

import android.content.Context
import android.widget.Toast
import com.mokee.imagnet.event.RequestFailEvent
import com.mokee.imagnet.model.RequestType
import com.mokee.imagnet.model.ResponseEvent
import com.mokee.imagnet.presenter.process.ali.AliDetailProcess
import com.mokee.imagnet.presenter.process.ali.AliItemProcess
import com.mokee.imagnet.presenter.process.ali.AliProcess
import com.mokee.imagnet.presenter.process.btdb.BtdbDetailProcess
import com.mokee.imagnet.presenter.process.btdb.BtdbMeItemProcess
import com.mokee.imagnet.presenter.process.btdb.BtdbProcess
import com.mokee.imagnet.presenter.process.btdb.BtdbSearchProcess
import com.mokee.imagnet.presenter.process.cilicat.*
import com.mokee.imagnet.presenter.process.nima.NimaDetailProcess
import com.mokee.imagnet.presenter.process.nima.NimaItemProcess
import com.mokee.imagnet.presenter.process.nima.NimaProcess
import com.mokee.imagnet.presenter.process.nima.NimaSearchProcess

class MessagePresenter {

    /** Process response */
    fun processResponse(event: ResponseEvent) {
        when(event.type) {
            RequestType.NIMA -> {
                NimaProcess.processResponse(event.response)
            }
            RequestType.NIMA_ITEM -> {
                NimaItemProcess.processResponse(event.response)
            }
            RequestType.NIMA_DETAIL -> {
                NimaDetailProcess.processResponse(event.response)
            }
            RequestType.NIMA_SEARCH -> {
                NimaSearchProcess.processResponse(event.response)
            }
            RequestType.CILICAT -> {
                CilicatProcess.processResponse(event.response)
            }
            RequestType.CILICAT_ITEM -> {
                CilicatItemProcess.processResponse(event.response)
            }
            RequestType.CILICAT_DETAIL -> {
                CilicatDetailProcess.processResponse(event.response)
            }
            RequestType.CILICAT_SEARCH -> {
                CilicatSearchProcess.processResponse(event.response)
            }
            RequestType.CILICAT_SEARCH_DETAIL -> {
                CilicatSearchDetailProcess.processResponse(event.response)
            }
            RequestType.ALI -> {
                AliProcess.processResponse(event.response)
            }
            RequestType.ALI_ITEM -> {
                AliItemProcess.processResponse(event.response)
            }
            RequestType.ALI_DETAIL -> {
                AliDetailProcess.processResponse(event.response)
            }
            RequestType.BTDB_ME -> {
                BtdbProcess.processResponse(event.response)
            }
            RequestType.BTDB_ME_ITEM -> {
                BtdbMeItemProcess.processResponse(event.response)
            }
            RequestType.BTDB_ME_DETAIL -> {
                BtdbDetailProcess.processResponse(event.response)
            }
            RequestType.BTDB_ME_SEARCH -> {
                BtdbSearchProcess.processResponse(event.response)
            }
            else -> {
            }
        }
    }

    fun processRequestFail(context: Context, event: RequestFailEvent) {
        Toast.makeText(
                context,
                "请求 ${event.requestUrl} 链接发生异常：${event.exception}",
                Toast.LENGTH_LONG)
                .show()
    }
}