package de.lanian.audiobookmobileclient.data;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import de.lanian.audiobookmobileclient.execptions.NoServerAccessException;

public class AudioBookListLoader {

    private final String server;

    public AudioBookListLoader(String server) {
        this.server = server;
    }
    public List<AudioBook> loadList() throws NoServerAccessException {
        try {
            URL url = new URL("http://" + server + ":8080/audiobook/list/");
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();

            if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                throw new NoServerAccessException();
            }

            String content = parseResponseMessage(connection.getInputStream());

            return createBookListFromJson(content);
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

    private List<AudioBook> createBookListFromJson(String content) {
        Type listType = new TypeToken<ArrayList<AudioBook>>(){}.getType();
        return new Gson().fromJson(content, listType);
    }
}
