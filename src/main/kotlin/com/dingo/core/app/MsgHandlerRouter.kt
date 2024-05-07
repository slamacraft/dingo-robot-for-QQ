package com.dingo.core.app

import com.dingo.core.global.GlobalFilterMsgHandler
import net.mamoe.mirai.event.events.MessageEvent

object MsgHandlerRouter {
    // 全局的消息处理器列表
    private val msgHandlerChain: MsgHandlerChain = MsgHandlerChain(GlobalFilterMsgHandler)

    fun register(handler: MsgHandler){
        msgHandlerChain + handler
    }

    fun handle(messageEvent: MessageEvent) {
        msgHandlerChain.handler(messageEvent.message, messageEvent.subject)
    }
}