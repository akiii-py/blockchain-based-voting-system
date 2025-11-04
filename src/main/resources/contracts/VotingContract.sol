// SPDX-License-Identifier: MIT
pragma solidity ^0.8.0;

contract VotingContract {
    struct Vote {
        uint256 electionId;
        uint256 candidateId;
        bytes32 voteHash;
        uint256 timestamp;
        bool exists;
    }

    mapping(bytes32 => Vote) public votes;
    mapping(uint256 => uint256) public electionVoteCount;
    mapping(uint256 => mapping(uint256 => uint256)) public candidateVoteCount;

    address public admin;

    event VoteCast(bytes32 indexed voteHash, uint256 electionId, uint256 candidateId, uint256 timestamp);

    modifier onlyAdmin() {
        require(msg.sender == admin, "Only admin can perform this action");
        _;
    }

    constructor() {
        admin = msg.sender;
    }

    function castVote(uint256 _electionId, uint256 _candidateId, bytes32 _voteHash) external onlyAdmin {
        require(!votes[_voteHash].exists, "Vote already exists");

        votes[_voteHash] = Vote({
            electionId: _electionId,
            candidateId: _candidateId,
            voteHash: _voteHash,
            timestamp: block.timestamp,
            exists: true
        });

        electionVoteCount[_electionId]++;
        candidateVoteCount[_electionId][_candidateId]++;

        emit VoteCast(_voteHash, _electionId, _candidateId, block.timestamp);
    }

    function getVote(bytes32 _voteHash) external view returns (uint256, uint256, uint256, bool) {
        Vote memory vote = votes[_voteHash];
        return (vote.electionId, vote.candidateId, vote.timestamp, vote.exists);
    }

    function getElectionVoteCount(uint256 _electionId) external view returns (uint256) {
        return electionVoteCount[_electionId];
    }

    function getCandidateVoteCount(uint256 _electionId, uint256 _candidateId) external view returns (uint256) {
        return candidateVoteCount[_electionId][_candidateId];
    }

    function verifyVote(bytes32 _voteHash) external view returns (bool) {
        return votes[_voteHash].exists;
    }
}
