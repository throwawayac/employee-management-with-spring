package com.tu.employeemanagement.dto;

import lombok.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class DepartmentDto {
    private Long id;

    @NotBlank(message = "Department name is required")
    @Size(min = 3, max = 30, message = "Department name must be between 3 and 30 characters")
    private String name;
}
