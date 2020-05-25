package cn.ancono.logistism.service

import cn.ancono.logistism.dao.*
import cn.ancono.logistism.entity.*
import org.apache.logging.log4j.LogManager
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.Instant
import kotlin.collections.LinkedHashSet


/*
 * Created by liyicheng at 2020-05-12 15:52
 */
/**
 * @author liyicheng
 */
@Service
open class PlanService
@Autowired
constructor(
        val orderDAO: OrderDAO,
        val postingPlanDAO: PostingPlanDAO,
        val postmanDAO: PostmanDAO,
        val shippingPlanDAO: ShippingPlanDAO,
        val repositoryDAO: RepositoryDAO,
        val storageDAO: StorageDAO,
        val transferService: TransferService
) {

    @Autowired
    lateinit var shipmentDAO: ShipmentDAO

    companion object {
        val logger = LogManager.getLogger()
    }


    protected open fun updateRetrieval() {
        //        logger.info("Updating posting plans...")
        val unplanned = orderDAO.findPostingUnplannedOrders(OrderState.NOT_RETRIEVED.ordinal)
        for (order in unplanned) {
            logger.info("Planning Retrieval: ${order.id}")
            val source = order.source ?: continue
            val location = source.location ?: continue
            val repos = repositoryDAO.findByDistrict(location.province!!, location.city!!, location.district!!)
            for (repo in repos) {
                val postmen = postmanDAO.findPostmanBelongToSortByTasks(repo.id!!)
                if (postmen.isEmpty()) {
                    continue
                }
                val p = postmen[0]
                val plan = PostingPlan()
                plan.order = order
                plan.postman = p
                plan.type = 0
                plan.repository = repo
                postingPlanDAO.save(plan)
                logger.info("Scheduled plan: ${order.id} ${p.username}")
                break
            }

        }
    }

    protected open fun findSuitableRepoAndPostman(location: Location): Pair<Repository, Postman>? {
        val repos = repositoryDAO.findByDistrict(location.province!!, location.city!!, location.district!!)
        for (repo in repos) {
            val postmen = postmanDAO.findPostmanBelongToSortByTasks(repo.id!!)
            if (postmen.isEmpty()) {
                continue
            }
            return repo to postmen.first()
        }
        return null
    }

    protected open fun updateDelivery() {
        //        logger.info("Updating posting plans...")
        val unplanned = orderDAO.findPostingUnplannedOrders(OrderState.WAIT_FOR_DELIVERY.ordinal)
        for (order in unplanned) {
            logger.info("Planning Delivery: ${order.id}")
            val source = order.destination ?: continue
            val location = source.location ?: continue
            val p = findSuitableRepoAndPostman(location)
            if (p == null) {
                logger.warn("No suitable repo for delivery: orderId = ${order.id}")
                continue
            }
            val (repo, postman) = p
            val plan = PostingPlan()
            plan.order = order
            plan.postman = postman
            plan.type = 1
            plan.repository = repo
            postingPlanDAO.save(plan)
            logger.info("Scheduled plan: ${order.id} ${postman.username}")
        }
    }

    open fun updatePostingPlans() {
        updateDelivery()
        updateRetrieval()
    }


    @Transactional
    open fun finishPostingPlan(plan: PostingPlan) {
        postingPlanDAO.deleteById(plan.id!!)
        if (plan.type == 0) {
            orderDAO.updateState(plan.order?.id!!, OrderState.ON_SHIPMENT.ordinal)
        } else {
            orderDAO.updateState(plan.order?.id!!, OrderState.FINISHED.ordinal)
        }
        transferService.finishPosting(plan)
    }

    class Node(var repo: Repository, var time: Instant, var prev: Node?, var shipment: Shipment?) {

    }

    protected open fun makeShippingPlan(order: Order, source: Repository, dest: Repository) {
        val graph = mutableMapOf<Long, Node>()
        val activated = LinkedHashSet<Long>()
        fun addToActivated(n: Node) {
            val id = n.repo.id
            val current = graph[id]
            if (current == null || n.time < current.time) {
                graph[id] = n
                activated += id
            }
        }
        graph[source.id!!] = Node(source, Instant.now(), null, null)
        activated.add(source.id)
        while (activated.isNotEmpty()) {
            val first = activated.first()
            activated.remove(first)
            val node = graph[first]!!
            val repo = node.repo
            val shipments = shipmentDAO.findAvailableBySource(repo.id!!, node.time)
            for (shipment in shipments) {
                val nNode = Node(shipment.destination!!, shipment.arrivalTime!!, node, shipment)
                addToActivated(nNode)
            }
        }
        if (dest.id !in graph) {
            logger.warn("No route from ${source.name} to ${dest.name}")
            return
        }
        var current = graph[dest.id]!!
        val plans = arrayListOf<ShippingPlan>()
        while (current.prev != null) {
            val plan = ShippingPlan()
            plan.order = order
            plan.shipment = current.shipment
            plans.add(plan)
            current = current.prev!!
        }
        shippingPlanDAO.saveAll(plans)
        logger.info("Planned shipping: ${order.id}")
    }

    @Transactional
    open fun updateShippingPlan(order: Order){
        val id = order.id!!
        val op = storageDAO.findById(id)
        if (op.isEmpty) {
            return
        }
        val storage = op.get()
        if (storage.type != 0) {
            logger.warn("Incorrect storage state of order ${order.id}")
            orderDAO.updateState(order.id!!,OrderState.ERROR.ordinal)
            return
        }
        val source = repositoryDAO.findById(storage.refId!!).get()
        val destEnd = order.destination?.location!!
        val p = findSuitableRepoAndPostman(destEnd)
        if (p == null) {
            logger.warn("No suitable repo for delivery: orderId = $id")
            return
        }
        val dest = p.first
        if (source.id == dest.id) { // arrived
            orderDAO.updateState(order.id!!,OrderState.WAIT_FOR_DELIVERY.ordinal)
            return
        }
        makeShippingPlan(order, source, dest)
    }

    @Transactional
    open fun updateShippingPlans() {
        val unplanned = orderDAO.findShippingUnplannedOrders(OrderState.ON_SHIPMENT.ordinal)
        for (order in unplanned) {
            updateShippingPlan(order)
        }
    }

    @Transactional
    open fun startShipping(shipment: Shipment) {
        val plans = shippingPlanDAO.findByShipment(shipment)
        for (plan in plans) {
            transferService.startShipping(plan)
        }
    }

    @Transactional
    open fun finishShipping(shipment: Shipment) {
        val plans = shippingPlanDAO.findByShipment(shipment)
        for (plan in plans) {
            transferService.finishShipping(plan)
        }
        shippingPlanDAO.deleteAll(plans)
    }

}