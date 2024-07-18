package com.example.va;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import androidx.appcompat.app.AppCompatActivity;
import android.widget.ImageView;

public class LogoActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_logo);

        ImageView splashImage = findViewById(R.id.splashImage);
        splashImage.setImageResource(R.drawable.haircut);

        // Delay for 3 seconds
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(LogoActivity.this, SplashActivity.class);
                startActivity(intent);
                finish();
            }
        }, 1000); // 3000 milliseconds delay
    }
}
