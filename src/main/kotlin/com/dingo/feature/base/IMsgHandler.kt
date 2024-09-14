package com.dingo.feature.base

interface IMsgHandler {

    /**
     * 处理收到的消息，然后返回下一个消息处理器，
     * 如果没有返回消息处理器，代表消息处理链结束
     */
    fun handle(msg: String): IMsgHandler?

}