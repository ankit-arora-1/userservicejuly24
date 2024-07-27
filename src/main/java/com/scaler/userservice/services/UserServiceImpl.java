package com.scaler.userservice.services;

import com.scaler.userservice.models.Token;
import com.scaler.userservice.models.User;
import com.scaler.userservice.repositories.TokenRepository;
import com.scaler.userservice.repositories.UserRepository;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Calendar;
import java.util.Date;
import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {

    private UserRepository userRepository;
    private TokenRepository tokenRepository;
    private BCryptPasswordEncoder bCryptPasswordEncoder;


    @Autowired
    public UserServiceImpl(
            UserRepository userRepository,
            TokenRepository tokenRepository,
            BCryptPasswordEncoder bCryptPasswordEncoder
    ) {
        this.userRepository = userRepository;
        this.tokenRepository = tokenRepository;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
    }

    @Override
    public Token login(String email, String password) {
        Optional<User> userOptional = userRepository.findByEmail(email);
        if(userOptional.isEmpty()) {
            // throw an exception or redirect user to signup
        }

        User user = userOptional.get();
        if(!bCryptPasswordEncoder.matches(password, user.getHashedPassword())) {
            // throw an exception
            return null;
        }

        Token token = createToken(user);
        return tokenRepository.save(token);
    }

    @Override
    public User signUp(String name, String email, String password) {
        Optional<User> userOptional = userRepository.findByEmail(email);
        if(userOptional.isPresent()) {
            // throw an exception or redirect user to login
        }

        User user = new User();
        user.setName(name);
        user.setEmail(email);
        user.setHashedPassword(bCryptPasswordEncoder.encode(password));

        userRepository.save(user);
        return user;
    }

    @Override
    public User validateToken(String token) {
        Optional<Token> tokenOptional = tokenRepository
                .findByValueAndDeletedAndExpiryAtGreaterThan(token, false, new Date());

        if(tokenOptional.isEmpty()) {
            // throw an expection
            return null;
        }

        return tokenOptional.get().getUser();
    }

    @Override
    public void logout(String token) {
        // Home work
    }

    private Token createToken(User user) {
         Token token = new Token();
         token.setUser(user);
         token.setValue(RandomStringUtils.randomAlphanumeric(128)); // Read about UUIDs

         Date currentDate = new Date();
         Calendar calendar = Calendar.getInstance();
         calendar.setTime(currentDate);
         calendar.add(Calendar.DAY_OF_YEAR, 30);
         Date date30DaysFromToday = calendar.getTime();

         token.setCreatedAt(date30DaysFromToday);

         return token;
    }
}
