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
    var email_: String,
    var password_: String,
    var enabled_: Boolean,
    var authorities_: MutableList<SimpleGrantedAuthority>,
) : UserDetails {

    constructor() : this("", "", true, mutableListOf())

    override fun getUsername() = email_

    override fun getPassword() = password_

    override fun isEnabled() = enabled_

    override fun getAuthorities() = authorities_

    override fun isAccountNonExpired() = true

    override fun isAccountNonLocked() = true

    override fun isCredentialsNonExpired() = true

}