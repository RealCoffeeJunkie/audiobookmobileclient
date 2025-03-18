package de.lanian.audiobookmobileclient.datatransfer;

import com.google.gson.Gson;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import de.lanian.audiobookmobileclient.data.AudioBook;
import de.lanian.audiobookmobileclient.execptions.NoServerAccessException;

    public class AudioBookDetailLoader {

        private final String server;

        public AudioBookDetailLoader(String server) {
            this.server = server;
        }
        public AudioBook loadAudioBookDetailsFromServer(String id) throws NoServerAccessException {
        try {
            URL url = new URL("http://" + server + ":8080/audiobook_v2/book/" + id);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setConnectTimeout(10000);

            if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                throw new NoServerAccessException();
            }

            String content = parseResponseMessage(connection.getInputStream());
            return createBookFromJson(content);
        } catch (Exception e) {
            throw new NoServerAccessException();
        }
    }

    private AudioBook createBookFromJson(String content) {
        return new Gson().fromJson(content, AudioBook.class);
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
}
