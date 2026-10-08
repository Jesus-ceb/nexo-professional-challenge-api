package com.jc.professional_challenge_api.exception;

//Thrown when a name or email is already taken by another record (answered with 409).
public class DuplicateResourceException extends RuntimeException {

    public DuplicateResourceException(String message) {
        super(message);
    }
}
