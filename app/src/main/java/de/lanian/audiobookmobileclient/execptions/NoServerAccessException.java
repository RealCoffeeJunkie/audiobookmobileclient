package de.lanian.audiobookmobileclient.execptions;

public class NoServerAccessException extends Exception {

    public NoServerAccessException() {
        super("Server couldn't be accessed.");
    }
}
