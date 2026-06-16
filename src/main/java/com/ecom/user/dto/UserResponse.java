package com.ecom.user.dto;

import com.ecom.user.model.User;
import java.time.LocalDateTime;

public class UserResponse {

    private String id;
    private String name;
    private String email;
    private String document;
    private String phone;
    private LocalDateTime createdAt;

    public static UserResponse fromEntity(User user) {
        UserResponse r = new UserResponse();
        r.id = user.getId();
        r.name = user.getName();
        r.email = user.getEmail();
        r.document = user.getDocument();
        r.phone = user.getPhone();
        r.createdAt = user.getCreatedAt();
        return r;
    }

    public String getId() { return id; }
    public String getName() { return name; }
    public String getEmail() { return email; }
    public String getDocument() { return document; }
    public String getPhone() { return phone; }
    public LocalDateTime getCreatedAt() { return createdAt; }
}
