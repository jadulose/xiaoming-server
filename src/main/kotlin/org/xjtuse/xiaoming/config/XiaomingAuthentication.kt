package org.xjtuse.xiaoming.config

import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.AuthenticationServiceException
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.authentication.dao.AbstractUserDetailsAuthenticationProvider
import org.springframework.security.core.Authentication
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter
import org.springframework.security.web.util.matcher.AntPathRequestMatcher
import org.xjtuse.xiaoming.model.LoginInfo
import org.xjtuse.xiaoming.model.LoginInfo.PasswordType.*
import org.xjtuse.xiaoming.model.LoginInfo.UsernameType.*
import org.xjtuse.xiaoming.model.User
import org.xjtuse.xiaoming.service.UserService
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

/**
 * @see org.springframework.security.authentication.UsernamePasswordAuthenticationToken
 */
class XiaomingAuthenticationToken(
    username: String, password: String, val loginInfo: LoginInfo,
    authorities: Collection<GrantedAuthority>? = null
) : UsernamePasswordAuthenticationToken(username, password, authorities) {
    init {
        authorities ?: super.setAuthenticated(false)
    }
}

/**
 * @see org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter
 */
class XiaomingAuthenticationFilter(
    authManager: AuthenticationManager
) : AbstractAuthenticationProcessingFilter(AntPathRequestMatcher(LOGIN_URL, "POST")) {
    init {
        authenticationManager = authManager
    }

    companion object {
        const val LOGIN_URL = "/xiaoming_login"

        private const val FORM_USERNAME = "username"
        private const val FORM_PASSWORD = "password"
        private const val FORM_USERNAME_TYPE = "${FORM_USERNAME}Type"
        private const val FORM_PASSWORD_TYPE = "${FORM_PASSWORD}Type"
    }

    override fun attemptAuthentication(request: HttpServletRequest, response: HttpServletResponse): Authentication {
        if (request.method != "POST")
            throw AuthenticationServiceException("Authentication method not supported: ${request.method}")
        val authRequest = XiaomingAuthenticationToken(
            request.getParameter(FORM_USERNAME) ?: "",
            request.getParameter(FORM_PASSWORD) ?: "",
            obtainLoginInfo(request)
        )
        authRequest.details = authenticationDetailsSource.buildDetails(request)
        return authenticationManager.authenticate(authRequest)
    }

    private fun obtainLoginInfo(request: HttpServletRequest): LoginInfo {
        val u = request.getParameter(FORM_USERNAME_TYPE)
        val p = request.getParameter(FORM_PASSWORD_TYPE)
        return LoginInfo(
            if (u == null) EMAIL else LoginInfo.UsernameType.valueOf(u),
            if (p == null) PASSWORD else LoginInfo.PasswordType.valueOf(p),
        )
    }

}

/**
 * @see org.springframework.security.authentication.dao.DaoAuthenticationProvider
 */
class XiaomingUserDetailsAuthenticationProvider(
    private val userService: UserService,
    private val passwordEncoder: PasswordEncoder
) : AbstractUserDetailsAuthenticationProvider() {

    private fun badCredentials(message: String) {
        logger.debug(message)
        throw BadCredentialsException(
            messages.getMessage("AbstractUserDetailsAuthenticationProvider.badCredentials", "Bad credentials")
        )
    }

    override fun additionalAuthenticationChecks(
        userDetails: UserDetails, authentication: UsernamePasswordAuthenticationToken
    ) {
        if (authentication.credentials == null) {
            badCredentials("由于未提供凭据，无法进行身份验证")
        }
        val presentedPassword = authentication.credentials.toString()
        if (userDetails is User && authentication is XiaomingAuthenticationToken
            && authentication.loginInfo.passwordType != PASSWORD
        ) {
            TODO()
        } else {
            if (!passwordEncoder.matches(presentedPassword, userDetails.password))
                badCredentials("验证失败，因为密码与存储的值不匹配")
        }
    }

    override fun retrieveUser(username: String, authentication: UsernamePasswordAuthenticationToken): UserDetails {
        if (authentication !is XiaomingAuthenticationToken) {
            logger.debug("暂不支持非${XiaomingAuthenticationToken::class.simpleName}类型的Token")
            return User()
        }
        return userService.find(username, authentication.loginInfo)
    }

}