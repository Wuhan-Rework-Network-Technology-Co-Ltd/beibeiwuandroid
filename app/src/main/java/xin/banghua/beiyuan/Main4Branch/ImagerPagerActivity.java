package xin.banghua.beiyuan.Main4Branch;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.daimajia.slider.library.Animations.DescriptionAnimation;
import com.daimajia.slider.library.SliderLayout;
import com.daimajia.slider.library.SliderTypes.BaseSliderView;
import com.daimajia.slider.library.SliderTypes.TextSliderView;

import xin.banghua.beiyuan.Adapter.LuntanList;
import xin.banghua.beiyuan.R;
import xin.banghua.beiyuan.util.ConstantValue;


public class ImagerPagerActivity extends AppCompatActivity {
    SliderLayout sliderShow;

    LuntanList luntanList;

    TextView back_tv;

    int image_index = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_imager_pager);

        back_tv = findViewById(R.id.back_tv);
        back_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        luntanList = (LuntanList) getIntent().getSerializableExtra("luntanlist");
        image_index = getIntent().getIntExtra("image_index",0);

        sliderShow = findViewById(R.id.image_pager_slider);

        for (int i=0;i<luntanList.getPostpicture().length;i++){
            TextSliderView textSliderView = new TextSliderView(this);
            textSliderView
                    .description("")
                    .setScaleType(BaseSliderView.ScaleType.FitCenterCrop)
                    .image(ConstantValue.getOssResourceUrl(luntanList.getPostpicture()[i]));
            sliderShow.addSlider(textSliderView);
        }
        sliderShow.setPresetTransformer(SliderLayout.Transformer.Accordion);
        sliderShow.setPresetIndicator(SliderLayout.PresetIndicators.Center_Bottom);
        sliderShow.setCustomAnimation(new DescriptionAnimation());
        sliderShow.stopAutoCycle();

        sliderShow.setCurrentPosition(image_index);

    }
}
