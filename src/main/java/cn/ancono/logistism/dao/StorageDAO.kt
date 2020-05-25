package cn.ancono.logistism.dao

import cn.ancono.logistism.entity.Storage
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query


/*
 * Created by liyicheng at 2020-05-12 21:50
 */
/**
 * @author liyicheng
 */
interface StorageDAO : JpaRepository<Storage, Long> {


//    @Modifying
//    @Query("delete from logistism.storage where ",nativeQuery = true)
//    fun deleteAllByOrder(orderId : Long)

}