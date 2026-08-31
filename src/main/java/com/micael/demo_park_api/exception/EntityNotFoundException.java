package com.micael.demo_park_api.exception;

import lombok.Getter;

@Getter
public class EntityNotFoundException extends  RuntimeException {

    private final String messageKey;
    private final transient Object[] args;

    public EntityNotFoundException(String messageKey, Object... args){
        super(messageKey);
        this.messageKey = messageKey;
        this.args = args;
    }




}
