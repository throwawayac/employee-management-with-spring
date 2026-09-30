package com.tu.employeemanagement.dto;

import lombok.*;
import jakarta.validation.constraints.*;
import org.hibernate.validator.constraints.URL;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class EmployeeDto {
    private Long id;

    @NotBlank(message = "First name is required")
    @Size(min = 2, max = 15, message = "First name must be between 2 and 15 characters")
    private String firstName;

    @NotBlank(message = "Last name is required")
    @Size(min = 2, max = 20, message = "Last name must be between 2 and 20 characters")
    private String lastName;

    @Size(max = 255, message = "Profile picture URL is too long")
    @URL(regexp = "^https?://.*", message = "Profile picture must be a valid http or https URL")
    private String profilePicture;

    @Pattern(regexp = "^\\+[1-9]\\d{7,14}$", message = "Phone number must be a valid format")
    private String phoneNumber;

    @NotBlank(message = "Email is required")
    @Email(message = "Email should be valid")
    private String email;

    @NotNull(message = "Department is required")
    private Long departmentId;
}
