package com.vietnguyen.ums.entity;

import jakarta.persistence.*;

@Entity
@Table(schema = "ums", name = "user_roles")
public class UserRoleEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Short id;

    @Column(name = "code")
    private String code;

    @Column(name = "name")
    private String name;

    public Short getId() { return id; }
    public void setId(Short id) { this.id = id; }

    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
}
