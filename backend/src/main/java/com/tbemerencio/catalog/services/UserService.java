package com.tbemerencio.catalog.services;

import com.tbemerencio.catalog.controllers.dtos.UserDTO;
import com.tbemerencio.catalog.controllers.dtos.UserRequestDTO;
import com.tbemerencio.catalog.entities.Role;
import com.tbemerencio.catalog.entities.User;
import com.tbemerencio.catalog.repositories.RoleRepository;
import com.tbemerencio.catalog.repositories.UserRepository;
import com.tbemerencio.catalog.services.exceptions.DataBaseIntegrityException;
import com.tbemerencio.catalog.services.exceptions.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;

import javax.persistence.EntityNotFoundException;
import java.util.Optional;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @Transactional(readOnly = true)
    public Page<UserDTO> findAllPaged(Pageable pageable) {
        return userRepository.findAll(pageable).map(UserDTO::new);
    }

    @Transactional(readOnly = true)
    public UserDTO findByID(Long id) {
        Optional<User> entityOPT = userRepository.findById(id);
        User entity = entityOPT.orElseThrow(() ->
                new ResourceNotFoundException("ID not found [" + id + "]"));
        return new UserDTO(entity);
    }

    @Transactional
    public UserDTO create(UserRequestDTO userDTO) {
        User entity = new User();
        entity = userRepository.save(userDTOToUser(userDTO, entity));
        return new UserDTO(entity);
    }

    @Transactional
    public UserDTO update(Long id, UserRequestDTO userRequestDTO) {
        try {
            User userEntity = userRepository.getOne(id);
            return new UserDTO(userRepository.save(userDTOToUser(userRequestDTO, userEntity)));
        } catch (EntityNotFoundException e) {
            throw new ResourceNotFoundException("ID not found " + id);
        }
    }

    public void delete(Long id) {
        try {
            userRepository.deleteById(id);
        } catch (EmptyResultDataAccessException e) {
            throw new ResourceNotFoundException("ID not found " + id);
        } catch (DataIntegrityViolationException e) {
            throw new DataBaseIntegrityException("Deletion of related items not allowed");
        }
    }

    private User userDTOToUser(UserRequestDTO userRequestDTO, User entity) {
        entity.setFirstName(userRequestDTO.getFirstName());
        entity.setLastName(userRequestDTO.getLastName());
        entity.setEmail(userRequestDTO.getEmail());
        entity.setPassword(ObjectUtils.isEmpty(userRequestDTO.getPassword())
                ? entity.getPassword() : passwordEncoder.encode(userRequestDTO.getPassword()));

        if (!CollectionUtils.isEmpty(userRequestDTO.getRoles())) {
            entity.getRoles().clear();

            userRequestDTO.getRoles().forEach(roleDTO -> {
                Role role = roleRepository.findById(roleDTO.getId()).orElseThrow(
                        () -> new ResourceNotFoundException("Role doesen't exists -> " + roleDTO.getId()));
                entity.getRoles().add(role);
            });
        }
        return entity;
    }
}