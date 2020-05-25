package cn.ancono.logistism.service

import cn.ancono.logistism.dao.PostmanDAO
import cn.ancono.logistism.dao.PostmanRepoDAO
import cn.ancono.logistism.dao.RepositoryDAO
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional


/*
 * Created by liyicheng at 2020-05-12 20:12
 */
/**
 * @author liyicheng
 */
@Service
open class PostmanService @Autowired constructor(
        val postmanDAO: PostmanDAO,
        val repositoryDAO: RepositoryDAO,
        val postmanRepoDAO: PostmanRepoDAO

){
    @Transactional
    open fun removePostRepo(pName: String, repoId : Long){
        postmanRepoDAO.deleteByPostmanUsernameAndRepositoryId(pName, repoId)
    }
}