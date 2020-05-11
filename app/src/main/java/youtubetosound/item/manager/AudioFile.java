package youtubetosound.item.manager;

import android.widget.ProgressBar;

import java.io.File;

public class AudioFile {
    private String downloadURL;
    private String name;
    private String author;

    private File webm;
    private File mp3;
    private ProgressBar progressBar;

    private AudioFile(){

    }

    public AudioFile(String downloadURL, String name, String author, File webm){
        this.downloadURL = downloadURL;
        this.name = name;
        this.author = author;
        this.webm = webm;
    }

    public boolean isAvailable(){
        return (mp3 != null && mp3.exists());
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
