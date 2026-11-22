package dev.udaylabs.reviewbuddy.users.domain;

import dev.udaylabs.reviewbuddy.shared.BadRequestException;
import dev.udaylabs.reviewbuddy.shared.ResourceNotFoundException;
import dev.udaylabs.reviewbuddy.users.models.UserDto;
import java.util.Collection;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public UserDto getByEmail(String email) {
        return userRepository
                .findByEmailIgnoreCase(email)
                .map(UserService::toDto)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + email));
    }

    public Map<Long, String> getUserNames(Collection<Long> userIds) {
        return userRepository.findAllById(userIds).stream()
                .collect(Collectors.toMap(UserEntity::getId, UserEntity::getName));
    }

    @Transactional
    public UserDto register(CreateUserCmd cmd) {
        if (userRepository.existsByEmailIgnoreCase(cmd.email())) {
            throw new BadRequestException("Email " + cmd.email() + " is already registered");
        }
        var user = new UserEntity(
                cmd.name().trim(),
                cmd.email().trim().toLowerCase(),
                passwordEncoder.encode(cmd.password()),
                Role.ROLE_USER);
        return toDto(userRepository.save(user));
    }

    private static UserDto toDto(UserEntity user) {
        return new UserDto(
                user.getId(), user.getName(), user.getEmail(), user.getRole().name());
    }
}
