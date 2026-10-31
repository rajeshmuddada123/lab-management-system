package com.college.resource.dto.response;

public class JwtResponse {
    private String token;
    private String type = "Bearer";
    private Long userId;
    private String username;
    private String email;
    private String role;
    private String status;


    public JwtResponse() {
    }

    public JwtResponse(String token, String type, Long userId, String username, String email, String role, String status) {
        this.token = token;
        this.type = type;
        this.userId = userId;
        this.username = username;
        this.email = email;
        this.role = role;
        this.status = status;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public static  Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private String token;
        private String type;
        private Long userId;
        private String username;
        private String email;
        private String role;
        private String status;
        public Builder token(String token) {
            this.token = token;
            return this;
        }
        public Builder type(String type) {
            this.type = type;
            return this;
        }
        public Builder userId(Long userId) {
            this.userId = userId;
            return this;
        }
        public Builder username(String username) {
            this.username = username;
            return this;
        }
        public Builder email(String email) {
            this.email = email;
            return this;
        }
        public Builder role(String role) {
            this.role = role;
            return this;
        }
        public Builder status(String status) {
            this.status = status;
            return this;
        }
        public JwtResponse build() {
            return new JwtResponse(this.token, this.type, this.userId, this.username, this.email, this.role, this.status);
        }
    }

}
