package com.amin.battlearena.infra;

// Thrown when an action results in a character death
public class DeadCharacterException extends Exception {
    public DeadCharacterException() { super(); }
    public DeadCharacterException(String message) { super(message); }
    public DeadCharacterException(String message, Throwable cause) { super(message, cause); }
}
