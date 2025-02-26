package de.lanian.audiobookmobileclient.execptions;

public class DownloadFailedException extends Exception {
    public DownloadFailedException(String message) {
        super(message);
    }
}
