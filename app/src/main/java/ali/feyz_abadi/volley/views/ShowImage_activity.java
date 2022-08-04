package ali.feyz_abadi.volley.views;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

import ali.feyz_abadi.volley.R;
import ali.feyz_abadi.volley.app.app;

public class ShowImage_activity extends AppCompatActivity implements View.OnClickListener {

    ImageView imageView_back, imageView;

    String link;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_image);

        init();
        onClick();
    }

    private void init() {
        imageView_back = findViewById(R.id.imageView_back);
        imageView = findViewById(R.id.imageView);

        link= getIntent().getStringExtra("link");

        Glide.with(this).load(app.BASE_URL+link).into(imageView);
    }

    private void onClick() {
        imageView_back.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v == imageView_back) {
            onBackPressed();
        }
    }

    @Override
    public void onBackPressed() {
        supportFinishAfterTransition();
        super.onBackPressed();
    }
}