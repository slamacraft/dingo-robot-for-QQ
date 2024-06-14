package com.dingo.core.app

import net.mamoe.mirai.contact.Contact
import net.mamoe.mirai.message.data.MessageChain
import java.util.*

/**
 * 有状态的消息处理器,需要交给状态机处理
 */
interface MsgHandler {

    /**
     * 带有状态返回值的处理函数
     *
     * @return 是否保持当前状态,如果返回false,则退出当前状态
     */
    fun handler(msg: MessageChain, subject: Contact, next: () -> MsgHandler): Boolean

    operator fun invoke(msg: MessageChain, subject: Contact, next: () -> MsgHandler): Boolean {
        return handler(msg, subject, next)
    }

    fun compose(other: MsgHandler): MsgHandlerChain {
        return MsgHandlerChain(this) + other
    }

    fun branch(other: MsgHandler): BranchMsgHandler {
        return BranchMsgHandler(this) / other
    }

    fun parallel(other: MsgHandler): ParallelMsgHandler {
        return ParallelMsgHandler(this) * other
    }

    operator fun plus(other: MsgHandler): MsgHandlerChain {
        return compose(other)
    }

    operator fun div(other: MsgHandler): BranchMsgHandler {
        return branch(other)
    }

    operator fun times(other: MsgHandler): ParallelMsgHandler {
        return parallel(other)
    }
}

object DefaultMsgHandler : MsgHandler {
    override fun handler(msg: MessageChain, subject: Contact, next: () -> MsgHandler): Boolean {
        return false
    }
}

/**
 * 异常消息处理器
 */
interface ExceptionMsgHandler : MsgHandler {
    override fun handler(msg: MessageChain, subject: Contact, next: () -> MsgHandler): Boolean {
        return false
    }

    fun handler(exception: Exception, subject: Contact, next: ExceptionMsgHandler)
}


/**
 * 消息处理器链
 */
class MsgHandlerChain : MsgHandler {
    private val handlers = mutableListOf<MsgHandler>()

    constructor(current: MsgHandler) {
        handlers.add(current)
    }

    override operator fun plus(handler: MsgHandler): MsgHandlerChain {
        handlers.add(handler)
        return this
    }

    fun handler(msg: MessageChain, subject: Contact): Boolean {
        var idx = 0
        return handler(msg, subject){
            idx++
            if (idx < handlers.size) {
                handlers[idx]
            } else
                DefaultMsgHandler
        }
    }

    override fun handler(msg: MessageChain, subject: Contact, next: () -> MsgHandler): Boolean {
        var idx = 0
        val head = handlers[0]
        return head.handler(msg, subject, next)
    }
}

class BranchMsgHandler : MsgHandler {
    private val handlers = mutableListOf<MsgHandler>()

    constructor(current: MsgHandler) {
        handlers.add(current)
    }

    override operator fun div(handler: MsgHandler): BranchMsgHandler {
        handlers.add(handler)
        return this
    }

    override fun handler(msg: MessageChain, subject: Contact, next: () -> MsgHandler): Boolean {
        var handled = false
        for (handler in handlers) {
            handled = handler(msg, subject, next)
            if (handled) {
                break
            }
        }
        return handled
    }
}

class ParallelMsgHandler : MsgHandler {
    private val handlers = mutableListOf<MsgHandler>()

    constructor(current: MsgHandler) {
        handlers.add(current)
    }

    override operator fun times(handler: MsgHandler): ParallelMsgHandler {
        handlers.add(handler)
        return this
    }

    override fun handler(msg: MessageChain, subject: Contact, next: () -> MsgHandler): Boolean {
        var handled = false
        for (handler in handlers) {
            val isHandled = handler(msg, subject, next)
            if (isHandled) {
                handled = isHandled
            }
        }
        return handled
    }
}

fun main() {
    val msgHandlerChain = DefaultMsgHandler + DefaultMsgHandler / DefaultMsgHandler * DefaultMsgHandler
    println(msgHandlerChain)
}