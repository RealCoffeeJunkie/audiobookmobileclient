package de.lanian.audiobookmobileclient.utils;

public enum RequestCodes {
    CHOOSE_AUDIOBOOK_DIRECTORY(1);

    public final int value;

    private RequestCodes(int value) {
        this.value = value;
    }
}
