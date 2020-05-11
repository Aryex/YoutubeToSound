package youtubetosound.item.manager;

import android.media.MediaMetadataRetriever;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.util.ArrayList;


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

    private void updateAvailableAudio() {
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
