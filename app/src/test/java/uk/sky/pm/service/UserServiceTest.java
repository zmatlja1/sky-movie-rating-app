package uk.sky.pm.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import uk.sky.pm.dao.UserRepository;
import uk.sky.pm.domain.User;
import uk.sky.pm.mapper.UserMapperImpl;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest(classes = {UserServiceImpl.class, UserMapperImpl.class})
class UserServiceImplTest {

    @MockitoBean
    private UserRepository userRepository;

    @Autowired
    private UserMapperImpl userMapper;

    @Autowired
    private UserService userService;

    @Test
    void loadUserByUsername_shouldReturnUserDetails_whenUserExists() {
        var user = new User();
        user.setEmail("test@example.com");

        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));

        var result = userService.loadUserByUsername(user.getEmail());

        assertNotNull(result);
        verify(userRepository).findByEmail(user.getEmail());
    }

    @Test
    void loadUserByUsername_shouldThrowException_whenUserNotFound() {
        var email = "missing@example.com";
        when(userRepository.findByEmail(email)).thenReturn(Optional.empty());

        var exception = assertThrows(
            UsernameNotFoundException.class,
            () -> userService.loadUserByUsername(email)
        );

        assertEquals("Could not find user: " + email, exception.getMessage());
        verify(userRepository).findByEmail(email);
    }
}
