package youtubetosound.ui;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.util.SparseArray;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;

import java.io.File;
import java.util.ArrayList;

import at.huber.youtubeExtractor.VideoMeta;
import at.huber.youtubeExtractor.YouTubeExtractor;
import at.huber.youtubeExtractor.YtFile;
import youtubetosound.item.manager.AudioFile;
import youtubetosound.item.manager.AudioFileManager;
import youtubetosound.model.FileManager;
import youtubetosound.ultility.ConverterScheduler;
import youtubetosound.ultility.PermissionsManager;

public class MainActivity extends AppCompatActivity {

    private final static String TAG = "MainActivity";
    private String youtubeLink = "";
    private static RecyclerViewAdapter adapter;
    private static Handler mainHandler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final Context context = this;
        final ProgressBar progressBar = findViewById(R.id.progress_bar);

        //ConverterScheduler.getInstance().setupConverter(this);

        PermissionsManager.checkExternalStoragePermission(this, new PermissionsManager.OnExternalStoragePermissionGranted() {
            @Override
            public void permissionAlreadyGranted() {
                setupRecyclerView();
            }
        });

        if (startedExternallyWithTextData(savedInstanceState)) {
            String string = getIntent().getStringExtra(Intent.EXTRA_TEXT);
            if (isYoutubeLink(string)) {
                youtubeLink = string;
            }
        }

        setupInputText();

        Button btn = findViewById(R.id.addButton);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               extractUrl(context, youtubeLink, adapter);
            }
        });

        btn = findViewById(R.id.startButton);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AudioFileManager.getInstance().startDownloads(context, mainHandler);
            }
        });

    }

    private void setupInputText() {
        EditText editText = findViewById(R.id.edit_text_URL);
        editText.setText(youtubeLink);
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                changeURL(s.toString());
            }
        });
    }

    private void setupRecyclerView() {
        Log.d(TAG, "setupRecyclerView: setting up recyclerview.");
        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        adapter = new RecyclerViewAdapter(this, AudioFileManager.getInstance().getFiles());
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    private boolean isYoutubeLink(String data) {
        return data != null
                && (data.contains("://youtu.be/") || data.contains("youtube.com/watch?v="));
    }

    private boolean startedExternallyWithTextData(Bundle savedInstanceState) {
        return savedInstanceState == null && Intent.ACTION_SEND.equals(getIntent().getAction())
                && getIntent().getType() != null && "text/plain".equals(getIntent().getType());
    }

    private void changeURL(String string) {
        this.youtubeLink = string;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case PermissionsManager.EXTERNAL_READ_WRITE_REQUEST_CODE: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    setupRecyclerView();
                }
            }
        }
    }

    private static void extractUrl(final Context context, String youtubeLink, final RecyclerViewAdapter adapter) {
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
                        //AudioFileManager.getInstance().add(new AudioFile(ytFile.getUrl(), videoTitle, vMeta.getAuthor()));

                        AudioFileManager.getInstance().add(new AudioFile(ytFile.getUrl(),
                                videoTitle,
                                vMeta.getAuthor(),
                                new File(FileManager.getInstance().getAbsolutePath() + "/" +filename)));
                        adapter.updateList(AudioFileManager.getInstance().getFiles());
                        adapter.notifyDataSetChanged();
                    }
                }
            }
        }.extract(youtubeLink, true, true);
    }

    private static boolean isYtFileAudioAndHighQuality(SparseArray<YtFile> ytFiles, int itag) {
        return ytFiles.get(itag).getFormat().getHeight() == -1
                && ytFiles.get(itag).getFormat().getAudioBitrate() >= 150;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        AudioFileManager.getInstance().clear();
    }

    public static RecyclerViewAdapter getAdapter() {
        return adapter;
    }

    public static void setRunnable(Runnable runnable){
        mainHandler.post(runnable);
    }
}
