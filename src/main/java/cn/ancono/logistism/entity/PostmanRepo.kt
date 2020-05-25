package cn.ancono.logistism.entity

import java.io.Serializable
import javax.persistence.*


/*
 * Created by liyicheng at 2020-05-13 20:52
 */
@Entity
@Table(name = "postman_repo")
open class PostmanRepo : Serializable{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    open var id : Long? = null

    @JoinColumn(name = "postman")
    @ManyToOne
    open var postman : Postman ? = null

    @JoinColumn(name = "repository")
    @ManyToOne
    open var repository : Repository ? = null
}
