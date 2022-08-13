package org.xjtuse.xiaoming.model

import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.UserDetails

data class LoginInfo(
    val usernameType: UsernameType,
    val passwordType: PasswordType,
) {
    enum class UsernameType {
        EMAIL, PHONE, NET_ID, STUDENT_ID,
    }

    enum class PasswordType {
        PASSWORD, EMAIL_LOGIN_CODE, PHONE_LOGIN_CODE,
    }
}

data class User(
    var id: Int,
    var email: String?,
    var phone: String?,
    var netId: String?,
    var studentId: String?,
    var password_: String,
    var name: String,
    var enabled: Boolean,
    var authorities_: MutableList<SimpleGrantedAuthority>,
) : UserDetails {

    constructor() : this(0, "", null, null, null, "", "", false, mutableListOf())

    companion object {
        /**
         * 输入信息不完整（用户名和密码为空）则会返回未定义用户
         */
        val UNDEFINED = User()
    }

    override fun getUsername(): String = email ?: phone ?: netId ?: studentId ?: ""

    override fun getPassword() = password_

    override fun isEnabled() = enabled

    override fun getAuthorities() = authorities_

    override fun isAccountNonExpired() = true

    override fun isAccountNonLocked() = true

    override fun isCredentialsNonExpired() = true

}