package com.weather.photo.activities;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.weather.photo.R;
import com.weather.photo.adapter.WeatherPhotosAdapter;
import com.weather.photo.cache.CacheManager;
import com.weather.photo.dialogs.CaptureImageDialogFragment;
import com.weather.photo.dialogs.ImagePreviewDialogFragment;
import com.weather.photo.interfaces.SharePhotoListener;
import com.weather.photo.interfaces.WeatherPhotoClickListener;
import com.weather.photo.sharedpref.SharedPreferenceManager;
import com.weather.photo.utils.Utils;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Mohamed Saleh
 */

public class MainActivity extends AppCompatActivity implements SharePhotoListener, WeatherPhotoClickListener{

    private final static int REQUEST_PERMISSIONS_CODE = 2;
    private TextView noPreviousPhotosTextView;
    private RecyclerView previousWeatherPhotosRecyclerView;
    private List<Bitmap> weatherBitmaps;
    private boolean isShowCaptureImageDialog = false;
    private boolean isImagePreviewShown = false;
    private ImagePreviewDialogFragment previewDialogFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        FloatingActionButton addNewPhotoButton = (FloatingActionButton) findViewById(R.id.addNewPhotoButton);
        addNewPhotoButton.setBackgroundTintList(ColorStateList.valueOf(Color.BLUE));
        addNewPhotoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkForPermissionsAndStartCapturePhoto(view);
            }
        });
        noPreviousPhotosTextView = findViewById(R.id.noPreviousPhotosTextView);
        previousWeatherPhotosRecyclerView = findViewById(R.id.previousWeatherPhotosRecyclerView);
        previousWeatherPhotosRecyclerView.addOnItemTouchListener(new RecyclerView.OnItemTouchListener() {
            @Override
            public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {
                return false;
            }

            @Override
            public void onTouchEvent(RecyclerView rv, MotionEvent e) {

            }

            @Override
            public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {

            }
        });
        new GetDataFromMemoryTask().execute();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (isShowCaptureImageDialog) {
            isShowCaptureImageDialog = false;
            showCaptureDialog();
        }
    }

    private void checkForPermissionsAndStartCapturePhoto(View view) {
        if (Utils.isPermissionsGranted(this)) {
            startCapturePhoto();
        } else {
            if (Utils.showPermissionsExplanation(this)) {
                Snackbar.make(view, getString(R.string.permissions_explanation), Snackbar.LENGTH_LONG).show();
            } else {
                ActivityCompat.requestPermissions(this, new String[] {Utils.CAMERA_PERMISSION,
                        Utils.LOCATION_PERMISSION}, REQUEST_PERMISSIONS_CODE);
            }
        }
    }

    private void startCapturePhoto() {
        if (isGpsEnabled()) {
            showCaptureDialog();
        } else {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage(R.string.enable_gps_explanation)
                    .setCancelable(false)
                    .setPositiveButton(R.string.change_from_settings, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                            dialog.cancel();
                        }
                    })
                    .setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.cancel();
                        }
                    });
            AlertDialog alert = builder.create();
            alert.show();
        }
    }

    private void showCaptureDialog() {
        CaptureImageDialogFragment captureImageDialogFragment = new CaptureImageDialogFragment();
        captureImageDialogFragment.registerSharePhotoListener(this);
        captureImageDialogFragment.show(getSupportFragmentManager(), "");
    }

    private boolean isGpsEnabled() {
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQUEST_PERMISSIONS_CODE && grantResults[0] == PackageManager.PERMISSION_GRANTED &&
                grantResults[1] == PackageManager.PERMISSION_GRANTED) {
            isShowCaptureImageDialog = true;
        }
    }

    @Override
    public void onCapturedPhotoShared(Bitmap bitmap) {
        if (weatherBitmaps.size() == 0) {
            weatherBitmaps.add(bitmap);
            showImagesList();
        } else {
            weatherBitmaps.add(bitmap);
        }
    }

    @Override
    public void onImageClickListener(int position) {
        isImagePreviewShown = true;
        previewDialogFragment = new ImagePreviewDialogFragment(weatherBitmaps.get(position));
        previewDialogFragment.show(getSupportFragmentManager(), "preview");
    }

    @Override
    public void onBackPressed() {
        if (isImagePreviewShown) {
            previewDialogFragment.dismiss();
            isImagePreviewShown = false;
            return;
        }
        super.onBackPressed();
    }

    private class GetDataFromMemoryTask extends AsyncTask<Integer, Void, Integer> {
        ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(MainActivity.this);
            progressDialog.setCancelable(false);
            progressDialog.setMessage(getString(R.string.please_wait));
            progressDialog.show();
        }

        @Override
        protected Integer doInBackground(Integer... params) {
            int imagesCount = SharedPreferenceManager.getInstance().getInt(Utils.IMAGES_COUNT_KEY, 0);
            weatherBitmaps = new ArrayList<>();
            for (int i = 1; i <= imagesCount; i++) {
                weatherBitmaps.add(CacheManager.getInstance().getBitmapFromMemCache(i));
            }
            return imagesCount;
        }

        @Override
        protected void onPostExecute(Integer integer) {
            super.onPostExecute(integer);
            if (integer == 0) {
                noPreviousPhotosTextView.setVisibility(View.VISIBLE);
                previousWeatherPhotosRecyclerView.setVisibility(View.GONE);
            } else {
                showImagesList();
            }
            progressDialog.cancel();
        }
    }

    private void showImagesList() {
        noPreviousPhotosTextView.setVisibility(View.GONE);
        previousWeatherPhotosRecyclerView.setVisibility(View.VISIBLE);
        previousWeatherPhotosRecyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        previousWeatherPhotosRecyclerView.setLayoutManager(mLayoutManager);
        previousWeatherPhotosRecyclerView.setItemAnimator(new DefaultItemAnimator());
        WeatherPhotosAdapter weatherPhotosAdapter = new WeatherPhotosAdapter(weatherBitmaps, this);
        previousWeatherPhotosRecyclerView.setAdapter(weatherPhotosAdapter);
    }
}
