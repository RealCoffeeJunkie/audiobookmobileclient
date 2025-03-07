package de.lanian.audiobookmobileclient;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
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
import android.provider.DocumentsContract;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import org.apache.commons.validator.routines.InetAddressValidator;

public class MainActivity extends AppCompatActivity {

    private AppBarConfiguration appBarConfiguration;

    /****************
     * Lifecycle
     ****************/

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //initializeApp();

        ActivityMainBinding binding = ActivityMainBinding.inflate(getLayoutInflater());
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
        if(requestCode == 1) {
            handleAudioBookDirectoryInput(data);
        }
    }

    private void handleServerIpInput() {
        View view = LayoutInflater.from(getApplicationContext()).inflate(R.layout.server_input, null);
        EditText input = ((EditText)view.findViewById(R.id.ipInput));

        AlertDialog ipInput = new AlertDialog.Builder(this, R.style.MyDialogTheme).
                setView(view).setPositiveButton(android.R.string.ok, null).create();

        ipInput.setOnShowListener(dialog -> {
            Button button = ((AlertDialog) dialog).getButton(AlertDialog.BUTTON_POSITIVE);
            button.setTextColor(getColor(R.color.black));
            button.setOnClickListener(view1 -> {
                String ip = input.getText().toString();
                boolean valid = InetAddressValidator.getInstance().isValidInet4Address(ip);

                if(valid) {
                    App.getApp().setAppPreference(Preferences.SERVER_IP, ip);
                    Toast.makeText(getApplicationContext(), getString(R.string.serverSaved), Toast.LENGTH_SHORT).show();
                    reload();
                    dialog.dismiss();
                } else {
                    Toast.makeText(getApplicationContext(), R.string.invalidIP, Toast.LENGTH_SHORT).show();
                }
            });
        });
        ipInput.show();
    }

    public void handleAudioBookDirectoryInput(Intent intent) {
        if(intent != null && intent.getData() != null) {
            Uri uri = intent.getData();
            Uri docUri = DocumentsContract.buildDocumentUriUsingTree(uri, DocumentsContract.getTreeDocumentId(uri));
            String path = PathHandler.getPath(this, docUri);

            if(path != null && !path.isEmpty()) {
                App.getApp().setAppPreference(Preferences.AUDIOBOOK_DIRECTORY, path);
            }
        }
    }
}