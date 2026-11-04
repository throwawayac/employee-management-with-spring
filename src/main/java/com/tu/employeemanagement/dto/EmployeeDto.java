package com.tu.employeemanagement.dto;

import lombok.*;
import jakarta.validation.constraints.*;
import org.hibernate.validator.constraints.URL;

import com.tu.employeemanagement.misc.EmployeeManagementConstants.ValidationMessages;
import com.tu.employeemanagement.misc.EmployeeManagementConstants.ValidationRules;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class EmployeeDto {
    private Long id;

    @NotBlank(message = ValidationMessages.FIRST_NAME_REQUIRED)
    @Size(min = ValidationRules.FIRST_NAME_MIN_LENGTH, max = ValidationRules.FIRST_NAME_MAX_LENGTH,
            message = ValidationMessages.FIRST_NAME_LENGTH)
    private String firstName;

    @NotBlank(message = ValidationMessages.LAST_NAME_REQUIRED)
    @Size(min = ValidationRules.LAST_NAME_MIN_LENGTH, max = ValidationRules.LAST_NAME_MAX_LENGTH,
            message = ValidationMessages.LAST_NAME_LENGTH)
    private String lastName;

    @Size(max = ValidationRules.PROFILE_PICTURE_MAX_LENGTH, message = ValidationMessages.PROFILE_PICTURE_TOO_LONG)
    @URL(regexp = ValidationRules.HTTP_URL_PATTERN, message = ValidationMessages.PROFILE_PICTURE_INVALID)
    private String profilePicture;

    @Pattern(regexp = ValidationRules.PHONE_NUMBER_PATTERN, message = ValidationMessages.PHONE_NUMBER_INVALID)
    private String phoneNumber;

    @NotBlank(message = ValidationMessages.EMAIL_REQUIRED)
    @Email(message = ValidationMessages.EMAIL_INVALID)
    private String email;

    @NotNull(message = ValidationMessages.DEPARTMENT_REQUIRED)
    private Long departmentId;

    private boolean isDeleted;
}
