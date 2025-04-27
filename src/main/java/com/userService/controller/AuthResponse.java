package com.userService.controller;

public class AuthResponse {

    private String token;
    private String message;

    public AuthResponse() {
    }

    public AuthResponse(String token, String message) {
        this.token = token;
        this.message = message;
    }

    // Getters y Setters

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    // Un constructor "Builder" manual
    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private String token;
        private String message;

        public Builder token(String token) {
            this.token = token;
            return this;
        }

        public Builder message(String message) {
            this.message = message;
            return this;
        }

        public AuthResponse build() {
            return new AuthResponse(token, message);
        }
    }
}
