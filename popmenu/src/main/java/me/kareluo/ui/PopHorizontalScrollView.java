package me.kareluo.ui;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.HorizontalScrollView;

import me.kareluo.ui.PopLayout.OnBulgeChangeCallback;

/**
 * Created by felix on 17/1/10.
 */
public class PopHorizontalScrollView extends HorizontalScrollView implements OnBulgeChangeCallback {

    public PopHorizontalScrollView(Context context) {
        super(context);
    }

    public PopHorizontalScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public PopHorizontalScrollView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public void onBulgeChanged(int site, int size) {
        if (getChildCount() > 0) {
            View view = getChildAt(0);
            if (view instanceof OnBulgeChangeCallback) {
                ((OnBulgeChangeCallback) view).onBulgeChanged(site, size);
            }
        }
    }
}
