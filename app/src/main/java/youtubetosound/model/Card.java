package youtubetosound.model;

import android.app.Activity;
import android.app.DownloadManager;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;

import java.io.File;

import youtubetosound.model.FileManager;
import youtubetosound.ui.MainActivity;

public class Card {
    private static final String TAG = "Card";
    private String downloadURL;
    private String name = "N/A";
    private String author = "N/A";

    private Status status = Status.NONE;

    private long downloadId;
    private File webm;
    private ProgressBar progressBar;
    private View view;

    public Card() {

    }

    public Card(String downloadURL, String name, String author, File webm) {
        this.downloadURL = downloadURL;
        this.name = name;
        this.author = author;
        this.webm = webm;
    }

    public boolean webmAvailable() {
        return (webm != null && webm.exists());
    }

    public String getDownloadURL() {
        return downloadURL;
    }

    public Card setDownloadURL(String downloadURL) {
        this.downloadURL = downloadURL;
        return this;
    }

    public String getName() {
        return name;
    }

    public Card setName(String name) {
        this.name = name;
        return this;
    }

    public String getAuthor() {
        return author;
    }

    public Card setAuthor(String author) {
        this.author = author;
        return this;
    }

    public View getView() {
        return view;
    }

    public void setView(View view) {
        this.view = view;
    }

    public String getFileNameWebm() {
        return webm.getName();
    }

    public void setProgressBar(ProgressBar progressBar) {
        this.progressBar = progressBar;
    }

    public ProgressBar getProgressBar() {
        return progressBar;
    }

    public File getWebm() {
        return webm;
    }

    public Status getStatus() {
        return this.status;

    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public void download(final Context context, Handler handler) {
        String downloadURL = this.downloadURL;
        String downloadTitle = this.name;
        String fileName = this.webm.getName();

        Log.d(TAG, "download: setting up download request");
        Uri uri = Uri.parse(downloadURL);
        DownloadManager.Request request = new DownloadManager.Request(uri)
                .setTitle(downloadTitle)
                .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
                .setDestinationInExternalPublicDir(Manager.getInstance().getRelativePath(), fileName);

        request.allowScanningByMediaScanner();

        File webmFile = new File(FileManager.getInstance().getAbsolutePath() + "/" + fileName);
        Log.d(TAG, "download: path" + webmFile.getPath());
        Log.d(TAG, "download: path" + webmFile.getAbsolutePath());

        DownloadManager manager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
        long downloadId = manager.enqueue(request);

        Log.d(TAG, "downloadFromUrl: download requested, creating a new download thread.");
        new Thread(new DownloadThread(context, downloadId, this.progressBar, handler, this)).start();
    }

    public String getStatusToString() {
        switch (this.status) {
            case READY:
                return "Ready";
            case EXTRACTING:
                return "Extracting";
            case DOWNLOADING:
                return "Dowloading";
            case READY_FOR_DOWNLOAD:
                return "Ready for download";
            default:
                return "None";
        }
    }

    private class DownloadThread implements Runnable {

        private final Handler handler;
        private Context context;
        private long downloadId;
        private ProgressBar progressBar;
        private Card card;

        DownloadThread(Context context, long downloadId, ProgressBar progressBar, Handler handler, Card card) {
            this.context = context;
            this.downloadId = downloadId;
            this.progressBar = progressBar;
            this.handler = handler;
            this.card = card;
        }

        @Override
        public void run() {
            boolean downloading = true;
            DownloadManager downloadManager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);

            while (downloading) {

                DownloadManager.Query q = new DownloadManager.Query();
                q.setFilterById(downloadId);

                Cursor cursor = downloadManager.query(q);
                cursor.moveToFirst();
                int bytes_downloaded = cursor.getInt(cursor
                        .getColumnIndex(DownloadManager.COLUMN_BYTES_DOWNLOADED_SO_FAR));
                int bytes_total = cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_TOTAL_SIZE_BYTES));

                if (cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_STATUS)) == DownloadManager.STATUS_SUCCESSFUL) {
                    downloading = false;
                }

                final int downloadProgress = (int) ((bytes_downloaded * 100l) / bytes_total);

                if (progressBar != null) {
                    ((Activity) context).runOnUiThread(new Runnable() {

                        @Override
                        public void run() {

                            progressBar.setProgress((int) downloadProgress);

                        }
                    });
                }
                cursor.close();
            }

            if (handler != null) {
                MainActivity.setRunnable(new Runnable() {
                    @Override
                    public void run() {
                        MainActivity.getRecyclerViewAdapter().notifyDataSetChanged();
                    }
                });
            }
        }
    }

    public enum Status {
        NONE, EXTRACTING, READY_FOR_DOWNLOAD, DOWNLOADING, READY
    }
}
