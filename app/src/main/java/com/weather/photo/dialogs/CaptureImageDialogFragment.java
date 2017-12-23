package com.weather.photo.dialogs;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.share.Sharer;
import com.facebook.share.model.ShareContent;
import com.facebook.share.model.SharePhoto;
import com.facebook.share.model.SharePhotoContent;
import com.facebook.share.widget.ShareDialog;
import com.weather.photo.R;
import com.weather.photo.api.APIClient;
import com.weather.photo.api.APIInterface;
import com.weather.photo.cache.CacheManager;
import com.weather.photo.interfaces.SharePhotoListener;
import com.weather.photo.model.WeatherModel;
import com.weather.photo.sharedpref.SharedPreferenceManager;
import com.weather.photo.utils.Utils;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * @author MohamedSaleh on 12/22/2017.
 */

public class CaptureImageDialogFragment extends DialogFragment implements LocationListener{

    private final static int REQUEST_IMAGE_CAPTURE = 1;
    private ImageView capturePhotoIcon, capturedPhotoImageView;
    private Bitmap capturedPhotoBitmap;
    private CallbackManager callbackManager;
    private LocationManager locationManager;
    private APIInterface apiInterface;
    private ProgressDialog progressDialog;
    private boolean isComeFromCamera = false;
    private boolean showProgressDialog = false;
    private SharePhotoListener sharePhotoListener;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View captureView = inflater.inflate(R.layout.dialog_capture_photo, container);
        ImageView closeCapturePhotoIcon = captureView.findViewById(R.id.closeCapturePhotoIcon);
        capturePhotoIcon = captureView.findViewById(R.id.capturePhotoIcon);
        capturedPhotoImageView = captureView.findViewById(R.id.capturedPhotoImageView);
        Button sharePhotoToFacebookButton = captureView.findViewById(R.id.sharePhotoToFacebookButton);
        callbackManager = CallbackManager.Factory.create();
        closeCapturePhotoIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        capturePhotoIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                capturePhoto();
            }
        });
        sharePhotoToFacebookButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sharePhotoOnFacebook();
                int imagesCount = SharedPreferenceManager.getInstance().getInt(Utils.IMAGES_COUNT_KEY, 0);
                CacheManager.getInstance().addBitmapToMemoryCache(1 + imagesCount, capturedPhotoBitmap);
                SharedPreferenceManager.getInstance().putInt(Utils.IMAGES_COUNT_KEY, 1 + imagesCount);
                if (sharePhotoListener != null) {
                    sharePhotoListener.onCapturedPhotoShared(capturedPhotoBitmap);
                }
                dismiss();
            }
        });
        if (Utils.weatherModel == null) {
            showProgressDialog = true;
            apiInterface = APIClient.getClient().create(APIInterface.class);
            locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
            Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            if (location == null) {
                Criteria criteria = new Criteria();
                criteria.setAccuracy(Criteria.ACCURACY_LOW);
                String bestProvider = locationManager.getBestProvider(criteria, true);
                locationManager.requestLocationUpdates(bestProvider, 0, 0, this);
            } else {
                getTemperatureInfo(location);
            }
        }
        return captureView;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (!isComeFromCamera && showProgressDialog) {
            progressDialog = new ProgressDialog(getActivity());
            progressDialog.setCancelable(false);
            progressDialog.setMessage(getString(R.string.please_wait));
            progressDialog.show();
        }
    }

    private void capturePhoto() {
        Intent capturePhotoIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (capturePhotoIntent.resolveActivity(getActivity().getPackageManager()) != null) {
            isComeFromCamera = true;
            startActivityForResult(capturePhotoIntent, REQUEST_IMAGE_CAPTURE);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == getActivity().RESULT_OK) {
            capturePhotoIcon.setVisibility(View.GONE);
            capturedPhotoBitmap = (Bitmap) data.getExtras().get("data");
            capturedPhotoImageView.setImageBitmap(addTextToImage());
            capturedPhotoImageView.setVisibility(View.VISIBLE);
        }
    }

    private void sharePhotoOnFacebook() {
        if (capturedPhotoBitmap != null) {
            SharePhoto sharePhoto = new SharePhoto.Builder()
                    .setBitmap(capturedPhotoBitmap)
                    .build();
            ShareContent shareContent = new SharePhotoContent.Builder()
                    .addPhoto(sharePhoto)
                    .build();
            ShareDialog shareDialog = new ShareDialog(getActivity());
            shareDialog.show(shareContent, ShareDialog.Mode.AUTOMATIC);
            shareDialog.registerCallback(callbackManager, new FacebookCallback<Sharer.Result>() {
                @Override
                public void onSuccess(Sharer.Result result) {
                    dismiss();
                }

                @Override
                public void onCancel() {
                    dismiss();
                }

                @Override
                public void onError(FacebookException error) {
                    dismiss();
                }
            });
        } else {
            Toast.makeText(getActivity(), R.string.take_photo_first, Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        locationManager.removeUpdates(this);
        getTemperatureInfo(location);
    }

    private void getTemperatureInfo(final Location location) {
        Call<WeatherModel> weatherCall = apiInterface.getWeatherTemperatureByLocation(location.getLatitude(),
                location.getLongitude(), Utils.WEATHER_API_KEY, "metric");
        weatherCall.enqueue(new Callback<WeatherModel>() {
            @Override
            public void onResponse(Call<WeatherModel> call, Response<WeatherModel> response) {
                Utils.weatherModel = response.body();
                List<Address> locationAddress = getGeoCoderAddress(getActivity(), location);
                Utils.weatherModel.setCountryName(locationAddress.get(0).getSubAdminArea());
                progressDialog.dismiss();
            }

            @Override
            public void onFailure(Call<WeatherModel> call, Throwable t) {
                progressDialog.dismiss();
                Toast.makeText(getActivity(), R.string.get_temp_failed, Toast.LENGTH_LONG).show();
                dismiss();
            }
        });
    }

    public List<Address> getGeoCoderAddress(Context context, Location location) {
        if (location != null) {

            Geocoder geocoder = new Geocoder(context, Locale.ENGLISH);

            try {
                /**
                 * Geocoder.getFromLocation - Returns an array of Addresses
                 * that are known to describe the area immediately surrounding the given latitude and longitude.
                 */
                List<Address> addresses = geocoder.getFromLocation(location.getLatitude(),
                        location.getLongitude(), 2);

                return addresses;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return null;
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
    }

    @Override
    public void onProviderEnabled(String provider) {
    }

    @Override
    public void onProviderDisabled(String provider) {
    }

    private Bitmap addTextToImage() {
        Bitmap newBitmap;
        Bitmap.Config config = capturedPhotoBitmap.getConfig();
        if (config == null) {
            config = Bitmap.Config.ARGB_8888;
        }
        newBitmap = Bitmap.createBitmap(capturedPhotoBitmap.getWidth(), capturedPhotoBitmap.getHeight(), config);
        Canvas canvas = new Canvas(newBitmap);
        canvas.drawBitmap(capturedPhotoBitmap, 0, 0, null);
        Paint paintText = new Paint(Paint.ANTI_ALIAS_FLAG);
        paintText.setColor(Color.WHITE);
        paintText.setTextSize(10);
        paintText.setStyle(Paint.Style.FILL);
        paintText.setShadowLayer(10f, 10f, 10f, Color.BLACK);
        int xPos = 2;
        int yPos = 20;
        canvas.drawText(Utils.weatherModel.getCountryName(), xPos, yPos, paintText);
        canvas.drawText(String.format("%.2f", Utils.weatherModel.getMainWeatherModel().getTemp()) + " ℃",
                xPos, 20 + yPos, paintText);
        canvas.drawText(Utils.weatherModel.getWeatherDescriptionModel().get(0).getDescription(),
                xPos, 40 + yPos, paintText);
        capturedPhotoBitmap = newBitmap;
        return newBitmap;
    }

    public void registerSharePhotoListener(SharePhotoListener sharePhotoListener) {
        this.sharePhotoListener = sharePhotoListener;
    }
}
