package cn.ancono.logistism.dao

import cn.ancono.logistism.entity.*
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param


/*
 * Created by liyicheng at 2020-05-10 21:27
 */
/**
 * @author liyicheng
 */
interface RepositoryDAO : JpaRepository<Repository, Long> {

    fun findByLocationId(locationId: Long): List<Repository>

    @Query("select r from Repository r where r.location.province = :province " +
            "and r.location.city = :city and r.location.district =:district")
    fun findByDistrict(@Param("province") province: String,
                       @Param("city") city: String,
                       @Param("district") district: String): List<Repository>
}

interface PostmanRepoDAO : JpaRepository<PostmanRepo, Long> {
    fun findByPostmanUsername(postman_username: String): List<PostmanRepo>

    fun findByRepositoryId(repository_id: Long): List<PostmanRepo>

    fun deleteByPostmanUsernameAndRepositoryId(postman_username: String, repository_id: Long)

    fun existsByPostmanAndRepository(postman: Postman, repository: Repository) : Boolean
}