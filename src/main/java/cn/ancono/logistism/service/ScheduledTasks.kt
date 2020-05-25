package cn.ancono.logistism.service

import org.apache.logging.log4j.LogManager
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.scheduling.config.ScheduledTask
import org.springframework.stereotype.Component
import java.time.Instant
import java.util.*

typealias Task = Pair<Instant, () -> Unit>
/*
 * Created by liyicheng at 2020-05-12 19:22
 */
/**
 * @author liyicheng
 */
@Component
class ScheduledTasks @Autowired constructor(
        val planService: PlanService,
        val shipmentService: ShipmentService
) {


    companion object {
        val logger = LogManager.getLogger()
    }

    private val tasks: PriorityQueue<Task> = PriorityQueue(compareBy { it.first })

    fun addTask(time: Instant, task: () -> Unit) {
        tasks.add(time to task)
    }

    @Scheduled(fixedRate = 5000)
    fun updateScheduled() {
        val now = Instant.now()
        while (tasks.isNotEmpty() && tasks.peek().first < now) {
            val task = tasks.poll().second
            task()
        }
    }

    @Scheduled(fixedRate = 5000)
    fun updatePlan(){
        planService.updatePostingPlans()
        planService.updateShippingPlans()
    }

    @Scheduled(fixedRate = 5000)
    fun updateShipments(){
        shipmentService.createShipments()
        shipmentService.updateShipments()
    }


}