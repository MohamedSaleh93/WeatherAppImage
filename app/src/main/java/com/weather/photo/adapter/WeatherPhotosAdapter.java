package com.weather.photo.adapter;

import android.graphics.Bitmap;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.weather.photo.R;
import com.weather.photo.interfaces.WeatherPhotoClickListener;

import java.util.List;

/**
 * @author MohamedSaleh on 12/23/2017.
 */

public class WeatherPhotosAdapter extends RecyclerView.Adapter<WeatherPhotosAdapter.ViewHolder>{

    private List<Bitmap> weatherBitmaps;
    private WeatherPhotoClickListener weatherPhotoClickListener;

    public WeatherPhotosAdapter(List<Bitmap> weatherBitmaps) {
        this.weatherBitmaps = weatherBitmaps;
    }

    public WeatherPhotosAdapter(List<Bitmap> weatherBitmaps, WeatherPhotoClickListener weatherPhotoClickListener) {
        this.weatherBitmaps = weatherBitmaps;
        this.weatherPhotoClickListener = weatherPhotoClickListener;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public ImageView weatherImageView;
        public ViewHolder(View v) {
            super(v);
            weatherImageView = v.findViewById(R.id.photoWeatherImageView);
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.weather_photos_list_item, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        holder.weatherImageView.setImageBitmap(weatherBitmaps.get(position));
        holder.weatherImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (weatherPhotoClickListener != null) {
                    weatherPhotoClickListener.onImageClickListener(position);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return weatherBitmaps.size();
    }
}
