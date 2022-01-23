package io.agora.chatroom.util;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import io.agora.chatroom.R;


/**
 * @author Huanglinqing
 *         desc 确定对话框 单个按钮
 */
public class GlobalDialogSingle extends Dialog {

    private TextView gloual_dialog_message;
    private TextView buttonok,buttoncancel;
    private Context mContext;
    private String title;
    private String message;
    private String positiveText,negativeText;
    private OnClickListener onClickListenerPositive,onClickListenerNegative;

    public GlobalDialogSingle(Context context) {
        super(context);
    }

    public GlobalDialogSingle(Context context, String title,
                              String message, String positiveText, OnClickListener onClickListenerPositive) {
        super(context);
        this.mContext = context;
        this.title = title;
        this.message = message;
        this.positiveText = positiveText;
        this.onClickListenerPositive = onClickListenerPositive;

    }

    public GlobalDialogSingle(Context context, String title,
                              String message, String positiveText, OnClickListener onClickListenerPositive,
                              String negativeText, OnClickListener onClickListenerNegative) {
        super(context);
        this.mContext = context;
        this.title = title;
        this.message = message;
        this.positiveText = positiveText;
        this.onClickListenerPositive = onClickListenerPositive;
        this.negativeText = negativeText;
        this.onClickListenerNegative = onClickListenerNegative;

    }


    protected GlobalDialogSingle(Context context, boolean cancelable, OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.gloual_dialog_one);
        gloual_dialog_message = findViewById(R.id.gloual_dialog_message);
        buttonok = findViewById(R.id.gloual_dialog_ok_but);
        buttoncancel = findViewById(R.id.gloual_dialog_cancel_but);

        Window dialogWindow = getWindow();
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        DisplayMetrics d = mContext.getResources().getDisplayMetrics();
        lp.width = (int) (d.widthPixels * 0.8);
        dialogWindow.setAttributes(lp);
        dialogWindow.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        GlobalDialogSingle.this.setCanceledOnTouchOutside(false);
        GlobalDialogSingle.this.setOnKeyListener(new OnKeyListener() {
            @Override
            public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_BACK) {
                    return true;
                } else {
                    return false;
                }
            }//屏蔽back键
        });
        gloual_dialog_message.setText(message);
        buttonok.setText(positiveText);
        buttonok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickListenerPositive.onClick(GlobalDialogSingle.this, DialogInterface.BUTTON_POSITIVE);
            }
        });
        if (negativeText!=null) {
            buttoncancel.setText(negativeText);
            buttoncancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onClickListenerNegative.onClick(GlobalDialogSingle.this, DialogInterface.BUTTON_NEGATIVE);
                }
            });
        }
    }
}
