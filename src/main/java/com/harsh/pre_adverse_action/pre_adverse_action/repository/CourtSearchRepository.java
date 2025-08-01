package com.harsh.pre_adverse_action.pre_adverse_action.repository;

import com.harsh.pre_adverse_action.pre_adverse_action.entities.CourtSearch;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CourtSearchRepository extends JpaRepository<CourtSearch, Integer> {
    List<CourtSearch> findByCandidateId(Long candidateId);

    List<CourtSearch> findByCandidateIdAndStatus(Long candidateId, String status);
}
