package com.amin.battlearena.infra;

/**
 * Thrown when an action results in a character death (or a dead-character is referenced).
 * Checked exception to force handling where relevant.
 */
public class DeadCharacterException extends Exception {
    public DeadCharacterException() { super(); }
    public DeadCharacterException(String message) { super(message); }
    public DeadCharacterException(String message, Throwable cause) { super(message, cause); }
}
