package youtubetosound.ui;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

public class AudioPlayerActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private static final String INTERNAL_CALL = "Intent created from a internal call";
    private static final String INTENT_EXTRA_FILEPATH_KEY = "filepath";

    public static Intent makeLaunchIntent(Context context, String filePath) {
        return new Intent(context, AudioPlayerActivity.class).putExtra(INTENT_EXTRA_FILEPATH_KEY, filePath);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_audio_player);

        TextView textView = findViewById(R.id.textView);
        textView.setText(getIntent().getStringExtra(INTENT_EXTRA_FILEPATH_KEY));

    }

}
