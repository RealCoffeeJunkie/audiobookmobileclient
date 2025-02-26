package de.lanian.audiobookmobileclient;

import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import de.lanian.audiobookmobileclient.databinding.ActivityMainBinding;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import org.apache.commons.validator.routines.InetAddressValidator;

public class MainActivity extends AppCompatActivity {

    public static final String SERVER_IP = "ServerIp";
    private AppBarConfiguration appBarConfiguration;
    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        handlingSetup();

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

        if (id == R.id.action_settings) {
            handleServerIpInout();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        return NavigationUI.navigateUp(navController, appBarConfiguration)
                || super.onSupportNavigateUp();
    }

    /******************
     * Configuration
     *****************/

    private void handlingSetup() {
        App.createInstance(getApplicationContext());
        if(getServerIp() == null)
            handleServerIpInout();
        else
            App.getApp().setServerIp(getServerIp());
    }

    private void handleServerIpInout() {
        final EditText input = new EditText(this);
        LinearLayout layoutName = new LinearLayout(this);
        layoutName.setOrientation(LinearLayout.VERTICAL);
        layoutName.addView(input);

        AlertDialog.Builder ipInput = new AlertDialog.Builder(this);
        ipInput.setTitle("Server IP:");
        ipInput.setView(layoutName);
        ipInput.setPositiveButton("Bestätigen", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String ip = input.getText().toString();
                boolean valid = InetAddressValidator.getInstance().isValidInet4Address(ip);
                if(valid) {
                    setServerIp(ip);
                    App.getApp().setServerIp(ip);
                    Toast.makeText(getApplicationContext(), "Server IP gesetzt!", Toast.LENGTH_SHORT).show();
                    reload();
                } else {
                    Toast.makeText(getApplicationContext(), "ungültige Server IP!", Toast.LENGTH_SHORT).show();
                }
            }
        });

        ipInput.show();
    }

    private String getServerIp()
    {
        SharedPreferences sp = getSharedPreferences(SERVER_IP,0);
        String str = sp.getString(SERVER_IP,null);
        return str;
    }

    private void setServerIp(String thePreference)
    {
        SharedPreferences.Editor editor = getSharedPreferences(SERVER_IP,0).edit();
        editor.putString(SERVER_IP, thePreference);
        editor.commit();
    }

    private void reload() {
        finish();
        startActivity(getIntent());
    }
}