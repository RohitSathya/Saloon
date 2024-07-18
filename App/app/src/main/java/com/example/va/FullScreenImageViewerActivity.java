package com.example.va;

import android.os.Bundle;
import android.widget.ImageButton;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;
import java.util.List;

public class FullScreenImageViewerActivity extends AppCompatActivity {

    public static final String EXTRA_IMAGE_URLS = "image_urls";
    public static final String EXTRA_CURRENT_POSITION = "current_position";

    private ViewPager viewPager;
    private ImageButton closeButton;
    private List<String> imageUrls;
    private int currentPosition;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_full_screen_image_viewer);

        imageUrls = getIntent().getStringArrayListExtra(EXTRA_IMAGE_URLS);
        currentPosition = getIntent().getIntExtra(EXTRA_CURRENT_POSITION, 0);

        viewPager = findViewById(R.id.viewPager);
        closeButton = findViewById(R.id.closeButton);

        FullScreenImagePagerAdapter adapter = new FullScreenImagePagerAdapter(this, imageUrls);
        viewPager.setAdapter(adapter);
        viewPager.setCurrentItem(currentPosition);

        closeButton.setOnClickListener(v -> finish());
    }
}
