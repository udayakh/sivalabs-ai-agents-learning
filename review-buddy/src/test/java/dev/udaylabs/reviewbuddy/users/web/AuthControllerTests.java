package dev.udaylabs.reviewbuddy.users.web;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestBuilders.formLogin;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.authenticated;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.unauthenticated;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import dev.udaylabs.reviewbuddy.AbstractIntegrationTest;
import org.junit.jupiter.api.Test;

class AuthControllerTests extends AbstractIntegrationTest {

    @Test
    void showsLoginPage() throws Exception {
        mockMvc.perform(get("/login")).andExpect(status().isOk()).andExpect(view().name("users/login"));
    }

    @Test
    void seededUserCanLogin() throws Exception {
        mockMvc.perform(formLogin("/login").user("user@gmail.com").password("user"))
                .andExpect(authenticated().withUsername("user@gmail.com"))
                .andExpect(redirectedUrl("/products"));
    }

    @Test
    void wrongPasswordIsRejected() throws Exception {
        mockMvc.perform(formLogin("/login").user("user@gmail.com").password("wrong"))
                .andExpect(unauthenticated())
                .andExpect(redirectedUrl("/login?error"));
    }

    @Test
    void registersNewUserWhoCanThenLogin() throws Exception {
        mockMvc.perform(get("/registration")).andExpect(status().isOk());

        mockMvc.perform(post("/registration")
                        .with(csrf())
                        .param("name", "New Person")
                        .param("email", "new.person@example.com")
                        .param("password", "secret123"))
                .andExpect(redirectedUrl("/login?registered"));

        mockMvc.perform(formLogin("/login").user("new.person@example.com").password("secret123"))
                .andExpect(authenticated());
    }

    @Test
    void rejectsDuplicateEmail() throws Exception {
        mockMvc.perform(post("/registration")
                        .with(csrf())
                        .param("name", "Copy Cat")
                        .param("email", "admin@gmail.com")
                        .param("password", "secret123"))
                .andExpect(status().isOk())
                .andExpect(view().name("users/registration"))
                .andExpect(model().attributeHasFieldErrors("form", "email"));
    }

    @Test
    void validatesRegistrationForm() throws Exception {
        mockMvc.perform(post("/registration")
                        .with(csrf())
                        .param("name", "")
                        .param("email", "not-an-email")
                        .param("password", "1"))
                .andExpect(status().isOk())
                .andExpect(model().attributeHasFieldErrors("form", "name", "email", "password"));
    }
}
