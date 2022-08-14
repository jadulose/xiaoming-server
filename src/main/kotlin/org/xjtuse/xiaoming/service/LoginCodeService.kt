package org.xjtuse.xiaoming.service

import org.xjtuse.xiaoming.model.User
import kotlin.random.Random

abstract class LoginCodeService {

    companion object {
        private const val CODE_LENGTH = 6

        private fun generateRandomCode() = (1..CODE_LENGTH)
            .map { Random.nextInt(0, 10) }
            .joinToString("");
    }

    protected fun generateLoginCodeAndSave(user: User): String {
        // TODO 保存进Cache
        return generateRandomCode()
    }

    abstract fun sendLoginCode(user: User)

}