package com.weather.photo.dialogs;

import android.app.Dialog;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;

import com.weather.photo.R;

/**
 * Created by MohamedSaleh on 12/23/2017.
 */

public class ImagePreviewDialogFragment extends DialogFragment {

    private Bitmap imageBitmap;

    public ImagePreviewDialogFragment(@NonNull Bitmap bitmap) {
        this.imageBitmap = bitmap;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = new Dialog(getActivity());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_image_preview);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        ImageView imagePreview = dialog.findViewById(R.id.imagePreview);
        imagePreview.setImageBitmap(imageBitmap);
        return dialog;
    }
}
