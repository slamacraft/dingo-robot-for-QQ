package com.dingo.feature.stopwatch

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.time.LocalDateTime
import java.time.ZoneOffset

open class StopWatchFuture(
    val id: String,
    private val taskList: List<StopWatchTask>,
    var count: Long = 1,
    var startTime: Long = now()    // 任务开始时间，单位秒
) {

    init {
        taskList.requireNoNulls()
        if (taskList.isEmpty()) {
            throw RuntimeException("StopWatchFuture param taskList required not empty")
        }
        startTime += taskList[0].waitTime
    }

    // 当前秒表任务执行的任务序号
    private var taskIndex = 0

    // 暂停的时间点，单位秒
    private var stopTimePoint: Long = 0

    // 是否暂停
    private var stopFlag = false

    fun isStop() = stopFlag

    fun stop() {
        stopTimePoint = now()
        stopFlag = true
    }

    fun toContinue() {
        val stopTime = now() - stopTimePoint
        startTime += stopTime
        stopFlag = true
    }

    fun nextTask(): StopWatchTask = taskList[indexNext()]

    private fun indexNext(): Int {
        val currentIndex = taskIndex
        taskIndex++
        count -= taskIndex / taskList.size
        taskIndex = if (taskIndex == 0) taskIndex else taskList.size % taskIndex
        startTime += taskList[taskIndex].waitTime
        return currentIndex
    }
}


open class StopWatchTask(private val task: StopWatchFuture.() -> Unit, val waitTime: Long = 0) {

    /**
     * 执行秒表任务
     */
    suspend fun execute(future: StopWatchFuture) = withContext(Dispatchers.Main) { task(future) }
}

fun now(): Long = LocalDateTime.now().toEpochSecond(ZoneOffset.of("+8"))
