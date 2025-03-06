package de.lanian.audiobookmobileclient.data;

import android.app.Activity;
import android.os.Handler;
import android.os.Message;
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
import de.lanian.audiobookmobileclient.utils.Preferences;

public class AudioBookDownloader {

    private AudioBook book;

    private Handler handler;

    private Activity activity;
    private static final int BUFSIZE = 8096;

    public AudioBookDownloader(AudioBook book, Handler handler) {
        this.book = book;
        this.handler = handler;
    }

    public Object downloadBook(String server) throws DownloadFailedException {
        String zipFile = null;

        try {
            URL url = new URL("http://" + server + ":8080/audiobook/download/" + book.Uid);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();

            if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                throw new NoServerAccessException("Server couldnt be accessed.");
            }

            String zipFileName = book.Author + " - " + book.Title + ".zip";

            zipFile = saveZipFile(connection.getInputStream(), zipFileName, connection.getContentLength());
            unpackZip(App.getApp().getAppPreference(Preferences.AUDIOBOOK_DIRECTORY), zipFileName, book.Author, book.Title);
        } catch (Exception e) {
            throw new DownloadFailedException(e.getMessage());
        } finally {
            if(zipFile != null) {
                File file = new File(zipFile);
                file.delete();
            }
        }
        return null;
    }

    private String saveZipFile(InputStream data, String fileName, int contentLength) throws DownloadFailedException {
        String musicDir = App.getApp().getAppPreference(Preferences.AUDIOBOOK_DIRECTORY);
        File file = new File(musicDir);
        try {
            String path = file.getPath() + "/" + fileName;
            int n = 0, progress = 0;
            double bytesBuffered = 0;

            File zip = new File(path);
            if(!zip.exists())
                zip.createNewFile();

            FileOutputStream fos = new FileOutputStream(zip);
            byte[] buf = new byte[BUFSIZE];
            while ((n = data.read(buf)) != -1) {
                bytesBuffered += n;
                fos.write(buf, 0, n);
                int i = (int)((bytesBuffered/contentLength)*75);
                if(i > progress) {
                    progress = i;
                    handler.handleMessage(Message.obtain(handler, progress));
                }
            }
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

            double done = 0.0;
            int progress = 75;
            int fileSize = is.available();

            while ((ze = zis.getNextEntry()) != null)
            {
                filename = ze.getName();
                FileOutputStream fout = new FileOutputStream(finalPath + "/" + filename);
                while ((count = zis.read(buffer)) != -1)
                {
                    done += count;
                    fout.write(buffer, 0, count);
                    int i = (int)((done/fileSize)*25) + 75;
                    if(i > progress) {
                        progress = i;
                        handler.handleMessage(Message.obtain(handler, progress));
                    }
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
}
