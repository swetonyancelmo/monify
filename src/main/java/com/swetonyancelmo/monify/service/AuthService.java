package com.swetonyancelmo.monify.service;

import com.swetonyancelmo.monify.config.TokenConfig;
import com.swetonyancelmo.monify.domain.User;
import com.swetonyancelmo.monify.dto.request.CreateUserDto;
import com.swetonyancelmo.monify.dto.request.LoginRequestDto;
import com.swetonyancelmo.monify.dto.response.LoginResponseDto;
import com.swetonyancelmo.monify.dto.response.UserResponseDto;
import com.swetonyancelmo.monify.exception.EmailAlreadyExistsException;
import com.swetonyancelmo.monify.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final TokenConfig tokenConfig;

    @Transactional
    public UserResponseDto create(CreateUserDto dto) {
        try {
            User user = new User();
            user.setName(dto.name());
            user.setEmail(dto.email());
            user.setPassword(passwordEncoder.encode(dto.password()));

            User userSaved = userRepository.save(user);

            return new UserResponseDto(userSaved.getId(), userSaved.getName(), userSaved.getEmail());
        } catch (DataIntegrityViolationException e) {
            if (userRepository.existsByEmail(dto.email())) {
                throw new EmailAlreadyExistsException("Email já cadastrado");
            }
            throw e;
        }

    }

    public LoginResponseDto login(LoginRequestDto dto) {

        UsernamePasswordAuthenticationToken userAndPass = new UsernamePasswordAuthenticationToken(dto.email(), dto.password());
        Authentication authentication = authenticationManager.authenticate(userAndPass);

        User user = (User) authentication.getPrincipal();
        assert user != null;
        String token = tokenConfig.generateToken(user);

        return new LoginResponseDto(token);
    }

}
