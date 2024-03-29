package xin.banghua.beiyuan.custom_ui;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;

import io.agora.chatroom.gift.GiftList;
import xin.banghua.beiyuan.R;


public class ImageTextView extends FrameLayout {
    private static final String TAG = "FrameLayoutExample";
    public ImageTextView(@NonNull Context context) {
        super(context);
    }

    public ImageTextView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public ImageTextView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    public ImageTextView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context);
    }

    private Context mContext;
    private View mView;

    private void init(Context context) {
        mContext = context;
        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mView = inflater.inflate(R.layout.fragment_mine, this, true);

        imageView = mView.findViewById(R.id.imageView);
        textView = mView.findViewById(R.id.textView);
    }


    ImageView imageView;
    TextView textView;

    GiftList giftList;
    public void initShow(GiftList giftList,int num){
        //自定义部分
        Glide.with(mContext).load(giftList.getGift_image()).into(imageView);
        if (num>1){
            textView.setText(giftList.getGift_name()+" x"+num);
        }else {
            textView.setText(giftList.getGift_name());
        }
    }
}
