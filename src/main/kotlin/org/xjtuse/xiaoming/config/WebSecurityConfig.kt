package org.xjtuse.xiaoming.config

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.provisioning.JdbcUserDetailsManager
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.authentication.www.BasicAuthenticationEntryPoint
import javax.sql.DataSource

/**
 * 安全配置
 */
@Configuration
@EnableWebSecurity
class WebSecurityConfig {
    @Autowired
    lateinit var dataSource: DataSource

    @Bean
    fun passwordEncoder(): PasswordEncoder = BCryptPasswordEncoder()

    @Bean
    fun userDetailsService(encoder: PasswordEncoder): UserDetailsService =
        JdbcUserDetailsManager(dataSource)

    @Bean
    fun securityFilter(http: HttpSecurity): SecurityFilterChain {
        http.csrf().disable()
            .authorizeRequests()
            .antMatchers("/user/**").hasRole("USER")
            .antMatchers("/hello").permitAll()
            .anyRequest().authenticated()
            .and()
            .formLogin().loginProcessingUrl("/xiaoming_login")
            .and()
            .logout().logoutUrl("/xiaoming_logout")
            .deleteCookies("JSESSIONID")
            .and()
            .rememberMe()
            .and()
            .httpBasic()
            .authenticationEntryPoint(object : BasicAuthenticationEntryPoint() {
                override fun afterPropertiesSet() {
                    realmName = "XJTUSE Xiaoming"
                    super.afterPropertiesSet()
                }
            })

        return http.build()
    }
}