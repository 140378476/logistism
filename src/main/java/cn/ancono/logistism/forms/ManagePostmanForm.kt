package cn.ancono.logistism.forms

import javax.validation.constraints.NotBlank
import javax.validation.constraints.NotNull
import javax.validation.constraints.Size


/*
 * Created by liyicheng at 2020-05-13 21:00
 */
class ManagePostmanForm {
    @NotNull
    var postman: String? = null

    @NotNull
    var repository: Long? = null

}