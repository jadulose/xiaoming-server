package org.xjtuse.xiaoming.config

import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.AuthenticationServiceException
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.authentication.dao.AbstractUserDetailsAuthenticationProvider
import org.springframework.security.core.Authentication
import org.springframework.security.core.AuthenticationException
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler
import org.springframework.security.web.util.matcher.AntPathRequestMatcher
import org.xjtuse.xiaoming.model.LoginInfo
import org.xjtuse.xiaoming.model.LoginInfo.PasswordType.*
import org.xjtuse.xiaoming.model.LoginInfo.UsernameType.*
import org.xjtuse.xiaoming.model.User
import org.xjtuse.xiaoming.service.LoginAttemptService
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
    authManager: AuthenticationManager,
    private val loginAttemptService: LoginAttemptService,
) : AbstractAuthenticationProcessingFilter(AntPathRequestMatcher(LOGIN_URL, "POST")) {
    init {
        authenticationManager = authManager
        // Failure
        setAuthenticationFailureHandler(object : SimpleUrlAuthenticationFailureHandler() {
            override fun onAuthenticationFailure(
                request: HttpServletRequest, response: HttpServletResponse, exception: AuthenticationException
            ) {
                loginAttemptService.loginFailed(getClientIP(request))
                super.onAuthenticationFailure(request, response, exception)
            }
        })
        // Success
        setAuthenticationSuccessHandler(object : SavedRequestAwareAuthenticationSuccessHandler() {
            override fun onAuthenticationSuccess(
                request: HttpServletRequest, response: HttpServletResponse, authentication: Authentication
            ) {
                loginAttemptService.loginSucceeded(getClientIP(request))
                super.onAuthenticationSuccess(request, response, authentication)
            }
        })
    }

    companion object {
        const val LOGIN_URL = "/xiaoming_login"

        private const val FORM_USERNAME = "username"
        private const val FORM_PASSWORD = "password"
        private const val FORM_USERNAME_TYPE = "${FORM_USERNAME}Type"
        private const val FORM_PASSWORD_TYPE = "${FORM_PASSWORD}Type"

        private fun getClientIP(request: HttpServletRequest): String {
            val xfHeader = request.getHeader("X-Forwarded-For")
            return if (xfHeader == null) request.remoteAddr
            else xfHeader.split(",")[0]
        }
    }

    override fun attemptAuthentication(request: HttpServletRequest, response: HttpServletResponse): Authentication {
        if (request.method != "POST")
            throw AuthenticationServiceException("不支持的认证方法：${request.method}")
        val clientIP = getClientIP(request)
        if (loginAttemptService.isBlocked(clientIP))
            throw AuthenticationServiceException("该客户端IP（$clientIP）已被封锁")

        // TODO 改用请求体而不是请求参数
        val authRequest = XiaomingAuthenticationToken(
            request.getParameter(FORM_USERNAME)?.trim() ?: User.UNDEFINED.username,
            request.getParameter(FORM_PASSWORD)?.trim() ?: User.UNDEFINED.password,
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
        if (authentication.credentials == null || userDetails == User.UNDEFINED)
            badCredentials("凭据不符合要求，无法进行身份验证")
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
            return User.UNDEFINED
        }
        return userService.find(username, authentication.loginInfo)
    }

}