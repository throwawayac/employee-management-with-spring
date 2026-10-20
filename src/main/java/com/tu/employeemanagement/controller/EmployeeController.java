package com.tu.employeemanagement.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import com.tu.employeemanagement.dto.EmployeeDto;
import com.tu.employeemanagement.mapper.EmployeeMapper;
import com.tu.employeemanagement.model.Department;
import com.tu.employeemanagement.model.Employee;
import com.tu.employeemanagement.repository.DepartmentRepository;
import com.tu.employeemanagement.repository.EmployeeRepository;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/employees")
@RequiredArgsConstructor
public class EmployeeController {

    private final EmployeeRepository employeeRepository;
    private final DepartmentRepository departmentRepository;
    private final EmployeeMapper employeeMapper;

    @GetMapping // GET /api/employees
    public List<EmployeeDto> getAll() {
        return employeeRepository.findAll().stream()
                .filter(employee -> !employee.isDeleted()) // soft-deleted employees are hidden
                .map(employeeMapper::toDto)
                .toList();
    }

    @GetMapping("/{id}") // GET /api/employees/1
    public EmployeeDto getById(@PathVariable Long id) {
        return employeeMapper.toDto(findEmployee(id));
    }

    @PostMapping // POST /api/employees
    @ResponseStatus(HttpStatus.CREATED)
    public EmployeeDto create(@Valid @RequestBody EmployeeDto dto) {
        // also counts soft-deleted employees: their email and phone number are still in the table's UNIQUE columns
        if (employeeRepository.existsByEmail(dto.getEmail())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Employee email is already in use");
        }
        // the phone number is optional, and existsByPhoneNumber(null) would match every employee without one
        if (dto.getPhoneNumber() != null && employeeRepository.existsByPhoneNumber(dto.getPhoneNumber())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Employee phone number is already in use");
        }

        Employee employee = employeeMapper.toEntity(dto);
        employee.setId(null); // the database generates the id, ignore one sent by the client
        employee.setDepartment(findDepartment(dto.getDepartmentId())); // the mapper ignores department
        return employeeMapper.toDto(employeeRepository.save(employee));
    }

    @PutMapping("/{id}") // PUT /api/employees/1
    public EmployeeDto update(@PathVariable Long id, @Valid @RequestBody EmployeeDto dto) {
        Employee employee = findEmployee(id);
        // only check values that changed, otherwise the employee's own email/phone number would count as a duplicate
        if (!dto.getEmail().equals(employee.getEmail()) && employeeRepository.existsByEmail(dto.getEmail())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Employee email is already in use");
        }
        if (dto.getPhoneNumber() != null && !dto.getPhoneNumber().equals(employee.getPhoneNumber())
                && employeeRepository.existsByPhoneNumber(dto.getPhoneNumber())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Employee phone number is already in use");
        }
        Department department = findDepartment(dto.getDepartmentId());

        employee.setFirstName(dto.getFirstName());
        employee.setLastName(dto.getLastName());
        employee.setProfilePicture(dto.getProfilePicture());
        employee.setPhoneNumber(dto.getPhoneNumber());
        employee.setEmail(dto.getEmail());
        employee.setDepartment(department);
        return employeeMapper.toDto(employeeRepository.save(employee));
    }

    @DeleteMapping("/{id}") // DELETE /api/employees/1 (soft delete: the row stays, marked as deleted)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        Employee employee = findEmployee(id);
        employee.setDeleted(true);
        employeeRepository.save(employee);
    }

    @Deprecated // use the soft delete above; this removes the row for good
    @DeleteMapping("/hard-delete/{id}") // DELETE /api/employees/hard-delete/1
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void hardDelete(@PathVariable Long id)
    {
        var employee = findEmployee(id);

        employeeRepository.delete(employee);
    }

    // soft-deleted employees count as not found
    private Employee findEmployee(Long id) {
        return employeeRepository.findById(id)
                .filter(employee -> !employee.isDeleted())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Employee " + id + " not found"));
    }

    // soft-deleted departments count as not found, so nobody can be added to them
    private Department findDepartment(Long id) {
        return departmentRepository.findById(id)
                .filter(department -> !department.isDeleted())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Department " + id + " not found"));
    }

}
