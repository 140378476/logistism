package cn.ancono.logistism.service

import cn.ancono.logistism.dao.RepositoryDAO
import cn.ancono.logistism.dao.ShipmentDAO
import cn.ancono.logistism.dao.ShipmentScheduleDAO
import cn.ancono.logistism.entity.Shipment
import cn.ancono.logistism.entity.ShipmentSchedule
import org.apache.logging.log4j.LogManager
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.*


/*
 * Created by liyicheng at 2020-05-13 13:07
 */
/**
 * @author liyicheng
 */
@Service
open class ShipmentService @Autowired constructor(
        val shipmentDAO: ShipmentDAO,
        val scheduleDAO: ShipmentScheduleDAO,
        val repositoryDAO: RepositoryDAO

) {
    @Autowired
    lateinit var planService: PlanService

    companion object {
        val logger = LogManager.getLogger()
    }

    val planningTime = Duration.ofMinutes(15)

    val planCount = 5

    private fun findNextScheduledTime(latest: Instant, schedule: ShipmentSchedule): Instant? {
        val dt = ZonedDateTime.ofInstant(latest, ZoneId.systemDefault())
        val time = schedule.repetitionTime
        return when (schedule.repetitionType) {
            "minute" -> {
                dt.plusMinutes(1).withSecond(time.second)
            }
            "hour" -> {
                dt.plusHours(1).withMinute(time.minute).withSecond(time.second)
            }
            "day" -> {
                dt.plusDays(1).withHour(time.hour).withMinute(time.minute).withSecond(time.second)
            }
            "week" -> {
                val t = dt.plusWeeks(1)
                        .withHour(time.hour)
                        .withMinute(time.minute)
                        .withSecond(time.second)
                val day = time.dayOfWeek.value - t.dayOfWeek.value
                t.plusDays(day.toLong())
            }

            else -> {
                null
            }
        }?.toInstant()
    }

    @Transactional
    open fun createShipments() {
        for (schedule in scheduleDAO.findAll()) {
            val shipments = shipmentDAO.findLatestOfSchedule(schedule.shipmentId)
            val now = Instant.now()
            val end = Instant.now().plus(planningTime)
            var latest = shipments.lastOrNull()?.departureTime ?: now
            if (shipments.size >= planCount && latest >= end) {
                continue
            }
            val remainCount = planCount - shipments.size
            var count = 0
            while (count < remainCount || latest < end) {
                val next = findNextScheduledTime(latest, schedule) ?: break
                var shipment = Shipment()
                shipment.shipmentScheduleId = schedule.shipmentId
                shipment.departureTime = next
                shipment.arrivalTime = next + schedule.timeCostDuration!!
                shipment.description = schedule.description
                shipment.source = repositoryDAO.findById(schedule.sourceId).get()
                shipment.destination = repositoryDAO.findById(schedule.destinationId).get()
                shipment.transportation = schedule.transportation
                latest = next
                shipment = shipmentDAO.save(shipment)
                logger.info("Shipment created: ${shipment.id}:${shipment.description}")
                count++
            }
        }
    }

    open fun updateShipments() {
        updateDeparture()
        updateArrival()
    }

    @Transactional
    protected open fun updateDeparture() {
        val shipments = shipmentDAO.findShouldHaveDeparted()
        for (shipment in shipments) {
            shipment.state = Shipment.ShipmentState.DEPARTED
            planService.startShipping(shipment)
            shipmentDAO.save(shipment)
            logger.info("Shipment began: ${shipment.id}:${shipment.description}")
        }
    }

    @Transactional
    protected open fun updateArrival() {
        val shipments = shipmentDAO.findShouldHaveArrived()
        for (shipment in shipments) {
            shipment.state = Shipment.ShipmentState.ARRIVED
            planService.finishShipping(shipment)
            shipmentDAO.save(shipment)
            logger.info("Shipment finished: ${shipment.id}:${shipment.description}")
        }
    }
}