package com.evoting.blockchainvotingsystem.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.evoting.blockchainvotingsystem.model.Election;

@Repository
public interface ElectionRepository extends JpaRepository<Election, Long> {

    List<Election> findByIsActiveTrue();

}
