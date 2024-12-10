package com.tbemerencio.catalog.services;

import com.tbemerencio.catalog.controllers.dtos.UserDTO;
import com.tbemerencio.catalog.controllers.dtos.UserRequestDTO;
import com.tbemerencio.catalog.controllers.dtos.UserUpdateDTO;
import com.tbemerencio.catalog.entities.Role;
import com.tbemerencio.catalog.entities.User;
import com.tbemerencio.catalog.repositories.RoleRepository;
import com.tbemerencio.catalog.repositories.UserRepository;
import com.tbemerencio.catalog.services.exceptions.DataBaseIntegrityException;
import com.tbemerencio.catalog.services.exceptions.ResourceNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;

import javax.persistence.EntityNotFoundException;
import java.util.Objects;
import java.util.Optional;

@Service
public class UserService implements UserDetailsService {

    private static Logger logger = LoggerFactory.getLogger(UserService.class);

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
        entity = userRepository.save(userRequestDTOToUser(userDTO, entity));
        return new UserDTO(entity);
    }

    @Transactional
    public UserDTO update(Long id, UserUpdateDTO userUpdateDTO) {
        try {
            User userEntity = userRepository.getOne(id);
            return new UserDTO(userRepository.save(userUpdateDTOToUser(userUpdateDTO, userEntity)));
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

    private User userRequestDTOToUser(UserRequestDTO userRequestDTO, User entity) {
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

    private User userUpdateDTOToUser(UserUpdateDTO userUpdateDTO, User entity) {
        entity.setFirstName(userUpdateDTO.getFirstName());
        entity.setLastName(userUpdateDTO.getLastName());
        entity.setEmail(userUpdateDTO.getEmail());

        if (!CollectionUtils.isEmpty(userUpdateDTO.getRoles())) {
            entity.getRoles().clear();

            userUpdateDTO.getRoles().forEach(roleDTO -> {
                Role role = roleRepository.findById(roleDTO.getId()).orElseThrow(
                        () -> new ResourceNotFoundException("Role doesen't exists -> " + roleDTO.getId()));
                entity.getRoles().add(role);
            });
        }
        return entity;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(username);;
        if (Objects.isNull(user)) {
            logger.error("Authentication error, user not found");
            throw new UsernameNotFoundException("User not found");
        } logger.info("Success, user found");
        return user;
    }
}