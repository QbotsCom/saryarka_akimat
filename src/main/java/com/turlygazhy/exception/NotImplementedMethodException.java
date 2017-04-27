package com.turlygazhy.exception;

import com.turlygazhy.command.CommandType;

/**
 * Created by user on 1/2/17.
 */
public class NotImplementedMethodException extends RuntimeException {

    public NotImplementedMethodException(String message) {
        super(message);
    }

    public NotImplementedMethodException(CommandType commandType) {
        super("Method not implemented for type: '" + commandType + "'");
    }
}
