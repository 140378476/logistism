package cn.ancono.logistism

import cn.ancono.logistism.service.UserService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.authentication.AuthenticationProvider
import org.springframework.security.authentication.dao.DaoAuthenticationProvider
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder


/*
 * Created by liyicheng at 2020-05-09 21:28
 */
/**
 * @author liyicheng
 */
@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
open class GlobalWebSecurityConfig : WebSecurityConfigurerAdapter() {

    @Autowired
    lateinit var userDetailsService: UserService

    @Bean
    open fun passwordEncoder(): PasswordEncoder {
        return BCryptPasswordEncoder()
    }


    //    override fun configure(http: HttpSecurity) {
//        http.authorizeRequests().antMatchers("/", "/repo").permitAll()
//        http.authorizeRequests().antMatchers("/manager/**").hasRole("manager")
//                .and().fo
//    }
//
//    override fun userDetailsService(): UserDetailsService {
//        return super.userDetailsService()
//    }
    @Bean
    open fun daoAuthenticationProvider(): AuthenticationProvider {
        val p = DaoAuthenticationProvider()
        p.setPasswordEncoder(passwordEncoder())
        p.setUserDetailsService(userDetailsService)
        return p
    }


//    @Autowired
//    fun configureGlobal(auth: AuthenticationManagerBuilder) {
//
////        auth.userDetailsService()
//    }


    override fun configure(auth: AuthenticationManagerBuilder) {
        auth.userDetailsService(userDetailsService)
//    val encoder = passwordEncoder()
//        auth.inMemoryAuthentication()
//                .withUser("lyc").password(encoder.encode("root")).roles("manager", "customer")
//                .and().withUser("test").password(encoder.encode("test")).roles("customer")
    }

    override fun configure(http: HttpSecurity) {
        http.authorizeRequests()
                .antMatchers("/", "/index", "/registry","/registryPostman", "/login*").permitAll()
                .antMatchers("/manager/**").hasRole("manager")
                .antMatchers("/customer/**").hasRole("customer")
                .antMatchers("/postman/**").hasRole("postman")
                .anyRequest().authenticated()
                .and()
                .formLogin().loginPage("/login").defaultSuccessUrl("/home").permitAll()
                .and()
                .logout().clearAuthentication(true).invalidateHttpSession(true)
                .and()


    }

    //    @Configuration
//    @Order(1)
//    open class CustomerLoginSecurityConfig : WebSecurityConfigurerAdapter() {
//
//        override fun configure(http: HttpSecurity) {
////            http.authorizeRequests().antMatchers("/", "/index","/registry","/login").permitAll()
////            http.authorizeRequests()
////                    .antMatchers("/customer/**").hasRole("customer")
////                    .and().formLogin().loginPage("/login").permitAll()
////                    .and().logout().permitAll()
////                    .clearAuthentication(true)
////                    .invalidateHttpSession(true)
//
//        }
//    }
//
//
//    @Configuration
//    @Order(5)
//    open class ManagerLoginSecurityConfig : WebSecurityConfigurerAdapter() {
//
//        override fun configure(http: HttpSecurity) {
////            http
////                    .authorizeRequests()
////                    .antMatchers("/manager/**").hasRole("manager")
////                    .and()
////                    .formLogin().loginPage("/manager/login").permitAll()
////                    .successForwardUrl("/manager")
////                    .failureUrl("/manager/login")
////                    .and()
////                    .logout().permitAll()
////                    .clearAuthentication(true)
////                    .invalidateHttpSession(true)
//
//        }
//    }


}