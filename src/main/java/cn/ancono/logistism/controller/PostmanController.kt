package cn.ancono.logistism.controller

import cn.ancono.logistism.dao.PostingPlanDAO
import cn.ancono.logistism.forms.PostmanRegistryForm
import cn.ancono.logistism.service.PlanService
import cn.ancono.logistism.service.PostmanService
import cn.ancono.logistism.service.UserService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Controller
import org.springframework.validation.BindingResult
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.servlet.ModelAndView
import javax.servlet.http.HttpServletRequest
import javax.validation.Valid


/*
 * Created by liyicheng at 2020-05-12 15:45
 */
/**
 * @author liyicheng
 */
@Controller
class PostmanController {

    @Autowired
    lateinit var userService: UserService


    @Autowired
    lateinit var postingPlanDAO: PostingPlanDAO

    @Autowired
    lateinit var planService: PlanService

    @RequestMapping("/postman", "/postman/index")
    fun postmanIndex(request: HttpServletRequest): ModelAndView {
        val mv = ModelAndView("/postman/index")
        val username = request.remoteUser
        mv.addObject("nickname", userService.getUserNickname(request.remoteUser))
        userService.getUserTypes(request.remoteUser).forEach { mv.addObject(it.s, 1) }

        val plans = postingPlanDAO.findByPostmanUsername(username)
        val (rPlans, dPlans) = plans.partition { it.type == 0 }
        mv.addObject("rPlans", rPlans)
        mv.addObject("dPlans", dPlans)
        return mv
    }

    @RequestMapping("/postman/finish/{planId}")
    fun finishPage(request: HttpServletRequest, @PathVariable(name = "planId") planId: Long): String {
        val username = request.remoteUser
        val plans = postingPlanDAO.findByPostmanUsername(username)
        plans.forEach {
            if (it.id == planId) {
                planService.finishPostingPlan(it)
            }
        }
        return "redirect:/postman"
    }


    @RequestMapping("/registryPostman", method = [RequestMethod.GET])
    fun registryPage(postmanRegistryForm: PostmanRegistryForm): String {
//        model.addAttribute("registryForm", UserRegistryForm())
        return "registryPostman"
    }

    @RequestMapping("/registryPostman", method = [RequestMethod.POST])
    fun doRegistry(
            @Valid
            postmanRegistryForm: PostmanRegistryForm,
            bindingResult: BindingResult
    ): String {
//        println(userRegistryForm)
        if (bindingResult.hasErrors()) {
            return "registryPostman"
        }
        if (postmanRegistryForm.password != postmanRegistryForm.password2) {
            bindingResult.rejectValue("password", "", "两次输入的密码不一致")
            return "registryPostman"
        }
        val succeeded = userService.registerPostman(postmanRegistryForm)
        if (!succeeded) {
            bindingResult.rejectValue("username", "", "用户名重复")
            return "registryPostman"
        }


        return "registrySuccess"
    }
}