package com.tu.employeemanagement.service;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.tu.employeemanagement.dto.EmployeeDto;
import com.tu.employeemanagement.mapper.EmployeeMapper;
import com.tu.employeemanagement.model.Department;
import com.tu.employeemanagement.model.Employee;
import com.tu.employeemanagement.repository.EmployeeRepository;

import lombok.RequiredArgsConstructor;

@Service
@Transactional // each public method runs in one transaction: its checks and its save succeed or fail together
@RequiredArgsConstructor
public class EmployeeService {

    private final EmployeeRepository employeeRepository;
    private final DepartmentService departmentService;
    private final EmployeeMapper employeeMapper;

    public List<EmployeeDto> getAll() {
        return employeeRepository.findAll().stream()
                .filter(employee -> !employee.isDeleted()) // soft-deleted employees are hidden
                .map(employeeMapper::toDto)
                .toList();
    }

    public EmployeeDto getById(Long id) {
        return employeeMapper.toDto(findEmployee(id));
    }

    public EmployeeDto create(EmployeeDto dto) {
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
        employee.setDepartment(departmentService.findDepartment(dto.getDepartmentId())); // the mapper ignores department
        return employeeMapper.toDto(employeeRepository.save(employee));
    }

    public EmployeeDto update(Long id, EmployeeDto dto) {
        Employee employee = findEmployee(id);
        // only check values that changed, otherwise the employee's own email/phone number would count as a duplicate
        if (!dto.getEmail().equals(employee.getEmail()) && employeeRepository.existsByEmail(dto.getEmail())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Employee email is already in use");
        }
        if (dto.getPhoneNumber() != null && !dto.getPhoneNumber().equals(employee.getPhoneNumber())
                && employeeRepository.existsByPhoneNumber(dto.getPhoneNumber())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Employee phone number is already in use");
        }
        Department department = departmentService.findDepartment(dto.getDepartmentId());

        employee.setFirstName(dto.getFirstName());
        employee.setLastName(dto.getLastName());
        employee.setProfilePicture(dto.getProfilePicture());
        employee.setPhoneNumber(dto.getPhoneNumber());
        employee.setEmail(dto.getEmail());
        employee.setDepartment(department);
        return employeeMapper.toDto(employeeRepository.save(employee));
    }

    public void delete(Long id) {
        Employee employee = findEmployee(id);
        employee.setDeleted(true);
        employeeRepository.save(employee);
    }

    @Deprecated // use delete(); this removes the row for good
    public void hardDelete(Long id) {
        employeeRepository.delete(findEmployee(id));
    }

    // soft-deleted employees count as not found
    private Employee findEmployee(Long id) {
        return employeeRepository.findById(id)
                .filter(employee -> !employee.isDeleted())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Employee " + id + " not found"));
    }

}
