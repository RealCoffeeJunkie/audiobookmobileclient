package de.lanian.audiobookmobileclient;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import com.google.gson.Gson;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import de.lanian.audiobookmobileclient.data.AudioBook;
import de.lanian.audiobookmobileclient.datatransfer.AudioBookDownloader;
import de.lanian.audiobookmobileclient.databinding.FragmentDetailsBinding;
import de.lanian.audiobookmobileclient.execptions.DownloadFailedException;
import de.lanian.audiobookmobileclient.utils.PermissionHandler;

public class DetailsFragment extends Fragment implements View.OnClickListener {

    private FragmentDetailsBinding binding;
    private AudioBook book;

    private final Executor executor = Executors.newCachedThreadPool();
    private final Handler handler = new Handler(Looper.getMainLooper()) {
        public void handleMessage(Message message) {
            showProgress(message.what);
        }
    };

    /****************
     * Lifecycle
     ****************/

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentDetailsBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        this.book = new Gson().fromJson(getArguments().getString("bookIndex"), AudioBook.class);
        showDetails();

        Button button = getView().findViewById(R.id.download);
        button.setOnClickListener(this);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    /****************
     * ContentHandling
     ****************/

    private void showDetails() {
        if(this.book != null) {
            Bitmap bm = BitmapFactory.decodeByteArray(book.CoverImageData, 0, book.CoverImageData.length);
            ((ImageView) getView().findViewById(R.id.cover)).setImageBitmap(bm);

            ((TextView) getView().findViewById(R.id.title)).setText(book.Title);
            if(book.Series != null && !book.Series.isEmpty())
                ((TextView) getView().findViewById(R.id.series)).setText("Reihe: " + book.Series + " " + book.PlaceInSeries);
            ((TextView) getView().findViewById(R.id.author)).setText("Author: " + book.Author);
            ((TextView) getView().findViewById(R.id.year)).setText("Erschienen: " + book.YearOfPublication);
            ((TextView) getView().findViewById(R.id.duration)).setText("Dauer: " + book.Duration + "min.");
            ((TextView) getView().findViewById(R.id.speaker)).setText("Sprecher: " + book.Speaker);
            ((TextView) getView().findViewById(R.id.description)).setText(book.Description);
        } else {
            Toast.makeText(App.getApp().getAppContext(), getString(R.string.dataNotLoaded), Toast.LENGTH_LONG);
        }
    }

    /****************
     * EventHandling
     ****************/

    @Override
    public void onClick(View v) {
        boolean permission = PermissionHandler.askPermissionStorage(getActivity());

        if(permission) {
            executor.execute(new Runnable() {
                public void run() {
                    try {
                        new AudioBookDownloader(book, handler).downloadBook();

                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                onTaskComplete(null);
                            }
                        });
                    } catch (DownloadFailedException e) {
                        Toast.makeText(App.getApp().getAppContext(), e.getMessage(), Toast.LENGTH_LONG);
                    } catch (Exception e) {
                        Toast.makeText(App.getApp().getAppContext(), getString(R.string.unexpectedError), Toast.LENGTH_LONG);
                    }
                }
            });
        } else {
            Toast.makeText(App.getApp().getAppContext(), getString(R.string.noPermissionGranted), Toast.LENGTH_LONG);
        }

    }

    public void onTaskComplete(Object result) {
        Toast.makeText(App.getApp().getAppContext(), getString(R.string.downloadDone), Toast.LENGTH_LONG);
        this.getParentFragmentManager().popBackStackImmediate();
    }

    public void showProgress(int i) {
        ((ProgressBar)getView().findViewById(R.id.progressBar)).setProgress(i);
        ((TextView)getView().findViewById(R.id.progressbartext)).setText(i+"%");
    }
}