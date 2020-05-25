package cn.ancono.logistism.entity

import javax.persistence.*


/*
 * Created by liyicheng at 2020-05-12 10:55
 */
/**
 * @author liyicheng
 */
@Entity
@Table(name = "endpoint")
class Endpoint {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null

    @ManyToOne
    @JoinColumn(name = "location")
    var location: Location? = null

    var name: String? = null

    var phone: String? = null


    var description: String? = null

    val details : String
        get() = "${location?.detailedAddress}  $name $phone"
}