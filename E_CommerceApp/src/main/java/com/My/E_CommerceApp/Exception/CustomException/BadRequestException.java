package com.My.E_CommerceApp.Exception.CustomException;

public class BadRequestException extends RuntimeException{
    public BadRequestException(String message) {
        super("🙈 Bad Request: " + message +
                ". The request looked sus — check your JSON again!");
    }
}
