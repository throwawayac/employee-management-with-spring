package com.tu.employeemanagement.model;

import jakarta.persistence.*;
import lombok.*;

import com.tu.employeemanagement.misc.EmployeeManagementConstants.ValidationRules;

@Builder
@Entity
@Table(name = "employees")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class Employee {
    @Id
    @Column(name = "employee_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "first_name", nullable = false, length = ValidationRules.FIRST_NAME_MAX_LENGTH)
    private String firstName;

    @Column(name = "last_name", nullable = false, length = ValidationRules.LAST_NAME_MAX_LENGTH)
    private String lastName;

    @Column(name = "profile_picture", nullable = true, length = ValidationRules.PROFILE_PICTURE_MAX_LENGTH)
    private String profilePicture;

    @Column(name = "phone_number", nullable = true, unique = true, length = ValidationRules.PHONE_NUMBER_MAX_LENGTH)
    private String phoneNumber;

    @Column(name = "email", nullable = false, unique = true)
    private String email;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "department_id", nullable = false)
    private Department department;

    @Builder.Default
    @Column(name = "is_deleted", nullable = false)
    private boolean isDeleted = false;
}
