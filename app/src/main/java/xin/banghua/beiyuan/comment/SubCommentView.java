package xin.banghua.beiyuan.comment;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;

import butterknife.BindView;
import butterknife.ButterKnife;
import xin.banghua.beiyuan.Personage.PersonageActivity;
import xin.banghua.beiyuan.R;
import xin.banghua.pullloadmorerecyclerview.NiceImageView;
import xin.banghua.beiyuan.custom_ui.ad.UIUtils;
import xin.banghua.beiyuan.utils.OkHttpResponseCallBack;


public class SubCommentView extends FrameLayout {
    private static final String TAG = "FrameLayoutExample";
    public SubCommentView(@NonNull Context context) {
        super(context);
        init(context);
    }

    public SubCommentView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public SubCommentView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    public SubCommentView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context);
    }

    private Context mContext;
    private View mView;



    private void init(Context context) {
        mContext = context;
        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mView = inflater.inflate(R.layout.item_comment_child_new, this, true);

        ButterKnife.bind(this, mView);
    }


    @BindView(R.id.comment_relative_layout)
    RelativeLayout comment_relative_layout;

    @BindView(R.id.iv_header)
    NiceImageView iv_header;
    @BindView(R.id.ll_like)
    LinearLayout ll_like;
    @BindView(R.id.tv_like_count)
    TextView tv_like_count;
    @BindView(R.id.tv_user_name)
    TextView tv_user_name;
    @BindView(R.id.if_auth_tv)
    TextView if_auth_tv;
    @BindView(R.id.reply_user_tv)
    TextView reply_user_tv;
    @BindView(R.id.reply_user_name)
    TextView reply_user_name;
    @BindView(R.id.ifauthlike_tv)
    TextView ifauthlike_tv;
    @BindView(R.id.tv_content)
    TextView tv_content;
    @BindView(R.id.tv_time)
    TextView tv_time;
    @BindView(R.id.tv_reply)
    TextView tv_reply;
    public void initShow(CommentList currentItem, CommentDialog commentDialog,int currentPosition){
        //自定义部分
        Glide.with(mContext).load(currentItem.getPortrait()).into(iv_header);
        iv_header.setOnClickListener(view1 -> {
            Intent intent = new Intent(mContext, PersonageActivity.class);
            intent.putExtra("userID",currentItem.getAuthid());
            Log.d(TAG, "onClick: 跳转个人页面");
            mContext.startActivity(intent);
        });
        tv_user_name.setText(currentItem.getNickname());
        tv_user_name.setOnClickListener(view1 -> {
            Intent intent = new Intent(mContext, PersonageActivity.class);
            intent.putExtra("userID",currentItem.getAuthid());
            Log.d(TAG, "onClick: 跳转个人页面");
            mContext.startActivity(intent);
        });
        ll_like.setOnClickListener(view1 -> {
            NetworkRequestComment.sendCommentLike(currentItem.getId(),commentDialog.ifauthreply);
            tv_like_count.setText((Integer.parseInt(tv_like_count.getText().toString())+1)+"");
        });
        tv_like_count.setText(currentItem.getLike());

        tv_time.setText(currentItem.getTime());

        tv_reply.setOnClickListener(view1 -> {
            commentDialog.mainID = currentItem.getMainID();
            commentDialog.mainID_user = currentItem.getMainID_user();
            commentDialog.subID = currentItem.getAuthid();
            commentDialog.subID_comment = currentItem.getId();
            Log.d(TAG, "onBindViewHolder: 回复2级评论"+commentDialog.subID+currentItem.getAuthid());
            commentDialog.comment_et.setHint("回复 "+currentItem.getNickname());
            commentDialog.comment_et.requestFocus();

            commentDialog.showInput();
            commentDialog.sub_nickname = currentItem.getNickname();
            commentDialog.sub_comment_layout = (LinearLayout) SubCommentView.this.getParent();

            commentDialog.currentPosition = currentPosition;
        });


        if (!currentItem.getSubID().equals("0")){
            reply_user_tv.setVisibility(View.VISIBLE);
            reply_user_name.setVisibility(View.VISIBLE);
            reply_user_name.setText(currentItem.getSub_nickname());
            reply_user_name.setOnClickListener(view1 -> {
                Intent intent = new Intent(mContext, PersonageActivity.class);
                intent.putExtra("userID",currentItem.getSubID());
                Log.d(TAG, "onClick: 跳转个人页面");
                mContext.startActivity(intent);
            });
        }else {
            reply_user_tv.setVisibility(View.GONE);
            reply_user_name.setVisibility(View.GONE);
        }
        if (currentItem.getAuthid().equals(commentDialog.luntanList.getAuthid())){
            if_auth_tv.setVisibility(View.VISIBLE);
        }else {
            if_auth_tv.setVisibility(View.GONE);
        }
        if (currentItem.getIfauthlike().equals("1")){
            ifauthlike_tv.setVisibility(View.VISIBLE);
        }else {
            ifauthlike_tv.setVisibility(View.GONE);
        }
        if (Integer.valueOf(currentItem.getForbid()) > Integer.valueOf((int) (System.currentTimeMillis()/1000))){
            tv_content.setText("内容已被屏蔽");
        }else {
            tv_content.setText(currentItem.getComment_text());
        }


//        comment_relative_layout.setOnLongClickListener(new View.OnLongClickListener(){
//            @Override
//            public boolean onLongClick(View v) {
//                Log.d(TAG, "长按中");
//                if (Common.myID.equals(currentItem.authid)||Common.myID.equals(commentDialog.luntanList.getAuthid())){
//                    initPopWindow(v,currentItem);
//                }
//                return true;
//            }
//        });
    }


    private void initPopWindow(View v, CommentList currentItem) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_popip, null, false);
        Button btn_xixi = (Button) view.findViewById(R.id.btn_delete);
        //1.构造一个PopupWindow，参数依次是加载的View，宽高
        final PopupWindow popWindow = new PopupWindow(view,
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, true);

        popWindow.setAnimationStyle(R.anim.anim_pop);  //设置加载动画

        //这些为了点击非PopupWindow区域，PopupWindow会消失的，如果没有下面的
        //代码的话，你会发现，当你把PopupWindow显示出来了，无论你按多少次后退键
        //PopupWindow并不会关闭，而且退不出程序，加上下述代码可以解决这个问题
        popWindow.setTouchable(true);
        popWindow.setTouchInterceptor(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return false;
                // 这里如果返回true的话，touch事件将被拦截
                // 拦截后 PopupWindow的onTouchEvent不被调用，这样点击外部区域无法dismiss
            }
        });
        popWindow.setBackgroundDrawable(new ColorDrawable(0x00000000));    //要为popWindow设置一个背景才有效


        //设置popupWindow显示的位置，参数依次是参照View，x轴的偏移量，y轴的偏移量
        popWindow.showAsDropDown(v, 0, 0);

        //设置popupWindow里的按钮的事件
        btn_xixi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NetworkRequestComment.deleteComment(currentItem.id, new OkHttpResponseCallBack() {
                    @Override
                    public void getResponseString(String responseString) {
                        UIUtils.removeFromParent(SubCommentView.this);
                    }
                });
                popWindow.dismiss();
            }
        });

        Log.d(TAG, "initPopWindow: 长按中显示");
    }
}
