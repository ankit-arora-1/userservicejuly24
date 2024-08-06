package com.scaler.userservice.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.scaler.userservice.dtos.SendEmailDto;
import com.scaler.userservice.models.Token;
import com.scaler.userservice.models.User;
import com.scaler.userservice.repositories.TokenRepository;
import com.scaler.userservice.repositories.UserRepository;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
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

    private KafkaTemplate<String, String> kafkaTemplate;
    private ObjectMapper objectMapper;


    @Autowired
    public UserServiceImpl(
            UserRepository userRepository,
            TokenRepository tokenRepository,
            BCryptPasswordEncoder bCryptPasswordEncoder,
            KafkaTemplate kafkaTemplate
    ) {
        this.userRepository = userRepository;
        this.tokenRepository = tokenRepository;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
        this.kafkaTemplate = kafkaTemplate;
        this.objectMapper = new ObjectMapper();
    }

    @Override
    public Token login(String email, String password) {
        Optional<User> userOptional = userRepository.findByEmail(email);
        if(userOptional.isEmpty()) {
            // throw an exception or redirect user to signup
            return null;
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
            return null;
        }

        User user = new User();
        user.setName(name);
        user.setEmail(email);
        user.setHashedPassword(bCryptPasswordEncoder.encode(password));
        userRepository.save(user);

        SendEmailDto sendEmailDto = new SendEmailDto();
        sendEmailDto.setFrom("arora.ankit7@gmail.com");
        sendEmailDto.setTo(user.getEmail());
        sendEmailDto.setSubject("Welcome");
        sendEmailDto.setBody("Welcome welcome welcome");

        String sendEmailDtoString = null;
        try {
            sendEmailDtoString = objectMapper.writeValueAsString(sendEmailDto);
        } catch (Exception ex) {
            System.out.println("Something went wrong");
        }

        kafkaTemplate.send("sendEmail1", sendEmailDtoString);

        return user;
    }

    @Override
    public User validateToken(String token) {
        System.out.println("calling validate token");
        Optional<Token> tokenOptional = tokenRepository
                .findByValueAndDeletedAndExpiryAtGreaterThan(token, false, new Date());

        if(tokenOptional.isEmpty()) {
            // throw an expection
            return null;
        }

        return tokenOptional.get().getUser();
    }

    @Override
    public void logout(String tokenValue) {
        Optional<Token> optionalToken = tokenRepository.findByValueAndDeleted(tokenValue, false);

        if (optionalToken.isEmpty()) {
            //Throw some exception
        }

        Token token = optionalToken.get();

        token.setDeleted(true);
        tokenRepository.save(token);
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

         token.setExpiryAt(date30DaysFromToday);
         token.setDeleted(false);

         return token;
    }
}
