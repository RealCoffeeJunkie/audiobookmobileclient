package de.lanian.audiobookmobileclient.data;

import android.os.Handler;
import android.os.Message;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import de.lanian.audiobookmobileclient.App;
import de.lanian.audiobookmobileclient.execptions.DownloadFailedException;
import de.lanian.audiobookmobileclient.execptions.NoServerAccessException;
import de.lanian.audiobookmobileclient.execptions.UnpackingZipFailedException;
import de.lanian.audiobookmobileclient.utils.Preferences;

public class AudioBookDownloader {

    private static final int BUFFER = 8096;
    private final AudioBook book;
    private final Handler handler;
    private final String zipFileName;
    private final String musicDirPath;

    public AudioBookDownloader(AudioBook book, Handler handler) {
        this.book = book;
        this.handler = handler;

        this.zipFileName = book.Author + " - " + book.Title + ".zip";
        this.musicDirPath = new File(App.getApp().getAppPreference(Preferences.AUDIOBOOK_DIRECTORY)).getPath();
    }

    public void downloadBook() throws DownloadFailedException {
        String zipFile = null;

        try {
            URL url = new URL("http://" + App.getApp().getAppPreference(Preferences.SERVER_IP)
                    + ":8080/audiobook/download/" + book.Uid);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();

            if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                throw new NoServerAccessException();
            }

            zipFile = saveZipFile(connection.getInputStream(), connection.getContentLength());
            unpackZip(App.getApp().getAppPreference(Preferences.AUDIOBOOK_DIRECTORY), zipFileName, book.Author, book.Title);
        } catch (Exception e) {
            throw new DownloadFailedException(e.getMessage());
        } finally {
            if(zipFile != null) {
                new File(zipFile).delete();
            }
        }
    }

    private String saveZipFile(InputStream data, int contentLength) throws DownloadFailedException {
        String path = this.musicDirPath + "/" + this.zipFileName;

        try {
            int n, progress = 0;
            double bytesBuffered = 0;

            File zip = new File(path);
            if(zip.exists() || zip.createNewFile()) {
                try (FileOutputStream fos = new FileOutputStream(zip)) {
                    byte[] buf = new byte[BUFFER];
                    while ((n = data.read(buf)) != -1) {
                        bytesBuffered += n;
                        fos.write(buf, 0, n);
                        int i = (int) ((bytesBuffered / contentLength) * 75);
                        if (i > progress) {
                            progress = i;
                            handler.handleMessage(Message.obtain(handler, progress));
                        }
                    }
                    fos.flush();
                }
            }
        } catch (IOException e) {
            throw new DownloadFailedException("Downloading zip file failed.");
        }

        return path;
    }

    private void unpackZip(String directory, String zipName, String author, String title) throws UnpackingZipFailedException {
        InputStream is;
        ZipInputStream zis = null;

        try {
            String filename;
            is = Files.newInputStream(Paths.get(URI.create(directory + "/" + zipName)));
            zis = new ZipInputStream(new BufferedInputStream(is));
            ZipEntry ze;
            byte[] buffer = new byte[1024];
            int count;

            boolean successAuthorDirectory, successTitleDirectory;
            successAuthorDirectory = createDirectoryIfNecessary(directory + "/" + author);
            successTitleDirectory = createDirectoryIfNecessary(directory + "/" + author + "/" + title);

            if(!successAuthorDirectory || !successTitleDirectory) {
                throw new UnpackingZipFailedException("Unpacking zip file failed.");
            }

            String finalPath = directory + "/" + author + "/" + title;

            double done = 0.0;
            int progress = 75;
            int fileSize = is.available();

            while ((ze = zis.getNextEntry()) != null)  {
                filename = ze.getName();
                FileOutputStream writer = new FileOutputStream(finalPath + "/" + filename);
                while ((count = zis.read(buffer)) != -1)
                {
                    done += count;
                    writer.write(buffer, 0, count);
                    int i = (int)((done/fileSize)*25) + 75;
                    if(i > progress) {
                        progress = i;
                        handler.handleMessage(Message.obtain(handler, progress));
                    }
                }
                writer.close();
                zis.closeEntry();
            }
            zis.close();
        } catch(IOException e) {
            throw new UnpackingZipFailedException("Unpacking zip file failed.");
        } finally {
            try {
                if (zis != null)
                    zis.close();
            } catch (IOException ioe) {
                //Do nothing here
            }
        }
    }

    private boolean createDirectoryIfNecessary(String path) {
        File directory = new File(path);
        if(!directory.exists())
            return directory.mkdir();

        return true;
    }
}
