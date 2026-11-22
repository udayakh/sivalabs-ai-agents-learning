package dev.udaylabs.reviewbuddy.users.domain;

public record CreateUserCmd(String name, String email, String password) {}
