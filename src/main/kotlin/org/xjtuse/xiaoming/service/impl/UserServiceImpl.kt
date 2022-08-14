package org.xjtuse.xiaoming.service.impl

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.stereotype.Service
import org.xjtuse.xiaoming.mapper.UserMapper
import org.xjtuse.xiaoming.model.LoginInfo.UsernameType
import org.xjtuse.xiaoming.model.LoginInfo.UsernameType.*
import org.xjtuse.xiaoming.model.User
import org.xjtuse.xiaoming.service.UserService

/**
 * @see org.springframework.security.core.userdetails.jdbc.JdbcDaoImpl
 */
@Service
class UserServiceImpl : UserService {
    @Autowired
    private lateinit var userMapper: UserMapper

    override fun exists(username: String, type: UsernameType) = try {
        find(username, type)
        true
    } catch (e: UsernameNotFoundException) {
        false
    }

    override fun find(username: String, type: UsernameType): User {
        return when (type) {
            EMAIL -> {
                userMapper.findByEmail(username)
                    ?: throw UsernameNotFoundException("无法找到邮箱为${username}的用户")
            }

            PHONE -> TODO()
            NET_ID -> TODO()
            STUDENT_ID -> TODO()
        }
    }

    override fun create(user: User) {
        TODO("Not yet implemented")
    }

    override fun update(user: User) {
        TODO("Not yet implemented")
    }

    override fun delete(user: User) {
        TODO("Not yet implemented")
    }

    override fun changePassword(oldPassword: String, newPasswor: String) {
        TODO("Not yet implemented")
    }
}