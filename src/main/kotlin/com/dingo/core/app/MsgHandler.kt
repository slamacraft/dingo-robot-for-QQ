package com.dingo.core.app

import net.mamoe.mirai.contact.Contact
import net.mamoe.mirai.message.data.MessageChain

/**
 * 有状态的消息处理器,需要交给状态机处理
 */
interface MsgHandler {

    /**
     * 带有状态返回值的处理函数
     *
     * @return 是否保持当前状态,如果返回false,则退出当前状态
     */
    fun handler(msg: MessageChain, subject: Contact, next: MsgHandler): Boolean
}

object DefaultMsgHandler : MsgHandler {
    override fun handler(msg: MessageChain, subject: Contact, next: MsgHandler): Boolean {
        return false
    }
}

/**
 * 全局的消息处理器
 */
interface GlobalMsgHandler : MsgHandler

/**
 * 异常消息处理器
 */
interface ExceptionMsgHandler : MsgHandler {
    override fun handler(msg: MessageChain, subject: Contact, next: MsgHandler): Boolean {
        return false
    }

    fun handler(exception: Exception, subject: Contact, next: ExceptionMsgHandler): Boolean
}


/**
 * 关键字触发型消息处理器
 */
interface TriggerMsgHandler : MsgHandler {
    /**
     * 出发关键字
     */
    fun trigger(): String

    /**
     * 前置处理器
     */
    fun preHandler(): List<MsgHandler> = arrayListOf()
}

/**
 * 无状态的消息处理器
 */
interface StatelessMsgHandler : TriggerMsgHandler {

    fun handle(msg: MessageChain, subject: Contact, next: MsgHandler)

    override fun handler(msg: MessageChain, subject: Contact, next: MsgHandler): Boolean {
        handle(msg, subject, next)
        return false
    }
}