package xin.banghua.beiyuan.custom_ui;

import android.content.Context;
import android.util.AttributeSet;

public class MyMarqueeTextView extends androidx.appcompat.widget.AppCompatTextView {


    public MyMarqueeTextView(Context context) {
        super(context);
    }

    public MyMarqueeTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MyMarqueeTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    //返回textview是否处在选中的状态
    //而只有选中的textview才能够实现跑马灯效果
    @Override
    public boolean isFocused() {
        return true;
    }
}
