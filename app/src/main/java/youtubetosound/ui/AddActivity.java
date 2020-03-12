package youtubetosound.ui;

import androidx.annotation.DrawableRes;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class AddActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private static final String INTERNAL_CALL = "Intent created from a internal call";

    public static Intent makeLaunchIntent(Context context) {
        return new Intent(context, AddActivity.class).setAction(INTERNAL_CALL);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add);

        toolbar = findViewById(R.id.mainToolbar);
        Intent intent = getIntent();
        String URL = intent.getStringExtra(Intent.EXTRA_TEXT);

        setupToolbarButton(R.drawable.ic_arrow_back, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        TextView tv = findViewById(R.id.addActivityTextView);
        tv.setText(URL);
        TextView tv2 = findViewById(R.id.textView2);
        tv2.setText(intent.getAction());
    }

    private void setupToolbarButton(@DrawableRes int resId, View.OnClickListener actionOnClick) {
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back);
        toolbar.setNavigationOnClickListener(actionOnClick);
    }
}
