package com.ingenico.demoacclib.feature.demo_pay;

public class Constant {

    private static String keyPrefix = "0043";
    private static String keyDukptData = "08";
    private static String keyDukptPin = "04";

    public static String getKeyPrefix() {
        return keyPrefix;
    }

    public static void setKeyPrefix(String keyPrefix) {
        Constant.keyPrefix = keyPrefix;
    }

    public static String getKeyDukptData() {
        return keyDukptData;
    }

    public static void setKeyDukptData(String keyDukptData) {
        Constant.keyDukptData = keyDukptData;
    }

    public static String getKeyDukptPin() {
        return keyDukptPin;
    }

    public static void setKeyDukptPin(String keyDukptPin) {
        Constant.keyDukptPin = keyDukptPin;
    }
}
