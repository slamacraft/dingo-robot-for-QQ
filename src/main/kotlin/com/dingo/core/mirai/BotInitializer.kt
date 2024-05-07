package com.dingo.core.mirai

import com.dingo.core.app.MsgHandlerRouter
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import net.mamoe.mirai.Bot
import net.mamoe.mirai.BotFactory
import net.mamoe.mirai.alsoLogin
import net.mamoe.mirai.event.GlobalEventChannel
import net.mamoe.mirai.event.events.FriendMessageEvent
import net.mamoe.mirai.event.events.GroupMessageEvent
import net.mamoe.mirai.utils.BotConfiguration

/**
 * @date 2020/10/12 8:53
 * @author slamacraft
 * @since JDK 1.8
 * @version 1.0
 */
object BotInitializer {

    private lateinit var bot: Bot

    /**
     * 将所有的机器人登录
     */
    private suspend fun botLogin(id: Long, pw: String) {
        val bot = BotFactory.newBot(id, pw) {
            fileBasedDeviceInfo()
            protocol = BotConfiguration.MiraiProtocol.ANDROID_PAD
        }
        bot.alsoLogin()
        BotInitializer.bot = bot
        registeredGroupMsgEvent { MsgHandlerRouter.handle(it) }
        registeredFriendMsgEvent { MsgHandlerRouter.handle(it) }
    }


    /**
     * 注册群消息事件[GroupMessageEvent]
     */
    private fun registeredGroupMsgEvent(eventMethod: suspend (eventType: GroupMessageEvent) -> Unit) {
        GlobalEventChannel.subscribeAlways<GroupMessageEvent> { event ->
            eventMethod(event)
        }
    }

    /**
     * 注册好友消息事件[FriendMessageEvent]
     */
    private fun registeredFriendMsgEvent(eventMethod: suspend (eventType: FriendMessageEvent) -> Unit) {
        GlobalEventChannel.subscribeAlways<FriendMessageEvent> { event ->
            eventMethod(event)
        }
    }


    fun start(id: Long, pw: String) {
        GlobalScope.launch {
            botLogin(id, pw)
        }
    }

}
