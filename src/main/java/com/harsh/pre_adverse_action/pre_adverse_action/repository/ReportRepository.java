package com.harsh.pre_adverse_action.pre_adverse_action.repository;



import com.harsh.pre_adverse_action.pre_adverse_action.entities.Report;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ReportRepository extends JpaRepository<Report, Long> {
    Optional<Report> findByCandidateId(Long candidateId);
}