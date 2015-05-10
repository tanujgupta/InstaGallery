package com.tanujgupta.bellytest.instagallery;

import android.app.Activity;
import android.os.Bundle;

import com.loopj.android.image.SmartImageView;

public class DetailsActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.details_activity);

        String url = getIntent().getExtras().getString("url");

        SmartImageView imageView = (SmartImageView) findViewById(R.id.image);
        imageView.setImageUrl(url);
    }
}
