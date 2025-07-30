package com.harsh.pre_adverse_action.pre_adverse_action.repository;

import com.harsh.pre_adverse_action.pre_adverse_action.entities.Candidate;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CandidateRepository extends JpaRepository<Candidate, Long> {
    List<Candidate> findCandidateByActive(Boolean active);
}
