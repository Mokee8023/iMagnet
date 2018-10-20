package com.mokee.imagnet.presenter.process

import okhttp3.Response

abstract class ProcessResponse {
    abstract fun processResponse(response: Response)
}