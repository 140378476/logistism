package cn.ancono.logistism.service

import cn.ancono.logistism.dao.EndpointDAO
import cn.ancono.logistism.dao.LocationDAO
import cn.ancono.logistism.dao.RepositoryDAO
import cn.ancono.logistism.entity.Endpoint
import cn.ancono.logistism.entity.Location
import cn.ancono.logistism.entity.Repository
import cn.ancono.logistism.forms.EndpointForm
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional


/*
 * Created by liyicheng at 2020-05-12 16:02
 */
/**
 * @author liyicheng
 */
@Transactional
@Service
open class LocationService @Autowired constructor(
        val repositoryDAO: RepositoryDAO,
        val endpointDAO: EndpointDAO,
        val locationDAO: LocationDAO
) {


    open fun getRepoOfSameDistrict(location: Location): List<Repository> {
        return repositoryDAO.findByDistrict(location.province!!, location.city!!, location.district!!)
    }

    open fun saveEndpoint(customerName: String, form: EndpointForm): Boolean {
        var location = Location()
        location.name = form.locationName
        location.province = form.province
        location.city = form.city
        location.district = form.district
        location.street = form.street
        location.address = form.address
        if (getRepoOfSameDistrict(location).isEmpty()) {
            return false
        }
        location = locationDAO.saveAndFlush(location)
        val locationId = location.id!!

        var endpoint = Endpoint()
        endpoint.name = form.name ?: ""
        endpoint.phone = form.phone ?: ""
        endpoint.description = form.description ?: ""
        endpoint.location = location
        endpoint = endpointDAO.saveAndFlush(endpoint)
        endpointDAO.saveCustomerEndpoint(endpoint.id!!, customerName)
        return true
    }

//    open fun getSupportedProvinces() : List<String>{
//        TODO()
//    }
//
//    open fun getSupportedCities()
}