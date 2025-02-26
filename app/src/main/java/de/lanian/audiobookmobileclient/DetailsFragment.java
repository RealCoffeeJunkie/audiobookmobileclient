package de.lanian.audiobookmobileclient;

import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContract;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.google.gson.Gson;

import java.security.Permission;
import java.security.Permissions;

import de.lanian.audiobookmobileclient.data.AudioBook;
import de.lanian.audiobookmobileclient.data.AudioBookDownloader;
import de.lanian.audiobookmobileclient.databinding.FragmentDetailsBinding;
import de.lanian.audiobookmobileclient.utils.PermissionHandler;

public class DetailsFragment extends Fragment {

    private FragmentDetailsBinding binding;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentDetailsBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        AudioBook book = new Gson().fromJson(getArguments().getString("bookIndex"), AudioBook.class);
        showDetails(book);

        Button button = getView().findViewById(R.id.download);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PermissionHandler.askPermissionStorage(getActivity());
                new AudioBookDownloader(book).execute(App.getApp().getServerIp());
            }
        });
    }

    private void showDetails(AudioBook book) {
        Bitmap bm = BitmapFactory.decodeByteArray(book.CoverImageData, 0, book.getCoverImageData().length);
        ((ImageView)getView().findViewById(R.id.cover)).setImageBitmap(bm);

        ((TextView)getView().findViewById(R.id.title)).setText(book.getTitle());
        ((TextView)getView().findViewById(R.id.series)).setText("Reihe: " + book.getSeries());
        ((TextView)getView().findViewById(R.id.author)).setText("Author: " + book.getAuthor());
        ((TextView)getView().findViewById(R.id.year)).setText("Erschienen: " + book.getYearOfPublication());
        ((TextView)getView().findViewById(R.id.duration)).setText("Dauer: " + book.getDuration() + "min.");
        ((TextView)getView().findViewById(R.id.author)).setText("Sprecher: " + book.getSpeaker());
        ((TextView)getView().findViewById(R.id.description)).setText(book.getDescription());
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}