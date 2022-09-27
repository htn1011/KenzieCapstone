package com.kenzie.appserver.repositories;

import com.kenzie.appserver.repositories.model.GameSummaryId;
import com.kenzie.appserver.repositories.model.GameSummaryRecord;

import org.socialsignin.spring.data.dynamodb.repository.EnableScan;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;

@EnableScan
public interface GameRepository extends CrudRepository<GameSummaryRecord, GameSummaryId> {
    // find all by date(GSI) ordered by results in ascending order
    List<GameSummaryRecord> findByDate(String date);

    // find all summaries for user - only primary key
    List<GameSummaryRecord> findByUserId(String userId);

    // find specific gamesummary for user
    Optional<GameSummaryRecord> findByGameSummaryId(GameSummaryId gameSummaryId);

    // Optional<GameSummaryRecord> findByUserIdAndSummarySortKey(String userId, String summarySortKey);

    // delete using the summaryIdClass
    void deleteById(GameSummaryId gameSummaryId);
}
