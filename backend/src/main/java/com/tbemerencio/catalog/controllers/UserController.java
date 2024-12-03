package com.tbemerencio.catalog.controllers;

import com.tbemerencio.catalog.controllers.dtos.UserDTO;
import com.tbemerencio.catalog.controllers.dtos.UserRequestDTO;
import com.tbemerencio.catalog.controllers.dtos.UserUpdateDTO;
import com.tbemerencio.catalog.services.UserRequestValid;
import com.tbemerencio.catalog.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.validation.Valid;
import java.net.URI;

@RestController
@RequestMapping(value = "api/users")
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping
    public ResponseEntity<Page<UserDTO>> findAll(Pageable pageable) {
        return ResponseEntity.ok(userService.findAllPaged(pageable));
    }

    @GetMapping(value = "/{id}")
    public ResponseEntity<UserDTO> findByID(@PathVariable Long id) {
        return ResponseEntity.ok(userService.findByID(id));
    }

    @PostMapping
    public ResponseEntity<UserDTO> create(@RequestBody @Valid UserRequestDTO userRequestDTO) {
        UserDTO userDTO = userService.create(userRequestDTO);
        URI uri = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}").buildAndExpand(userDTO.getId()).toUri();
        return ResponseEntity.created(uri).body(userDTO);
    }

    @PutMapping(value = "/update/{id}")
    public ResponseEntity<UserDTO> update(@PathVariable Long id,
                                             @RequestBody @Valid UserUpdateDTO userUpdateDTO) {
        return ResponseEntity.ok(userService.update(id, userUpdateDTO));
    }

    @DeleteMapping(value = "/delete/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        userService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
