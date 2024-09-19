package com.dingo.feature.base

/**
 * 消息处理器分组
 */
interface IMsgHandlerGroup {

    fun hit(msg: String): Boolean

    fun handle(msg: String)

}