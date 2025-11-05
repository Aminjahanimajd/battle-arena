package com.amin.battlearena.infra;

// Thrown when an attempted action is invalid according to game rules
public class InvalidActionException extends Exception {
    public InvalidActionException() { super(); }
    public InvalidActionException(String message) { super(message); }
    public InvalidActionException(String message, Throwable cause) { super(message, cause); }
}
