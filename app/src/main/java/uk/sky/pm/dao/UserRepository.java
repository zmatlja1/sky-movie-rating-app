package uk.sky.pm.dao;

import org.springframework.data.repository.CrudRepository;
import uk.sky.pm.domain.User;

import java.util.Optional;

public interface UserRepository extends CrudRepository<User, Long> {

    Optional<User> findByEmail(String email);

}
