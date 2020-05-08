package youtubetosound.ultility;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.util.Log;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

public class PermissionsManager {
    private static String TAG = "PermissionManager";

    public static final int EXTERNAL_READ_WRITE_REQUEST_CODE = 1234;

    public static void checkExternalStoragePermission(Activity activity, OnExternalStoragePermissionGranted callback) {
        if (ContextCompat.checkSelfPermission(activity, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            if (ContextCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {

                ActivityCompat.requestPermissions(activity,
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE,
                                Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        EXTERNAL_READ_WRITE_REQUEST_CODE);
            }
        } else {
            if (ContextCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {

                ActivityCompat.requestPermissions(activity,
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE,
                                Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        EXTERNAL_READ_WRITE_REQUEST_CODE);
            }
        }

        if (callback != null) {
            Log.d(TAG, "External R/W permission already granted. ");
            callback.permissionAlreadyGranted();
        }
    }

    public interface OnExternalStoragePermissionGranted {
        void permissionAlreadyGranted();
    }
}
