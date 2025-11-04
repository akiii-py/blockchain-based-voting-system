package com.evoting.swingclient.api;

import com.evoting.swingclient.model.Candidate;
import com.evoting.swingclient.model.Election;
import com.google.gson.reflect.TypeToken;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.List;

public class ElectionService {
    
    private static final Type ELECTION_LIST_TYPE = new TypeToken<List<Election>>(){}.getType();
    private static final Type CANDIDATE_LIST_TYPE = new TypeToken<List<Candidate>>(){}.getType();
    
    @SuppressWarnings("unchecked")
    public static List<Election> getActiveElections(String token) throws IOException {
        return (List<Election>) ApiService.get("/elections/active", ELECTION_LIST_TYPE, token);
    }
    
    public static List<Election> getAllElections(String token) throws IOException {
        return (List<Election>) ApiService.get("/elections/all", ELECTION_LIST_TYPE, token);
    }
    
    public static Election getElectionById(Long id, String token) throws IOException {
        return ApiService.get("/elections/" + id, Election.class, token);
    }
    
    @SuppressWarnings("unchecked")
    public static List<Candidate> getCandidates(Long electionId, String token) throws IOException {
        return (List<Candidate>) ApiService.get("/elections/" + electionId + "/candidates", CANDIDATE_LIST_TYPE, token);
    }
    
    public static Election createElection(String title, String description, 
                                         String token) throws IOException {
        String formData = "title=" + ApiService.encode(title) +
                         "&description=" + ApiService.encode(description);
        return ApiService.postForm("/admin/elections", formData, Election.class, token);
    }
    
    public static Candidate addCandidate(Long electionId, String name, String party, 
                                        String description, String token) throws IOException {
        String formData = "name=" + ApiService.encode(name) +
                         "&party=" + ApiService.encode(party) +
                         "&description=" + ApiService.encode(description);
        
        return ApiService.postForm("/elections/" + electionId + "/candidates", formData, Candidate.class, token);
    }
    
    public static void activateElection(Long electionId, String token) throws IOException {
        ApiService.put("/admin/elections/" + electionId + "/activate", Void.class, token);
    }
    
    public static void deactivateElection(Long electionId, String token) throws IOException {
        ApiService.put("/admin/elections/" + electionId + "/deactivate", Void.class, token);
    }
}
