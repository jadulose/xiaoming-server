package org.xjtuse.xiaoming.config

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter
import org.springframework.security.web.authentication.www.BasicAuthenticationEntryPoint
import org.xjtuse.xiaoming.service.LoginAttemptService
import org.xjtuse.xiaoming.service.LoginCodeService
import org.xjtuse.xiaoming.service.UserService

/**
 * 安全配置
 */
@Configuration
@EnableWebSecurity
class SecurityConfig @Autowired constructor(
    val userService: UserService,
    val loginAttemptService: LoginAttemptService,
    val loginCodeService: LoginCodeService,
) {
    @Bean
    fun filterChain(http: HttpSecurity, authConfig: AuthenticationConfiguration): SecurityFilterChain =
        http.csrf().disable()
            .authorizeRequests()
            .antMatchers("/user/login-code").permitAll()
            .antMatchers("/user/**").hasRole("USER")
            .antMatchers("/hello").permitAll()
            .anyRequest().authenticated()
            .and()
            .formLogin().loginProcessingUrl(XiaomingAuthenticationFilter.LOGIN_URL)
            .and()
            .logout().logoutUrl("/xiaoming_logout")
            .deleteCookies("JSESSIONID")
            .and()
            .httpBasic()
            .authenticationEntryPoint(object : BasicAuthenticationEntryPoint() {
                override fun afterPropertiesSet() {
                    realmName = "XJTUSE Xiaoming"
                    super.afterPropertiesSet()
                }
            })
            .and()
            .addFilterAt(
                XiaomingAuthenticationFilter(authConfig.authenticationManager, loginAttemptService),
                UsernamePasswordAuthenticationFilter::class.java
            )
            .build()

    private fun passwordEncoder(): PasswordEncoder = BCryptPasswordEncoder()

    @Autowired
    fun initialize(authBuilder: AuthenticationManagerBuilder) {
        authBuilder.authenticationProvider(
            XiaomingUserDetailsAuthenticationProvider(userService, loginCodeService, passwordEncoder())
        )
    }
}