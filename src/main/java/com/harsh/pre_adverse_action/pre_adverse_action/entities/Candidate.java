package com.harsh.pre_adverse_action.pre_adverse_action.entities;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;

import java.time.LocalDate;
import java.util.List;

import lombok.Data;
import java.sql.Timestamp;

@Entity
@Table(name = "candidate")
@Data
public class Candidate {

    @Id
    private Long id;

    @Column(length = 45)
    private String name;

    @Column(length = 45)
    private String location;

    @Column(length = 45, unique = true)
    private String email;

    private LocalDate dob;

    @Column(length = 45)
    private String phone;

    @Column(length = 45)
    private String zipcode;

    @Column(name = "social_security", length = 45, unique = true)
    private String socialSecurity;

    @Column(name = "drivers_license", length = 45, unique = true)
    private String driversLicense;

    @Column(name = "created_at")
    private Timestamp createdAt;

    @Column(name = "updated_at")
    private Timestamp updatedAt;

    private Boolean active;

    @OneToOne(mappedBy = "candidate")
    @JsonManagedReference
    private Report report;

    @OneToMany(mappedBy = "candidate")
    @JsonManagedReference
    private List<CourtSearch> courtSearches;
}
