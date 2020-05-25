package cn.ancono.logistism.dao

import cn.ancono.logistism.entity.*
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.relational.core.mapping.Table


/*
 * Created by liyicheng at 2020-05-11 16:27
 */
/**
 * @author liyicheng
 */
@Table("user")
interface UserDAO : JpaRepository<EUser, String> {

}
interface CustomerDAO : JpaRepository<Customer, String> {

}

interface ManagerDAO : JpaRepository<Manager, String> {

}

interface PostmanDAO : JpaRepository<Postman, String> {

    @Query("select t.* from logistism.postman t inner join logistism.postman_repo s " +
            "on t.username = s.postman where s.repository = ?1", nativeQuery = true)
    fun findPostmanBelongTo(repoId: Long): List<Postman>

    @Query("select t.* from logistism.postman t inner join logistism.postman_repo s on t.username = s.postman " +
            "where s.repository = ?1 order by (select count(*) from logistism.posting_plan r where r.postman = t.username)", nativeQuery = true)
    fun findPostmanBelongToSortByTasks(repoId: Long) : List<Postman>

}