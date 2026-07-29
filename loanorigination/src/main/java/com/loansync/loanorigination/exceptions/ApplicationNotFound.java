package com.loansync.loanorigination.exceptions;

public class ApplicationNotFound extends RuntimeException {
    public ApplicationNotFound(String message) {
        super(message);
    }

    public ApplicationNotFound() {super();}
}
