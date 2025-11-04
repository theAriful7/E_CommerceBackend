package com.My.E_CommerceApp.Exception.CustomException;

public class BusinessValidationException extends RuntimeException {
    public BusinessValidationException(String message) {
        super(message);
    }
}