package uk.sky.pm.dao;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import uk.sky.pm.domain.User;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Test
    void findByEmail_shouldReturnUser_whenEmailExists() {
        var user = new User();
        user.setEmail("test@example.com");
        var savedUser = userRepository.save(user);

        var result = userRepository.findByEmail("test@example.com");

        assertTrue(result.isPresent());
        assertEquals(savedUser.getId(), result.get().getId());
        assertEquals(savedUser.getEmail(), result.get().getEmail());
    }

    @Test
    void findByEmail_shouldReturnEmpty_whenEmailDoesNotExist() {
        var result = userRepository.findByEmail("missing@example.com");
        assertTrue(result.isEmpty());
    }
}
