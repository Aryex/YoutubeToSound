package youtubetosound.ultility;

import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.util.Log;
import android.util.SparseArray;
import android.widget.Toast;

import java.io.File;

import at.huber.youtubeExtractor.VideoMeta;
import at.huber.youtubeExtractor.YouTubeExtractor;
import at.huber.youtubeExtractor.YtFile;
import youtubetosound.model.FileManager;

public class Downloader {
    private static final String TAG = "Downloader";

    public static void Download(Context context, String ytLink) {
        extractUrl(context, ytLink);
    }

    private static void extractUrl(final Context context, String youtubeLink) {
        new YouTubeExtractor(context) {
            @Override
            public void onExtractionComplete(SparseArray<YtFile> ytFiles, VideoMeta vMeta) {
                for (int i = 0; i < ytFiles.size(); i++) {
                    int itag;
                    itag = ytFiles.keyAt(i);
                    // ytFile represents one file with its url and meta data
                    if (isYtFileAudioAndHighQuality(ytFiles, itag)) {

                        YtFile ytFile = ytFiles.get(itag);
                        String videoTitle = vMeta.getTitle();
                        String filename;
                        if (videoTitle.length() > 55) {
                            filename = videoTitle.substring(0, 55) + "." + ytFile.getFormat().getExt();
                        } else {
                            filename = videoTitle + "." + ytFile.getFormat().getExt();
                        }
                        filename = filename.replaceAll("[\\\\><\"|*?%:#/]", "");

                        Log.d(TAG, "onExtractionComplete: IT'S AUDIO!!!!");
                        Log.d(TAG, "onExtractionComplete: VIDEO TITLE " + videoTitle);
                        Log.d(TAG, "onExtractionComplete: VIDEO AUTHOR " + vMeta.getAuthor());
                        Log.d(TAG, "onExtractionComplete: LENGTH: " + vMeta.getVideoLength());
                        Log.d(TAG, "onExtractionComplete: FILE NAME: " + filename);
                        Log.d(TAG, "onExtractionComplete: " + ytFile.toString());

                        downloadFromUrl(context, ytFile.getUrl(), videoTitle, filename);
                    }
                }
            }
        }.extract(youtubeLink, true, true);
    }

    private static void downloadFromUrl(Context context, String youtubeDlUrl, String downloadTitle, String fileName) {

        Uri uri = Uri.parse(youtubeDlUrl);
        DownloadManager.Request request = new DownloadManager.Request(uri);
        request.setTitle(downloadTitle);
        request.allowScanningByMediaScanner();
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        request.setDestinationInExternalPublicDir(FileManager.getInstance().getRelativePath(), fileName);

        File downloadFile = new File(FileManager.getInstance().getAbsolutePath() + "/" + fileName);

        Log.d(TAG, "File Manager rel path: " + FileManager.getInstance().getRelativePath());
        Log.d(TAG, "File Manager abs path: " + FileManager.getInstance().getAbsolutePath());

        DownloadManager manager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
        long downloadId = manager.enqueue(request);
        Log.d(TAG, "downloadFromUrl: FILE NAME FOR CONVERSION " + fileName);

        context.registerReceiver(new onComplete(downloadFile, downloadId), new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));
    }


    /** You are registering a BroadcastReceiver each time you download a file.
     * That means, the second time you download a file, you'll have two receivers registered.
     * You should probably unregister them using unregisterReceiver() after the work is done (probably in onReceive()).*/
    private static class onComplete extends BroadcastReceiver {
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
                if(downloadId == intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1)){
                    ConverterScheduler.getInstance().add(context, file);
                    context.unregisterReceiver(this);
                }else{
                    Log.d(TAG, "incorrect download id");
                }
            }
        }
    }

    private static boolean isYtFileAudioAndHighQuality(SparseArray<YtFile> ytFiles, int itag) {
        return ytFiles.get(itag).getFormat().getHeight() == -1
                && ytFiles.get(itag).getFormat().getAudioBitrate() >= 150;
    }
}
