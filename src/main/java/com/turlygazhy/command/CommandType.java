package com.turlygazhy.command;

import com.turlygazhy.exception.DataDoesNotExistException;
import com.turlygazhy.exception.NotImplementedMethodException;

/**
 * Created by user on 1/1/17.
 */
public enum CommandType {
    SHOW_INFO(1, true),
    CHANGE_INFO(2, false),
    ADD_TO_LIST(3, false),
    DELETE_FROM_LIST(4, false),
    SHOW_ALL_LIST(5, false),
    INFORM_ADMIN(6, false),
    REQUEST_CALL(7, false),
    PUT_TEXT_INSTEAD_BUTTON(8, false),
    COLLECT_INFO_COMMAND(9, false),
    SHOW_INFO_ABOUT_MEMBER(10, false),
    CHANGE_NISHA(11, false),
    CHANGE_NAVIKI(12, false),
    KEY_WORDS(13, false),
    SEARCH(14, false),
    RESERVATION(15, false),
    ADD_PLAN(16, false),
    SHARE_ACHIEVEMENTS(17, false),
    READING(18, false),
    ASK_ACCESS(19, false),
    SHOW_GOAL(20, false),
    ADD_GOAL(21, false),
    SHOW_ALL_GOALS(22, false),
    SHOW_THESIS(23, false),
    SHOW_ALL_THESIS(24, false),
    SHOW_CHART(25, false),
    CHANGE_GOAL(26, false),
    MY_BOTS(27, false),
    ADD_NEW_BOT(28, false),
    FEEDBACK(29, true),
    FEEDBACK_AKIMAT(30, false),
    SHOW_NOT_EXECUTED_TICKETS(31, false);

    private final int id;
    private final boolean implemented;

    CommandType(int id, boolean implemented) {
        this.id = id;
        this.implemented = implemented;
    }

    public int getId() {
        return id;
    }

    public static CommandType getType(long id) {
        for (CommandType type : values()) {
            if (type.getId() == id) {
                return type;
            }
        }
        throw new NotImplementedMethodException("There are no type for id: " + id);
    }

    public static String getCommandList() {
        String result = "";
        for (CommandType type : values()) {
            if (type.implemented) {
                result = result + "\n/" + type.toString();
            }
        }
        return result;
    }

    public static int getTypeId(String commandType) throws DataDoesNotExistException {
        commandType = commandType.replaceAll("/", "");
        for (CommandType type : values()) {
            if (type.toString().contains(commandType)) {
                return type.getId();
            }
        }
        throw new DataDoesNotExistException();
    }
}
