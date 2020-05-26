package cn.ancono.logistism.dao

import cn.ancono.logistism.entity.Order
import cn.ancono.logistism.entity.OrderState
import cn.ancono.logistism.entity.PostingLog
import cn.ancono.logistism.entity.TransferLog
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param


/*
 * Created by liyicheng at 2020-05-12 11:23
 */
/**
 * @author liyicheng
 */
interface OrderDAO : JpaRepository<Order, Long> {

    fun findOrderByCustomerUsername(name: String): List<Order>

    @Query("select t.* from logistism.`order` t left join logistism.posting_plan s on t.id = s.order_id where t.state = ?1 and s.id is null", nativeQuery = true)
    fun findPostingUnplannedOrders(
            state: Int
    ): List<Order>

    @Query("select t.* from logistism.`order` t left join logistism.shipping_plan s on t.id = s.order_id where t.state = ?1 and s.id is null", nativeQuery = true)
    fun findShippingUnplannedOrders(
            state: Int
    ): List<Order>

    @Modifying
    @Query("insert into logistism.`order`(item_name, customer, source, destination, creation_date, state, remark) " +
            "value (?1,?2,?3,?4,utc_timestamp(),0,?5);", nativeQuery = true)
    fun addOrder(itemName: String, customer: String, source: Long, destination: Long, remark: String)

    @Modifying
    @Query("update logistism.`order` set state = ?2 where id = ?1", nativeQuery = true)
    fun updateState(orderId: Long, state: Int)

    @Query("select o.* from logistism.`order` o join logistism.storage s on o.id = s.`order` where s.type = 0 and s.ref_id = ?1", nativeQuery = true)
    fun findOrdersIn(repoId: Long): List<Order>
}

interface PostingLogDAO : JpaRepository<PostingLog, Long> {
    fun findAllByOrderId(orderId: Long): List<PostingLog>
}

interface TransferLogDAO : JpaRepository<TransferLog, Long> {
    fun findAllByOrderId(orderId: Long): List<TransferLog>
}