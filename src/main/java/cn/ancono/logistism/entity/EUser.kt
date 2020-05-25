package cn.ancono.logistism.entity

import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.Id
import javax.persistence.Table


/*
 * Created by liyicheng at 2020-05-10 19:58
 */
/**
 * @author liyicheng
 */
@Entity
@Table(name = "user")
class EUser {
    @Id
    var username : String? = null
    var password : String? = null
    @Column(name = "nickname")
    var nickname : String? = null
}

@Entity
@Table(name = "manager")
open class Manager {
    @Id
    @Column(name = "username")
    open var name : String? = null

    open var level : Int? = null


    override fun toString(): String {
        return "$name"
    }
}

@Entity
@Table(name = "customer")
open class Customer {
    @Id
    open var username : String? = null
}

@Entity
@Table(name = "postman")
open class Postman{
    @Id
    open var username : String? = null

    @Column(name = "real_name")
    open var realName : String? = null

    open var phone : String? = null

    val nameAndPhone : String
        get() = "$realName ($phone)"
}