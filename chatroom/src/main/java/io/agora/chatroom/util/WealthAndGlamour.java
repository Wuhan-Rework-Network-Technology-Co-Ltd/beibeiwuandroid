package io.agora.chatroom.util;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import io.agora.chatroom.Common;
import io.agora.chatroom.R;


public class WealthAndGlamour extends FrameLayout {
    private static final String TAG = "FrameLayoutExample";
    public WealthAndGlamour(@NonNull Context context) {
        super(context);
    }

    public WealthAndGlamour(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public WealthAndGlamour(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    public WealthAndGlamour(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context, attrs);
    }

    private Context mContext;
    private View mView;


    ImageView imageView;
    TextView textView;
    private void init(Context context, AttributeSet attrs) {
        mContext = context;
        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mView = inflater.inflate(R.layout.wealth_and_glamour, this, true);

        imageView = mView.findViewById(R.id.imageView);
        textView = mView.findViewById(R.id.textView);
    }



    public void initShow(String type,String value){
        //自定义部分
        if (type.equals("wealth")){
            imageView.setImageResource(R.mipmap.wealth_icon);
            textView.setTextColor(mView.getResources().getColor(R.color.wealth_color,null));
        }else {
            imageView.setImageResource(R.mipmap.glamour_icon);
            textView.setTextColor(mView.getResources().getColor(R.color.glamour_color,null));
        }
        textView.setText(Common.changeNumberFormatIntoW(Double.parseDouble(value)));
    }
}
