package uk.sky.pm.service;

import lombok.AllArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import uk.sky.pm.dao.UserRepository;
import uk.sky.pm.mapper.UserMapper;

@Service
@AllArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Override
    public UserDetails loadUserByUsername(final String username) throws UsernameNotFoundException {
        var userDetail = userRepository.findByEmail(username)
            .orElseThrow(() -> new UsernameNotFoundException("Could not find user: " + username));
        return userMapper.map(userDetail);

    }

}
