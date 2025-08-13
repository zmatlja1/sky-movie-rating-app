package uk.sky.pm.util;

import org.springframework.security.core.context.SecurityContextHolder;
import uk.sky.pm.dto.UserDto;

public enum SecurityUser {

    LOGGED_USER;

    public UserDto get() {
        return (UserDto) SecurityContextHolder.getContext()
            .getAuthentication()
            .getPrincipal();
    }

}
