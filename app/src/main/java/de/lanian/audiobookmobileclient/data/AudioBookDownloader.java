package de.lanian.audiobookmobileclient.data;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Environment;
import android.view.Window;
import android.widget.ProgressBar;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import de.lanian.audiobookmobileclient.App;
import de.lanian.audiobookmobileclient.execptions.DownloadFailedException;
import de.lanian.audiobookmobileclient.execptions.NoServerAccessException;
import de.lanian.audiobookmobileclient.execptions.UnpackingZipFailedException;

public class AudioBookDownloader extends AsyncTask {

    private AudioBook book;
    private Activity activity;
    private static final int BUFSIZE = 8096;

    private ProgressDialog dialog;

    public AudioBookDownloader(AudioBook book) {
        this.book = book;
    }

    @Override
    protected Object doInBackground(Object[] objects) {
        String zipFile = null;

        try {
            URL url = new URL("http://" + objects[0] + ":8080/audiobook/download/" + book.Uid);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();

            if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                throw new NoServerAccessException("Server couldnt be accessed.");
            }

            String zipFileName = book.Author + " - " + book.getTitle() + ".zip";

            zipFile = saveZipFile(connection.getInputStream(), zipFileName);
            unpackZip(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MUSIC).getPath(), zipFileName, book.Author, book.Title);
        } catch (Exception e) {
            return new DownloadFailedException(e.getMessage());
        } finally {
            if(zipFile != null) {
                File file = new File(zipFile);
                file.delete();
            }
        }
        return null;
    }

    private String saveZipFile(InputStream data, String fileName) throws DownloadFailedException {
        File file = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MUSIC);
        try {
            String path = file.getPath() + "/" + fileName;
            int n, bytesBuffered = 0;
            FileOutputStream fos = new FileOutputStream(path);
            byte[] buf = new byte[BUFSIZE];
            while (-1 != (n = data.read(buf))) {
                bytesBuffered += n;
                fos.write(buf, 0, n);
            }
            publishProgress(50);
            fos.flush();
        } catch (IOException e) {
            throw new DownloadFailedException("Downloading zip file failed.");
        }

        return file.getPath() + "/" + fileName;
    }

    private boolean unpackZip(String path, String zipname, String author, String title) throws UnpackingZipFailedException {
        InputStream is;
        ZipInputStream zis = null;

        try {
            String filename;
            is = new FileInputStream(path + "/" + zipname);
            zis = new ZipInputStream(new BufferedInputStream(is));
            ZipEntry ze;
            byte[] buffer = new byte[1024];
            int count;

            File dirAuthor = new File(path + "/" + author);
            if(!dirAuthor.exists())
                dirAuthor.mkdir();
            File dirTitle = new File(path + "/" + author + "/" + title);
            if(!dirTitle.exists())
                dirTitle.mkdir();

            String finalPath = dirTitle.getPath();

            float progressStep = 50 / (zis.available() / buffer.length);
            int progress = 50;

            while ((ze = zis.getNextEntry()) != null)
            {
                filename = ze.getName();
                FileOutputStream fout = new FileOutputStream(finalPath + "/" + filename);
                while ((count = zis.read(buffer)) != -1)
                {
                    fout.write(buffer, 0, count);
                    progress += progressStep;
                    publishProgress(progress);
                }
                fout.close();
                zis.closeEntry();
            }
            zis.close();
        }
        catch(IOException e)
        {
            throw new UnpackingZipFailedException("Unpacking zip file failed.");
        } finally {
            try {
                if (zis != null)
                    zis.close();
            } catch (IOException ioe) {}
        }

        return true;
    }

    @Override
    protected void onPreExecute() {

        super.onPreExecute();
        // create dialog
        dialog=new ProgressDialog(App.getApp().getAppContext());
        dialog.setCancelable(true);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        dialog.show();
    }

    protected void onProgressUpdate(Integer... values) {
        super.onProgressUpdate(values);
        dialog.setProgress(values[0]);
    }

    protected void onPostExecute(Void result) {
        super.onPostExecute(result);
        dialog.dismiss();
    }
}
