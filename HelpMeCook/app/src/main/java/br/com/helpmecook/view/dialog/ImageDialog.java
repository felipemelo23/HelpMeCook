package br.com.helpmecook.view.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;

import br.com.helpmecook.R;

/**
 * Created by Kandarpa on 18/06/2015.
 */
public class ImageDialog extends Dialog implements android.view.View.OnClickListener {

    public Activity activity;
    public Dialog dialog;
    private ImageView ivFullBannerImage;
    Bitmap image;

    public ImageDialog(Activity activity, Bitmap image) {
        super(activity);
        this.activity = activity;
        this.image = image;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_image);
        ivFullBannerImage = (ImageView) findViewById(R.id.fullBannerImage);
        ivFullBannerImage.setImageBitmap(image);

        ivFullBannerImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

    }

    @Override
    public void onClick(View view) {
        dismiss();
    }
}
