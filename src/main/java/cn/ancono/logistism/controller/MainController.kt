package cn.ancono.logistism.controller

import cn.ancono.logistism.dao.OrderDAO
import cn.ancono.logistism.dao.UserDAO
import cn.ancono.logistism.service.OrderService
import cn.ancono.logistism.service.UserService
import cn.ancono.logistism.service.UserType
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.*
import org.springframework.web.servlet.ModelAndView
import javax.servlet.http.HttpServletRequest


/*
 * Created by liyicheng at 2020-05-09 18:43
 */
/**
 * @author liyicheng
 */
@Controller
class MainController {

    @Autowired
    lateinit var userDAO: UserDAO

    @Autowired
    lateinit var userService: UserService

    @Autowired
    lateinit var orderService: OrderService

    @RequestMapping("/", "/index")
    fun indexPage(): String {
//        println("Index page")


        return "index"
    }

    @RequestMapping("/logout")
    fun logoutPage(): String {
        return "logout"
    }

    @RequestMapping("/login", method = [RequestMethod.GET])
    fun loginPage(@RequestParam(value = "error", required = false) error: String?,
                  @RequestParam(value = "logout", required = false) logout: String?
    ): ModelAndView {
        val mv = ModelAndView("/login")
        if (error != null) {
            mv.addObject("error", "用户名或者密码不正确")
        }
        return mv
    }

    @RequestMapping("/home")
    fun homePage(
            request: HttpServletRequest, model: Model
    ): String {
        val username = request.remoteUser
        val nickname = userDAO.findById(username).map { it.nickname }.orElseGet { "null" }
        model.addAttribute("username", request.remoteUser)
        model.addAttribute("nickname", nickname)

        val types = userService.getUserTypes(username)
        types.forEach { model.addAttribute(it.s, 1) }
        if (UserType.Manager in types && UserType.Customer !in types) {
            return "redirect:/manager"
        }
        if (UserType.Postman in types && UserType.Customer !in types) {
            return "redirect:/postman"
        }

        val orders = orderService.getOrdersByName(username)
        model.addAttribute("orders", orders)

        return "/home"
    }


}