package com.irsyad.springmysqlredis.model;

import lombok.Data;

import javax.persistence.*;

@Entity
@Table(name = "employees")
@Data
public class Employee extends BaseEntity{

    @Column(name="name", length = 256, nullable = false)
    private String name;
    private int age;
    private Double salary;

}
