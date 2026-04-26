package com.swetonyancelmo.monify.service;

import com.swetonyancelmo.monify.dto.request.UpdateUserDto;
import com.swetonyancelmo.monify.domain.User;
import com.swetonyancelmo.monify.dto.response.UserResponseDto;
import com.swetonyancelmo.monify.exception.ResourceNotFoundException;
import com.swetonyancelmo.monify.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

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
            user.setPassword(dto.password());
        }

        User userUpdated = userRepository.save(user);

        return new UserResponseDto(userUpdated.getId(), userUpdated.getName(), userUpdated.getEmail());
    }
}
