package youtubetosound.ui;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import youtubetosound.item.manager.AudioFile;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder> {
    private final static String TAG = "RecyclerViewAdapter";
    private Context context;
    private ArrayList<AudioFile> files;

    public RecyclerViewAdapter(Context context, ArrayList<AudioFile> files) {
        this.context = context;
        this.files = files;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_list_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        Log.d(TAG, "onBindViewHolder: called.");
        setupViewHolder(holder, files.get(position));
    }

    @Override
    public int getItemCount() {
        return files.size();
    }

    private void setupViewHolder(ViewHolder holder, AudioFile audioFile) {
        holder.setAudioName(audioFile.getName());
        holder.setAuthor(audioFile.getAuthor());
        holder.setStatus(audioFile.isAvailable());
        audioFile.setProgressBar(holder.getProgressBar());
        audioFile.setView(holder.getView());
    }

    public void updateList(ArrayList<AudioFile> files) {
        this.files = files;
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        LinearLayout parentLayout;
        TextView audioName;
        TextView author;
        TextView status;
        ProgressBar progressBar;

        ViewHolder(View itemView) {
            super(itemView);

            parentLayout = itemView.findViewById(R.id.parentLayout);
            audioName = itemView.findViewById(R.id.audioNameTextView);
            author = itemView.findViewById(R.id.audioAuthorTextView);
            status = itemView.findViewById(R.id.statusTextView);
            progressBar = itemView.findViewById(R.id.progress_bar);
        }

        public void setAudioName(String name) {
            audioName.setText(name);
        }

        public void setAuthor(String author) {
            this.author.setText(author);
        }

        public void setStatus(boolean status) {
            this.status.setText(availabilityToString(status));
        }

        public ProgressBar getProgressBar() {
            return progressBar;
        }

        private String availabilityToString(boolean available) {
            if (available) {
                return "Available";
            }
            return "Ready for download";
        }

        public View getView() {
            return itemView;
        }
    }
}
