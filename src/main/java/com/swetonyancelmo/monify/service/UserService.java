package com.swetonyancelmo.monify.service;

import com.swetonyancelmo.monify.domain.User;
import com.swetonyancelmo.monify.dto.request.UpdateUserDto;
import com.swetonyancelmo.monify.dto.response.UserResponseDto;
import com.swetonyancelmo.monify.exception.ResourceNotFoundException;
import com.swetonyancelmo.monify.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional(readOnly = true)
    public UserResponseDto findById(UUID id) {
        User user = userRepository.findById(id)
                    .orElseThrow(() -> new ResourceNotFoundException("Usuário com o ID: " + id + " não encontrado"));

        return new UserResponseDto(user.getId(), user.getName(), user.getEmail());
    }

    @Transactional
    public UserResponseDto update(UpdateUserDto dto, UUID id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário com o ID: " + id + " não encontrado"));

        if (dto.name() != null && !dto.name().isEmpty()) {
            user.setName(dto.name());
        }

        if (dto.password() != null && !dto.password().isEmpty()) {
            user.setPassword(passwordEncoder.encode(dto.password()));
        }

        User userUpdated = userRepository.save(user);

        return new UserResponseDto(userUpdated.getId(), userUpdated.getName(), userUpdated.getEmail());
    }
}
