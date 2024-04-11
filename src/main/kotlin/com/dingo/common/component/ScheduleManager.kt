package com.dingo.common.component

import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler
import java.util.*
import java.util.concurrent.ScheduledFuture

/**
 * 定时任务执行组件
 */
object ScheduleManager {
    private val threadPoolTaskScheduler = ThreadPoolTaskScheduler()
    private val scheduledMap = HashMap<String, ScheduledFuture<*>>()

    init {
        threadPoolTaskScheduler.initialize()
    }

    fun start(task: Task) {
        cancel(task.id)
        scheduledMap[task.id] = threadPoolTaskScheduler.schedule(task.execFun, task.fireTime)
    }

    fun cancel(taskId: String) {
        scheduledMap[taskId]?.cancel(true)
    }
}

data class Task(
    val id: String, // 任务id
    val fireTime: Date, // 执行时间
    val execFun: Runnable  // 执行方法
)