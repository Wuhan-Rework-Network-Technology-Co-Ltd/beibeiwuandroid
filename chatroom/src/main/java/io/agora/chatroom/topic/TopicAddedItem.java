package io.agora.chatroom.topic;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import io.agora.chatroom.R;
import io.agora.chatroom.activity.PublishPostActivity;
import io.agora.chatroom.util.UIUtils;


public class TopicAddedItem extends FrameLayout {
    private static final String TAG = "FrameLayoutExample";
    public TopicAddedItem(@NonNull Context context) {
        super(context);
        init(context);
    }

    public TopicAddedItem(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public TopicAddedItem(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    public TopicAddedItem(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context);
    }

    private Context mContext;
    private View mView;

    private void init(Context context) {
        mContext = context;
        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mView = inflater.inflate(R.layout.topic_added_item, this, true);
        topic_tv = mView.findViewById(R.id.topic_tv);
        topic_delete = mView.findViewById(R.id.topic_delete);
    }



    TextView topic_tv;
    ImageView topic_delete;
    PublishPostActivity publishPostActivity;
    public void initShow(PublishPostActivity publishPostActivity,TopicList topicList){
        //自定义部分
        this.publishPostActivity = publishPostActivity;

        topic_tv.setText("#"+topicList.getTopic());
        topic_delete.setOnClickListener(v -> {
            publishPostActivity.topicLists.remove(topicList);
            UIUtils.removeFromParent(TopicAddedItem.this);
            Log.d(TAG, "initShow: "+publishPostActivity.topicLists.size());
        });
    }


    public void initShow(TopicList topicList){
        //自定义部分
        topic_tv.setText("#"+topicList.getTopic());
        topic_tv.setOnClickListener(v -> {

        });
        topic_delete.setVisibility(GONE);
    }
}
