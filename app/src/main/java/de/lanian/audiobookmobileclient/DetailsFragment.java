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
import de.lanian.audiobookmobileclient.datatransfer.AudioBookDetailLoader;
import de.lanian.audiobookmobileclient.datatransfer.AudioBookDownloader;
import de.lanian.audiobookmobileclient.databinding.FragmentDetailsBinding;
import de.lanian.audiobookmobileclient.execptions.DownloadFailedException;
import de.lanian.audiobookmobileclient.utils.PermissionHandler;
import de.lanian.audiobookmobileclient.utils.Preferences;

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

        loadDetails(book.uid);

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

    private void loadDetails(String uid) {
        executor.execute(() -> {
            try {
                AudioBook book = new AudioBookDetailLoader(App.getApp().getAppPreference(Preferences.SERVER_IP)).loadAudioBookDetailsFromServer(uid);
                handler.post(() -> onLoadDetailsTaskComplete(book));
            } catch (Exception e) {}
        });
    }

    private void showDetails() {
        if(this.book != null) {
            if(this.book.previewImageData != null) {
                Bitmap bm = BitmapFactory.decodeByteArray(book.previewImageData, 0, book.previewImageData.length);
                ((ImageView) getView().findViewById(R.id.cover)).setImageBitmap(bm);
            } else
                ((ImageView) getView().findViewById(R.id.cover)).setImageResource(R.drawable.placeholder);

            ((TextView) getView().findViewById(R.id.title)).setText(book.title);
            if(book.series != null && !book.series.isEmpty())
                ((TextView) getView().findViewById(R.id.series)).setText("Reihe: " + book.series + " " + book.placeInSeries);
            ((TextView) getView().findViewById(R.id.author)).setText("Author: " + book.author);
            ((TextView) getView().findViewById(R.id.year)).setText("Erschienen: " + book.yearOfPublication);
            ((TextView) getView().findViewById(R.id.duration)).setText("Dauer: " + book.duration + "min.");
            ((TextView) getView().findViewById(R.id.speaker)).setText("Sprecher: " + book.speaker);
            ((TextView) getView().findViewById(R.id.description)).setText(book.description);
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
                                onDownloadTaskComplete(null);
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

    public void onDownloadTaskComplete(Object result) {
        Toast.makeText(App.getApp().getAppContext(), getString(R.string.downloadDone), Toast.LENGTH_LONG);
        this.getParentFragmentManager().popBackStackImmediate();
    }

    public void onLoadDetailsTaskComplete(Object result) {
        if(result instanceof AudioBook) {
            AudioBook b = (AudioBook) result;
            if(b.coverImageData != null) {
                Bitmap bm = BitmapFactory.decodeByteArray(b.coverImageData, 0, b.coverImageData.length);
                ((ImageView) getView().findViewById(R.id.cover)).setImageBitmap(bm);
            }
        }
    }

    public void showProgress(int i) {
        ((ProgressBar)getView().findViewById(R.id.progressBar)).setProgress(i);
        ((TextView)getView().findViewById(R.id.progressbartext)).setText(i+"%");
    }
}