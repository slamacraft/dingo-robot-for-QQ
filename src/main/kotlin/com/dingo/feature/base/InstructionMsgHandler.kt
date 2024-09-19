package com.dingo.feature.base

/**
 * 指令触发器,即以@开头的指令，例如@dk, @打卡等
 */
interface InstructionMsgHandler : IMsgHandler, IOrder {

    /**
     * 消息的触发字符
     * 例如 'dk',当有人发送@dk时,就会触发该功能
     * 触发字符可以是多个
     */
    fun trigger(): String

    /**
     * 帮助信息
     */
    fun helpMsg(): String
}