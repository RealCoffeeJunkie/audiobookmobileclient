package de.lanian.audiobookmobileclient;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import de.lanian.audiobookmobileclient.databinding.ActivityMainBinding;
import de.lanian.audiobookmobileclient.utils.PathHandler;
import de.lanian.audiobookmobileclient.utils.Preferences;
import de.lanian.audiobookmobileclient.utils.RequestCodes;

import android.os.Environment;
import android.provider.DocumentsContract;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;
import org.apache.commons.validator.routines.InetAddressValidator;

import java.net.URLEncoder;

public class MainActivity extends AppCompatActivity {

    public static final String SERVER_IP = "ServerIp";
    private AppBarConfiguration appBarConfiguration;
    private ActivityMainBinding binding;

    /****************
     * Lifecycle
     ****************/

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initializeApp();

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setSupportActionBar(binding.toolbar);
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        appBarConfiguration = new AppBarConfiguration.Builder(navController.getGraph()).build();
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_server) {
            handleServerIpInput();
            return true;
        } else if(id == R.id.action_directory) {
            Intent i = new Intent(Intent.ACTION_OPEN_DOCUMENT_TREE);
            i.addCategory(Intent.CATEGORY_DEFAULT);
            startActivityForResult(Intent.createChooser(i, "Choose directory"), RequestCodes.CHOOSE_AUDIOBOOK_DIRECTORY.value);
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        return NavigationUI.navigateUp(navController, appBarConfiguration)
                || super.onSupportNavigateUp();
    }

    private void reload() {
        finish();
        startActivity(getIntent());
    }

    /******************
     * Configuration
     *****************/

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch(requestCode) {
            case 1:
                handleAudioBookDirectoryInput(data);
                break;
        }
    }

    private void initializeApp() {
        App.createInstance(getApplicationContext());

        String server = App.getApp().getAppPreference(Preferences.SERVER_IP);
        if(server == null || server.isEmpty())
            handleServerIpInput();

        String directory = App.getApp().getAppPreference(Preferences.AUDIOBOOK_DIRECTORY);
        if(directory == null || directory.isEmpty())
            App.getApp().setAppPreference(Preferences.AUDIOBOOK_DIRECTORY, Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MUSIC).getPath());
    }

    private void handleServerIpInput() {
        AlertDialog.Builder ipInput = new AlertDialog.Builder(this);
        View view = LayoutInflater.from(getApplicationContext()).inflate(R.layout.server_input, null);
        ipInput.setView(view);
        EditText text = (EditText)view.findViewById(R.id.ipInput) ;
        ipInput. setPositiveButton("Bestätigen", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String ip = text.getText().toString();
                        boolean valid = InetAddressValidator.getInstance().isValidInet4Address(ip);
                        if(valid) {
                            App.getApp().setAppPreference(Preferences.SERVER_IP, ip);
                            Toast.makeText(getApplicationContext(), "Server IP gesetzt!", Toast.LENGTH_SHORT).show();
                            reload();
                        } else {
                            Toast.makeText(getApplicationContext(), "ungültige Server IP!", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
        ipInput.show();
    }

    public void handleAudioBookDirectoryInput(Intent intent) {
        if(intent != null && intent.getData() != null) {
            PathHandler.getExternalMounts();
            Uri uri = intent.getData();
            Uri docUri = DocumentsContract.buildDocumentUriUsingTree(uri, DocumentsContract.getTreeDocumentId(uri));
            String path = PathHandler.getPath(this, docUri);

            /*String path = intent.getDataString();*/
            if(path != null && !path.isEmpty()) {
                App.getApp().setAppPreference(Preferences.AUDIOBOOK_DIRECTORY, path);
            } else {
                // Do nothing, keep the current value
            }
        }
    }
}