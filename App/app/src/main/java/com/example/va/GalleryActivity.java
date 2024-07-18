package com.example.va;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

public class GalleryActivity extends AppCompatActivity {

    private ViewPager viewPager;
    private Button buttonClose;
    private List<String> imageUrls;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallery);

        viewPager = findViewById(R.id.viewPager);
        buttonClose = findViewById(R.id.buttonClose);

        imageUrls = getIntent().getStringArrayListExtra("image_urls");

        GalleryAdapter adapter = new GalleryAdapter(this, imageUrls);
        viewPager.setAdapter(adapter);

        buttonClose.setOnClickListener(v -> finish());
    }

    private static class GalleryAdapter extends androidx.viewpager.widget.PagerAdapter {
        private List<String> imageUrls;
        private LayoutInflater inflater;

        public GalleryAdapter(Context context, List<String> imageUrls) {
            this.imageUrls = imageUrls;
            this.inflater = LayoutInflater.from(context);
        }

        @Override
        public int getCount() {
            return imageUrls.size();
        }

        @Override
        public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
            return view.equals(object);
        }

        @NonNull
        @Override
        public Object instantiateItem(@NonNull ViewGroup container, int position) {
            View view = inflater.inflate(R.layout.item_image, container, false);
            ImageView imageView = view.findViewById(R.id.imageView);

            Glide.with(view.getContext()).load(imageUrls.get(position)).into(imageView);

            container.addView(view);
            return view;
        }

        @Override
        public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
            container.removeView((View) object);
        }
    }
}
