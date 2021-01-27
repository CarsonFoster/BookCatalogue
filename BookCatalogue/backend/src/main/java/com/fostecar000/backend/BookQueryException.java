package com.fostecar000.backend;

public class BookQueryException extends RuntimeException {
    public BookQueryException(String msg, Throwable exception) {
        super(msg, exception);
    }

    public BookQueryException(String msg) {
        super(msg);
    }
}