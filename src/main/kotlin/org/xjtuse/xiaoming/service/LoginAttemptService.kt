package org.xjtuse.xiaoming.service

import com.google.common.cache.CacheBuilder
import com.google.common.cache.CacheLoader
import com.google.common.cache.LoadingCache
import org.springframework.stereotype.Service
import org.xjtuse.xiaoming.util.Logger
import java.util.concurrent.TimeUnit

private typealias TmpType = Byte

@Service
class LoginAttemptService {
    companion object : Logger() {
        private const val MAX_ATTEMPT: TmpType = 10
    }

    private val attemptsCache = CacheBuilder.newBuilder()
        .expireAfterWrite(1, TimeUnit.DAYS)
        .build(object : CacheLoader<String, TmpType>() {
            override fun load(key: String): TmpType = 0
        })

    private fun LoadingCache<String, TmpType>.getOrDefault(key: String) =
        this.getIfPresent(key) ?: 0

    fun loginSucceeded(key: String) {
        attemptsCache.invalidate(key)
        log.info("客户端${key}登录成功")
    }

    fun loginFailed(key: String) {
        var attempts = attemptsCache.getOrDefault(key)
        attemptsCache.put(key, ++attempts)
        log.info("客户端${key}登录失败，还剩${MAX_ATTEMPT - attempts}次尝试机会")
    }

    fun isBlocked(key: String): Boolean = attemptsCache.getOrDefault(key) >= MAX_ATTEMPT
}