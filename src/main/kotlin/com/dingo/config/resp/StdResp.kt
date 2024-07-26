package com.dingo.config.resp

data class StdResp<T>(
    val code: Int = 200,
    val msg: String = "success",
    val data: T
)