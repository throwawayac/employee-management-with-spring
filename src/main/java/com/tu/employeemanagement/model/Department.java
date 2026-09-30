package com.tu.employeemanagement.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.Set;

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

    @Column(name = "name", nullable = false, unique = true, length = 30)
    private String name;

    @OneToMany(mappedBy = "department")
    private Set<Employee> employees = new HashSet<>();

}
