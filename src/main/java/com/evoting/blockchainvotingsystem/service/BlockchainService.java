package com.evoting.blockchainvotingsystem.service;

import java.math.BigInteger;
import java.util.concurrent.CompletableFuture;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.web3j.crypto.Credentials;
import org.web3j.protocol.Web3j;

@Service
public class BlockchainService {

    private final Web3j web3j;
    private final Credentials credentials;

    @Value("${web3j.contract-address:}")
    private String contractAddress;

    public BlockchainService(Web3j web3j, @Value("${web3j.private-key:}") String privateKey) {
        this.web3j = web3j;
        this.credentials = privateKey != null && !privateKey.isEmpty()
            ? Credentials.create(privateKey)
            : Credentials.create("0x" + "0".repeat(64)); // Default for testing
    }

    public CompletableFuture<String> castVoteOnBlockchain(
            Long electionId,
            Long candidateId,
            String voteHash) {

        // Note: This would use the generated Web3j wrapper class
        // For now, returning a simulated transaction hash
        // In production, this would interact with the deployed VotingContract

        return CompletableFuture.completedFuture("0x" + voteHash.substring(0, 64));
    }

    public CompletableFuture<BigInteger> getBlockNumber() {
        return web3j.ethBlockNumber().sendAsync()
                .thenApply(ethBlockNumber -> ethBlockNumber.getBlockNumber());
    }

    public boolean isConnected() {
        try {
            return web3j.ethBlockNumber().send().getBlockNumber() != null;
        } catch (Exception e) {
            return false;
        }
    }
}
