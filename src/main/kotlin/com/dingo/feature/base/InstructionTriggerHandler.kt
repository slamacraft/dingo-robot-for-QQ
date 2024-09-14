package com.dingo.feature.base

import com.dingo.common.PackageScanner

/**
 *  触发功能的处理器
 *
 *  用于从多个
 */
object InstructionTriggerHandler : IMsgHandlerGroup {

    override fun order(): Int = 0

    /**
     * 所有的指令触发器
     */
    private val triggers = PackageScanner.classes
        .filter { InstructionTrigger::class.java.isAssignableFrom(it) && !it.isInterface }
        .map { it.getConstructor().newInstance() as InstructionTrigger }
        .sortedBy { it.order() }

    override fun handle(msg: String): IMsgHandler? {
        triggers.first {
            it.trigger().any { triggerMsg ->
                msg.contains("@${triggerMsg}")
            }
        }?.let { it.handle(msg) }

        // 所有指令类消息处理器都是一次性的
        return null
    }


}
