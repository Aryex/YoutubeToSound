package youtubetosound.item.manager;

import android.view.View;
import android.widget.ProgressBar;

import java.io.File;

public class AudioFile {
    private String downloadURL;
    private String name;
    private String author;

    private File webm;
    private ProgressBar progressBar;
    private View view;

    private AudioFile(){

    }

    public AudioFile(String downloadURL, String name, String author, File webm){
        this.downloadURL = downloadURL;
        this.name = name;
        this.author = author;
        this.webm = webm;
    }

    public boolean isAvailable(){
        return (webm != null && webm.exists());
    }

    public String getDownloadURL() {
        return downloadURL;
    }

    public AudioFile setDownloadURL(String downloadURL) {
        this.downloadURL = downloadURL;
        return this;
    }

    public String getName() {
        return name;
    }

    public AudioFile setName(String name) {
        this.name = name;
        return this;
    }

    public String getAuthor() {
        return author;
    }

    public AudioFile setAuthor(String author) {
        this.author = author;
        return this;
    }

    public View getView() {
        return view;
    }

    public void setView(View view) {
        this.view = view;
    }

    public String getFileNameWebm() {
        return webm.getName();
    }

    public void setProgressBar(ProgressBar progressBar) {
        this.progressBar = progressBar;
    }

    public ProgressBar getProgressBar(){
        return progressBar;
    }

    public File getWebm() {
        return webm;
    }
}
