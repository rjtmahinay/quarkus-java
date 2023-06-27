package com.rjtmahinay.resource;

import com.rjtmahinay.config.EmployeeProperties;
import com.rjtmahinay.entity.Employee;
import com.rjtmahinay.model.EmployeeDTO;
import com.rjtmahinay.repository.EmployeeRepository;
import io.quarkus.cache.CacheInvalidate;
import io.quarkus.cache.CacheKey;
import io.quarkus.cache.CacheName;
import io.quarkus.cache.CacheResult;
import io.quarkus.cache.runtime.UndefinedCacheKeyGenerator;
import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Any;
import jakarta.enterprise.inject.Default;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.jboss.logging.Logger;
import org.jboss.resteasy.annotations.cache.Cache;

import java.util.List;

@Path("/employee")
@ApplicationScoped
public class EmployeeResource {
    private static final Logger LOG = Logger.getLogger(EmployeeResource.class);

    @Inject
    EmployeeRepository employeeRepository;

    @Inject
    EmployeeProperties employeeProperties;

    @ConfigProperty(name = "employee.name")
    String employeeName;

    @GET
    @Path("/property")
    @Produces(MediaType.APPLICATION_JSON)
    public Uni<String> getEmployeeProperty() {
        LOG.info("GET AN EMPLOYEE PROPERTY");
        return Uni.createFrom().item(employeeProperties.name());
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public List<Employee> getEmployee() {
        LOG.info("GET EMPLOYEE");
        return employeeRepository.findAll().list();
    }

    @GET
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    @CacheResult(cacheName = "employee-cache")
    public Employee getEmployee(@PathParam("id") Long id) {
        LOG.infof("GET EMPLOYEE by ID: %d", id);
        return employeeRepository.findById(id);
    }

    @POST
    @Path("/add")
    @Consumes(MediaType.APPLICATION_JSON)
    @Transactional
    public void addEmployee(@CacheKey EmployeeDTO employeeDTO) {
        LOG.info("ADD EMPLOYEE");
        Employee employee = new Employee();
        employee.name = employeeDTO.name();
        employee.title = employeeDTO.title();
        employee.active = "Y";

        employeeRepository.persist(employee);
    }

    @PUT
    @Path("/update/{id}")
    @Consumes(MediaType.APPLICATION_JSON)
    @CacheInvalidate(cacheName = "employee-cache")
    @Transactional
    public void updateEmployee(@CacheKey @PathParam("id") Long id, EmployeeDTO employeeDTO) {
        LOG.info("UPDATE EMPLOYEE");
        Employee employee = employeeRepository.findById(id);
        employee.name = employeeDTO.name();
        employee.title = employeeDTO.title();

        employeeRepository.persist(employee);
    }

    @DELETE
    @Path("/delete/{id}")
//    @CacheInvalidate(cacheName = "employee-cache")
    @Transactional
    public void deleteEmployee(@PathParam("id") Long id) {
        LOG.info("DELETE EMPLOYEE");
        boolean isDeleted = employeeRepository.deleteById(id);

        if (isDeleted) {
            LOG.infof("Employee with ID: %d is deleted", id);
        }
    }
}
