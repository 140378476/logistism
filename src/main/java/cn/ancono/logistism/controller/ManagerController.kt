package cn.ancono.logistism.controller

import cn.ancono.logistism.dao.*
import cn.ancono.logistism.entity.PostmanRepo
import cn.ancono.logistism.forms.ManagePostmanForm
import cn.ancono.logistism.forms.UserRegistryForm
import cn.ancono.logistism.service.PostmanService
import cn.ancono.logistism.service.UserService
import org.apache.logging.log4j.LogManager
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.validation.BindingResult
import org.springframework.web.bind.annotation.*
import org.springframework.web.servlet.ModelAndView
import javax.servlet.http.HttpServletRequest
import javax.validation.Valid


/*
 * Created by liyicheng at 2020-05-09 21:23
 */
/**
 * Deals with pages of managers, including login.
 *
 * @author liyicheng
 */
@Controller
class ManagerController {
    companion object {
        val logger = LogManager.getLogger()
    }


    @Autowired
    lateinit var orderDAO: OrderDAO

    @Autowired
    lateinit var postmanDAO: PostmanDAO

    @Autowired
    lateinit var repoDAO: RepositoryDAO

    @Autowired
    lateinit var userService: UserService

    @Autowired
    lateinit var postmanRepoDAO: PostmanRepoDAO

    @Autowired
    lateinit var postmanService: PostmanService
    @Autowired
    lateinit var shipmentDAO: ShipmentDAO

    @RequestMapping("/manager", "/manager/index")
    fun managerIndex(request: HttpServletRequest): ModelAndView {
        val mv = ModelAndView("/manager/index")
        mv.addObject("nickname", userService.getUserNickname(request.remoteUser))
        userService.getUserTypes(request.remoteUser).forEach { mv.addObject(it.s, 1) }
        return mv
    }

    @RequestMapping("/manager/repo", "/repo")
    fun managerRepo(model: Model): String {
        model.addAttribute("repos", repoDAO.findAll())
        return "/manager/repositories"
    }

    @RequestMapping("/manager/addManager", method = [RequestMethod.GET])
    fun addManagerPage(userRegistryForm: UserRegistryForm): String {
//        model.addAttribute("registryForm", UserRegistryForm())
        return "/manager/addManager"
    }

    @RequestMapping("/manager/addManager", method = [RequestMethod.POST])
    fun doRegistry(
            model: Model,
            @Valid
            userRegistryForm: UserRegistryForm,
            bindingResult: BindingResult
    ): String {
//        println(userRegistryForm)
        if (bindingResult.hasErrors()) {
            return "/manager/addManager"
        }
        if (userRegistryForm.password != userRegistryForm.password2) {
            bindingResult.rejectValue("password", "", "两次输入的密码不一致")
            return "/manager/addManager"
        }
        val succeeded = userService.registerManager(userRegistryForm)
        if (!succeeded) {
            bindingResult.rejectValue("username", "", "用户名重复")
            return "/manager/addManager"
        }

        model.addAttribute("success", 1)
        return "/manager/addManager"
    }

    @RequestMapping("/manager/repo/{repoId}")
    fun storageDetails(@PathVariable repoId: Long, model: Model): String {
        val repo = repoDAO.findById(repoId)
        if (repo.isEmpty) {
            return "redirect:/manager/repo"
        }
        val orders = orderDAO.findOrdersIn(repoId)
        model.addAttribute("orders", orders)
        return "/manager/repo"
    }


    @RequestMapping("/manager/postmen")
    fun viewPostmen(model: Model): String {
        val postmen = postmanDAO.findAll()
        model.addAttribute("postmen", postmen)
        return "/manager/postmen"
    }



    @RequestMapping("/manager/managePostman", method = [RequestMethod.GET])
    fun managePostman(
            model: Model,
            managePostmanForm: ManagePostmanForm,
            @RequestParam(required = false) pName: String?,
            @RequestParam(required = false) repoId: Long?
    ): String {
//        model.addAttribute("registryForm", UserRegistryForm())
        val entries = when {
            pName != null -> {
                postmanRepoDAO.findByPostmanUsername(pName)
            }
            repoId != null -> {
                postmanRepoDAO.findByRepositoryId(repoId)
            }
            else -> {
                postmanRepoDAO.findAll()
            }
        }
        val postmen = postmanDAO.findAll()
        val repositories = repoDAO.findAll()
        model.addAttribute("entries", entries)
        model.addAttribute("postmen", postmen)
        model.addAttribute("repositories", repositories)
        return "/manager/managePostman"
    }

    @RequestMapping("/manager/managePostman", method = [RequestMethod.POST])
    fun doManagePostman(
            model: Model,
            @Valid
            managePostmanForm: ManagePostmanForm,
            bindingResult: BindingResult
    ): String {
//        println(userRegistryForm)
        if (bindingResult.hasErrors()) {
            return "/manager/managePostman"
        }
        val p0 = postmanDAO.findById(managePostmanForm.postman!!)
        val r0 = repoDAO.findById(managePostmanForm.repository!!)
        if (p0.isEmpty) {
            bindingResult.rejectValue("postman", "", "不存在的快递员")
        }
        if (r0.isEmpty) {
            bindingResult.rejectValue("repository", "", "不存在的快递点")
        }
        val p = p0.get()
        val r = r0.get()
        val en = PostmanRepo()
        en.postman = p
        en.repository = r
        if (!postmanRepoDAO.existsByPostmanAndRepository(p, r)) {
            postmanRepoDAO.save(en)
        }

//        model.addAttribute("success", 1)
        return "redirect:/manager/managePostman"
    }

    @RequestMapping("/manager/removePostmanRepo/{pName}/{repoId}")
    fun removePostRepo(@PathVariable pName: String, @PathVariable repoId: Long): String {
        postmanService.removePostRepo(pName, repoId)
        return "redirect:/manager/managePostman"
    }

    @RequestMapping("/manager/shipments")
    fun viewShipments(model: Model) : String{
        val shipments = shipmentDAO.findLatest(20)
        model.addAttribute("shipments",shipments)
        return "/manager/shipments"
    }
}