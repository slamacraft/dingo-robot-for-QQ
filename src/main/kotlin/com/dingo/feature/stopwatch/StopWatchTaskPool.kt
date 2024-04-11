package com.dingo.feature.stopwatch

import java.util.*
import javax.security.auth.DestroyFailedException
import kotlin.concurrent.thread

object StopWatchTaskPool {

    init {
        thread {
            StopWatchExecutor.run()
        }
    }

    val heap = PriorityQueue<StopWatchFuture>(Comparator.comparingLong { it.startTime })

    /**
     * 获取秒表任务最小堆顶部的任务实例
     *
     * 当堆为空时，将会返回null
     */
    fun get(): StopWatchFuture? = heap.peek()

    /**
     * 新增秒表任务
     *
     * 当[StopWatchFuture.count]大于0时，便会将该任务插入到最小堆中，返回true
     *
     * 当[StopWatchFuture.count]小于0时，则表示无限次执行，返回true
     *
     * 当[StopWatchFuture.count]等于0时，不会将该任务添加到任务池，返回false。
     */
    fun add(future: StopWatchFuture): Boolean {
        if (future.count == 0L) {
            return false
        }
        synchronized(StopWatchExecutor) {
            heap.add(future)
            StopWatchExecutor.awake()
        }
        return true
    }

    /**
     * 从任务池移除秒表任务
     *
     * 如果秒表任务在最小堆中存在，则返回true，否则返回false
     */
    fun remove(future: StopWatchFuture?): Boolean {
        synchronized(this) {
            val result: Boolean = heap.remove(future)
            StopWatchExecutor.awake()
            return result
        }
    }

    /**
     * 移除最小堆的堆顶秒表任务并返回
     *
     * 如果堆的大小为0，则返回null
     */
    fun remove(): StopWatchFuture? {
        synchronized(this) {
            if (heap.isEmpty()) {
                return null
            }
            val result = heap.remove() as StopWatchFuture
            StopWatchExecutor.awake()
            return result
        }
    }

    /**
     * 销毁任务池
     *
     * @throws DestroyFailedException 终止秒表任务执行器失败
     */
    @Throws(DestroyFailedException::class)
    fun destroy() {
        synchronized(StopWatchExecutor) { StopWatchExecutor.awake() }
    }

}
