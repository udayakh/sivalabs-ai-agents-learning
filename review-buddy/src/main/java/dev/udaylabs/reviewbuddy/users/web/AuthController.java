package dev.udaylabs.reviewbuddy.users.web;

import dev.udaylabs.reviewbuddy.shared.BadRequestException;
import dev.udaylabs.reviewbuddy.users.domain.CreateUserCmd;
import dev.udaylabs.reviewbuddy.users.domain.UserService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
class AuthController {

    private final UserService userService;

    AuthController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/login")
    String loginForm() {
        return "users/login";
    }

    @GetMapping("/registration")
    String registrationForm(Model model) {
        model.addAttribute("form", RegistrationForm.empty());
        return "users/registration";
    }

    @PostMapping("/registration")
    String register(@Valid @ModelAttribute("form") RegistrationForm form, BindingResult result) {
        if (result.hasErrors()) {
            return "users/registration";
        }
        try {
            userService.register(new CreateUserCmd(form.name(), form.email(), form.password()));
        } catch (BadRequestException e) {
            result.rejectValue("email", "email.exists", e.getMessage());
            return "users/registration";
        }
        return "redirect:/login?registered";
    }
}
