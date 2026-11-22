package dev.udaylabs.reviewbuddy.users.web;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

record RegistrationForm(
        @NotBlank(message = "Name is required") @Size(max = 100) String name,

        @NotBlank(message = "Email is required") @Email(message = "Invalid email address") String email,

        @NotBlank(message = "Password is required") @Size(min = 4, max = 64, message = "Password must be 4-64 characters") String password) {

    static RegistrationForm empty() {
        return new RegistrationForm("", "", "");
    }
}
