package cn.ancono.logistism.service

import cn.ancono.logistism.dao.PostingLogDAO
import cn.ancono.logistism.dao.StorageDAO
import cn.ancono.logistism.dao.TransferLogDAO
import cn.ancono.logistism.entity.*
import org.apache.logging.log4j.LogManager
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import java.time.Instant
import java.util.*


/*
 * Created by liyicheng at 2020-05-12 20:50
 */
@Service
open class TransferService @Autowired constructor(
        val postingLogDAO: PostingLogDAO,
        val transferLogDAO: TransferLogDAO,
        val storageDAO: StorageDAO
) {

    companion object {
        val logger = LogManager.getLogger()
    }

    protected open fun createStorage(order: Order, repo: Repository) {
        val storage = Storage()
        storage.order = order.id
        storage.refId = repo.id
        storage.type = 0 // repository
        storageDAO.save(storage)
        val log = TransferLog()
        log.orderId = order.id
        log.type = 0
        log.repository = repo
        log.time = Instant.now()
        transferLogDAO.save(log)
    }

    protected open fun transferStorage(order: Order, refId: Long, isRepo: Boolean) {
        val s = storageDAO.findById(order.id!!).get()
        s.refId = refId
        s.type = if (isRepo) {
            0
        } else {
            1
        }
        storageDAO.save(s)
    }

    protected open fun removeStorage(order: Order) {
        storageDAO.deleteById(order.id!!)
    }


    open fun finishPosting(postingPlan: PostingPlan) {
        val log = PostingLog()
        log.postman = postingPlan.postman
        log.type = postingPlan.type
        log.time = Instant.now()
        log.orderId = postingPlan.order?.id
        postingLogDAO.save(log)
        if (postingPlan.type == 0) {
            createStorage(postingPlan.order!!, postingPlan.repository!!)
        } else {
            removeStorage(postingPlan.order!!)
        }
        logger.info("Finished plan: ${postingPlan.id}")
    }

    open fun startShipping(shippingPlan: ShippingPlan) {
        val order = shippingPlan.order!!
        val log = TransferLog()
        val shipment = shippingPlan.shipment
        log.orderId = order.id
        log.shipment = shipment
        log.repository = shipment?.source
        log.type = 1
        log.time = Instant.now()
        transferLogDAO.save(log)
        transferStorage(order, shipment?.id!!, false)
        logger.info("start shipping: ${shippingPlan.id}")
    }

    open fun finishShipping(shippingPlan: ShippingPlan) {
        val order = shippingPlan.order!!
        val log = TransferLog()
        val dest = shippingPlan.shipment?.destination
        val shipment = shippingPlan.shipment
        log.orderId = order.id
        log.shipment = shipment
        log.repository = shipment?.destination
        log.type = 0
        log.time = Instant.now()
        transferLogDAO.save(log)
        transferStorage(order, dest?.id!!, true)
        logger.info("finish shipping: ${shippingPlan.id}")
    }


}