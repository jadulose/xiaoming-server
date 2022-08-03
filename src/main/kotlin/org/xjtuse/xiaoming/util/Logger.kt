package org.xjtuse.xiaoming.util

import org.slf4j.Logger as Slf4jLogger
import org.slf4j.LoggerFactory
import kotlin.reflect.KClass

/**
 * 日志工具类
 *
 * @author Fan Peilin
 * @sample org.xjtuse.xiaoming.controller.HomeController.Companion
 */
open class Logger {
    companion object {
        fun <T : Any> create(clazz: KClass<T>): Slf4jLogger = LoggerFactory.getLogger(
            if (clazz.isCompanion) clazz.java.enclosingClass else clazz.java
        )
    }

    val log = create(this::class)
}
