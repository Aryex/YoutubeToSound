package youtubetosound.downloader;

import android.app.Activity;
import android.app.DownloadManager;
import android.content.Context;
import android.database.Cursor;
import android.widget.ProgressBar;

import java.io.File;

import youtubetosound.ultility.ConverterScheduler;


/**
 * https://stackoverflow.com/questions/37700853/how-to-access-the-percent-of-android-download-manager
 * */

class DownloadThread {

    DownloadThread(final Context context, final long downloadId, final File webmFile, final DownloadManager downloadManager, final ProgressBar progressBar) {
        new Thread(new Runnable() {

            @Override
            public void run() {
                boolean downloading = true;

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

                    if(progressBar != null){
                        ((Activity) context).runOnUiThread(new Runnable() {

                            @Override
                            public void run() {

                                progressBar.setProgress((int) downloadProgress);

                            }
                        });
                    }
                    cursor.close();
                }
            }
        }).start();
    }
}
