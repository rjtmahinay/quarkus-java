package com.rjtmahinay.resource;

import com.rjtmahinay.entity.Employee;
import com.rjtmahinay.model.EmployeeDTO;
import com.rjtmahinay.repository.EmployeeRepository;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import org.jboss.logging.Logger;

import java.util.List;

@Path("/employee")
public class EmployeeResource {
    private static final Logger LOG = Logger.getLogger(EmployeeResource.class);

    @Inject
    EmployeeRepository employeeRepository;

    @GET
    @Path("/")
    @Produces(MediaType.APPLICATION_JSON)
    public List<Employee> getEmployee() {
        LOG.info("GET EMPLOYEE");
        return employeeRepository.findAll().list();
    }

    @POST
    @Path("/add")
    @Consumes(MediaType.APPLICATION_JSON)
    @Transactional
    public void addEmployee(EmployeeDTO employeeDTO) {
        LOG.info("ADD EMPLOYEE");
        Employee employee = new Employee();
        employee.name = employeeDTO.name();
        employee.title = employeeDTO.title();

        employeeRepository.persist(employee);
    }

    @PUT
    @Path("/update/{id}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Transactional
    public void updateEmployee(@PathParam("id") Long id, EmployeeDTO employeeDTO) {
        LOG.info("UPDATE EMPLOYEE");
        Employee employee = employeeRepository.findById(id);
        employee.name = employeeDTO.name();
        employee.title = employeeDTO.title();

        employeeRepository.persist(employee);
    }

    @DELETE
    @Path("/delete/{id}")
    @Transactional
    public void deleteEmployee(@PathParam("id") Long id) {
        LOG.info("DELETE EMPLOYEE");
        boolean isDeleted = employeeRepository.deleteById(id);

        if (isDeleted) {
            LOG.infof("Employee with ID: %d is deleted", id);
        }
    }
}
