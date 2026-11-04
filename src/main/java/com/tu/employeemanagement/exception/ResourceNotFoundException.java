package com.tu.employeemanagement.exception;

// thrown when an employee or department doesn't exist (or is soft-deleted); GlobalExceptionHandler turns it into a 404
public class ResourceNotFoundException extends RuntimeException {

    public ResourceNotFoundException(String message) {
        super(message);
    }

}
