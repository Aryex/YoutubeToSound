package youtubetosound.downloader;

import android.app.Activity;
import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Handler;
import android.util.Log;
import android.util.SparseArray;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.io.File;

import at.huber.youtubeExtractor.VideoMeta;
import at.huber.youtubeExtractor.YouTubeExtractor;
import at.huber.youtubeExtractor.YtFile;
import youtubetosound.item.manager.AudioFile;
import youtubetosound.item.manager.AudioFileManager;
import youtubetosound.model.FileManager;
import youtubetosound.ui.MainActivity;
import youtubetosound.ui.R;
import youtubetosound.ultility.ConverterScheduler;

public class Downloader {
    private static final String TAG = "Downloader";
    private static Downloader instance;

    public static Downloader getInstance() {
        if (instance == null) {
            instance = new Downloader();
        }
        return instance;
    }

    public void download(final Context context, Handler handler, AudioFile audioFile) {
        String downloadURL = audioFile.getDownloadURL();
        String downloadTitle = audioFile.getName();
        String fileName = audioFile.getFileNameWebm();

        Log.d(TAG, "download: setting up download request");
        Uri uri = Uri.parse(downloadURL);
        DownloadManager.Request request = new DownloadManager.Request(uri)
                .setTitle(downloadTitle)
                .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
                .setDestinationInExternalPublicDir(AudioFileManager.getInstance().getRelativePath(), fileName);

        request.allowScanningByMediaScanner();

        File webmFile = new File(FileManager.getInstance().getAbsolutePath() + "/" + fileName);
        Log.d(TAG, "download: path" + webmFile.getPath());
        Log.d(TAG, "download: path" + webmFile.getAbsolutePath());

        DownloadManager manager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
        long downloadId = manager.enqueue(request);

        Log.d(TAG, "downloadFromUrl: download requested, creating a new download thread.");
        new Thread(new DownloadThread(context, downloadId, audioFile.getProgressBar(), handler, audioFile)).start();
    }

    private Downloader() {
    }

    private class DownloadThread implements Runnable {

        private final Handler handler;
        private Context context;
        private long downloadId;
        private ProgressBar progressBar;
        private AudioFile audioFile;

        DownloadThread(Context context, long downloadId, ProgressBar progressBar, Handler handler, AudioFile audioFile) {
            this.context = context;
            this.downloadId = downloadId;
            this.progressBar = progressBar;
            this.handler = handler;
            this.audioFile = audioFile;
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

            if(handler!=null){
                MainActivity.setRunnable(new Runnable() {
                    @Override
                    public void run() {
                        MainActivity.getAdapter().notifyDataSetChanged();
                    }
                });
            }
        }
    }


}


//    public void start(Context context, String ytLink, ProgressBar progressBar) {
//        extractUrl(context, ytLink, progressBar, this);
//    }

//    private static void extractUrl(final Context context, String youtubeLink, final ProgressBar progressBar, final Downloader downloader) {
//        new YouTubeExtractor(context) {
//            @Override
//            public void onExtractionComplete(SparseArray<YtFile> ytFiles, VideoMeta vMeta) {
//                for (int i = 0; i < ytFiles.size(); i++) {
//                    int itag;
//                    itag = ytFiles.keyAt(i);
//                    // ytFile represents one file with its url and meta data
//                    if (isYtFileAudioAndHighQuality(ytFiles, itag)) {
//
//                        YtFile ytFile = ytFiles.get(itag);
//                        String videoTitle = vMeta.getTitle();
//                        String filename;
//                        if (videoTitle.length() > 55) {
//                            filename = videoTitle.substring(0, 55) + "." + ytFile.getFormat().getExt();
//                        } else {
//                            filename = videoTitle + "." + ytFile.getFormat().getExt();
//                        }
//                        filename = filename.replaceAll("[\\\\><\"|*?%:#/]", "");
//
//                        Log.d(TAG, "onExtractionComplete: IT'S AUDIO!!!!");
//                        Log.d(TAG, "onExtractionComplete: VIDEO TITLE " + videoTitle);
//                        Log.d(TAG, "onExtractionComplete: VIDEO AUTHOR " + vMeta.getAuthor());
//                        Log.d(TAG, "onExtractionComplete: LENGTH: " + vMeta.getVideoLength());
//                        Log.d(TAG, "onExtractionComplete: FILE NAME: " + filename);
//                        Log.d(TAG, "onExtractionComplete: " + ytFile.toString());
//
//                        Downloader.downloadFromUrl(context, ytFile.getUrl(), videoTitle, filename, progressBar, downloader);
//                    }
//                }
//            }
//        }.extract(youtubeLink, true, true);
//    }


//    private static void downloadFromUrl(Context context, String youtubeDlUrl, String downloadTitle, String fileName, ProgressBar progressBar, Downloader downloader) {
//        Log.d(TAG, "downloadFromUrl: setting up download request");
//        Uri uri = Uri.parse(youtubeDlUrl);
//        DownloadManager.Request request = new DownloadManager.Request(uri)
//                .setTitle(downloadTitle)
//                .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
//                .setDestinationInExternalPublicDir(FileManager.getInstance().getRelativePath(), fileName);
//
//        request.allowScanningByMediaScanner();
//
//        File downloadFile = new File(FileManager.getInstance().getAbsolutePath() + "/" + fileName);
//
//        DownloadManager manager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
//        long downloadId = manager.enqueue(request);
//
//        Log.d(TAG, "downloadFromUrl: download requested, creating a new download thread.");
//        new DownloadThread(context, downloadId, downloadFile, manager, progressBar);
//        //context.registerReceiver(new onComplete(downloadFile, downloadId), new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));
//    }


/**
 * You are registering a BroadcastReceiver each time you download a file.
 * That means, the second time you download a file, you'll have two receivers registered.
 * You should probably unregister them using unregisterReceiver() after the work is done (probably in onReceive()).
 */
/*private static class onComplete extends BroadcastReceiver {
    private File file;
    private long downloadId;

    public onComplete(File file, long downloadId) {
        this.file = file;
        this.downloadId = downloadId;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        //Add to scheduler.
        //ConverterScheduler.getInstance().add(context, file);
        Log.d(TAG, "onReceive: Start");
        // File file = new File(FileManager.getInstance().getAbsolutePath() + "/" +Drake - Toosie Slide.webm");
        Log.d(TAG, "download completed.");
        Log.d(TAG, "file name: " + file.getName());
        Log.d(TAG, "file abs path: " + file.getAbsolutePath());
        Log.d(TAG, "file  path: " + file.getPath());

        if (!file.exists()) {
            Log.d(TAG, "File doesnt exists: ");
        } else {
            Log.d(TAG, "File exists: ");
            if (downloadId == intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1)) {
                ConverterScheduler.getInstance().add(context, file);
                context.unregisterReceiver(this);
            } else {
                Log.d(TAG, "incorrect download id");
            }
        }
    }

}

    private static boolean isYtFileAudioAndHighQuality(SparseArray<YtFile> ytFiles, int itag) {
        return ytFiles.get(itag).getFormat().getHeight() == -1
                && ytFiles.get(itag).getFormat().getAudioBitrate() >= 150;
    }
}*/
