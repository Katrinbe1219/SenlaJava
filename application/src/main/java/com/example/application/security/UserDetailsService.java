package com.example.application.security;

import com.example.application.hibernate.UserHibImpl;
import com.example.application.model.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

@Component
public class UserDetailsService implements org.springframework.security.core.userdetails.UserDetailsService {

    private UserHibImpl userHib;
    public UserDetailsService(UserHibImpl userHib) {
        this.userHib = userHib;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userHib.getUserByLogin(username);
    }
}
