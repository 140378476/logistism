package cn.ancono.logistism.service

import cn.ancono.logistism.dao.*
import cn.ancono.logistism.entity.Order
import cn.ancono.logistism.entity.OrderLog
import cn.ancono.logistism.entity.OrderState
import cn.ancono.logistism.forms.OrderForm
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.*


/*
 * Created by liyicheng at 2020-05-11 16:53
 */
/**
 * @author liyicheng
 */
@Service
open class OrderService @Autowired constructor(

        val orderDAO: OrderDAO,
        val transferLogDAO: TransferLogDAO,
        val postingLogDAO: PostingLogDAO,
        val postingPlanDAO: PostingPlanDAO,
        val shippingPlanDAO: ShippingPlanDAO,
        val storageDAO: StorageDAO

) {

    open fun getOrdersByName(name: String): List<Order> {
        val orders = orderDAO.findOrderByCustomerUsername(name)
        return orders.sortedByDescending { it.creationDate }
    }

    open fun getOrderLogs(orderId: Long): List<OrderLog> {
        val log1 = postingLogDAO.findAllByOrderId(orderId)
        val log2 = transferLogDAO.findAllByOrderId(orderId)
        return (log1 + log2).sortedBy { it.time }
    }

    @Transactional
    open fun createOrder(username: String, orderForm: OrderForm): Boolean {
        val itemName = orderForm.itemName ?: "none"
        val source = orderForm.source!!
        val destination = orderForm.destination!!
        val remark = orderForm.remark ?: ""
        orderDAO.addOrder(itemName, username, source, destination, remark)
        return true
//        order.source =
    }

    open fun getOrderCurrentStateDetails(orderId: Long): String? {
        val order = orderDAO.findById(orderId).get()
        when (order.state) {
            OrderState.NOT_RETRIEVED, OrderState.WAIT_FOR_DELIVERY -> {
                val op = postingPlanDAO.findByOrderId(orderId)
                if (op.isEmpty()) {
                    return null
                }
                val plan = op.first()
                val postman = plan.postman ?: return null
                return if (order.state == OrderState.NOT_RETRIEVED) {
                    "等待快递员 ${postman.nameAndPhone} 取件"
                } else {
                    "等待快递员 ${postman.nameAndPhone} 派送"
                }

            }
            OrderState.ON_SHIPMENT -> {
                val op = storageDAO.findById(orderId)
                if (op.isEmpty) {
                    return null
                }
                val storage = op.get()
                val plan = shippingPlanDAO.findByOrderId(orderId).find {
                    val shipment = it.shipment
                    shipment?.source?.id == storage.refId ||
                            shipment?.id == storage.refId
                } ?: return null
                val prefix = if (storage.type == 0) {
                    "等待"
                } else {
                    "正在"
                }
                return prefix + plan.details
            }
            else -> return null
        }
    }


}