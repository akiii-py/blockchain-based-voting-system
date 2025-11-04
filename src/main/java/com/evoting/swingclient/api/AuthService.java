package com.evoting.swingclient.api;

import com.evoting.swingclient.model.User;
import com.google.gson.JsonObject;
import java.io.IOException;
import java.util.Map;

public class AuthService {
    
    public static class LoginResponse {
        public String token;
        public User user;
    }
    
    public static LoginResponse login(String username, String password) throws IOException {
        JsonObject requestBody = new JsonObject();
        requestBody.addProperty("username", username);
        requestBody.addProperty("password", password);
        
        return ApiService.post("/auth/login", requestBody, LoginResponse.class, null);
    }
    
    public static User register(String username, String password, String email, 
                               String fullName, String voterId) throws IOException {
        JsonObject requestBody = new JsonObject();
        requestBody.addProperty("username", username);
        requestBody.addProperty("password", password);
        requestBody.addProperty("email", email);
        requestBody.addProperty("fullName", fullName);
        requestBody.addProperty("voterId", voterId);
        
        return ApiService.post("/auth/register", requestBody, User.class, null);
    }
}
