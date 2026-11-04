package com.tu.employeemanagement.dto;

import lombok.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import com.tu.employeemanagement.misc.EmployeeManagementConstants.ValidationMessages;
import com.tu.employeemanagement.misc.EmployeeManagementConstants.ValidationRules;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class DepartmentDto {
    private Long id;

    @NotBlank(message = ValidationMessages.DEPARTMENT_NAME_REQUIRED)
    @Size(min = ValidationRules.DEPARTMENT_NAME_MIN_LENGTH, max = ValidationRules.DEPARTMENT_NAME_MAX_LENGTH,
            message = ValidationMessages.DEPARTMENT_NAME_LENGTH)
    private String name;

    
    private java.util.Set<EmployeeDto> employees;

    private boolean isDeleted;
}
