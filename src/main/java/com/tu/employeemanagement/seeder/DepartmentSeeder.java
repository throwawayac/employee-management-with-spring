package com.tu.employeemanagement.seeder;

import java.util.List;

import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import com.tu.employeemanagement.model.Department;
import com.tu.employeemanagement.repository.DepartmentRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j // for cleaner logging messages to the console during seeding
@Component
@RequiredArgsConstructor
@Order(1) // must run before EmployeeSeeder, which looks these departments up by name
public class DepartmentSeeder implements CommandLineRunner {

    private final DepartmentRepository departmentRepository;

    @Override
    public void run(String... args) throws Exception {

        if (departmentRepository.count() > 0) {
            log.info("Departments already exist. Skipping seeding.");
            return;
        }

        // names must pass DepartmentDto validation (3-30 characters)
        Department hr = Department.builder()
                .name("Human Resources")
                .build();

        Department it = Department.builder()
                .name("Information Technology")
                .build();

        Department finance = Department.builder()
                .name("Finance")
                .build();

        Department marketing = Department.builder()
                .name("Marketing")
                .build();

        // saveAll runs in a single transaction, so a failure can't leave the table half-seeded
        departmentRepository.saveAll(List.of(hr, it, finance, marketing));

        log.info("Departments seeded successfully.");
    }

}
