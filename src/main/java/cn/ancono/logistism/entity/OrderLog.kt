package cn.ancono.logistism.entity

import java.time.Instant
import java.util.*
import javax.persistence.*

@MappedSuperclass
sealed class OrderLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    open var id: Long? = null

    @Column(name = "order_id")
    open var orderId: Long? = null

//    @Temporal(TemporalType.TIMESTAMP)
    open var time: Instant? = null

    abstract val details: String
}

/*
 * Created by liyicheng at 2020-05-12 14:02
 */
@Entity
@Table(name = "transfer_log")
open class TransferLog : OrderLog() {


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "repository")
    open var repository: Repository? = null

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "shipment")
    open var shipment: Shipment? = null


    open var type: Int? = null

    override val details: String
        get() {
            return if (type == 0) {
                "运送至 ${repository?.name}"
            } else if (type == 1) {
                "从 ${repository?.name} 送出"
            } else {
                "（未知）"
            }
        }
}


@Entity
@Table(name = "posting_log")
open class PostingLog : OrderLog() {

//    enum class PostingType{
//        RETRIEVE,
//        DELIVERY
//    }


    @JoinColumn(name = "postman")
    @ManyToOne
    open var postman: Postman? = null


    open var type: Int? = null

    override val details: String
        get() {
            return if (type == 0) {
                "由快递员 ${postman?.realName}(${postman?.phone}) 取件"
            } else if (type == 1) {
                "由快递员 ${postman?.realName}(${postman?.phone}) 送达"
            } else {
                "（未知）"
            }
        }
}

@Entity
@Table(name = "storage")
open class Storage {



    @Id
    @Column(name = "`order`")
    open var order: Long? = null


    @Column(name = "ref_id")
    open var refId: Long? = null

    /**
     * 0: repo
     * 1: shipment
     */
    open var type: Int? = null
}