package cn.ancono.logistism.entity

import javax.persistence.*


/*
 * Created by liyicheng at 2020-05-12 10:01
 */
/**
 * @author liyicheng
 */
@Entity
@Table(name = "location")
open class Location {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    open var id: Long? = null
    open var name: String? = null

    open var province: String? = null
    open var city: String? = null
    open var district: String? = null

    open var street: String? = null
    open var address: String? = null

    val detailedDistrict : String
        get() = "$province $city $district "

    val detailedAddress : String
        get() = "$province $city $district $street $address"
}