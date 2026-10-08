package com.jc.professional_challenge_api.exception;

//Thrown when a request is valid but breaks a business rule, e.g. removing the parent admin role (answered with 409).
public class BusinessRuleException extends RuntimeException {

    public BusinessRuleException(String message) {
        super(message);
    }
}
