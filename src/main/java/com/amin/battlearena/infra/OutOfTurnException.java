package com.amin.battlearena.infra;

public class OutOfTurnException extends Exception {
    public OutOfTurnException(String message) {
        super(message);
    }
}
