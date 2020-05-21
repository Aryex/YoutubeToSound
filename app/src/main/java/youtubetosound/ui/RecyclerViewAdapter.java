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

import youtubetosound.model.Card;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder> {
    private final static String TAG = "RecyclerViewAdapter";
    private Context context;
    private ArrayList<Card> files;

    public RecyclerViewAdapter(Context context, ArrayList<Card> files) {
        this.context = context;
        this.files = files;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_card, parent, false);
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

    private void setupViewHolder(ViewHolder holder, Card card) {

        switch (card.getStatus()){
            case READY_FOR_DOWNLOAD:
                holder.showInfo();
                holder.setAudioName(card.getName());
                holder.setAuthor(card.getAuthor());
                holder.setStatus(card.getStatusToString());
                card.setProgressBar(holder.getProgressBar());
                card.setView(holder.getView());
                return;
            default:
                return;
        }

    }

    public void updateList(ArrayList<Card> files) {
        this.files = files;
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        LinearLayout parentLayout;
        LinearLayout infoLayout;
        LinearLayout loadingLayout;

        TextView audioName;
        TextView author;
        TextView status;
        ProgressBar progressBar;

        ViewHolder(View itemView) {
            super(itemView);

            parentLayout = itemView.findViewById(R.id.parentLayout);
            infoLayout = itemView.findViewById(R.id.linearLayoutCardInfo);
            loadingLayout = itemView.findViewById(R.id.linearLayoutCardLoading);

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

        public void setStatus(String status) {
            this.status.setText(status);
        }

        public ProgressBar getProgressBar() {
            return progressBar;
        }

        public View getView() {
            return itemView;
        }

        public void showInfo() {
            loadingLayout.setVisibility(View.GONE);
            infoLayout.setVisibility(View.VISIBLE);
        }
    }
}
