package de.lanian.audiobookmobileclient.backup;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;

import java.io.File;
import java.util.ArrayList;
import de.lanian.audiobookmobileclient.ListFragment;
import de.lanian.audiobookmobileclient.R;
import de.lanian.audiobookmobileclient.data.AudioBook;

public class AudioBookAdapter extends RecyclerView.Adapter<AudioBookAdapter.ViewHolder> {

    private ArrayList<AudioBook> books;
    private ListFragment parent;

    public AudioBookAdapter(ArrayList<AudioBook> books, ListFragment fragment) {
        this.books = books;
        parent = fragment;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.audiobooklist_contentrow, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int position) {
        AudioBook book = books.get(position);

        byte[] cover = this.books.get(position).CoverImageData;
        Bitmap bm = BitmapFactory.decodeByteArray(cover, 0, cover.length);
        viewHolder.getImageViewCover().setImageBitmap(bm);

        viewHolder.getTextViewTitle().setText(book.Title);
        if(book.Series != null && book.Series.length() > 0)
            viewHolder.getTextViewSeries().setText("(" + book.Series + ")");
        viewHolder.getTextViewAuthor().setText(book.Author);

        if(isAudioBookAlreadyDownloaded(book))
            viewHolder.getCheckBoxDownloaded().setChecked(true);
        else
            viewHolder.getCheckBoxDownloaded().setVisibility(View.INVISIBLE);

        viewHolder.getImageViewCover().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bundle bundle = new Bundle();
                bundle.putString("bookIndex", new Gson().toJson(book, AudioBook.class));
                NavHostFragment.findNavController(parent)
                        .navigate(R.id.action_ListFragment_to_DetailsFragment, bundle);
            }
        });
    }

    private boolean isAudioBookAlreadyDownloaded(AudioBook book) {
        String path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MUSIC).getPath();
        if(!path.endsWith("/"))
            path += "/";
        path += book.Author + "/" + book.Title;

        File file = new File(path);

        if(file.exists())
            return true;

        return false;
    }

    @Override
    public int getItemCount() {
        return books.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView title;
        private final TextView series;
        private final TextView author;
        private final ImageView cover;
        private final CheckBox box;

        public ViewHolder(View view) {
            super(view);
            title = (TextView) view.findViewById(R.id.title);
            series = (TextView) view.findViewById(R.id.series);
            cover = (ImageView) view.findViewById(R.id.cover);
            author = (TextView) view.findViewById(R.id.author);
            box = (CheckBox) view.findViewById(R.id.isDownloaded);
        }

        public TextView getTextViewTitle() {
            return title;
        }

        public TextView getTextViewSeries() {
            return series;
        }

        public ImageView getImageViewCover() {
            return cover;
        }

        public TextView getTextViewAuthor() {
            return author;
        }

        public CheckBox getCheckBoxDownloaded() { return box; }
    }
}
