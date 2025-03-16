package de.lanian.audiobookmobileclient.datatransfer;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import de.lanian.audiobookmobileclient.App;
import de.lanian.audiobookmobileclient.data.AudioBook;
import de.lanian.audiobookmobileclient.execptions.NoServerAccessException;

public class AudioBookListLoader {

    private final String server;

    public AudioBookListLoader(String server) {
        this.server = server;
    }


    public List<AudioBook> loadListFromFile() throws NoServerAccessException{
        String content = loadJsonFile();
        if(content == null || content.isEmpty())
            return loadListFromServer();
        return createBookListFromJson(content, true);
    }

    public List<AudioBook> loadListFromServer() throws NoServerAccessException {
        try {
            URL url = new URL("http://" + server + ":8080/audiobook/list/");
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setConnectTimeout(10000);

            if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                throw new NoServerAccessException();
            }

            String content = parseResponseMessage(connection.getInputStream());
            saveJsonFile(content);
            return createBookListFromJson(content, false);
        } catch (Exception e) {
            throw new NoServerAccessException();
        }
    }

    private String parseResponseMessage(InputStream stream) throws IOException {
        StringBuilder buffer = new StringBuilder();
        if (stream != null) {
            BufferedReader in = new BufferedReader(new InputStreamReader(stream));
            String inputLine;

            while ((inputLine = in.readLine()) != null) {
                buffer.append(inputLine);
            }
            in.close();
        }

        return buffer.toString();
    }

    private List<AudioBook> createBookListFromJson(String content, boolean locale) {
        Type type = new TypeToken<ArrayList<AudioBook>>(){}.getType();
        return new Gson().fromJson(content, type);
    }

    private void saveJsonFile(String content) {
        FileWriter writer = null;
        try {
            String jsonFile = App.getApp().getAppContext().getApplicationInfo().dataDir + "/audioBookList.json";
            File file = new File(jsonFile);
            if (!file.exists())
                file.createNewFile();
            writer = new FileWriter(file);
            writer.write(content);
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

    private String loadJsonFile() {
        String jsonFile = App.getApp().getAppContext().getApplicationInfo().dataDir + "/audioBookList.json";
        File file = new File(jsonFile);
        if(!file.exists())
            return null;

        StringBuilder result = new StringBuilder();
        try(FileInputStream reader = new FileInputStream(file)) {
            try {
                BufferedReader myReader = new BufferedReader(new InputStreamReader(
                        reader));
                String aDataRow = "";

                while ((aDataRow = myReader.readLine()) != null) {
                    result.append(aDataRow);
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
            return null;
        }

        if(result.length() > 0)
            return result.toString();
        else
            return null;
    }
}
