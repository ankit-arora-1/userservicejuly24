package com.scaler.userservice.security.services;

import com.scaler.userservice.models.User;
import com.scaler.userservice.repositories.UserRepository;
import com.scaler.userservice.security.models.CustomUserDetails;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private UserRepository userRepository;

    public CustomUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /*
    * We are simply returning user details object by fetching it from the DB
    * It is spring's responsibility to do the matching from the password entered from the browser
    * */
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Optional<User> userOptional = userRepository.findByEmail(email);
        if(userOptional.isEmpty()) {
            throw new UsernameNotFoundException("User does not exist");
        }

        User user = userOptional.get();

        return new CustomUserDetails(user);
    }
}
