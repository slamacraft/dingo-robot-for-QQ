package com.dingo.feature.base

/**
 * 指令触发器,即以.开头的指令
 */
interface InstructionTrigger {

    /**
     * 消息的触发字符
     * 例如 'dk',当有人发送.dk时,就会触发该功能
     */
    val trigger: String

    /**
     * 优先级
     */
    val order: Int

}