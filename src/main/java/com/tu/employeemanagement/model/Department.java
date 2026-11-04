package com.tu.employeemanagement.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.Set;

import com.tu.employeemanagement.misc.EmployeeManagementConstants.ValidationRules;

@Builder
@Entity
@Table(name = "departments")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class Department {
    @Id
    @Column(name = "department_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", nullable = false, unique = true, length = ValidationRules.DEPARTMENT_NAME_MAX_LENGTH)
    private String name;

    @Builder.Default
    @OneToMany(mappedBy = "department", orphanRemoval = true)
    private Set<Employee> employees = new HashSet<>();

    @Builder.Default
    @Column(name = "is_deleted", nullable = false)
    private boolean isDeleted = false;

}
