package com.mw.security.auth;

import com.mw.security.app.model.Member;
import com.mw.security.app.services.MemberService;
import com.mw.security.user.Role;
import com.mw.security.user.User;
import com.mw.security.user.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

@Component
public class RegisterRequestValidator {

    UserRepository userRepository;

    @Autowired
    public RegisterRequestValidator(
            MemberService memberService,
            UserRepository userRepository){
        this.userRepository=userRepository;
    }

    public void validate(RegisterRequest request){
        Optional<User> userFetched = userRepository.findByEmail(request.getEmail());
        if(userFetched.isPresent()) {
          throw new ResponseStatusException(HttpStatus.CONFLICT);
        }
        if(request.getRole()==null) {
            request.setRole(Role.USER);
        }

    }
}
