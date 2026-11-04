package com.tu.employeemanagement.exception;

// thrown when a request clashes with existing data (a name, email or phone number already in use, or a department
// that still has employees); GlobalExceptionHandler turns it into a 409
public class ConflictException extends RuntimeException {

    public ConflictException(String message) {
        super(message);
    }

}
