package com.dingo.feature.base

interface ITrigger {

    /**
     * 消息处理触发器
     */
    fun hit(msg: String): Boolean

}