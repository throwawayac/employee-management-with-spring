package com.tu.employeemanagement.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.tu.employeemanagement.model.Employee;

@Repository
public interface EmployeeRepository extends JpaRepository<Employee, Long> {
    // names aren't unique, so these can match several employees
    List<Employee> findByFirstName(String firstName); // SELECT * FROM employees WHERE first_name = ?
    List<Employee> findByFirstNameAndLastName(String firstName, String lastName); // SELECT * FROM employees WHERE first_name = ? AND last_name = ?

    List<Employee> findByDepartmentId(Long departmentId); // SELECT * FROM employees WHERE department_id = ?

    Optional<Employee> findByEmail(String email); // SELECT * FROM employees WHERE email = ?

    // email and phone_number are UNIQUE: check before saving so you can return a validation error instead of a DB constraint error
    boolean existsByEmail(String email); // SELECT employee_id FROM employees WHERE email = ? LIMIT 1
    boolean existsByPhoneNumber(String phoneNumber); // SELECT employee_id FROM employees WHERE phone_number = ? LIMIT 1

    // counts soft-deleted employees too: their rows still point at the department, and a hard delete would remove
    // them along with it (orphanRemoval = true), so check this before hard-deleting a department
    boolean existsByDepartmentId(Long departmentId); // SELECT employee_id FROM employees WHERE department_id = ? LIMIT 1

    // only active employees, so a department whose employees are all soft-deleted can be soft-deleted too
    boolean existsByDepartmentIdAndIsDeletedFalse(Long departmentId); // SELECT employee_id FROM employees WHERE department_id = ? AND is_deleted = false LIMIT 1

}
