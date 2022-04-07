package io.agora.chatroom.topic;

import android.content.Context;
import android.content.res.Resources;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SearchView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.alibaba.fastjson.JSON;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.wuxiaolong.pullloadmorerecyclerview.PullLoadMoreRecyclerView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.agora.chatroom.OkHttpInstance;
import io.agora.chatroom.R;
import io.agora.chatroom.R2;
import io.agora.chatroom.activity.PublishPostActivity;

public class TopicDialog {
    private static final String TAG = "DialogExample";

    /**  */
    public static final int DIALOG_EXAMPLE = 1;

    public Context mContext;
    private BottomSheetDialog bottomSheetDialog;

//    @BindView(R2.id.iv_dialog_close)
//    Button iv_dialog_close;

    @BindView(R2.id.pullLoadMoreRecyclerView)
    PullLoadMoreRecyclerView pullLoadMoreRecyclerView;

    @BindView(R2.id.topic_search)
    SearchView topic_search;

    public TopicDialog(Context mContext) {
        this.mContext = mContext;
        initSheetDialog();
    }
    View view;
    private float slideOffset = 0;
    private void initSheetDialog() {
        if (bottomSheetDialog != null) {
            return;
        }
        //view   需要设置中固定高度android:layout_height="844dp"
        view = View.inflate(mContext, R.layout.topic_dialog, null);

        ButterKnife.bind(this, view);

        //dialog
        bottomSheetDialog = new BottomSheetDialog(mContext, R.style.dialog);
        bottomSheetDialog.setContentView(view);
        bottomSheetDialog.setCanceledOnTouchOutside(true);
        bottomSheetDialog.setOnDismissListener(dialog -> {
            //消失监听
        });
        BottomSheetBehavior mDialogBehavior = BottomSheetBehavior.from((View) view.getParent());
        mDialogBehavior.setPeekHeight(getWindowHeight());
        //dialog滑动监听
        mDialogBehavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
                if (newState == BottomSheetBehavior.STATE_HIDDEN) {
                    mDialogBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                } else if (newState == BottomSheetBehavior.STATE_SETTLING) {
                    Log.d(TAG, "onStateChanged: slideOffset"+slideOffset);
                    if (slideOffset <= -0.12 && slideOffset != -1) {
                        //当向下滑动时 值为负数
                        bottomSheetDialog.dismiss();
                    }
                }
            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {
                //show的时候会先调用一次onSlide，导致slideOffset为-1，所以判断是否下滑隐藏时，加个判断slideOffset != -1
                TopicDialog.this.slideOffset = slideOffset;//记录滑动值
            }
        });


        //iv_dialog_close.setOnClickListener(v -> bottomSheetDialog.dismiss());
    }

    private int getWindowHeight() {
        Resources res = mContext.getResources();
        DisplayMetrics displayMetrics = res.getDisplayMetrics();
        return displayMetrics.heightPixels;
    }


    public List<TopicList> topicLists = new ArrayList<>();

    String topic = "";
    ExampleFragmentAdapter exampleFragmentAdapter;

    PublishPostActivity publishPostActivity;
    public void initShow(PublishPostActivity publishPostActivity){
        //自定义部分
        this.publishPostActivity = publishPostActivity;
        topic_search.onActionViewExpanded();
        topic_search.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (!newText.equals(topic)){
                    topic = newText;
                    OkHttpInstance.getTopic(topic, responseString -> {
                        if (!responseString.equals("[]")){
                            topicLists = JSON.parseArray(responseString,TopicList.class);
                            exampleFragmentAdapter.setTopicLists(topicLists);
                        }else {
                            topicLists.clear();
                            topicLists.add(new TopicList(topic,"创建新话题"));
                            exampleFragmentAdapter.setTopicLists(topicLists);
                        }
                    });
                }
                return false;
            }
        });

        //adpter绑定数据
        exampleFragmentAdapter = new ExampleFragmentAdapter(topicLists);
        //RecyclerView绑定adpter
        pullLoadMoreRecyclerView.setAdapter(exampleFragmentAdapter);
        pullLoadMoreRecyclerView.getRecyclerView().setLayoutManager(new LinearLayoutManager(mContext));
        pullLoadMoreRecyclerView.addItemDecoration(new DividerItemDecoration(mContext, DividerItemDecoration.VERTICAL));//增加分割线

        pullLoadMoreRecyclerView.setLinearLayout();
        pullLoadMoreRecyclerView.setOnPullLoadMoreListener(new PullLoadMoreRecyclerView.PullLoadMoreListener() {
            @Override
            public void onRefresh() {

//                pageindex = 1;
//                OkHttpInstance.getNewUserList(String.valueOf(pageindex),selectedGender,(resultString)->{
//                    TopicLists = JSON.parseArray(resultString,TopicList.class);
//                    newUserAdapter.setTopicLists(TopicLists);
//                    newUserAdapter.notifyDataSetChanged();
//                });

                pullLoadMoreRecyclerView.postDelayed(()->pullLoadMoreRecyclerView.setPullLoadMoreCompleted(),1000);
            }
            @Override
            public void onLoadMore() {

//                pageindex = pageindex + 1;
//                OkHttpInstance.getNewUserList(String.valueOf(pageindex),selectedGender,(resultString)->{
//                    TopicLists.addAll(JSON.parseArray(resultString,TopicList.class));
//                    newUserAdapter.swapData(TopicLists);
//                });

                pullLoadMoreRecyclerView.postDelayed(()->pullLoadMoreRecyclerView.setPullLoadMoreCompleted(),1000);
            }
        });



        //网络请求

        OkHttpInstance.getTopic(topic, responseString -> {
            if (!responseString.equals("[]")){
                topicLists = JSON.parseArray(responseString,TopicList.class);
                exampleFragmentAdapter.setTopicLists(topicLists);
            }
        });

        slideOffset = 0;
        bottomSheetDialog.show();
    }


    public class ExampleFragmentAdapter extends RecyclerView.Adapter<ViewHolder>{
        List<TopicList> topicLists = new ArrayList<>();

        public ExampleFragmentAdapter(List<TopicList> TopicLists) {
            this.topicLists = TopicLists;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.topic_item,parent,false);
            ViewHolder viewHolder = new ViewHolder(view);
            return viewHolder;
        }

        public void setTopicLists(List<TopicList> TopicLists){
            this.topicLists = TopicLists;
            notifyDataSetChanged();
        }
        public void swapData(List<TopicList> topicLists){
            int oldSize = this.topicLists.size();
            int newSize = topicLists.size();
            this.topicLists = topicLists;
            notifyItemRangeInserted(oldSize , newSize);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            Log.d(TAG, "onBindViewHolder: 刷新");

            TopicList currentItem = this.topicLists.get(position);

            holder.topic_tv.setText("#"+currentItem.getTopic());

            if (currentItem.getTopicdescription().equals("创建新话题")){
                holder.topic_hot.setText(currentItem.getTopicdescription());
            }else {
                holder.topic_hot.setText("热度："+currentItem.getTopicpv());
            }
            holder.topic_layout.setOnClickListener(v -> {
                if (currentItem.getTopicdescription().equals("创建新话题")){
                    OkHttpInstance.createTopic(currentItem.getTopic(),responseString -> {
                        if (responseString.equals("success")){
                            TopicAddedItem topicAddedItem = new TopicAddedItem(mContext);
                            topicAddedItem.initShow(publishPostActivity,currentItem);
                            publishPostActivity.topic_layout.addView(topicAddedItem);
                            publishPostActivity.topicLists.add(currentItem);
                        }
                    });
                }else {
                    TopicAddedItem topicAddedItem = new TopicAddedItem(mContext);
                    topicAddedItem.initShow(publishPostActivity,currentItem);
                    publishPostActivity.topic_layout.addView(topicAddedItem);
                    publishPostActivity.topicLists.add(currentItem);
                }
                bottomSheetDialog.dismiss();
            });
        }

        @Override
        public int getItemCount() {
            return topicLists.size();
        }
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        ConstraintLayout topic_layout;
        TextView topic_tv;
        TextView topic_hot;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            topic_layout = itemView.findViewById(R.id.topic_layout);
            topic_tv = itemView.findViewById(R.id.topic_tv);
            topic_hot = itemView.findViewById(R.id.topic_hot);
        }
    }
}
