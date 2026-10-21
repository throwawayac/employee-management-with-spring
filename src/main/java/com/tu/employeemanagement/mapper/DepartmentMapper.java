package com.tu.employeemanagement.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.tu.employeemanagement.dto.DepartmentDto;
import com.tu.employeemanagement.model.Department;

@Mapper(uses = EmployeeMapper.class) // lets this mapper call EmployeeMapper's methods for department.employees
public interface DepartmentMapper {

    @Mapping(target = "employees", qualifiedByName = "activeEmployees") // EmployeeMapper.toActiveDtos: soft-deleted employees are left out
    DepartmentDto toDto(Department department);

    @Mapping(target = "employees", ignore = true) // employees are linked from the Employee side
    @Mapping(target = "isDeleted", ignore = true) // only the delete endpoint may mark a department as deleted
    Department toEntity(DepartmentDto dto);

}
