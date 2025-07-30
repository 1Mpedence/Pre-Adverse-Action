package com.harsh.pre_adverse_action.pre_adverse_action.entities;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.Data;

import java.sql.Timestamp;

@Entity
@Table(name = "court_searches", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"candidate_id", "search"})
})
@Data
public class CourtSearch {

    @Id
    private Long id;

    @ManyToOne
    @JoinColumn(name = "candidate_id")
    @JsonBackReference
    private Candidate candidate;

    @Column(name = "candidate_id", insertable = false, updatable = false)
    private Long candidateId;

    @Column(length = 45)
    private String search;

    @Column(length = 45)
    private String status;

    @Column(name = "date")
    private Timestamp date;
}
