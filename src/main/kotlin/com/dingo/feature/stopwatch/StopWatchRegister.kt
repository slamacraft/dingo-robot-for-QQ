package com.dingo.feature.stopwatch

import java.util.*
import javax.security.auth.DestroyFailedException

object StopWatchRegister {

    /**
     * 存放秒表任务的map
     * key：id，value：StopWatchFuture
     * 通过秒表任务设定的id确定唯一性
     */
    private val futureMap: MutableMap<String, StopWatchFuture> = HashMap()

    /**
     * 秒表任务处理器
     * 负责对从本注册器实例注册的秒表任务进行管理
     */
    private val taskPool = StopWatchTaskPool

    /**
     * 通过id获取注册过的秒表任务
     * 即使秒表任务已经结束，同样也可以在本方法中得到
     * @param id    秒表任务id
     * @return  查询到的秒表任务
     */
    fun getFuture(id: String): StopWatchFuture? {
        return futureMap[id]
    }


    /**
     * 新增秒表任务
     *
     * 通过秒表任务自带的id判断唯一性
     * 如果id相同，后注册的秒表任务会覆盖掉先注册的秒表任务
     * @param future    秒表任务实例
     */
    fun addFuture(future: StopWatchFuture) {
        val runningFuture = getFuture(future.id)
        if (runningFuture != null) {
            StopWatchTaskPool.remove(runningFuture)
        }
        futureMap[future.id] = future
        StopWatchTaskPool.add(future)
    }


    /**
     * 暂停秒表任务
     * 通过输入秒表任务的id来暂停秒表任务
     * 如果秒表任务的id在本实例中注册过且正在运行，则返回true
     * 否则返回false
     * @param id    秒表任务id
     * @return  是否暂停成功
     */
    fun stopFuture(id: String): Boolean {
        val future = futureMap[id] ?: return false
        future.stop()
        return StopWatchTaskPool.remove(future)
    }


    /**
     * 继续暂停的秒表任务
     * 通过输入秒表任务的id来继续秒表任务
     * 如果输入的id在本实例中注册过且正在暂停，则返回true，
     * 否则返回false
     * @param id    秒表任务id
     * @return  是否成功继续
     */
    fun continueFuture(id: String): Boolean {
        val future = futureMap[id]
        if (future == null || future.isStop()) {
            return false
        }
        future.toContinue()
        return StopWatchTaskPool.add(future)
    }


    /**
     * 移除秒表任务
     * 通过输入的id移除秒表任务，会将秒表任务从本实例中移除，
     * 并且立即中断正在或准备执行的秒表任务
     * 如果输入的id在本实例中注册过，则返回true，
     * 否则返回false
     * @param id    秒表任务id
     * @return  是否移除成功
     */
    fun removeFuture(id: String): Boolean {
        val toRemoveFuture = futureMap.remove(id)
        return StopWatchTaskPool.remove(toRemoveFuture)
    }

    /**
     * 秒表注册器销毁
     * @throws DestroyFailedException 销毁失败异常
     */
    @Throws(DestroyFailedException::class)
    fun destroy() {
        StopWatchTaskPool.destroy()
    }

}
