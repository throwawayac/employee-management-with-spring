package com.tu.employeemanagement.seeder;

import java.util.List;

import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import com.tu.employeemanagement.misc.EmployeeManagementConstants.LogMessages;
import com.tu.employeemanagement.misc.EmployeeManagementConstants.SeedDepartments;
import com.tu.employeemanagement.misc.EmployeeManagementConstants.SeederOrder;
import com.tu.employeemanagement.model.Department;
import com.tu.employeemanagement.repository.DepartmentRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j // for cleaner logging messages to the console during seeding
@Component
@RequiredArgsConstructor
@Order(SeederOrder.DEPARTMENTS) // must run before EmployeeSeeder, which looks these departments up by name
public class DepartmentSeeder implements CommandLineRunner {

    private final DepartmentRepository departmentRepository;

    @Override
    public void run(String... args) throws Exception {

        if (departmentRepository.count() > 0) {
            log.info(LogMessages.DEPARTMENTS_ALREADY_EXIST);
            return;
        }

        // names must pass DepartmentDto validation (DEPARTMENT_NAME_MIN_LENGTH to DEPARTMENT_NAME_MAX_LENGTH characters)
        Department hr = Department.builder()
                .name(SeedDepartments.HUMAN_RESOURCES)
                .build();

        Department it = Department.builder()
                .name(SeedDepartments.INFORMATION_TECHNOLOGY)
                .build();

        Department finance = Department.builder()
                .name(SeedDepartments.FINANCE)
                .build();

        Department marketing = Department.builder()
                .name(SeedDepartments.MARKETING)
                .build();

        // saveAll runs in a single transaction, so a failure can't leave the table half-seeded
        departmentRepository.saveAll(List.of(hr, it, finance, marketing));

        log.info(LogMessages.DEPARTMENTS_SEEDED);
    }

}
