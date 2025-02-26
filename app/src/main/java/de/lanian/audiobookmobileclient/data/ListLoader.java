package de.lanian.audiobookmobileclient.data;

import android.os.AsyncTask;
import android.widget.Toast;

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

import de.lanian.audiobookmobileclient.App;

public class ListLoader extends AsyncTask {

    @Override
    protected ArrayList<AudioBook> doInBackground(Object[] objects) {
        ArrayList<AudioBook> books = new ArrayList<>();

        try {
            URL url = new URL("http://" + objects[0] + ":8080/audiobook/list/");
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();

            if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                // request was not successful ...
            }

            String content = parseResponseMessage(connection.getInputStream());

            Type listType = new TypeToken<ArrayList<AudioBook>>(){}.getType();
            books = new Gson().fromJson(content, listType);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

        return books;
    }

    private String parseResponseMessage(InputStream stream) throws IOException {
        StringBuffer buffer = new StringBuffer();
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
