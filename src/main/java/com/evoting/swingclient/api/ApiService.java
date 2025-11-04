package com.evoting.swingclient.api;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializer;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Scanner;

public class ApiService {
    private static final Gson gson;
    private static final String BASE_URL = "http://localhost:8080/api";
    
    static {
        GsonBuilder gsonBuilder = new GsonBuilder();
        // Register LocalDateTime deserializer
        gsonBuilder.registerTypeAdapter(LocalDateTime.class, 
            (JsonDeserializer<LocalDateTime>) (json, type, context) -> 
                LocalDateTime.parse(json.getAsString(), DateTimeFormatter.ISO_LOCAL_DATE_TIME)
        );
        gson = gsonBuilder.create();
    }
    
    public static <T> T get(String endpoint, Class<T> responseType, String token) throws IOException {
        return get(endpoint, (java.lang.reflect.Type)responseType, token);
    }
    
    @SuppressWarnings("unchecked")
    public static <T> T get(String endpoint, java.lang.reflect.Type responseType, String token) throws IOException {
        URL url = new URL(BASE_URL + endpoint);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");
        conn.setRequestProperty("Accept", "application/json");
        
        if (token != null && !token.isEmpty()) {
            conn.setRequestProperty("Authorization", "Bearer " + token);
        }
        
        int responseCode = conn.getResponseCode();
        
        if (responseCode == 200) {
            String response = readResponse(conn.getInputStream());
            return (T) gson.fromJson(response, responseType);
        } else {
            String error = readResponse(conn.getErrorStream());
            throw new IOException("HTTP " + responseCode + ": " + error);
        }
    }
    
    public static <T> T post(String endpoint, Object requestBody, Class<T> responseType, String token) throws IOException {
        URL url = new URL(BASE_URL + endpoint);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Content-Type", "application/json");
        conn.setRequestProperty("Accept", "application/json");
        conn.setDoOutput(true);
        
        if (token != null && !token.isEmpty()) {
            conn.setRequestProperty("Authorization", "Bearer " + token);
        }
        
        if (requestBody != null) {
            String json = gson.toJson(requestBody);
            try (OutputStream os = conn.getOutputStream()) {
                byte[] input = json.getBytes(StandardCharsets.UTF_8);
                os.write(input, 0, input.length);
            }
        }
        
        int responseCode = conn.getResponseCode();
        
        if (responseCode >= 200 && responseCode < 300) {
            String response = readResponse(conn.getInputStream());
            return gson.fromJson(response, responseType);
        } else {
            String error = readResponse(conn.getErrorStream());
            throw new IOException("HTTP " + responseCode + ": " + error);
        }
    }
    
    public static <T> T postForm(String endpoint, String formData, Class<T> responseType, String token) throws IOException {
        URL url = new URL(BASE_URL + endpoint);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
        conn.setRequestProperty("Accept", "application/json");
        conn.setDoOutput(true);
        
        if (token != null && !token.isEmpty()) {
            conn.setRequestProperty("Authorization", "Bearer " + token);
        }
        
        if (formData != null) {
            try (OutputStream os = conn.getOutputStream()) {
                byte[] input = formData.getBytes(StandardCharsets.UTF_8);
                os.write(input, 0, input.length);
            }
        }
        
        int responseCode = conn.getResponseCode();
        
        if (responseCode >= 200 && responseCode < 300) {
            String response = readResponse(conn.getInputStream());
            return gson.fromJson(response, responseType);
        } else {
            String error = readResponse(conn.getErrorStream());
            throw new IOException("HTTP " + responseCode + ": " + error);
        }
    }
    
    public static <T> T put(String endpoint, Class<T> responseType, String token) throws IOException {
        URL url = new URL(BASE_URL + endpoint);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("PUT");
        conn.setRequestProperty("Accept", "application/json");
        
        if (token != null && !token.isEmpty()) {
            conn.setRequestProperty("Authorization", "Bearer " + token);
        }
        
        int responseCode = conn.getResponseCode();
        
        if (responseCode >= 200 && responseCode < 300) {
            String response = readResponse(conn.getInputStream());
            if (response == null || response.trim().isEmpty()) {
                return null;
            }
            return gson.fromJson(response, responseType);
        } else {
            String error = readResponse(conn.getErrorStream());
            throw new IOException("HTTP " + responseCode + ": " + error);
        }
    }
    
    private static String readResponse(InputStream inputStream) throws IOException {
        if (inputStream == null) {
            return "";
        }
        try (Scanner scanner = new Scanner(inputStream, StandardCharsets.UTF_8.name())) {
            scanner.useDelimiter("\\A");
            return scanner.hasNext() ? scanner.next() : "";
        }
    }
    
    public static String encode(String value) {
        try {
            return URLEncoder.encode(value, StandardCharsets.UTF_8.name());
        } catch (UnsupportedEncodingException e) {
            return value;
        }
    }
    
    public static Gson getGson() {
        return gson;
    }
}
