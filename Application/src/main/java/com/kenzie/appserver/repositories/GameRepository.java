package com.kenzie.appserver.repositories;

import com.kenzie.appserver.repositories.model.GameSummaryRecord;

import org.socialsignin.spring.data.dynamodb.repository.EnableScan;
import org.springframework.data.repository.CrudRepository;

@EnableScan
public interface GameRepository extends CrudRepository<GameSummaryRecord, String> {
}
