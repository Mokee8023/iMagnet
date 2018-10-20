package com.mokee.imagnet.presenter

import com.mokee.imagnet.event.RequestFailEvent
import com.mokee.imagnet.event.RequestType
import com.mokee.imagnet.event.ResponseEvent
import com.mokee.imagnet.presenter.process.ali.AliProcess
import com.mokee.imagnet.presenter.process.btdb.BtdbProcess
import com.mokee.imagnet.presenter.process.cilicat.CilicatProcess
import com.mokee.imagnet.presenter.process.nima.NimaItemProcess
import com.mokee.imagnet.presenter.process.nima.NimaProcess

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
            RequestType.ALI -> {
                AliProcess.processResponse(event.response)
            }
            RequestType.CILICAT -> {
                CilicatProcess.processResponse(event.response)
            }
            RequestType.BTDB -> {
                BtdbProcess.processResponse(event.response)
            }
        }
    }

    fun processRequestFail(event: RequestFailEvent) {
    }
}