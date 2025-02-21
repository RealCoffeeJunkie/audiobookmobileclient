package de.lanian.audiobookmobileclient;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.google.gson.Gson;

import de.lanian.audiobookmobileclient.data.AudioBook;
import de.lanian.audiobookmobileclient.databinding.FragmentDetailsBinding;

public class DetailsFragment extends Fragment {

    private FragmentDetailsBinding binding;

    private AudioBook book;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentDetailsBinding.inflate(inflater, container, false);
        return binding.getRoot();

    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        this.book = new Gson().fromJson(getArguments().getString("bookIndex"), AudioBook.class);

        Bitmap bm = BitmapFactory.decodeByteArray(book.CoverImageData, 0, book.getCoverImageData().length);
        ((ImageView)getView().findViewById(R.id.cover)).setImageBitmap(bm);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

}