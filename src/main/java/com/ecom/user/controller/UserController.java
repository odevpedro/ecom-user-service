package com.ecom.user.controller;

import com.ecom.user.dto.UserResponse;
import com.ecom.user.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/{id}")
    public UserResponse getById(@PathVariable String id) {
        return userService.findById(id);
    }
}
