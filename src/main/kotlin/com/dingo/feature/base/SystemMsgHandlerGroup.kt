package com.dingo.feature.base

import com.dingo.common.PackageScanner

object SystemMsgHandlerGroup : IMsgHandlerGroup {

    /**
     * 所有的指令触发器
     */
    private val triggers = PackageScanner.classes
        .filter { SystemMsgHandler::class.java.isAssignableFrom(it) && !it.isInterface }
        .map { it.getConstructor().newInstance() as SystemMsgHandler }
        .sortedBy { it.order() }

    override fun hit(msg: String) = msg.startsWith("@")

    override fun handle(msg: String) {
        triggers.first {
            it.trigger().any { triggerMsg ->
                msg.contains(".${triggerMsg}")
            }
        }?.let { it.handle(msg) }
    }
}