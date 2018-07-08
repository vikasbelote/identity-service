package com.suntan.identityservice.service;

import java.util.ArrayList;
import java.util.List;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import com.suntan.identityservice.model.AuthorityModel;
import com.suntan.identityservice.model.UserModel;
import com.suntan.identityservice.repository.UserRepository;

@Service("userDetailsServiceImpl")
@Slf4j
public class UserDetailsServiceImpl implements UserDetailsService  {
	
	@Autowired
	private PasswordEncoder bCryptPasswordEncoder;
	
	@Autowired
	private UserRepository userRepository;

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		UserModel userModel = userRepository.findByUserName(username);
		log.info("usermodel:"+userModel);
		Assert.notNull(userModel, "User Model is empty");
		return new User(userModel.getUserName(), userModel.getUserPassword(), this.getUserAuthority(userModel));
	}
	
	public void saveUserDetail(UserModel user) {
		String encodedPassword = bCryptPasswordEncoder.encode(user.getUserPassword());
		user.setUserPassword(encodedPassword);
		userRepository.save(user);
	}
	
	private List<GrantedAuthority> getUserAuthority(UserModel userModel) {
		List<GrantedAuthority> authorities = new ArrayList<GrantedAuthority>();
		for(AuthorityModel authority : userModel.getAuthorities()) {
			UserAuthority userAuthority = new UserAuthority();
			userAuthority.setRole(authority.getAuthortiName());
			authorities.add(userAuthority);
		}
		return authorities;
	}
}

class UserAuthority implements GrantedAuthority {
	
	private static final long serialVersionUID = 1L;
	
	private String role;

	public String getRole() {
		return role;
	}

	public void setRole(String role) {
		this.role = role;
	}

	@Override
	public String getAuthority() {
		return role;
	}
}
