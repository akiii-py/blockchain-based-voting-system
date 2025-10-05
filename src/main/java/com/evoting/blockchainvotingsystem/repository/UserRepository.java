package com.evoting.blockchainvotingsystem.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.evoting.blockchainvotingsystem.model.User;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByUsername(String username);

    Optional<User> findByEmail(String email);

    Optional<User> findByVoterId(String voterId);

    boolean existsByUsername(String username);

    boolean existsByEmail(String email);

    boolean existsByVoterId(String voterId);
}
