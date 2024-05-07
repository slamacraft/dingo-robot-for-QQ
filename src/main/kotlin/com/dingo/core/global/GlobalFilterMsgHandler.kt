package com.dingo.core.global

import com.dingo.core.app.MsgHandler
import net.mamoe.mirai.contact.Contact
import net.mamoe.mirai.message.data.MessageChain

/**
 * 过滤消息处理器，只会响应特定的群或者用户，不是每个用户都会响应
 */
object GlobalFilterMsgHandler : MsgHandler {

    override fun handler(msg: MessageChain, subject: Contact, next: () -> MsgHandler): Boolean {
        return next()(msg, subject, next)
    }
}