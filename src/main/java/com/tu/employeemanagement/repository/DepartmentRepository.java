package com.tu.employeemanagement.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.tu.employeemanagement.model.Department;

@Repository
public interface DepartmentRepository extends JpaRepository<Department, Long> {
    Optional<Department> findByName(String name); // SELECT * FROM departments WHERE name = ?

    // name is UNIQUE: check before saving so you can return a validation error instead of a DB constraint error
    boolean existsByName(String name); // SELECT department_id FROM departments WHERE name = ? LIMIT 1

    // departments with no active employees. LEFT JOIN keeps departments that have no matching employee
    // (e is NULL for them), an INNER JOIN would drop exactly those rows. The ON condition skips soft-deleted
    // employees while joining; in the WHERE clause it would also drop the departments we're looking for
    @Query("SELECT d FROM Department d LEFT JOIN d.employees e ON e.isDeleted = false WHERE e.id IS NULL")
    List<Department> findDepartmentsWithoutEmployees(); // SELECT d.* FROM departments d LEFT JOIN employees e ON e.department_id = d.department_id AND e.is_deleted = false WHERE e.employee_id IS NULL

}
