package com.tu.employeemanagement.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import com.tu.employeemanagement.dto.EmployeeDto;
import com.tu.employeemanagement.service.EmployeeService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/employees")
@RequiredArgsConstructor
public class EmployeeController {

    private final EmployeeService employeeService;

    @GetMapping // GET /api/employees
    public List<EmployeeDto> getAll() {
        return employeeService.getAll();
    }

    @GetMapping("/{id}") // GET /api/employees/1
    public EmployeeDto getById(@PathVariable Long id) {
        return employeeService.getById(id);
    }

    @PostMapping // POST /api/employees
    @ResponseStatus(HttpStatus.CREATED)
    public EmployeeDto create(@Valid @RequestBody EmployeeDto dto) {
        return employeeService.create(dto);
    }

    @PutMapping("/{id}") // PUT /api/employees/1
    public EmployeeDto update(@PathVariable Long id, @Valid @RequestBody EmployeeDto dto) {
        return employeeService.update(id, dto);
    }

    @DeleteMapping("/{id}") // DELETE /api/employees/1 (soft delete: the row stays, marked as deleted)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        employeeService.delete(id);
    }

    @Deprecated // use the soft delete above; this removes the row for good
    @DeleteMapping("/hard-delete/{id}") // DELETE /api/employees/hard-delete/1
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void hardDelete(@PathVariable Long id)
    {
        employeeService.hardDelete(id);
    }

}
