package com.dingo.feature.base

abstract class AbstractTriggerHandler {

    /**
     * 触发处理器的优先级，越小越高
     */
    abstract val order: Int

    abstract fun handle(msg: String)

}