package youtubetosound.item.manager;

import android.content.Context;
import android.media.MediaMetadataRetriever;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;

import java.io.File;
import java.util.ArrayList;

import youtubetosound.downloader.Downloader;


public class AudioFileManager {
    private static final String TAG = "AudioFileManager";
    private static final String REL_PATH = "Podcasterize";

    private static String absPath;
    private static File directory;

    private ArrayList<AudioFile> audioFiles = new ArrayList<>();
    private static AudioFileManager instance;

    public static AudioFileManager getInstance() {
        if (instance == null) {
            instance = new AudioFileManager();
        }
        return instance;
    }

    private AudioFileManager(){
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
        for(AudioFile file : audioFiles){
            if(file.isAvailable()){
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

    public void add(AudioFile file){
        audioFiles.add(file);
    }

    public void startDownloads(Context context, Handler handler) {
        Downloader downloader = Downloader.getInstance();
        for(AudioFile file: audioFiles){
            if(!file.isAvailable()){
                Log.d(TAG, "startDownloads: file " + file.getWebm().getName() + " is not available.");
                Log.d(TAG, "startDownloads: starting download...");
                downloader.download(context, handler, file);
            }
        }
    }

    public int count() {
        return audioFiles.size();
    }

    public AudioFile getFile(int position) {
        return audioFiles.get(position);
    }

    public ArrayList<AudioFile> getFiles() {
        return audioFiles;
    }

    public String getRelativePath(){
        return directory.getName();
    }
}
