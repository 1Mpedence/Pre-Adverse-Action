package com.harsh.pre_adverse_action.pre_adverse_action.entities;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.*;

import java.sql.Timestamp;

@Entity
@Table(name = "report")
@Data
public class Report {

    @Id
    private Long id;

    @Column(length = 45)
    private String adjudication;

    @OneToOne
    @JoinColumn(name = "candidate_id", unique = true)
    @JsonBackReference
    private Candidate candidate;

    @Column(name = "candidate_id", insertable = false, updatable = false)
    private Long candidateId;

    @Column(length = 45)
    private String status;

    @Column(name = "package", length = 45)
    private String packageName;

    @Column(name = "created_at")
    private Timestamp createdAt;

    @Column(name = "updated_at")
    private Timestamp updatedAt;

    @Column(name = "completed_at")
    private Timestamp completedAt;

    @Column(name = "turn_around_time")
    private Timestamp turnAroundTime;

    @Column(name = "auto_send_duration ")
    private Long autoSendDuration;

    @Column(name = "last_notification_sent ")
    private Timestamp lastNotificationSent ;

    @Column(name = "available_charges", columnDefinition = "json")
    private String availableCharges;

    @Column(name = "selected_charges", columnDefinition = "json")
    private String selectedCharges;

}