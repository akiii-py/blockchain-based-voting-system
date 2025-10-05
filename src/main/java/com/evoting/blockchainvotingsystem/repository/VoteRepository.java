package com.evoting.blockchainvotingsystem.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.evoting.blockchainvotingsystem.model.Vote;

@Repository
public interface VoteRepository extends JpaRepository<Vote, Long> {

    Optional<Vote> findByUserIdAndElectionId(Long userId, Long electionId);

    List<Vote> findByElectionId(Long electionId);

    boolean existsByUserIdAndElectionId(Long userId, Long electionId);

}
