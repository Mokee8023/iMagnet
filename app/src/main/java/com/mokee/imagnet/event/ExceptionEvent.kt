package com.mokee.imagnet.event

import java.lang.Exception

sealed class ExceptionEvent

data class RequestFailEvent(val requestUrl: String, val exception: Exception) : ExceptionEvent()