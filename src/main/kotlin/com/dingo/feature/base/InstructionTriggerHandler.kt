package com.dingo.feature.base

import com.dingo.common.PackageScanner
import com.dingo.core.dfa.StageConverter

/**
 *  触发功能的处理器
 *
 *  用于从多个
 */
object InstructionTriggerHandler : AbstractTriggerHandler() {

    /**
     * 所有的指令触发器
     */
    private val triggers = PackageScanner.classes
        .filter { InstructionTrigger::class.java.isAssignableFrom(it) && !it.isInterface }
        .map { it.getConstructor().newInstance() as InstructionTrigger }
        .sortedBy { it.order }

    override fun handle(msg: String) {

    }

}
