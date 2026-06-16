package com.ecom.user.controller;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @PostMapping
    public String create() {
        return "create - not implemented";
    }

    @GetMapping("/{id}")
    public String getById(@PathVariable String id) {
        return "getById - not implemented";
    }

    @PostMapping("/auth/login")
    public String login() {
        return "login - not implemented";
    }

    @PostMapping("/auth/register")
    public String register() {
        return "register - not implemented";
    }
}
