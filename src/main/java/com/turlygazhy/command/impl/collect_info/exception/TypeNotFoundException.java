package com.turlygazhy.command.impl.collect_info.exception;

/**
 * Created by user on 4/23/17.
 */
public class TypeNotFoundException extends Exception {
    public TypeNotFoundException(String infoName) {
        super("Type with name '" + infoName + "' was not found");
    }
}
