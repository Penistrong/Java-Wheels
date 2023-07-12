package org.penistrong.wheel.idempotence.exception;

public class IdempotenceException extends RuntimeException{

        public IdempotenceException(String message){
            super(message);
        }

        public IdempotenceException(Throwable cause){
            super(cause);
        }

        public IdempotenceException(String message, Throwable cause){
            super(message, cause);
        }
}
