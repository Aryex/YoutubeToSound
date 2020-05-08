package youtubetosound.ui;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.io.File;

import youtubetosound.model.FileManager;
import youtubetosound.ultility.ConverterScheduler;
import youtubetosound.ultility.Downloader;
import youtubetosound.ultility.PermissionsManager;

public class MainActivity extends AppCompatActivity {

    private final static String TAG = "MainActivity";
    private FileManager fileManager;
    private String youtubeLink;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final Context context = this;

        ConverterScheduler.getInstance().setupConverter(this);


        PermissionsManager.checkExternalStoragePermission(this, new PermissionsManager.OnExternalStoragePermissionGranted() {
            @Override
            public void permissionAlreadyGranted() {
                fileManager = FileManager.getInstance();
            }
        });


//        File file = new File(fileManager.getHOME_DIRECTORY() + "/Drake - Toosie Slide.webm");
//
//        if(!file.exists()){
//            Toast.makeText(context, "File does not exist", Toast.LENGTH_SHORT).show();
//        }else{
//            Toast.makeText(context, "Yay!", Toast.LENGTH_SHORT).show();
//        }

        EditText editText = findViewById(R.id.edit_text_URL);
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                changeURL(s.toString());
            }
        });

        Button btn = findViewById(R.id.mainButton);
        btn.setText("Download");
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Downloader.Download(context, youtubeLink);
            }
        });

    }

    private void changeURL(String string) {
        this.youtubeLink = string;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case PermissionsManager.EXTERNAL_READ_WRITE_REQUEST_CODE: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    fileManager = FileManager.getInstance();
                }
            }
        }
    }
}
