package com.example.imf_demo;

import android.Manifest;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityOptionsCompat;

import android.util.Log;
import android.view.View;

import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.image.measure.editor.MeasureImageActivity;
import org.image.measure.editor.db.TemplateDB;
import org.image.measure.editor.view.MeasurementData;
import org.image.measure.editor.view.TemplateDownloadListener;
import org.image.measure.editor.view.TemplateDownloader;

import java.io.File;
import java.net.URISyntaxException;

public class MainActivity extends AppCompatActivity {

    private static final int MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 123;
    private static final String TAG = "MainActivity";
    ImageView currentImageView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);



        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });


        if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (shouldShowRequestPermissionRationale(
                    Manifest.permission.READ_EXTERNAL_STORAGE)) {
                // Explain to the user why we need to read the contacts
            }

            requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                    MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);

            // MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE is an
            // app-defined int constant that should be quite unique


        }
    }

    private void importAvailableTemplates() {
        TemplateDB.getInstance(this).importTemplates(null);
    }

    private void startPickAndMeasure() {
        Intent in = new Intent(MainActivity.this, org.image.measure.gallery.activities.LFMainActivity.class);
        startActivityForResult(in, REQ_CODE_PICK_SCAN_MEASURE);
    }
    /*
    private void enterImageMeasurement() {
        Intent in = new Intent(MainActivity.this, org.image.measure.editor.MeasureImageActivity.class);
        startActivity(in);
    }

     */
    private static final int REQ_CODE_PICK_SCAN_MEASURE = 198;
    private static final int REQ_CODE_SCAN_MEASURE = 199;
    public void startScanAndMeasureOnSingleMedia() {
        final View view = findViewById(android.R.id.content).getRootView();
        Intent intent = new Intent(MainActivity.this, org.image.measure.gallery.activities.SingleMediaActivity.class);
        if (currentFileUri != null) {
            intent.setData(currentFileUri);
        }
        if (currentMeasurementDataJson != null) {
            intent.putExtra(MeasureImageActivity.KEY_MEASURE_RESULT, currentMeasurementDataJson);
        }
        view.setTransitionName("scan a single media");
        ActivityOptionsCompat options = ActivityOptionsCompat.
                makeSceneTransitionAnimation(MainActivity.this, view, view.getTransitionName());
        intent.setAction("android.intent.action.pagerAlbumMedia");
        startActivityForResult(intent, REQ_CODE_SCAN_MEASURE);
    }

    private void downloadTemplates() {
        final View view = findViewById(android.R.id.content).getRootView();
        new TemplateDownloader(this).start("develop", new TemplateDownloadListener() {
            @Override
            public void onTemplateDownloaded(TemplateDB.Template template) {
                Snackbar.make(view, "Downloaded template: " + template.relativePath(), Snackbar.LENGTH_SHORT)
                        .setAction("Action", null).show();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement

        if (id == R.id.action_open_local_picture) {
            this.showFileChooser();
            return true;
        }
        else if (id == R.id.action_download_templates) {
            downloadTemplates();
            return true;
        }
        else if (id == R.id.action_import_templates) {
            importAvailableTemplates();
            return true;
        }
        else if (id == R.id.action_measure_new) {
            startPickAndMeasure();
            return true;
        }
        else if (id == R.id.action_measure_existing) {
            startScanAndMeasureOnSingleMedia();
        }
        return super.onOptionsItemSelected(item);
    }


    private static final int FILE_SELECT_CODE = 0;

    public void showFileChooser() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);

        // Update with mime types
        intent.setType("*/*");

        // Update with additional mime types here using a String[].
        //intent.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes);

        // Only pick openable and local files. Theoretically we could pull files from google drive
        // or other applications that have networked files, but that's unnecessary for this example.
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.putExtra(Intent.EXTRA_LOCAL_ONLY, true);

        // REQUEST_CODE = <some-integer>
        startActivityForResult(intent, FILE_SELECT_CODE);
    }

    public Uri currentFileUri = null;
    public String currentMeasurementDataJson = "";
    public MeasurementData currentMeasurementData = null;
    public String currentMeasuredPictureSavePath = "";
    public String currentMeasurementComments = "";
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case FILE_SELECT_CODE:
                if (resultCode == RESULT_OK) {
                    // Get the Uri of the selected file
                    Uri uri = data.getData();
                    Log.i(TAG, "File Uri: " + uri.toString());
                    currentFileUri = uri;
                    // Get the path
                    try {
                        String currentFilePath = getPath(this, uri);
                        Log.i(TAG, "File Path: " + currentFilePath);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    if (currentFileUri != null){
                        displayImageFromUri(currentFileUri);
                    }
                    // Get the file instance
                    // File file = new File(path);
                    // Initiate the upload
                }
                break;
            case REQ_CODE_PICK_SCAN_MEASURE:
            case REQ_CODE_SCAN_MEASURE: {
                if (resultCode == RESULT_OK) {
                    currentFileUri = data.getParcelableExtra(MeasureImageActivity.KEY_SCANNED_RAW_URI);
                    /*
                    String rawUri = data.getExtras().getString(MeasureImageActivity.KEY_SCANNED_RAW_URI);
                    if (rawUri != null) {
                        currentFileUri = Uri.parse(rawUri);
                    }

                     */
                    currentMeasurementDataJson = data.getExtras().getString(MeasureImageActivity.KEY_MEASURE_RESULT);
                    if (currentMeasurementDataJson != null && !currentMeasurementDataJson.isEmpty()) {
                        currentMeasurementData = MeasurementData.decode(currentMeasurementDataJson);
                    }
                    currentMeasuredPictureSavePath = data.getExtras().getString(MeasureImageActivity.KEY_MEASURED_PIC_SAVE_PATH, "");
                    currentMeasurementComments = data.getExtras().getString(MeasureImageActivity.KEY_COMMENTS);
                    if (currentMeasurementData != null) {
                        currentRulerInfos = currentMeasurementData.rulerInfos;
                        currentMatchedTemplates = currentMeasurementData.matchedTemplates;
                    }
                    Log.i(TAG, "onActivityResult, MeasurementData: " + currentMeasurementData
                            + ", measuredPicture: " + currentMeasuredPictureSavePath
                            + ", raw file uri: " + currentFileUri
                            + ", comments:" + currentMeasurementComments);
                    this.refresh();
                }
                break;
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    public String getPath(Context context, Uri uri) throws URISyntaxException {
        if ("content".equalsIgnoreCase(uri.getScheme())) {
            String[] projection = { "_data" };
            Cursor cursor = null;

            try {
                cursor = context.getContentResolver().query(uri, projection, null, null, null);
                int column_index = cursor.getColumnIndexOrThrow("_data");
                if (cursor.moveToFirst()) {
                    return cursor.getString(column_index);
                }
            } catch (Exception e) {
                // Eat it
            }
        }
        else if ("file".equalsIgnoreCase(uri.getScheme())) {
            return uri.getPath();
        }

        return null;
    }

    void displayImageFromUri(Uri uri) {
        if (uri == null) {
            return;
        }
        currentImageView = (ImageView) findViewById(R.id.imageView2);
        if (currentImageView != null) {
            currentImageView.setImageURI(uri);
        }
    }
    void displayMeasuredImageFile() {
        if (currentMeasuredPictureSavePath.isEmpty()) {
            return;
        }
        File imgFile = new File(currentMeasuredPictureSavePath);  // e.g. "/sdcard/Images/test_image.jpg"
        Log.i("MainActivity", "displayMeasuredImageFile: measuredPicture: " + currentMeasuredPictureSavePath + ", absolutePath: " + imgFile.getAbsolutePath());
        if(imgFile.exists()){

            Bitmap myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());

            ImageView view = (ImageView) findViewById(R.id.imageView3);
            if (view != null) {
                view.setImageBitmap(myBitmap);
            }
        }
    }

    public void displayMeasurementResult() {
        if (currentRulerInfos == null) {
            return;
        }
        int[] textViewIds = {
                R.id.textViewMeasureData1,
                R.id.textViewMeasureData2,
                R.id.textViewMeasureData3,
                R.id.textViewMeasureData4,
                R.id.textViewMeasureData5,
                R.id.textViewMeasureData6
        };
        for (int i = 0; i < textViewIds.length; ++i) {
            TextView view = (TextView) findViewById(textViewIds[i]);
            if (view == null) {
                break;
            }
            if (currentRulerInfos == null || i >= currentRulerInfos.length) {
                view.setText("");
                continue;
            }
            view.setText(currentRulerInfos[i].title + ":" + currentRulerInfos[i].value);
        }
    }
    MeasurementData.TemplateMatchInfo[] currentMatchedTemplates = null;
    MeasurementData.RulerInfo[] currentRulerInfos = null;
    public void displayTemplateMatchingResult() {
        if (currentMatchedTemplates == null) {
            return;
        }
        int[] textViewIds = {
                R.id.textViewMatchedTemplate1,
                R.id.textViewMatchedTemplate2
        };
        for (int i = 0; i < textViewIds.length; ++i) {
            TextView view = (TextView) findViewById(textViewIds[i]);
            if (view == null) {
                break;
            }
            if (currentMatchedTemplates == null || i >= currentMatchedTemplates.length) {
                view.setText("");
                continue;
            }
            TemplateDB.Template templateInfo = TemplateDB.getInstance(this).getTemplateById(currentMatchedTemplates[i].id);
            view.setText("模版" + (i+1) + "\n" + templateInfo.toString());
        }
    }
    public void refresh() {
        this.displayImageFromUri(this.currentFileUri);
        this.displayMeasurementResult();
        this.displayTemplateMatchingResult();
        this.displayMeasuredImageFile();
    }
}