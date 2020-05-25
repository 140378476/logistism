package cn.ancono.logistism.forms

import javax.validation.constraints.NotBlank
import javax.validation.constraints.Size


/*
 * Created by liyicheng at 2020-05-12 15:57
 */
class EndpointForm {

    @NotBlank(message = "地址名称不能为空")
    @Size(max = 40)
    var locationName: String? = null

    @NotBlank(message = "姓名不能为空")
    @Size(max = 40)
    var name: String? = null

    @NotBlank(message = "电话号码不能为空")
    @Size(max = 40)
    var phone: String? = null

    @NotBlank(message = "地址不能为空")
    @Size(max = 40)
    var province: String? = null

    @NotBlank(message = "地址不能为空")
    @Size(max = 40)
    var city: String? = null

    @NotBlank(message = "地址不能为空")
    @Size(max = 40)
    var district: String? = null

    @NotBlank(message = "地址不能为空")
    @Size(max = 40)
    var street: String? = null

    @NotBlank(message = "地址不能为空")
    @Size(max = 40)
    var address: String? = null

    var description: String? = null

}