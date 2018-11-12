package com.mokee.imagnet.presenter

import android.content.Context
import android.widget.Toast
import com.mokee.imagnet.event.RequestFailEvent
import com.mokee.imagnet.model.RequestType
import com.mokee.imagnet.model.ResponseEvent
import com.mokee.imagnet.presenter.process.ali.AliProcess
import com.mokee.imagnet.presenter.process.btdb.BtdbProcess
import com.mokee.imagnet.presenter.process.cilicat.CilicatDetailProcess
import com.mokee.imagnet.presenter.process.cilicat.CilicatItemProcess
import com.mokee.imagnet.presenter.process.cilicat.CilicatProcess
import com.mokee.imagnet.presenter.process.cilicat.CilicatSearchProcess
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
            RequestType.ALI -> {
                AliProcess.processResponse(event.response)
            }
            RequestType.BTDB -> {
                BtdbProcess.processResponse(event.response)
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