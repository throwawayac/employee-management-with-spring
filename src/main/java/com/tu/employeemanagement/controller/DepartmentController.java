package com.tu.employeemanagement.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import com.tu.employeemanagement.dto.DepartmentDto;
import com.tu.employeemanagement.service.DepartmentService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/departments")
@RequiredArgsConstructor
public class DepartmentController {

    private final DepartmentService departmentService;

    @GetMapping // GET /api/departments
    public List<DepartmentDto> getAll() {
        return departmentService.getAll();
    }

    @GetMapping("/{id}") // GET /api/departments/1
    public DepartmentDto getById(@PathVariable Long id) {
        return departmentService.getById(id);
    }

    @GetMapping("/without-employees") // GET /api/departments/without-employees (the LEFT JOIN @Query)
    public List<DepartmentDto> getWithoutEmployees() {
        return departmentService.getWithoutEmployees();
    }

    @PostMapping // POST /api/departments
    @ResponseStatus(HttpStatus.CREATED)
    public DepartmentDto create(@Valid @RequestBody DepartmentDto dto) {
        return departmentService.create(dto);
    }

    @PutMapping("/{id}") // PUT /api/departments/1
    public DepartmentDto update(@PathVariable Long id, @Valid @RequestBody DepartmentDto dto) {
        return departmentService.update(id, dto);
    }

    @DeleteMapping("/{id}") // DELETE /api/departments/1 (soft delete: the row stays, marked as deleted)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        departmentService.delete(id);
    }

    @Deprecated // use the soft delete above; this removes the row for good
    @DeleteMapping("/hard-delete/{id}") // DELETE /api/departments/hard-delete/1
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void hardDelete(@PathVariable Long id) {
        departmentService.hardDelete(id);
    }

}
