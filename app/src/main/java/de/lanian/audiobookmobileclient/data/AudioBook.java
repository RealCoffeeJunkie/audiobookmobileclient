package de.lanian.audiobookmobileclient.data;

import java.io.Serializable;

public class AudioBook implements Serializable {
    public String Uid;
    public String Title;
    public String Series;
    public String Author;
    public String Speaker;
    public String Genre;
    public int YearOfPublication;
    public int Duration;
    public byte[] CoverImageData;
    public String Description;
    public int PlaceInSeries;

    public int getPlaceInSeries() {
        return this.PlaceInSeries;
    }
}
