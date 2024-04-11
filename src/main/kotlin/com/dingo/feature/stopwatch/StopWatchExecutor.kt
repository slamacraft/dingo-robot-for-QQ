package com.dingo.feature.stopwatch

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import javax.security.auth.Destroyable

object StopWatchExecutor : Runnable, Destroyable {

    var runningFlag = true
    val lock = Object()

    override fun run() {
        while (runningFlag) {
            val stopWatchFuture: StopWatchFuture? = StopWatchTaskPool.get()
            if (stopWatchFuture == null) {
                synchronized(lock) { lock.wait() }
                continue
            }

            val time = stopWatchFuture.startTime - now()
            if (time > 0) {
                synchronized(lock) { lock.wait(time * 1000) }
                continue
            }

            val future: StopWatchFuture? = StopWatchTaskPool.remove()
            GlobalScope.launch(Dispatchers.Main) {
                future!!.nextTask().execute(future)
            }
            StopWatchTaskPool.add(future!!)
        }
    }

    fun awake() {
        synchronized(lock) {
            lock.notify()
        }
    }

    override fun destroy() {
        runningFlag = false
    }
}
