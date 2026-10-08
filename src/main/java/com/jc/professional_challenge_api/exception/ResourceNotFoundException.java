package com.jc.professional_challenge_api.exception;

//Thrown when a requested resource (product, category, user...) does not exist (answered with 404).
public class ResourceNotFoundException extends RuntimeException {

    public ResourceNotFoundException(String message) {
        super(message);
    }
}
