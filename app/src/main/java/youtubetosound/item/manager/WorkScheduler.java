package youtubetosound.item.manager;

import android.content.Context;
import android.widget.ProgressBar;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;

import youtubetosound.downloader.Downloader;

public class WorkScheduler {
    private static WorkScheduler instance;
    private ArrayList<AudioFile> schedule = new ArrayList<>();

    public static WorkScheduler getInstance() {
        if(instance == null){
            instance = new WorkScheduler();
        }
        return instance;
    }

    private WorkScheduler(){}

    public void add(AudioFile audioFile) {
        schedule.add(audioFile);
    }

    public int count() {
        return schedule.size();
    }

    public AudioFile getFile(int position) {
        return schedule.get(position);
    }

    public ArrayList<AudioFile> getFiles() {
        return schedule;
    }

    public void start(Context context) {
        Downloader downloader = Downloader.getInstance();
        for(AudioFile file: schedule){
            downloader.download(context, file);
        }
    }
}
