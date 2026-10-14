package com.tu.employeemanagement.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.tu.employeemanagement.dto.EmployeeDto;
import com.tu.employeemanagement.model.Employee;

@Mapper
public interface EmployeeMapper {

    @Mapping(source = "department.id", target = "departmentId") // employee.getDepartment().getId() -> dto.setDepartmentId(...)
    EmployeeDto toDto(Employee employee);

    @Mapping(target = "department", ignore = true) // the DTO only has the id; the controller looks the department up
    @Mapping(target = "isDeleted", ignore = true) // only the delete endpoint may mark an employee as deleted
    Employee toEntity(EmployeeDto dto);

}
