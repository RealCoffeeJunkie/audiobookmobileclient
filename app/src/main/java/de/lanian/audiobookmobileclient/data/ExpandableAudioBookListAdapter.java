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
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

import de.lanian.audiobookmobileclient.ListFragment;
import de.lanian.audiobookmobileclient.R;

public class ExpandableAudioBookListAdapter extends BaseExpandableListAdapter {

    private final Context context;
    private final ListFragment fragment;
    private List<String> expandableListTitle;
    private final HashMap<String, List<AudioBook>> expandableListDetail;

    public ExpandableAudioBookListAdapter(Context context, ListFragment fragment, List<AudioBook> books, SortParam sortParam) {
        this.context = context;
        this.fragment = fragment;
        expandableListTitle = new ArrayList<>();
        expandableListDetail = new HashMap<>();

        for(AudioBook book : books) {
            String sortedParam = getGroupHeader(sortParam, book);
            if(!expandableListDetail.containsKey(sortedParam)) {
                expandableListTitle.add(sortedParam);
                expandableListDetail.put(sortedParam, new ArrayList<>());
            }

            expandableListDetail.get(sortedParam).add(book);
        }

        expandableListTitle = expandableListTitle.stream().sorted(Comparator.naturalOrder()).collect(Collectors.toList());

        for (String key : expandableListDetail.keySet()) {
            List<AudioBook> newOrder = expandableListDetail.get(key).stream().sorted(Comparator.comparingInt(AudioBook::getPlaceInSeries)).collect(Collectors.toList());
            expandableListDetail.put(key, newOrder);
        }
    }

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

    @Override
    public int getGroupCount() {
        return expandableListTitle.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return expandableListDetail.get(expandableListTitle.get(groupPosition)).size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return this.expandableListTitle.get(groupPosition);
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return this.expandableListDetail.get(this.expandableListTitle.get(groupPosition)).get(childPosition);
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
            LayoutInflater layoutInflater = (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
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
            LayoutInflater layoutInflater = (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
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

        if(isAudioBookAlreadyDownloaded(book))
            ((CheckBox) convertView.findViewById(R.id.isDownloaded)).setChecked(true);
        else
            ((CheckBox) convertView.findViewById(R.id.isDownloaded)).setVisibility(View.INVISIBLE);

        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bundle bundle = new Bundle();
                bundle.putString("bookIndex", new Gson().toJson(book, AudioBook.class));
                NavHostFragment.findNavController(fragment)
                        .navigate(R.id.action_ListFragment_to_DetailsFragment, bundle);
            }
        });

        return convertView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }

    private boolean isAudioBookAlreadyDownloaded(AudioBook book) {
        String path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MUSIC).getPath();
        if(!path.endsWith("/"))
            path += "/";
        path += book.Author + "/" + book.Title;

        File file = new File(path);

        return file.exists();
    }
}
