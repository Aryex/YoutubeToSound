package youtubetosound.ui;

import androidx.annotation.DrawableRes;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
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

        Intent intent = getIntent();
        if(intent != null){
            String[] ytURL = intent.getStringArrayExtra("URL");
            if(ytURL != null){
                ListView listView = findViewById(R.id.list_item);

                ArrayAdapter adapter = new ArrayAdapter<String>(
                        this,     // Context for the activity.
                        R.layout.activity_add,
                        R.id.list_item,// Layout to use (create)
                        ytURL);
               // ArrayAdapter adapter = new ArrayAdapter<String>(this,R.layout.activity_add, ytURL);
                listView.setAdapter(adapter);
            }
        }


    }

    private void setupToolbarButton(@DrawableRes int resId, View.OnClickListener actionOnClick) {
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back);
        toolbar.setNavigationOnClickListener(actionOnClick);
    }
}
