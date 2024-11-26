package com.tbemerencio.catalog.services;

import com.tbemerencio.catalog.controllers.dtos.UserRequestDTO;
import com.tbemerencio.catalog.controllers.exceptions.MessageField;
import com.tbemerencio.catalog.entities.User;
import com.tbemerencio.catalog.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class UserRequestValidator implements ConstraintValidator<UserRequestValid, UserRequestDTO> {

    @Autowired
    private UserRepository userRepository;

    @Override
    public void initialize(UserRequestValid ann) {
    }

    @Override
    public boolean isValid(UserRequestDTO userRequestDTO, ConstraintValidatorContext constraintValidatorContext) {
        List<MessageField> messageField = new ArrayList<>();

        User user = userRepository.findByEmail(userRequestDTO.getEmail());

        if (Objects.nonNull(user)) messageField.add(new MessageField("email", "Email in use" ));

        messageField.forEach(error -> {
            constraintValidatorContext.disableDefaultConstraintViolation();
            constraintValidatorContext.buildConstraintViolationWithTemplate
                            (error.getFieldMessage()).addPropertyNode(error.getFieldName())
                    .addConstraintViolation();
        });

        return messageField.isEmpty();
    }
}