package com.micael.demo_park_api.jwt;

import com.micael.demo_park_api.domain.User;
import com.micael.demo_park_api.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;


@RequiredArgsConstructor
@Service

public class JwtUserDetailsService implements UserDetailsService {

    private final UserService userService;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userService.buscarPorUsername(username);
        return new JwtUserDetails(user);
    }

    public JwtToken getTokenAuthenticated(String username){

       User user = userService.buscarPorUsername(username);
       return JwtUtils.createToken(user.getIdUser(), username, user.getRole().getDisplayName());

    }
}
