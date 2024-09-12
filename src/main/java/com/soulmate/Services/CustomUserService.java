package com.soulmate.Services;

import com.soulmate.Entites.Role;
import com.soulmate.Entites.UserInfo;
import com.soulmate.Repository.UserRepository;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;

import java.util.Optional;

@Service
public class CustomUserService implements UserDetailsService,UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    @Lazy
    public CustomUserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Optional<UserInfo>user=userRepository.findByEmail(email);
        if(user.isPresent()) {
//            var userObj = user.get();
//            return User.withUsername(userObj.getFirstname())
//                   .username(userObj.getEmail())
//                    .password(userObj.getPassword())
//                    .roles(userObj.getRole().name())
//                    .build();
            return user.get();
        }
        else{
            throw  new UsernameNotFoundException(STR."User not found\{email}");
        }
    }
    @Override
    public void createUser(UserInfo userInfo ){
        if(userRepository.findByEmail(userInfo.getEmail()).isPresent()){
            throw  new UsernameNotFoundException("User already exists");

        }
        userInfo.setPassword(passwordEncoder.encode(userInfo.getPassword()));
        userRepository.save(userInfo);
    }

    @Override
    public Optional<UserInfo> FindByUsername(String firstname) {
        return userRepository.findByFirstname(firstname);
    }

    @Override
    public Optional<UserInfo> FindByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public boolean loginUser(String email, String password){
        Optional<UserInfo> userInfo = userRepository.findByEmail(email);
        return userInfo.isPresent() && passwordEncoder.matches(password, userInfo.get().getPassword());
    }

}
