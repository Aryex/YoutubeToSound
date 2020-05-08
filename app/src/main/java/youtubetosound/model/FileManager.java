package youtubetosound.model;

import android.os.Environment;
import android.util.Log;

import java.io.File;

public class FileManager {

    private static FileManager instance;
    private static final String HOME_REL_PATH = "Podcasterize";
    private final String HOME_ABS_PATH;
    private final File directory;
    private final String TAG = "FileManager";

    public static FileManager getInstance() {
        if (instance == null) {
            instance = new FileManager();
        }
        return instance;
    }

    private FileManager() {
        HOME_ABS_PATH = Environment.getExternalStorageDirectory().toString() + "/" + HOME_REL_PATH;
        Log.d("Files", "Path: " + HOME_ABS_PATH);
        directory = new File(HOME_ABS_PATH);

        try {
            directory.mkdir();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void listDir(){
        Log.d(TAG, "Printing directories: ");
        File[] files = directory.listFiles();

        Log.d(TAG, "Size: " + files.length);

        for (File file : files) {
            Log.d(TAG, "FileName:" + file.getName());
        }
    }

    public String getAbsolutePath(){
        return directory.getAbsolutePath();
    }

    public String getRelativePath(){
        return directory.getName();
    }
}
