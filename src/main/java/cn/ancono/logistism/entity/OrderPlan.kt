package cn.ancono.logistism.entity

import java.util.*
import javax.persistence.*


/*
 * Created by liyicheng at 2020-05-12 19:35
 */
@Entity
@Table(name = "posting_plan")
open class PostingPlan {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    open var id: Long? = null

    @JoinColumn(name = "order_id")
    @ManyToOne
    open var order: Order? = null

    open var type: Int? = null

    @JoinColumn(name = "postman")
    @ManyToOne
    open var postman: Postman? = null

    @JoinColumn(name = "repository")
    @ManyToOne
    open var repository: Repository? = null

}

@Entity
@Table(name = "shipping_plan")
open class ShippingPlan {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    open var id: Long? = null

    @JoinColumn(name = "order_id")
    @ManyToOne
    open var order: Order? = null

    @JoinColumn(name = "shipment_id")
    @ManyToOne
    open var shipment: Shipment? = null

    val details : String
        get() = shipment?.details?:""
}