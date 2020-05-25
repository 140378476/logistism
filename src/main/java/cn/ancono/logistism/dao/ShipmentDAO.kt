package cn.ancono.logistism.dao

import cn.ancono.logistism.entity.Repository
import cn.ancono.logistism.entity.Shipment
import cn.ancono.logistism.entity.ShipmentSchedule
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import java.time.Instant


/*
 * Created by liyicheng at 2020-05-13 12:32
 */
/**
 * @author liyicheng
 */
interface ShipmentDAO : JpaRepository<Shipment, Long> {


    @Query("select * from logistism.shipment where shipment_schedule_id = ?1 and departure_time > utc_timestamp() " +
            "order by departure_time", nativeQuery = true)
    fun findLatestOfSchedule(scheduleId: Long): List<Shipment>

    @Query("select * from logistism.shipment where departure_time < utc_timestamp and state = 0", nativeQuery = true)
    fun findShouldHaveDeparted(): List<Shipment>

    @Query("select * from logistism.shipment where arrival_time < utc_timestamp and state = 1", nativeQuery = true)
    fun findShouldHaveArrived(): List<Shipment>

    @Query("select s from Shipment s where s.state = 0 and s.departureTime > ?2 " +
            "and s.source.id = ?1")
    fun findAvailableBySource(sourceId: Long, from: Instant): List<Shipment>

    @Query("select * from logistism.shipment where state < 2 order by departure_time limit ?1",nativeQuery = true)
    fun findLatest(count : Int) : List<Shipment>
}

interface ShipmentScheduleDAO : JpaRepository<ShipmentSchedule, Long> {

//    fun findBy
}