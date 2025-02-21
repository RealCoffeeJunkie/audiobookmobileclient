package de.lanian.audiobookmobileclient.data;

import java.io.Serializable;
import java.util.List;

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
    public String StoragePath;

    public AudioBook(String uid, String title, String series, String author, String genre, int yearOfPublication,
                     int duration, byte[] coverImageData, List<byte []> mp3s, String description) {
        this.Uid = uid;
        this.Title = title;
        this.Series = series;
        this.Author = author;
        this.Genre = genre;
        this.YearOfPublication = yearOfPublication;
        this.Duration = duration;
        this.CoverImageData = coverImageData;
        this.Description = description;
    }

    public AudioBook() {}

    /**
     * Getter & Setter
     */

    public String getUid() {
        return Uid;
    }

    public void setUid(String uid) {
        this.Uid = uid;
    }

    public String getTitle() {
        return Title;
    }

    public void setTitle(String title) {
        this.Title = title;
    }

    public String getSeries() {
        return Series;
    }

    public void setSeries(String series) {
        this.Series = series;
    }

    public String getAuthor() {
        return Author;
    }

    public void setAuthor(String author) {
        this.Author = author;
    }

    public String getSpeaker() {
        return Speaker;
    }

    public void setSpeaker(String speaker) {
        this.Speaker = speaker;
    }

    public String getGenre() {
        return Genre;
    }

    public void setGenre(String genre) {
        this.Genre = genre;
    }

    public int getYearOfPublication() {
        return YearOfPublication;
    }

    public void setYearOfPublication(int yearOfPublication) {
        this.YearOfPublication = yearOfPublication;
    }

    public int getDuration() {
        return Duration;
    }

    public void setDuration(int duration) {
        this.Duration = duration;
    }

    public byte[] getCoverImageData() {
        return CoverImageData;
    }

    public void setCoverImageData(byte[] coverImageData) {
        this.CoverImageData = coverImageData;
    }

    public String getDescription() {
        return Description;
    }

    public void setDescription(String description) {
        this.Description = description;
    }
}
