package com.evoting.blockchainvotingsystem.service;

import java.time.LocalDateTime;
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

    public Election createElection(String title, String description, LocalDateTime startTime, LocalDateTime endTime) {
        Election election = new Election(title, description, startTime, endTime, false);
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

    public Optional<Election> getElectionById(Long id) {
        return electionRepository.findById(id);
    }

    public List<Candidate> getCandidatesByElectionId(Long electionId) {
        return candidateRepository.findByElection_Id(electionId);
    }

    public void activateElection(Long electionId) {
        Optional<Election> electionOpt = electionRepository.findById(electionId);
        if (electionOpt.isPresent()) {
            Election election = electionOpt.get();
            election.setActive(true);
            electionRepository.save(election);
        }
    }

    public void deactivateElection(Long electionId) {
        Optional<Election> electionOpt = electionRepository.findById(electionId);
        if (electionOpt.isPresent()) {
            Election election = electionOpt.get();
            election.setActive(false);
            electionRepository.save(election);
        }
    }
}
