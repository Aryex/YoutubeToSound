package youtubetosound.model;

import android.content.Context;
import android.media.MediaMetadataRetriever;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;

import java.io.File;
import java.util.ArrayList;

import youtubetosound.downloader.Downloader;


public class Manager {
    private static final String TAG = "AudioFileManager";
    private static final String REL_PATH = "Podcasterize";

    private static String absPath;
    private static File directory;

    private ArrayList<Card> cards = new ArrayList<>();
    private static Manager instance;

    public static Manager getInstance() {
        if (instance == null) {
            instance = new Manager();
        }
        return instance;
    }

    private Manager(){
        absPath = Environment.getExternalStorageDirectory().toString() + "/" + REL_PATH;
        directory = new File(absPath);

        if(!directory.exists()){
            Log.d(TAG, "AudioFileManager: directory does not exist!");
            try {
                directory.mkdir();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        updateAvailableAudio();
    }

    public void clear(){
        for(Card file : cards){
            if(file.webmAvailable()){
                Log.d(TAG, "clear: file " + file.getWebm().getName() + " is available.");
                Log.d(TAG, "clear: deleting...");
                file.getWebm().delete();
            }
        }
    }

    public void updateAvailableAudio() {
        MediaMetadataRetriever mediaMetadataRetriever = new MediaMetadataRetriever();
        for(File file:directory.listFiles()){
            mediaMetadataRetriever.setDataSource(file.getPath());
            Log.d(TAG, "updateAvailableAudio: " + file.getName());
            Log.d(TAG, "updateAvailableAudio: ID3 Tag: " + mediaMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST));
        }
    }

    public Card add(Card card){
        cards.add(card);
        return card;
    }

    public Card add(Card card, int position){
        cards.add(position, card);
        return card;
    }

    public void startDownloads(Context context, Handler handler) {
        for(Card card: cards){
            if(!card.webmAvailable()){
                Log.d(TAG, "startDownloads: card " + card.getWebm().getName() + " is not available.");
                Log.d(TAG, "startDownloads: starting download...");
                card.download(context, handler);
            }
        }
    }

    public int count() {
        return cards.size();
    }

    public Card get(int position) {
        return cards.get(position);
    }

    public ArrayList<Card> getCards() {
        return cards;
    }

    public String getRelativePath(){
        return directory.getName();
    }

    public int getSize() {
        return cards.size();
    }

    public void remove(int position) {
        cards.remove(position);
    }
}
