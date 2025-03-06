package de.lanian.audiobookmobileclient;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import de.lanian.audiobookmobileclient.data.AudioBook;
import de.lanian.audiobookmobileclient.data.AudioBookListLoader;
import de.lanian.audiobookmobileclient.data.ExpandableAudioBookListAdapter;
import de.lanian.audiobookmobileclient.data.SortParam;
import de.lanian.audiobookmobileclient.databinding.FragmentListBinding;
import de.lanian.audiobookmobileclient.execptions.NoServerAccessException;
import de.lanian.audiobookmobileclient.utils.Preferences;

public class ListFragment extends Fragment implements View.OnClickListener {

    private FragmentListBinding binding;
    private ArrayList<AudioBook> bookList;
    private SortParam sortParam = SortParam.AUTHOR;

    private final Executor executor = Executors.newCachedThreadPool();
    private final Handler handler = new Handler(Looper.getMainLooper());

    /****************
     * Lifecycle
     ****************/

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentListBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if(bookList == null || this.bookList.size() == 0)
            loadBookList();
        else
            showBookList();

        getView().findViewById(R.id.author).setOnClickListener(this);
        getView().findViewById(R.id.series).setOnClickListener(this);
        getView().findViewById(R.id.speaker).setOnClickListener(this);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    /****************
     * ContentHandling
     ****************/

    private void loadBookList() {
        executor.execute(new Runnable() {
            public void run() {
                try {
                    final List<AudioBook> books = new AudioBookListLoader(App.getApp().getAppPreference(Preferences.SERVER_IP)).loadList();
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            onTaskComplete(books);
                        }
                    });
                } catch (NoServerAccessException e) {
//                    Toast.makeText(App.getApp().getAppContext(), e.getMessage(), Toast.LENGTH_LONG);
                } catch (Exception e) {
//                    Toast.makeText(App.getApp().getAppContext(), "Ein unerwarteter Fehler ist aufgetreten.", Toast.LENGTH_LONG);
                }
            }
        });
    }

    public void onTaskComplete(Object result) {
        this.bookList = (ArrayList<AudioBook>) result;
        this.showBookList();
    }

    private void showBookList() {
        ExpandableListView listExpandable = getView().findViewById(R.id.bookListExpandable);
        ExpandableAudioBookListAdapter adapter = new ExpandableAudioBookListAdapter(getContext(), this, this.bookList, sortParam);
        listExpandable.setAdapter(adapter);
    }

    @Override
    public void onClick(View v) {
        SortParam param = null;
        if(v.getId() == R.id.author) {
            param = SortParam.AUTHOR;
        } else if(v.getId() == R.id.series) {
            param = SortParam.SERIES;
        } else if(v.getId() == R.id.speaker) {
            param = SortParam.SPEAKER;
        }

        if(param != null) {
            ExpandableListView view = ((ExpandableListView)getView().findViewById(R.id.bookListExpandable));
            view.setAdapter(new ExpandableAudioBookListAdapter(getContext(), this, bookList, param));
        }
    }
}