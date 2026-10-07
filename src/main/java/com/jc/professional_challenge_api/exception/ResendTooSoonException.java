package com.jc.professional_challenge_api.exception;

//Thrown when the user asks to resend the confirmation email before the cooldown ends (answered with 429).
public class ResendTooSoonException extends RuntimeException {

    private final long secondsLeft;

    public ResendTooSoonException(long secondsLeft) {
        super("Espera " + secondsLeft + " segundos para reenviar el correo");
        this.secondsLeft = secondsLeft;
    }

    public long getSecondsLeft() {
        return secondsLeft;
    }
}
