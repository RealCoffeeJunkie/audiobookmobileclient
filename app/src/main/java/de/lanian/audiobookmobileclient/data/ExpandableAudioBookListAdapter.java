package de.lanian.audiobookmobileclient.data;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.CheckBox;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.navigation.fragment.NavHostFragment;
import com.google.gson.Gson;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;
import de.lanian.audiobookmobileclient.App;
import de.lanian.audiobookmobileclient.ListFragment;
import de.lanian.audiobookmobileclient.R;
import de.lanian.audiobookmobileclient.utils.FavoriteHandler;

public class ExpandableAudioBookListAdapter extends BaseExpandableListAdapter {
    private final ListFragment fragment;
    private List<String> listTitles;
    private final HashMap<String, List<AudioBook>> listItems;
    private FavoriteHandler favoriteHandler;

    public ExpandableAudioBookListAdapter(ListFragment fragment, List<AudioBook> books, SortParam sortParam) {
        this.fragment = fragment;
        listTitles = new ArrayList<>();
        listItems = new HashMap<>();

        //Sorting List Items in Groups
        for(AudioBook book : books) {
            String sortedParam = getGroupHeader(sortParam, book);

            if(!listItems.containsKey(sortedParam)) {
                listTitles.add(sortedParam);
                ArrayList<AudioBook> list = new ArrayList<>();
                listItems.put(sortedParam, list);
            }

            listItems.get(sortedParam).add(book);
        }

        //Sorting Group-Titles
        listTitles = listTitles.stream().sorted(Comparator.naturalOrder()).collect(Collectors.toList());

        //Sorting each Group by Series and PlaceInSeries of the books
        for (String key : listItems.keySet()) {
            List<AudioBook> newOrder = listItems.get(key).stream().sorted(Comparator.comparing(AudioBook::getComparableBySeries)).collect(Collectors.toList());
            listItems.put(key, newOrder);
        }

        this.favoriteHandler = new FavoriteHandler();
    }

    /**
     * Helper Methods
     */

    private String getGroupHeader(SortParam param, AudioBook book) {
        switch(param) {
            case SERIES:
                return book.Series;
            case SPEAKER:
                return book.Speaker;
            default:
                return book.Author;
        }
    }

    private boolean isAudioBookAlreadyDownloaded(AudioBook book) {
        String path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MUSIC).getPath();
        if(!path.endsWith("/"))
            path += "/";
        path += book.Author + "/" + book.Title;

        File file = new File(path);

        return file.exists();
    }

    /**
     * Adapter Methods
     */

    @Override
    public int getGroupCount() {
        return listTitles.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return listItems.get(listTitles.get(groupPosition)).size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return this.listTitles.get(groupPosition);
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return this.listItems.get(this.listTitles.get(groupPosition)).get(childPosition);
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @SuppressLint("InflateParams")
    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        String listTitle = (String) getGroup(groupPosition);
        if (convertView == null) {
            LayoutInflater layoutInflater = (LayoutInflater) App.getApp().getAppContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = layoutInflater.inflate(R.layout.audiobooklist_headerrow, null);
        }
        TextView listTitleTextView = (TextView) convertView.findViewById(R.id.listheader);
        listTitleTextView.setTypeface(null, Typeface.BOLD);
        listTitleTextView.setText(listTitle);

        ExpandableListView eLV = (ExpandableListView) parent;
        eLV.expandGroup(groupPosition);

        return convertView;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        AudioBook book = (AudioBook) getChild(groupPosition, childPosition);

        if (convertView == null) {
            LayoutInflater layoutInflater = (LayoutInflater) App.getApp().getAppContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = layoutInflater.inflate(R.layout.audiobooklist_contentrow, null);
        }

        ((TextView) convertView.findViewById(R.id.title)).setText(book.Title);
        if(book.Series == null || book.Series.isEmpty()) {
            ((TextView) convertView.findViewById(R.id.series)).setText("");
        } else {
            ((TextView) convertView.findViewById(R.id.series)).setText("Buchreihe: " + book.Series + " " + book.PlaceInSeries);
        }
        ((TextView) convertView.findViewById(R.id.author)).setText("Author: " + book.Author);
        ((TextView) convertView.findViewById(R.id.speaker)).setText("Sprecher: " + book.Speaker);

        byte[] cover = book.CoverImageData;
        Bitmap bm = BitmapFactory.decodeByteArray(cover, 0, cover.length);
        ((ImageView) convertView.findViewById(R.id.cover)).setImageBitmap(bm);

        if(isAudioBookAlreadyDownloaded(book)) {
            ((CheckBox) convertView.findViewById(R.id.isDownloaded)).setChecked(true);
            ((CheckBox) convertView.findViewById(R.id.isDownloaded)).setVisibility(View.VISIBLE);
        } else {
            ((CheckBox) convertView.findViewById(R.id.isDownloaded)).setChecked(false);
            ((CheckBox) convertView.findViewById(R.id.isDownloaded)).setVisibility(View.INVISIBLE);
        }

        if(this.favoriteHandler.isAudioBookFavorite(book.Uid)) {
            ((CheckBox) convertView.findViewById(R.id.isFavorite)).setVisibility(View.VISIBLE);
            ((CheckBox) convertView.findViewById(R.id.isFavorite)).setChecked(true);
        }
        else {
            ((CheckBox) convertView.findViewById(R.id.isFavorite)).setVisibility(View.INVISIBLE);
            ((CheckBox) convertView.findViewById(R.id.isFavorite)).setChecked(false);
        }
        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bundle bundle = new Bundle();
                bundle.putString("bookIndex", new Gson().toJson(book, AudioBook.class));
                NavHostFragment.findNavController(fragment)
                        .navigate(R.id.action_ListFragment_to_DetailsFragment, bundle);
            }
        });

        CheckBox box = convertView.findViewById(R.id.isFavorite);

        convertView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if(favoriteHandler.isAudioBookFavorite(book.Uid)) {
                    favoriteHandler.removeFavorite(book.Uid);
                    box.setChecked(false);
                    box.setVisibility(View.INVISIBLE);
                } else {
                    favoriteHandler.addFavorite(book.Uid);
                    box.setChecked(true);
                    box.setVisibility(View.VISIBLE);
                }
                return true;
            }
        });

        return convertView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }
}
