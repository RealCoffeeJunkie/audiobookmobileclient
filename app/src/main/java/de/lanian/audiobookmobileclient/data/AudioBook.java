package de.lanian.audiobookmobileclient.data;

import java.io.Serializable;

import lombok.Getter;
import lombok.Setter;

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

    public String getComparableBySeries() {
        StringBuilder builder = new StringBuilder(this.Series + " ");
        if(this.PlaceInSeries < 10)
            builder.append("00");
        else if(this.PlaceInSeries < 100)
            builder.append("0");
        builder.append(PlaceInSeries);
        return builder.toString();
    }
}
