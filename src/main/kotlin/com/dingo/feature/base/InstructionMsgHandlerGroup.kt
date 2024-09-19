package com.dingo.feature.base

import com.dingo.common.PackageScanner

/**
 *  触发功能的处理器
 *
 *  用于从多个
 */
object InstructionMsgHandlerGroup : IMsgHandlerGroup {

    /**
     * 所有的指令触发器
     */
    private val triggers = mutableListOf<InstructionMsgHandler>()

    fun register(handler: InstructionMsgHandler) {
        triggers.add(handler)
    }

    override fun hit(msg: String) = msg.startsWith(".")

    override fun handle(msg: String) {
        val firstWord = msg.split(" ")[0]
        triggers.first {
            it.trigger().valid(firstWord)
        }?.let {
            if (firstWord.endsWith("?")) {
                it.helpMsg()
            } else {
                it.handle(msg)
            }
        }
    }


}

fun String.valid(msg: String): Boolean {
    return this == ".${msg}" || this == ".${msg}?"
}
