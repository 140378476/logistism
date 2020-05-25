package cn.ancono.logistism.entity

import java.time.Instant
import javax.persistence.*


/*
 * Created by liyicheng at 2020-05-12 14:06
 */
/**
 * @author liyicheng
 */
@Entity
@Table(name = "shipment")
open class Shipment {

    enum class ShipmentState(val display : String) {
        NOT_DEPARTED("未出发"),
        DEPARTED("运送中"),
        ARRIVED("已完成")
    }


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    open var id: Long? = null

    @Column(name = "shipment_schedule_id")
    open var shipmentScheduleId: Long? = null

    open var description: String? = null

    open var transportation: String? = null

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "source_id")
    open var source: Repository? = null

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "destination_id")
    open var destination: Repository? = null

    @Column(name = "departure_time")
//    @Temporal(TemporalType.TIMESTAMP)
    open var departureTime: Instant? = null

    @Column(name = "arrival_time")
//    @Temporal(TemporalType.TIMESTAMP)
    open var arrivalTime: Instant? = null

    @Enumerated(EnumType.ORDINAL)
    open var state: ShipmentState = ShipmentState.NOT_DEPARTED

    val details: String
        get() = "从 ${source?.name} 运送至 ${destination?.name}"

}