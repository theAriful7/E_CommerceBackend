package com.My.E_CommerceApp.Exception.CustomException;

public class ValidationException extends RuntimeException{
    public ValidationException(String fieldName, String details) {
        super("⚠️ Validation failed for field: " + fieldName + ". Hint: " + details +
                " — Please double-check before hitting send!");
    }
}
