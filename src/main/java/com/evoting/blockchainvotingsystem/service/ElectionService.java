package com.evoting.blockchainvotingsystem.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.evoting.blockchainvotingsystem.model.Candidate;
import com.evoting.blockchainvotingsystem.model.Election;
import com.evoting.blockchainvotingsystem.repository.CandidateRepository;
import com.evoting.blockchainvotingsystem.repository.ElectionRepository;

@Service
public class ElectionService {

    private final ElectionRepository electionRepository;
    private final CandidateRepository candidateRepository;

    public ElectionService(ElectionRepository electionRepository, CandidateRepository candidateRepository) {
        this.electionRepository = electionRepository;
        this.candidateRepository = candidateRepository;
    }

    public Election createElection(String title, String description) {
        Election election = new Election(title, description, false);
        return electionRepository.save(election);
    }

    public Candidate addCandidate(Long electionId, String name, String party, String description) {
        Optional<Election> electionOpt = electionRepository.findById(electionId);
        if (electionOpt.isEmpty()) {
            throw new RuntimeException("Election not found: " + electionId);
        }

        Election election = electionOpt.get();
        Candidate candidate = new Candidate(name, party, description, election);
        return candidateRepository.save(candidate);
    }

    public List<Election> getActiveElections() {
        return electionRepository.findByIsActiveTrue();
    }

    public List<Election> getAllElections() {
        return electionRepository.findAll();
    }

    public Optional<Election> getElectionById(Long id) {
        return electionRepository.findById(id);
    }

    public List<Candidate> getCandidatesByElectionId(Long electionId) {
        return candidateRepository.findByElection_Id(electionId);
    }

    @org.springframework.transaction.annotation.Transactional
    public void activateElection(Long electionId) {
        Optional<Election> electionOpt = electionRepository.findById(electionId);
        if (electionOpt.isEmpty()) {
            throw new RuntimeException("Election not found: " + electionId);
        }
        Election election = electionOpt.get();
        election.setActive(true);
        electionRepository.save(election);
        electionRepository.flush(); // Force immediate database write
    }

    @org.springframework.transaction.annotation.Transactional
    public void deactivateElection(Long electionId) {
        Optional<Election> electionOpt = electionRepository.findById(electionId);
        if (electionOpt.isEmpty()) {
            throw new RuntimeException("Election not found: " + electionId);
        }
        Election election = electionOpt.get();
        election.setActive(false);
        electionRepository.save(election);
        electionRepository.flush(); // Force immediate database write
    }
}
