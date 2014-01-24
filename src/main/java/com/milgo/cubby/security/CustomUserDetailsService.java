package com.milgo.cubby.security;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.milgo.cubby.dao.UserDao;

@Service
@Transactional(readOnly=true)
public class CustomUserDetailsService implements UserDetailsService {

	@Autowired
	public UserDao userDao;
	
	@Override
	public UserDetails loadUserByUsername(String userName)
			throws UsernameNotFoundException {
		
		com.milgo.cubby.model.User domainUser = userDao.getUserByLogin(userName);
		
		boolean enabled = false;
		boolean accountNonExpired = true;
		boolean credentialsNonExpired = true;
		boolean accountNonLocked = true;
		
		if(domainUser.getEnabled() == 1)
			enabled = true;

		return new User(domainUser.getLogin(), 
				domainUser.getPassword(),
				enabled,
				accountNonExpired,
				credentialsNonExpired,
				accountNonLocked,
				getAuthorities(domainUser.getRole().getId()));
	}

    public Collection<SimpleGrantedAuthority> getAuthorities(Integer role) {
        List<SimpleGrantedAuthority> authList = getGrantedAuthorities(getRoles(role));
        return authList;
    }

    public List<String> getRoles(Integer role) {

        List<String> roles = new ArrayList<String>();

        if (role.intValue() == 1) {
            roles.add("ROLE_MODERATOR");
            roles.add("ROLE_ADMIN");
        }else if (role.intValue() == 2) {
            roles.add("ROLE_MODERATOR");
        }else if (role.intValue() == 3) {
            roles.add("ROLE_USER");
        }
        return roles;
    }

    public static List<SimpleGrantedAuthority> getGrantedAuthorities(List<String> roles) {
        List<SimpleGrantedAuthority> authorities = new ArrayList<SimpleGrantedAuthority>();

        for (String role : roles) {
            authorities.add(new SimpleGrantedAuthority(role));
        }
        return authorities;

    }


}
