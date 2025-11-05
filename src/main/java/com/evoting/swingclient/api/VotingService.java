package com.evoting.swingclient.api;

import com.evoting.swingclient.model.Vote;
import com.google.gson.reflect.TypeToken;
import java.io.IOException;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

public class VotingService {
    
    private static final Type VOTE_LIST_TYPE = new TypeToken<List<Vote>>(){}.getType();
    
    public static Vote castVote(Long userId, Long candidateId, Long electionId, String token) throws IOException {
        if (userId == null) {
            throw new IOException("User ID is required to vote");
        }
        String formData = "userId=" + ApiService.encode(String.valueOf(userId)) +
                          "&candidateId=" + ApiService.encode(String.valueOf(candidateId)) +
                          "&electionId=" + ApiService.encode(String.valueOf(electionId));
        return ApiService.postForm("/voting/vote", formData, Vote.class, token);
    }
    
    @SuppressWarnings("unchecked")
    public static List<Vote> getVotesByElection(Long electionId, String token) throws IOException {
        return (List<Vote>) ApiService.get("/voting/election/" + electionId + "/votes", VOTE_LIST_TYPE, token);
    }
    
    public static boolean hasUserVoted(Long userId, Long electionId, String token) throws IOException {
        String endpoint = "/voting/user/" + userId + "/election/" + electionId + "/status";
        Boolean result = ApiService.get(endpoint, Boolean.class, token);
        return result != null && result;
    }
    
    public static Vote getVoteByUserAndElection(Long userId, Long electionId, String token) throws IOException {
        String endpoint = "/voting/user/" + userId + "/election/" + electionId;
        return ApiService.get(endpoint, Vote.class, token);
    }
}
