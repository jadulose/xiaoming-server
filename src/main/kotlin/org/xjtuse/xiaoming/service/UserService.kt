package org.xjtuse.xiaoming.service

import org.springframework.stereotype.Service
import org.xjtuse.xiaoming.model.LoginInfo
import org.xjtuse.xiaoming.model.User

interface UserService {

    fun exists(username: String, info: LoginInfo): Boolean

    /**
     * 根据LoginInfo描述的登录方式查找用户信息
     *
     * @param username 登录名（如邮箱、电话、NetID、学号）
     * @param info 登录方式信息
     * @return 完整用户信息
     * @throws org.springframework.security.core.userdetails.UsernameNotFoundException
     */
    fun find(username: String, info: LoginInfo): User

    fun create(user: User)

    fun update(user: User)

    fun delete(user: User)

    fun changePassword(oldPassword: String, newPasswor: String)

}