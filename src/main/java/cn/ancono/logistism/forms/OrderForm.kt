package cn.ancono.logistism.forms

import javax.validation.constraints.NotBlank
import javax.validation.constraints.Size


/*
 * Created by liyicheng at 2020-05-12 17:58
 */
/**
 * @author liyicheng
 */
class OrderForm {
    //    @NotBlank(message = "地址不能为空")
    @Size(max = 40)
    var itemName: String? = null

    var source: Long? = null
    var destination: Long? = null

    @Size(max = 40)
    var remark: String? = null
}