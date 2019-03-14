package mobilecrowdsourceStudy.nctu.minuku_2.AllUtils;

import android.content.Context;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.util.Log;

import java.io.File;

/**
 * Created by chiaenchiang on 14/12/2018.
 */

public class SingleMediaScanner implements MediaScannerConnection.MediaScannerConnectionClient {
    public String TAG = "VideoResult";
    private MediaScannerConnection mMs;
    private File mFile;
    public Uri muri;

    public SingleMediaScanner(Context context, File f) {
        mFile = f;
        mMs = new MediaScannerConnection(context, this);
        mMs.connect();
    }

    @Override
    public void onMediaScannerConnected() {
        Log.d(TAG,"connected");
        mMs.scanFile(mFile.getAbsolutePath(), null);
    }

    @Override
    public void onScanCompleted(String path, Uri uri) {
        muri = uri;
        Log.i(TAG,"ExternalStorage"+ " Scanned " + path + ":");
        Log.i(TAG,"ExternalStorage"+ "-> uri=" + uri);
        mMs.disconnect();
    }

}