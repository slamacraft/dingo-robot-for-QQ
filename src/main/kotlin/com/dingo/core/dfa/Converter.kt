package com.dingo.core.dfa

import com.dingo.core.mirai.MsgSender
import com.dingo.common.enums.UserStageEnum
import net.mamoe.mirai.event.events.MessageEvent

interface StageConverter {

    fun order(): Int = 0

    fun node():List<UserStageEnum>

    fun applyIf(msg: MessageEvent): Boolean

    fun convert(msg: MessageEvent): UserStage
}

class HelloHandler : StageConverter {

    override fun node(): List<UserStageEnum> = listOf(UserStageEnum.DEFAULT)

    override fun applyIf(msg: MessageEvent): Boolean {
        return true
    }

    override fun convert(msg: MessageEvent): UserStage {
        MsgSender.sendMsg(msg, "你好")
        return UserStage(UserStageEnum.DEFAULT)
    }

}
