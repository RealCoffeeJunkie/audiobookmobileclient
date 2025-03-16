package de.lanian.audiobookmobileclient;

import android.os.Bundle;
import android.os.Parcelable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import java.util.List;
import de.lanian.audiobookmobileclient.data.AudioBook;
import de.lanian.audiobookmobileclient.data.ExpandableAudioBookListAdapter;
import de.lanian.audiobookmobileclient.data.SortParam;
import de.lanian.audiobookmobileclient.databinding.FragmentListBinding;

public class ListFragment extends Fragment implements View.OnClickListener {

    private FragmentListBinding binding;
    private List<AudioBook> bookList;
    private SortParam sortParam = SortParam.AUTHOR;
    private Parcelable listState;
    private int mListPosition = 0;
    private int mItemPosition = 0;

    /****************
     * Lifecycle
     ****************/

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentListBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if(bookList == null || this.bookList.size() == 0) {
            this.bookList = App.getApp().getAudioBookList();
        }
        showBookList();

        getView().findViewById(R.id.author).setOnClickListener(this);
        getView().findViewById(R.id.series).setOnClickListener(this);
        getView().findViewById(R.id.speaker).setOnClickListener(this);

        if(listState != null) {
            ExpandableListView listView = getView().findViewById(R.id.bookListExpandable);
            listView.onRestoreInstanceState(listState);
            listView.setSelectionFromTop(mListPosition, mItemPosition);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    @Override
    public void onPause() {
        ExpandableListView listView = getView().findViewById(R.id.bookListExpandable);
        listState = listView.onSaveInstanceState();
        mListPosition = listView.getFirstVisiblePosition();
        View itemView = listView.getChildAt(0);
        mItemPosition = itemView == null ? 0 : (itemView.getTop()*(-1));
        super.onPause();
    }

    /****************
     * ContentHandling
     ****************/

    private void showBookList() {
        if(this.bookList == null || this.bookList.isEmpty())
            Toast.makeText(getContext(), "Keine Daten verfügbar.", Toast.LENGTH_LONG).show();
        else {
            ExpandableListView listExpandable = getView().findViewById(R.id.bookListExpandable);
            ExpandableAudioBookListAdapter adapter = new ExpandableAudioBookListAdapter(this, this.bookList, sortParam);
            listExpandable.setAdapter(adapter);
        }
    }

    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.author) {
            sortParam = SortParam.AUTHOR;
        } else if(v.getId() == R.id.series) {
            sortParam = SortParam.SERIES;
        } else if(v.getId() == R.id.speaker) {
            sortParam = SortParam.SPEAKER;
        }

        ExpandableListView view = ((ExpandableListView)getView().findViewById(R.id.bookListExpandable));
        view.setAdapter(new ExpandableAudioBookListAdapter(this, bookList, sortParam));
    }
}