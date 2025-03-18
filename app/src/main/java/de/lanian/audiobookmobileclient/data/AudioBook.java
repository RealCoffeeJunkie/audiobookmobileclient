package de.lanian.audiobookmobileclient.data;

import java.io.Serializable;

public class AudioBook implements Serializable {
    public String uid;
    public String title;
    public String series;
    public String author;
    public String speaker;
    public String genre;
    public int yearOfPublication;
    public int duration;
    public byte[] coverImageData;
    public String description;
    public int placeInSeries;
    public byte[] previewImageData;

    public String getComparableBySeries() {
        StringBuilder builder = new StringBuilder(this.series + " ");
        if(this.placeInSeries < 10)
            builder.append("00");
        else if(this.placeInSeries < 100)
            builder.append("0");
        builder.append(placeInSeries);
        return builder.toString();
    }
}
