package com.tu.employeemanagement.seeder;

import java.util.List;

import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import com.tu.employeemanagement.model.Department;
import com.tu.employeemanagement.model.Employee;
import com.tu.employeemanagement.repository.DepartmentRepository;
import com.tu.employeemanagement.repository.EmployeeRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j // for cleaner logging messages to the console during seeding
@Component
@RequiredArgsConstructor
@Order(2) // runs after DepartmentSeeder, which creates the departments used here
public class EmployeeSeeder implements CommandLineRunner {

    private final EmployeeRepository employeeRepository;
    private final DepartmentRepository departmentRepository;

    @Override
    public void run(String... args) throws Exception {

        if (employeeRepository.count() > 0) {
            log.info("Employees already exist. Skipping seeding.");
            return;
        }

        // phone numbers must pass EmployeeDto validation (E.164: "+" and 8-15 digits);
        // 555-0100..0199 numbers are reserved for fictional use
        Employee johnDoe = Employee.builder()
                .firstName("John")
                .lastName("Doe")
                .email("john.doe@example.com")
                .phoneNumber("+12025550101")
                .department(findDepartment("Human Resources"))
                .build();

        Employee janeSmith = Employee.builder()
                .firstName("Jane")
                .lastName("Smith")
                .email("jane.smith@example.com")
                .phoneNumber("+12025550102")
                .department(findDepartment("Information Technology"))
                .build();

        Employee aliceJohnson = Employee.builder()
                .firstName("Alice")
                .lastName("Johnson")
                .email("alice.johnson@example.com")
                .phoneNumber("+12025550103")
                .department(findDepartment("Finance"))
                .build();

        Employee bobBrown = Employee.builder()
                .firstName("Bob")
                .lastName("Brown")
                .email("bob.brown@example.com")
                .phoneNumber("+12025550104")
                .department(findDepartment("Marketing"))
                .build();

        // saveAll runs in a single transaction, so a failure can't leave the table half-seeded
        employeeRepository.saveAll(List.of(johnDoe, janeSmith, aliceJohnson, bobBrown));

        log.info("Employees seeded successfully.");
    }

    private Department findDepartment(String name) {
        return departmentRepository.findByName(name)
                .orElseThrow(() -> new IllegalStateException("Cannot seed employees: department '" + name + "' does not exist"));
    }

}
