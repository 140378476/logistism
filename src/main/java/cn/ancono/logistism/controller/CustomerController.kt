package cn.ancono.logistism.controller

import cn.ancono.logistism.dao.EndpointDAO
import cn.ancono.logistism.dao.OrderDAO
import cn.ancono.logistism.forms.EndpointForm
import cn.ancono.logistism.forms.OrderForm
import cn.ancono.logistism.forms.UserRegistryForm
import cn.ancono.logistism.service.LocationService
import cn.ancono.logistism.service.OrderService
import cn.ancono.logistism.service.UserService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.validation.BindingResult
import org.springframework.web.bind.annotation.*
import org.springframework.web.servlet.ModelAndView
import java.text.SimpleDateFormat
import javax.servlet.http.HttpServletRequest
import javax.validation.Valid


/*
 * Created by liyicheng at 2020-05-11 16:57
 */
/**
 * @author liyicheng
 */
@Controller
class CustomerController {

    @Autowired
    lateinit var userService: UserService

    @Autowired
    lateinit var orderDAO: OrderDAO

    @Autowired
    lateinit var endpointDAO: EndpointDAO

    @Autowired
    lateinit var orderService: OrderService

    @Autowired
    lateinit var locationService: LocationService


    @RequestMapping("/registry", method = [RequestMethod.GET])
    fun registryPage(userRegistryForm: UserRegistryForm): String {
//        model.addAttribute("registryForm", UserRegistryForm())
        return "registry"
    }

    @RequestMapping("/registry", method = [RequestMethod.POST])
    fun doRegistry(
            @Valid
            userRegistryForm: UserRegistryForm,
            bindingResult: BindingResult
    ): String {
//        println(userRegistryForm)
        if (bindingResult.hasErrors()) {
            return "registry"
        }
        if (userRegistryForm.password != userRegistryForm.password2) {
            bindingResult.rejectValue("password", "", "两次输入的密码不一致")
            return "registry"
        }
        val succeeded = userService.registerCustomer(userRegistryForm)
        if (!succeeded) {
            bindingResult.rejectValue("username", "", "用户名重复")
            return "registry"
        }


        return "registrySuccess"
    }

    @GetMapping("/registrySuccess")
    fun registrySuccessPage(): String {
        return "registrySuccess"
    }

    @GetMapping("/order/{orderId}")
    fun orderDetail(request: HttpServletRequest,
                    @PathVariable(required = true) orderId: Long): ModelAndView {
        val mv = ModelAndView("customer/order")
        val username = request.remoteUser
        val op = orderDAO.findById(orderId)
        if (op.isEmpty) {
            mv.addObject("error", 1)
            return mv
        }
        val order = op.get()
        if (order.customer?.username != username) {
            mv.addObject("error", 1)
            return mv
        }
        mv.addObject("order", order)
        val logs = orderService.getOrderLogs(orderId)
        mv.addObject("orderLogs", logs)
        val currentState = orderService.getOrderCurrentStateDetails(orderId)
        mv.addObject("currentState", currentState)
        return mv
    }

    @GetMapping("/create")
    fun createOrder(request: HttpServletRequest, model: Model, orderForm: OrderForm): String {
        val username = request.remoteUser!!
        val endpoints = endpointDAO.findAllByCustomer(username)
        model.addAttribute("endpoints", endpoints)
        return "customer/create"
    }

    @PostMapping("/create")
    fun doCreateOrder(
            request: HttpServletRequest,
            model: Model,
            @Valid
            orderForm: OrderForm,
            bindingResult: BindingResult
    ): String {
        val username = request.remoteUser!!
        val endpoints = endpointDAO.findAllByCustomer(username)
        model.addAttribute("endpoints", endpoints)
//        println(userRegistryForm)
        if (bindingResult.hasErrors()) {
            return "customer/create"
        }
        val succeeded = orderService.createOrder(request.remoteUser, orderForm)
        if (!succeeded) {
            bindingResult.rejectValue("address", "", "不支持的地址")
            return "customer/create"
        }

        return "redirect:/home"
    }


    @GetMapping("/address")
    fun createAddress(request: HttpServletRequest, endpointForm: EndpointForm): String {
        return "customer/address"
    }

    @PostMapping("/address")
    fun doCreateAddress(
            request: HttpServletRequest,
            @Valid
            endpointForm: EndpointForm,
            bindingResult: BindingResult
    ): String {
//        println(userRegistryForm)
        if (bindingResult.hasErrors()) {
            return "customer/address"
        }
        val succeeded = locationService.saveEndpoint(request.remoteUser, endpointForm)
        if (!succeeded) {
            bindingResult.rejectValue("address", "", "不支持的地址")
            return "customer/address"
        }

        return "redirect:/create"
    }
}