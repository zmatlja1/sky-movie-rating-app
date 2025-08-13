package uk.sky.pm.service;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import uk.sky.pm.dao.UserRepository;
import uk.sky.pm.mapper.UserMapper;

@Service
public class UserServiceImpl implements UserService {

    private UserRepository userRepository;
    private UserMapper userMapper;

    public UserServiceImpl(UserRepository userRepository, UserMapper userMapper) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
    }

    @Override
    public UserDetails loadUserByUsername(final String username) throws UsernameNotFoundException {
        var userDetail = userRepository.findByEmail(username)
            .orElseThrow(() -> new UsernameNotFoundException("Could not find user: " + username));
        return userMapper.map(userDetail);

    }

}
