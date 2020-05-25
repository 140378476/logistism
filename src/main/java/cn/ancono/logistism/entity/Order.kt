package cn.ancono.logistism.entity

import java.time.Instant
import java.util.*
import javax.persistence.*

enum class OrderState(val display: String) {
    NOT_RETRIEVED("未取件"),
    ON_SHIPMENT("正在派送"),
    WAIT_FOR_DELIVERY("正在派送"),
    FINISHED("已送达"),
    ERROR("出错")
}
/*
 * Created by liyicheng at 2020-05-12 10:54
 */
/**
 * @author liyicheng
 */
@Entity
@Table(name = "`order`")
open class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    open var id: Long? = null

    @Column(name = "item_name")
    open var itemName: String = ""

    @ManyToOne
    @JoinColumn(name = "customer")
    open var customer: Customer? = null

    @ManyToOne
    @JoinColumn(name = "source")
    open var source: Endpoint? = null

    @ManyToOne
    @JoinColumn(name = "destination")
    open var destination: Endpoint? = null

//    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "creation_date")
    open var creationDate: Instant? = null

    @Enumerated(EnumType.ORDINAL)
    open var state: OrderState? = null

    open var remark: String? = null
}