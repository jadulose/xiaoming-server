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
import org.xjtuse.xiaoming.service.UserService

/**
 * 安全配置
 */
@Configuration
@EnableWebSecurity
class SecurityConfig @Autowired constructor(
    val userService: UserService,
) {
    @Bean
    fun filterChain(http: HttpSecurity, authConfig: AuthenticationConfiguration): SecurityFilterChain =
        http.csrf().disable()
            .authorizeRequests()
            .antMatchers("/user/**").hasRole("USER")
            .antMatchers("/hello").permitAll()
            .anyRequest().authenticated()
            .and()
            .formLogin().loginProcessingUrl(XiaomingAuthenticationFilter.LOGIN_URL)
            .and()
            .logout().logoutUrl("/xiaoming_logout")
            .deleteCookies("JSESSIONID")
//            .and()
//            .rememberMe()
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
                XiaomingAuthenticationFilter(authConfig.authenticationManager),
                UsernamePasswordAuthenticationFilter::class.java
            )
            .build()

    private fun passwordEncoder(): PasswordEncoder = BCryptPasswordEncoder()

    @Autowired
    fun initialize(authBuilder: AuthenticationManagerBuilder) {
        authBuilder.authenticationProvider(
            XiaomingUserDetailsAuthenticationProvider(userService, passwordEncoder())
        )
    }
}