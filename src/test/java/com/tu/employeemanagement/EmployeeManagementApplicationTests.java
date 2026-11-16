package com.tu.employeemanagement;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

// starts the whole application (every bean, JPA, the seeders) and fails if any part of it can't start.
// The "test" profile adds application-test.properties, so it runs on H2 instead of PostgreSQL
@SpringBootTest
@ActiveProfiles("test")
class EmployeeManagementApplicationTests {

    @Test
    void contextLoads() {
    }

}
