package cn.ancono.logistism.dao

import cn.ancono.logistism.entity.Order
import cn.ancono.logistism.entity.PostingPlan
import cn.ancono.logistism.entity.Shipment
import cn.ancono.logistism.entity.ShippingPlan
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query


/*
 * Created by liyicheng at 2020-05-12 19:44
 */
interface PostingPlanDAO : JpaRepository<PostingPlan, Long> {

    fun findByPostmanUsername(postman: String): List<PostingPlan>

    @Query("select p from PostingPlan p where p.order.id = ?1")
    fun findByOrderId(orderId: Long) : List<PostingPlan>
}

interface ShippingPlanDAO : JpaRepository<ShippingPlan, Long> {
    fun findByShipment(shipment: Shipment): List<ShippingPlan>

    @Query("select p from ShippingPlan p where p.order.id = ?1")
    fun findByOrderId(orderId: Long): List<ShippingPlan>
}
