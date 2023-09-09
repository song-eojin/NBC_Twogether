package com.example.twogether.common.security;

import com.example.twogether.user.entity.User;
import com.example.twogether.user.repository.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {
    private final UserRepository userRepository;

    public UserDetailsServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public UserDetailsImpl loadUserById(Long id) throws UsernameNotFoundException {
        User user = userRepository.findById(id)
            .orElseThrow(() -> new UsernameNotFoundException("User Not Found"));

        return new UserDetailsImpl(user);
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(email)
            .orElseThrow(() -> new UsernameNotFoundException("Not found " + email));

        return new UserDetailsImpl(user);
    }
}