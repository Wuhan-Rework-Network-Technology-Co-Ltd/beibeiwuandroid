package xin.banghua.beiyuan.Main4Branch;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import xin.banghua.beiyuan.R;

public class DouyinActivity extends AppCompatActivity {

    ImageView back_img;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_douyin);

        getWindow().setStatusBarColor(getResources().getColor(R.color.text_color_1,null));
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_VISIBLE);

        back_img = findViewById(R.id.back_img);
        back_img.setOnClickListener(v -> {
            onBackPressed();
        });
    }
}