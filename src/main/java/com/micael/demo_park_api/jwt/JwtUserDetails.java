package com.micael.demo_park_api.jwt;



import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.User;

public class JwtUserDetails extends User {

    private com.micael.demo_park_api.domain.User user;

    public JwtUserDetails(com.micael.demo_park_api.domain.User user){
        super(user.getUsername(), user.getPassword(), AuthorityUtils.createAuthorityList(user.getRole().name()));
        this.user = user;
    }

    public Long getId(){
        return this.user.getIdUser();
    }

    public String getRole(){
        return user.getRole().name();
    }


}
