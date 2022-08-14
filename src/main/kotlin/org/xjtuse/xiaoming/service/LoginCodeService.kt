package org.xjtuse.xiaoming.service

import com.google.common.cache.CacheBuilder
import com.google.common.cache.CacheLoader
import org.xjtuse.xiaoming.model.User
import java.util.concurrent.TimeUnit
import kotlin.random.Random

abstract class LoginCodeService {

    companion object {
        private const val CODE_LENGTH = 6

        private fun generateRandomCode() = (1..CODE_LENGTH)
            .map { Random.nextInt(0, 10) }
            .joinToString("");
    }

    private val loginCodeCache = CacheBuilder.newBuilder()
        .expireAfterWrite(10, TimeUnit.MINUTES)
        .build(object : CacheLoader<Int, String>() {
            override fun load(key: Int): String = ""
        })

    protected fun generateAndSave(user: User): String {
        val code = generateRandomCode()
        loginCodeCache.put(user.id, code)
        return code
    }

    abstract fun send(user: User)

    fun get(user: User): String? = loginCodeCache.getIfPresent(user.id)

    fun remove(user: User) = loginCodeCache.invalidate(user.id)

}