package org.example.Security;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;

public class JwtAuthenticationToken extends UsernamePasswordAuthenticationToken {

    public JwtAuthenticationToken(UserDetails principal, Object credentials, Collection authorities) {
        super(principal, credentials, authorities);
    }

    public JwtAuthenticationToken(String token) {
        super(token, null);
    }
}

