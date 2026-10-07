package com.tu.employeemanagement.repository;

import org.springframework.stereotype.Repository;

@Deprecated 
@Repository 
public class ExampleRepository {

    public String getExampleData() {
        // This is a placeholder method to demonstrate the repository structure.
        // Hibernate and Spring Data JPA will handle the actual data retrieval in a real implementation.
        // They will generate the necessary SQL queries based on the method names and annotations in the repository interface.
        return "Example data from the repository.";
    }
}
