package com.tu.employeemanagement.repository;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;

import com.tu.employeemanagement.model.Department;
import com.tu.employeemanagement.model.Employee;

// a slice test for the database layer: Spring starts only JPA and the repositories, on an in-memory H2 database
// (it replaces the configured one on its own, so no test profile is needed). Each test runs in a transaction
// that's rolled back at the end, so the tests can't see each other's rows. Mocks can't test a query; this can
@DataJpaTest
class DepartmentRepositoryTest {

    @Autowired
    private DepartmentRepository departmentRepository;

    @Autowired
    private EmployeeRepository employeeRepository;

    @Test
    void findDepartmentsWithoutEmployeesIgnoresSoftDeletedEmployees() {
        Department finance = departmentRepository.save(Department.builder().name("Finance").build());
        Department marketing = departmentRepository.save(Department.builder().name("Marketing").build());
        departmentRepository.save(Department.builder().name("Research").build());
        employeeRepository.save(employee("john.doe@example.com", finance, false));
        employeeRepository.save(employee("jane.smith@example.com", marketing, true));

        List<Department> result = departmentRepository.findDepartmentsWithoutEmployees();

        // Finance has an active employee. Marketing's only employee is soft-deleted, so it counts as having none;
        // with the soft-delete condition in WHERE instead of ON, the query would return no departments at all
        assertThat(result).extracting(Department::getName).containsExactlyInAnyOrder("Marketing", "Research");
    }

    private static Employee employee(String email, Department department, boolean deleted) {
        return Employee.builder()
                .firstName("Test")
                .lastName("Employee")
                .email(email)
                .department(department)
                .isDeleted(deleted)
                .build();
    }

}
