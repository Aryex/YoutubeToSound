package youtubetosound.ui;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.ItemTouchHelper;
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
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;

import at.huber.youtubeExtractor.VideoMeta;
import at.huber.youtubeExtractor.YouTubeExtractor;
import at.huber.youtubeExtractor.YtFile;
import youtubetosound.model.Card;
import youtubetosound.model.Manager;
import youtubetosound.ultility.PermissionsManager;

public class MainActivity extends AppCompatActivity {

    private final static String TAG = "MainActivity";
    private String youtubeLink = "";
    private static RecyclerViewAdapter recyclerViewAdapter;
    private static Handler mainHandler = new Handler();
    private Manager manager;

    //TODO: convert cards to be a singular entity each controlling its view.

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final Context context = this;

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

        manager = Manager.getInstance();

        setupInputText();

        Button btn = findViewById(R.id.addButton);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (isYoutubeLink(youtubeLink)) {
                    Card newCard = makeNewCard();
                    newCard.setStatus(Card.Status.EXTRACTING);
                    extractUrl(context, youtubeLink, newCard, recyclerViewAdapter);
                } else {
                    Toast.makeText(context, "Invalid link!", Toast.LENGTH_SHORT).show();
                }
            }
        });

        btn = findViewById(R.id.startButton);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Manager.getInstance().startDownloads(context, mainHandler);
            }
        });

        ImageView img = findViewById(R.id.action_image);
        img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = getPackageManager().getLaunchIntentForPackage("com.google.android.youtube");
                context.startActivity(intent);
            }
        });

    }

    private Card makeNewCard() {
        Card newCard = manager.add(new Card());
        recyclerViewAdapter.notifyItemInserted(manager.getSize() - 1);
        return newCard;
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
        recyclerViewAdapter = new RecyclerViewAdapter(this, Manager.getInstance().getCards());
        recyclerView.setAdapter(recyclerViewAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        new ItemTouchHelper(new ItemTouchHelperCallBack(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT))
                .attachToRecyclerView(recyclerView);
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

    private static void extractUrl(final Context context, final String youtubeLink, final Card card, final RecyclerViewAdapter adapter) {
        new YouTubeExtractor(context) {
            @Override
            public void onExtractionComplete(SparseArray<YtFile> ytFiles, VideoMeta vMeta) {

                if (ytFiles == null) {
                    Log.d(TAG, "onExtractionComplete: NULL");
                    extractUrl(context, youtubeLink, card, adapter);
                    return;
                }

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

                        card.setDownloadURL(ytFile.getUrl())
                                .setName(videoTitle)
                                .setAuthor(vMeta.getAuthor())
                                .setStatus(Card.Status.READY_FOR_DOWNLOAD);

//                        Manager.getInstance().add(new Card(ytFile.getUrl(),
//                                videoTitle,
//                                vMeta.getAuthor(),
//                                new File(FileManager.getInstance().getAbsolutePath() + "/" +filename)));
                        adapter.updateList(Manager.getInstance().getCards());
                        adapter.notifyDataSetChanged();
                        //MainActivity.setRunnable(adapter);
                        //((MainActivity)context).runOnUiThread(new);
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
        Manager.getInstance().clear();
    }

    public static RecyclerViewAdapter getRecyclerViewAdapter() {
        return recyclerViewAdapter;
    }

    public static void setRunnable(Runnable runnable) {
        mainHandler.post(runnable);
    }

    private class ItemTouchHelperCallBack extends ItemTouchHelper.SimpleCallback {

        private int positionRemoved;
        private Card cardRemoved;

        public ItemTouchHelperCallBack(int dragDirs, int swipeDirs) {
            super(dragDirs, swipeDirs);
        }

        @Override
        public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
            return false;
        }

        @Override
        public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
            positionRemoved = viewHolder.getAdapterPosition();
            cardRemoved = manager.get(positionRemoved);

            manager.remove(positionRemoved);
            recyclerViewAdapter.notifyItemRemoved(positionRemoved);

            showUndoSnackbar();
        }

        private void showUndoSnackbar() {
            View view = findViewById(R.id.linearLayoutMainActivity);
            Snackbar snackbar = Snackbar.make(view, "Undo",
                    Snackbar.LENGTH_LONG);
            snackbar.setAction("Undo", v -> undoDelete());
            snackbar.show();
        }

        private void undoDelete() {
            manager.add(cardRemoved, positionRemoved);
            recyclerViewAdapter.notifyItemInserted(positionRemoved);
        }
    }
}
