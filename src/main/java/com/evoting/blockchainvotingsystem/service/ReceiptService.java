package com.evoting.blockchainvotingsystem.service;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.SecureRandom;
import java.security.Signature;
import java.util.Base64;

import org.springframework.stereotype.Service;

@Service
public class ReceiptService {

    private final KeyPair keyPair;

    public ReceiptService() throws Exception {
        // Generate RSA key pair for signing receipts
        KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA");
        keyGen.initialize(2048, new SecureRandom());
        this.keyPair = keyGen.generateKeyPair();
    }

    public String generateVoteHash(Long userId, Long candidateId, Long electionId, String timestamp) {
        String data = userId + ":" + candidateId + ":" + electionId + ":" + timestamp;
        try {
            java.security.MessageDigest digest = java.security.MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(data.getBytes("UTF-8"));
            return Base64.getEncoder().encodeToString(hash);
        } catch (Exception e) {
            throw new RuntimeException("Failed to generate vote hash", e);
        }
    }

    public String generateReceipt(String voteHash, String transactionHash) {
        String receiptData = voteHash + ":" + transactionHash;
        try {
            Signature signature = Signature.getInstance("SHA256withRSA");
            signature.initSign(keyPair.getPrivate());
            signature.update(receiptData.getBytes("UTF-8"));
            byte[] digitalSignature = signature.sign();
            return Base64.getEncoder().encodeToString(digitalSignature);
        } catch (Exception e) {
            throw new RuntimeException("Failed to generate receipt", e);
        }
    }

    public boolean verifyReceipt(String voteHash, String transactionHash, String receipt) {
        String receiptData = voteHash + ":" + transactionHash;
        try {
            Signature signature = Signature.getInstance("SHA256withRSA");
            signature.initVerify(keyPair.getPublic());
            signature.update(receiptData.getBytes("UTF-8"));
            byte[] signatureBytes = Base64.getDecoder().decode(receipt);
            return signature.verify(signatureBytes);
        } catch (Exception e) {
            return false;
        }
    }

    public String getPublicKey() {
        return Base64.getEncoder().encodeToString(keyPair.getPublic().getEncoded());
    }
}
