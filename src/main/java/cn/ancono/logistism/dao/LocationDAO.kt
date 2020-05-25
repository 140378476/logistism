package cn.ancono.logistism.dao

import cn.ancono.logistism.entity.Customer
import cn.ancono.logistism.entity.Endpoint
import cn.ancono.logistism.entity.Location
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository


/*
 * Created by liyicheng at 2020-05-12 16:31
 */
@Repository
interface EndpointDAO : JpaRepository<Endpoint, Long> {


    @Query("select t2.* from logistism.customer_endpoint t1 left join logistism.endpoint t2 on t1.endpoint_id = t2.id where customer = :name", nativeQuery = true)
    fun findAllByCustomer(@Param("name") name: String) : List<Endpoint>

    @Modifying
    @Query("insert into logistism.customer_endpoint(customer, endpoint_id) " +
            "VALUE (:customer,:endpointId)", nativeQuery = true)
    fun saveCustomerEndpoint(@Param("endpointId") endpointId: Long,
                             @Param("customer") customer: String)

//    @Query("insert into logistism.endpoint(location, name, phone, description) value (?1,?2,?3,?4)", nativeQuery = true)
//    fun saveEndpoint(locationId: Long, name: String, phone: String, description: String)

}

@Repository
interface LocationDAO : JpaRepository<Location, Long> {

}