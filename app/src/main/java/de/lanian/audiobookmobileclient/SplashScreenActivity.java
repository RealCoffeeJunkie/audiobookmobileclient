package de.lanian.audiobookmobileclient;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import org.apache.commons.validator.routines.InetAddressValidator;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import de.lanian.audiobookmobileclient.data.AudioBook;
import de.lanian.audiobookmobileclient.datatransfer.AudioBookListLoader;
import de.lanian.audiobookmobileclient.utils.Preferences;

@SuppressLint("CustomSplashScreen")
public class SplashScreenActivity extends AppCompatActivity {
    private final Executor executor = Executors.newCachedThreadPool();
    private final Handler handler = new Handler(Looper.getMainLooper());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initializeApp();
    }

    private void initializeApp() {
        App.createInstance(getApplicationContext());

        String directory = App.getApp().getAppPreference(Preferences.AUDIOBOOK_DIRECTORY);
        if(directory == null || directory.isEmpty())
            App.getApp().setAppPreference(Preferences.AUDIOBOOK_DIRECTORY, Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MUSIC).getPath());

        String server = App.getApp().getAppPreference(Preferences.SERVER_IP);
        if(server == null || server.isEmpty()) {
            handleServerIpInput();
        } else  {
            //boolean locale = App.getApp().getAudioBookList() != null && !App.getApp().getAudioBookList().isEmpty();
            loadBookList(true);
        }
    }

    /**
     * Handle Input of Server IP on first App Start
     */
    private void handleServerIpInput() {
        View view = LayoutInflater.from(getApplicationContext()).inflate(R.layout.server_input, null);
        EditText input = view.findViewById(R.id.ipInput);

        AlertDialog ipInput = new AlertDialog.Builder(this, R.style.MyDialogTheme).
                setView(view).setPositiveButton(android.R.string.ok, null).create();

        ipInput.setOnShowListener(dialog -> {
            Button button = ((AlertDialog) dialog).getButton(AlertDialog.BUTTON_POSITIVE);
            button.setOnClickListener(view1 -> {
                String ip = input.getText().toString();
                boolean valid = InetAddressValidator.getInstance().isValidInet4Address(ip);

                if(valid) {
                    App.getApp().setAppPreference(Preferences.SERVER_IP, ip);
                    Toast.makeText(getApplicationContext(), getString(R.string.serverSaved), Toast.LENGTH_SHORT).show();
                    loadBookList(false);
                    dialog.dismiss();
                } else {
                    Toast.makeText(getApplicationContext(), getString(R.string.invalidIP), Toast.LENGTH_SHORT).show();
                }
            });
        });
        ipInput.show();

        ipInput.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(getColor(R.color.black));
    }

    /**
     * Load Data from Server in Background
     */
    private void loadBookList(boolean local) {
        executor.execute(() -> {
            try {
                final List<AudioBook> books;
                if(local) {
                    books = new AudioBookListLoader(App.getApp().getAppPreference(Preferences.SERVER_IP)).loadListFromFile();
                } else {
                    books = new AudioBookListLoader(App.getApp().getAppPreference(Preferences.SERVER_IP)).loadListFromServer();
                }
                handler.post(() -> onTaskComplete(books));
            } catch (Exception e) {
                onError(e.getMessage());
            }
        });
    }

    /**
     * Go to App Main Screen when loading is done
     */
    public void onTaskComplete(List<AudioBook> result) {
        App.getApp().setAudioBookList(result);
        startActivity(new Intent(SplashScreenActivity.this, MainActivity.class));
    }

    public void onError(String message) {
        Toast.makeText(getApplicationContext(), "Daten konnten nicht geladen werden.", Toast.LENGTH_LONG).show();
        startActivity(new Intent(SplashScreenActivity.this, MainActivity.class));
    }
}
