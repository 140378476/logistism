package cn.ancono.logistism.service

import cn.ancono.logistism.forms.UserRegistryForm
import cn.ancono.logistism.dao.CustomerDAO
import cn.ancono.logistism.dao.ManagerDAO
import cn.ancono.logistism.dao.PostmanDAO
import cn.ancono.logistism.dao.UserDAO
import cn.ancono.logistism.entity.Customer
import cn.ancono.logistism.entity.EUser
import cn.ancono.logistism.entity.Manager
import cn.ancono.logistism.entity.Postman
import cn.ancono.logistism.forms.PostmanRegistryForm
import org.apache.logging.log4j.LogManager
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.User
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Component
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.*

enum class UserType(val s: String) {
    Customer("customer"),
    Manager("manager"),
    Postman("postman")
}
/*
 * Created by liyicheng at 2020-05-11 15:09
 */
/**
 * @author liyicheng
 */
@Transactional
@Service
open class UserService(

) : UserDetailsService {

    @Autowired
    lateinit var userDAO: UserDAO
    @Autowired
    lateinit var managerDAO: ManagerDAO
    @Autowired
    lateinit var customerDAO: CustomerDAO
    @Autowired
    lateinit var postmanDAO: PostmanDAO
    @Autowired
    lateinit var encoder: PasswordEncoder

    companion object {
        val logger = LogManager.getLogger()
    }


    override fun loadUserByUsername(username: String): UserDetails {
        val re = userDAO.findById(username)
        logger.info("Loading user $username")
        if (re.isEmpty) {
            throw UsernameNotFoundException("User $username does not exist")
        }
        val u = re.get()

        val auth = arrayListOf<GrantedAuthority>()
        for (t in getUserTypes(username)) {
            auth.add(SimpleGrantedAuthority("ROLE_" + t.s))
        }
        return User(u.username, u.password, auth)
    }

    open fun getUserTypes(username: String): EnumSet<UserType> {
        val set = EnumSet.noneOf(UserType::class.java)
        if (customerDAO.existsById(username)) {
            set.add(UserType.Customer)
        }
        if (managerDAO.existsById(username)) {
            set.add(UserType.Manager)
        }
        if (postmanDAO.existsById(username)) {
            set.add(UserType.Postman)
        }
        return set
    }


    open fun registerCustomer(form: UserRegistryForm): Boolean {
        if (!registerUser(form)) {
            return false
        }
        val customer = Customer()
        customer.username = form.username
        customerDAO.save(customer)
        return true
    }


    open fun registerUser(form: UserRegistryForm): Boolean {
        val name = form.username!!
        val pass = form.password!!
        val encodedPassword = encoder.encode(pass)
        if (userDAO.existsById(name)) {
            return false
        }
        val user = EUser()
        user.username = name
        user.password = encodedPassword
        user.nickname = name
        userDAO.save(user)
        return true
    }


    open fun registerManager(form: UserRegistryForm): Boolean {
        if (!registerUser(form)) {
            return false
        }
        val manager = Manager()
        manager.name = form.username
        manager.level = 1
        managerDAO.save(manager)
        return true
    }

    open fun registerPostman(form: PostmanRegistryForm): Boolean {
        if (!registerUser(form)) {
            return false
        }
        val postman = Postman()
        postman.username = form.username
        postman.realName = form.realName
        postman.phone = form.phone
        postmanDAO.save(postman)
        return true
    }


    open fun getUserNickname(username: String): String {
        return userDAO.findById(username).map { it.nickname!! }.orElse("")
    }


}