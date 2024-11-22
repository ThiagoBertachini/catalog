package com.tbemerencio.catalog.controllers.exceptions;

public class MessageField {
    private String fieldName;
    private String fieldMessage;

    public MessageField() {
    }

    public MessageField(String fieldName, String fieldMessage) {
        this.fieldName = fieldName;
        this.fieldMessage = fieldMessage;
    }

    public String getFieldName() {
        return fieldName;
    }

    public String getFieldMessage() {
        return fieldMessage;
    }
}