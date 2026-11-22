package dev.udaylabs.reviewbuddy.users.domain;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** Loads users from the database for Spring Security form login (username = email). */
@Service
@Transactional(readOnly = true)
class SecurityUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    SecurityUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) {
        return userRepository
                .findByEmailIgnoreCase(username)
                .map(u -> new SecurityUser(
                        u.getEmail(), u.getPassword(), u.getName(), u.getRole().name()))
                .orElseThrow(() -> new UsernameNotFoundException("No user with email " + username));
    }
}
