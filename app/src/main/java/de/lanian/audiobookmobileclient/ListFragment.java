package de.lanian.audiobookmobileclient;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;
import de.lanian.audiobookmobileclient.data.AudioBook;
import de.lanian.audiobookmobileclient.data.AudioBookAdapter;
import de.lanian.audiobookmobileclient.data.ListLoader;
import de.lanian.audiobookmobileclient.databinding.FragmentListBinding;

public class ListFragment extends Fragment {

    private FragmentListBinding binding;
    private ArrayList<AudioBook> bookList;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentListBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if(bookList == null || this.bookList.size() == 0)
            this.bookList = loadBookList();
        this.showBookList();
    }

    private ArrayList<AudioBook> loadBookList() {
        String [] params = new String []{App.getApp().getServerIp()};
        ArrayList<AudioBook> list;
        ListLoader loader = new ListLoader();
        loader.execute(params);
        try {
            list = (ArrayList<AudioBook>)loader.get();
        } catch (ExecutionException e) {
            throw new RuntimeException(e);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        return list;
    }

    private void showBookList() {
        RecyclerView list = getView().findViewById(R.id.bookList);
        this.binding.bookList.setLayoutManager(new LinearLayoutManager(getActivity()));
        list.setAdapter(new AudioBookAdapter(bookList, this));
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }


}