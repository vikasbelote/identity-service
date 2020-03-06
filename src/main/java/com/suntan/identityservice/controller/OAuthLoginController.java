package com.suntan.identityservice.controller;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.servlet.ModelAndView;

import com.suntan.identityservice.model.AuthorityModel;
import com.suntan.identityservice.model.PersonModel;
import com.suntan.identityservice.model.UserModel;
import com.suntan.identityservice.service.UserDetailsServiceImpl;

@CrossOrigin(origins = "*")
@Controller
@SessionAttributes("authorizationRequest")
public class OAuthLoginController {
	
	@Autowired
	@Qualifier("userDetailsServiceImpl")
	private UserDetailsServiceImpl userDetailsService;
	
	@RequestMapping("login")
	public ModelAndView loginForm() {
		return new ModelAndView("login.html");
	}
	
	@RequestMapping(value = "register")
	public ModelAndView register() {
		return new ModelAndView("register.html");
	}
	
	@RequestMapping(value = "register", method = RequestMethod.POST)
	public String performLogin(HttpServletRequest request) {
		
		String firstName = request.getParameter("firstName");
		String lastName = request.getParameter("lastName");
		String userName = request.getParameter("userName");
		String password = request.getParameter("password");
		String confirmPassword = request.getParameter("confirm-password");
		
		if(password.equals(confirmPassword)) {
			UserModel user = new UserModel();
			user.setUserName(userName);
			user.setUserPassword(password);
			
			PersonModel person = new PersonModel();
			person.setFirstName(firstName);
			person.setLastName(lastName);
			person.setUserModel(user);
			user.setPersonModel(person);
			
			AuthorityModel authority = new AuthorityModel();
			authority.setAuthortiName("USER");
			authority.setUserModel(user);
			List<AuthorityModel> authorities = new ArrayList<AuthorityModel>();
			authorities.add(authority);
			user.setAuthorities(authorities);
			
			userDetailsService.saveUserDetail(user);
		}
		return "redirect:/login";
	}

    @RequestMapping(value = "logout", method = RequestMethod.POST)
    public String logoutDo(HttpServletRequest request, HttpServletResponse response){
        HttpSession session= request.getSession(false);
        SecurityContextHolder.clearContext();
        session= request.getSession(false);
        if(session != null) {
            session.invalidate();
        }
        for(Cookie cookie : request.getCookies()) {
            cookie.setMaxAge(0);
        }

        return "logout";
    }
}
