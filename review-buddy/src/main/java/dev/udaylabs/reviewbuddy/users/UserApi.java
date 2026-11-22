package dev.udaylabs.reviewbuddy.users;

import dev.udaylabs.reviewbuddy.users.domain.UserService;
import dev.udaylabs.reviewbuddy.users.models.UserDto;
import java.util.Collection;
import java.util.Map;
import org.springframework.stereotype.Component;

/**
 * The ONLY entry point other modules may use to talk to the users module. UserService may have
 * many methods; we expose just what other modules need (narrow public surface area).
 */
@Component
public class UserApi {

    private final UserService userService;

    UserApi(UserService userService) {
        this.userService = userService;
    }

    public UserDto getByEmail(String email) {
        return userService.getByEmail(email);
    }

    public Map<Long, String> getUserNames(Collection<Long> userIds) {
        return userService.getUserNames(userIds);
    }
}
