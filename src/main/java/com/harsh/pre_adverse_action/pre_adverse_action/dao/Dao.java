package com.harsh.pre_adverse_action.pre_adverse_action.dao;

import com.harsh.pre_adverse_action.pre_adverse_action.dtos.CandidateReportSummaryDTO;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class Dao {
    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    Dao(NamedParameterJdbcTemplate  namedParameterJdbcTemplate) {
        this.namedParameterJdbcTemplate  = namedParameterJdbcTemplate ;
    }

    public List<CandidateReportSummaryDTO> findAllCandidateReportSummaries(String search, String adjudication, String status) {
        String sql = """
                SELECT
                    c.name, c.location, c.created_at, r.adjudication, r.status
                FROM 
                    candidate c
                JOIN report r 
                    ON c.id = r.candidate_id
                where 
                    c.active = true
                    and case when :search is not null then c.name like :search else true end
                    and case when :adjudication is not null then r.adjudication = :adjudication else true end
                    and case when :status is not null then r.status = :status else true end
            """;

        MapSqlParameterSource parameter = new MapSqlParameterSource();
        parameter.addValue("search", Optional.ofNullable(search).map(s -> "%" + s + "%").orElse(null));
        parameter.addValue("adjudication", adjudication);
        parameter.addValue("status", status);
        return namedParameterJdbcTemplate.query(sql, parameter, new BeanPropertyRowMapper<>(CandidateReportSummaryDTO.class));
    }

    public void updateCourtSearchesStatus(List<String> names) {
        String sql = "UPDATE court_searches SET status = CASE WHEN search IN (:names) THEN 'CONSIDER' ELSE 'CLEAR' END";

        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("names", names);

        namedParameterJdbcTemplate.update(sql, params);
    }
}