package com.turlygazhy.command.impl.collect_info.entity;

import com.turlygazhy.command.impl.collect_info.exception.TypeNotFoundException;

/**
 * Created by user on 4/23/17.
 */
public enum InfoType {
    TEXT,
    PHOTO,
    CONTACT,
    CONTACT_AS_TEXT;

    public static InfoType getType(String infoName) throws TypeNotFoundException {
        infoName = infoName.replace("/", "");
        for (InfoType type : values()) {
            if (type.toString().equals(infoName)) {
                return type;
            }
        }
        throw new TypeNotFoundException(infoName);
    }

    public static String getTextForProvidingTypes() {
        String result = "Please choose type:";
        for (InfoType type : values()) {
            result = result + "\n/" + type;
        }
        return result;
    }
}
