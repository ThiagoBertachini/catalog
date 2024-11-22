package com.tbemerencio.catalog.controllers.exceptions;

import java.util.ArrayList;
import java.util.List;

public class ValidationException extends DefaultException {

    private List<MessageField> messageFields = new ArrayList<>();

    public List<MessageField> getMessageFields() {
        return messageFields;
    }

    public void setMessageFields(List<MessageField> messageFields) {
        this.messageFields = messageFields;
    }

    public void addError(String name, String message) {
        messageFields.add(new MessageField(name, message));
    }
}
