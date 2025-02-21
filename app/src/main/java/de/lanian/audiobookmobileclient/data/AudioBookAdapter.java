package de.lanian.audiobookmobileclient.data;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.navigation.NavOptions;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;

import java.util.ArrayList;
import de.lanian.audiobookmobileclient.ListFragment;
import de.lanian.audiobookmobileclient.R;

public class AudioBookAdapter extends RecyclerView.Adapter<AudioBookAdapter.ViewHolder> {

    ArrayList<AudioBook> books;
    ListFragment parent;

    public AudioBookAdapter(ArrayList<AudioBook> books, ListFragment fragment) {
        this.books = books;
        parent = fragment;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.audiobook_row, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int position) {
        AudioBook book = books.get(position);

        byte[] cover = this.books.get(position).CoverImageData;
        Bitmap bm = BitmapFactory.decodeByteArray(cover, 0, cover.length);
        viewHolder.getImageViewCover().setImageBitmap(bm);

        viewHolder.getTextViewTitle().setText(book.getTitle());
        if(book.getSeries() != null && book.getSeries().length() > 0)
            viewHolder.getTextViewSeries().setText("(" + book.getSeries() + ")");
        viewHolder.getTextViewAuthor().setText(book.getAuthor());

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

    @Override
    public int getItemCount() {
        return books.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView title;
        private final TextView series;
        private final TextView author;
        private final ImageView cover;

        public ViewHolder(View view) {
            super(view);
            title = (TextView) view.findViewById(R.id.title);
            series = (TextView) view.findViewById(R.id.series);
            cover = (ImageView) view.findViewById(R.id.cover);
            author = (TextView) view.findViewById(R.id.author);
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
    }
}
