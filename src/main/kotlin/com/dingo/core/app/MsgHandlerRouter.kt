package com.dingo.core.app

import net.mamoe.mirai.contact.Contact
import net.mamoe.mirai.event.events.MessageEvent
import net.mamoe.mirai.message.data.MessageChain

object MsgHandlerRouter {
    // 全局的消息处理器
    private val globalMsgHandlerList = mutableListOf<GlobalMsgHandler>()

    // 全局消息处理器链
    private lateinit var globalMsgHandlerChain: MsgHandlerChain<GlobalMsgHandler>;

    // 处理器链上的最后一个
    private lateinit var lastGlobalMsgHandlerChain: MsgHandlerChain<GlobalMsgHandler>

    // 触发型消息处理器
    private val triggerMsgHandlerMap = mutableMapOf<String, TriggerMsgHandler>()

    fun register(msgHandler: GlobalMsgHandler) {
        globalMsgHandlerList.add(msgHandler)
        if (globalMsgHandlerChain == null) {
            globalMsgHandlerChain = MsgHandlerChain(msgHandler, null)
            lastGlobalMsgHandlerChain = globalMsgHandlerChain
        } else {
            val last = MsgHandlerChain(msgHandler, null)
            lastGlobalMsgHandlerChain.next = last
            lastGlobalMsgHandlerChain = last
        }
    }

    fun register(msgHandler: TriggerMsgHandler) {
        triggerMsgHandlerMap[msgHandler.trigger()] = msgHandler
    }

    fun handle(messageEvent: MessageEvent) {
        globalMsgHandlerChain.handler()
    }
}

class MsgHandlerChain<T : MsgHandler>(
    private val current: T,
    var next: MsgHandlerChain<T>?
) : MsgHandler {

    fun handler(msg: MessageChain, subject: Contact) {

    }

    override fun handler(msg: MessageChain, subject: Contact, next: MsgHandler): Boolean {
        return current.handler(msg, subject, next)
    }
}
