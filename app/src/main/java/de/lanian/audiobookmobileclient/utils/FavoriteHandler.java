package de.lanian.audiobookmobileclient.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashSet;

import de.lanian.audiobookmobileclient.App;

public class FavoriteHandler {

    private HashSet<String> favs = new HashSet<>();

    public FavoriteHandler() {
        this.loadFavoritesFile();
    }

    public boolean isAudioBookFavorite(String bookUid) {
        if(this.favs != null)
            return this.favs.contains(bookUid);
        return false;
    }

    public void addFavorite(String bookUid) {
        this.favs.add(bookUid);
        writeFavoritesToFile();
    }

    public void removeFavorite(String bookUid) {
        this.favs.remove(bookUid);
        writeFavoritesToFile();
    }

    public void loadFavoritesFile() {
        String jsonFile = App.getApp().getAppContext().getApplicationInfo().dataDir + "/favorites.txt";
        File file = new File(jsonFile);
        if(!file.exists())
            this.favs = new HashSet<String>();

        HashSet<String> favs = new HashSet<>();
        try(FileInputStream reader = new FileInputStream(file)) {
            try {
                BufferedReader myReader = new BufferedReader(new InputStreamReader(reader));
                String aDataRow = "";

                while ((aDataRow = myReader.readLine()) != null) {
                    favs.add(aDataRow);
                }
                myReader.close();
            } catch (Exception e) {
                System.out.println();
            } finally {
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (IOException ignored) {
                    }
                }
            }
        } catch (IOException ioe) {
            this.favs = new HashSet<String>();
        }

        this.favs = favs;
    }

    private void writeFavoritesToFile() {
        FileWriter writer = null;
        try {
            String jsonFile = App.getApp().getAppContext().getApplicationInfo().dataDir + "/favorites.txt";
            File file = new File(jsonFile);
            if (!file.exists())
                file.createNewFile();
            writer = new FileWriter(file);

            for(String fav : this.favs) {
                writer.write(fav + "\n");
            }
            writer.flush();
        } catch (IOException ioe) {
            System.out.println();
        } finally {
            if(writer != null)
                try {
                    writer.close();
                } catch (IOException ioe) {}
        }
    }
}
