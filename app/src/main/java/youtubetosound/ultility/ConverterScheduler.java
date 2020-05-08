package youtubetosound.ultility;

import android.app.Activity;
import android.content.Context;
import android.util.Log;

import java.io.File;
import java.util.ArrayList;

import youtubetosound.AndroidAudioConverter.AndroidAudioConverter;
import youtubetosound.AndroidAudioConverter.callback.IConvertCallback;
import youtubetosound.AndroidAudioConverter.callback.ILoadCallback;
import youtubetosound.AndroidAudioConverter.model.AudioFormat;

public class ConverterScheduler {

    private static String TAG = "Scheduler";
    private ArrayList<File> schedule = new ArrayList<>();
    private static ConverterScheduler instance;

    public static ConverterScheduler getInstance() {
        if (instance == null) {
            instance = new ConverterScheduler();
        }
        return instance;
    }

    private ConverterScheduler() {
    }

    public void add(Context context, File file) {
        Log.d(TAG, "add: ADD TO SCHEDULE");
        Log.d(TAG, "file name: " + file.getName());

        schedule.add(file);

        if(!AndroidAudioConverter.isRunning(context)){
            startSchedule(context, getNextScheduledFile());
        }
    }

    private void startSchedule(final Context context, final File file) {
        Log.d(TAG, "Start schedule: ");
        logSchedule();

        if (file.exists()) {
            Log.d(TAG, "startSchedule: File exist");
        } else {
            Log.d(TAG, "startSchedule: File doesnt exist :(");
        }

        IConvertCallback callback = new IConvertCallback() {
            @Override
            public void onSuccess(File convertedFile) {
                // So fast? Love it!
                Log.d(TAG, "Conversion successful.");
                Log.d(TAG, "File name: " + convertedFile.getName());
                Log.d(TAG, "path: " + convertedFile.getPath());

                file.delete();

                if (!schedule.isEmpty()) {
                    startSchedule(context, getNextScheduledFile());
                }
            }

            @Override
            public void onFailure(Exception error) {
                // Oops! Something went wrong
                Log.d(TAG, "onFailure: Conversion failed.");
                error.printStackTrace();
            }
        };

        AndroidAudioConverter.with(context)
                // Your current audio file
                .setFile(file)

                // Your desired audio format
                .setFormat(AudioFormat.MP3)

                // An callback to know when conversion is finished
                .setCallback(callback)

                // Start conversion
                .convert();
    }

    private File getNextScheduledFile() {
        File file = schedule.get(0);
        schedule.remove(0);
        return file;
    }

    public void setupConverter(Activity activity) {
        if (AndroidAudioConverter.isLoaded()) {
            return;
        }

        AndroidAudioConverter.load(activity, new ILoadCallback() {
            @Override
            public void onSuccess() {
                // Great!
                Log.d(TAG, "onSuccess: AndroidAudioConverter loaded");
            }

            @Override
            public void onFailure(Exception error) {
                // FFmpeg is not supported by device
                Log.d(TAG, "onFailure: AndroidAudioConverter not loaded. FFmpeg is not supported by device?");
                error.printStackTrace();
            }
        });
    }


    private void logSchedule() {
        for(File file : schedule){
            Log.d(TAG, "logSchedule: fileName" + file.getName());
        }
        Log.d(TAG, "");
    }
}
