package com.tbemerencio.catalog.services;

import com.tbemerencio.catalog.controllers.dtos.UserUpdateDTO;
import com.tbemerencio.catalog.controllers.exceptions.MessageField;
import com.tbemerencio.catalog.entities.User;
import com.tbemerencio.catalog.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.HandlerMapping;

import javax.servlet.http.HttpServletRequest;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class UserUpdateValidator implements ConstraintValidator<UserUpdateValid, UserUpdateDTO> {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private HttpServletRequest request;

    @Override
    public void initialize(UserUpdateValid ann) {
    }

    @Override
    public boolean isValid(UserUpdateDTO userUpdateDTO, ConstraintValidatorContext constraintValidatorContext) {
        List<MessageField> messageField = new ArrayList<>();

        @SuppressWarnings("unchecked")
        var uriVars = (Map<String, String>) request.getAttribute(HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE);

        long idValue = Long.parseLong(uriVars.get("id"));

        User user = userRepository.findByEmail(userUpdateDTO.getEmail());

        if (Objects.nonNull(user) && !user.getId().equals(idValue)) messageField.add(new MessageField("email", "Email in use"));

        messageField.forEach(error -> {
            constraintValidatorContext.disableDefaultConstraintViolation();
            constraintValidatorContext.buildConstraintViolationWithTemplate(error.getFieldMessage())
                    .addPropertyNode(error.getFieldName())
                    .addConstraintViolation();
        });

        return messageField.isEmpty();
    }
}