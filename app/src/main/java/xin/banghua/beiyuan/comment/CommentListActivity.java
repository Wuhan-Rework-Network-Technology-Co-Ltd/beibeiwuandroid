package xin.banghua.beiyuan.comment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.alibaba.fastjson.JSON;
import com.bumptech.glide.Glide;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import xin.banghua.beiyuan.Main5Branch.SomeonesluntanActivity;
import xin.banghua.beiyuan.Personage.PersonageActivity;
import xin.banghua.beiyuan.R;
import xin.banghua.beiyuan.utils.Common;
import xin.banghua.pullloadmorerecyclerview.CusPullLoadMoreRecyclerView;
import xin.banghua.pullloadmorerecyclerview.NiceImageView;


public class CommentListActivity extends AppCompatActivity {
    private static final String TAG = "CommentListActivity";

    @BindView(R.id.back_img)
    ImageView back_img;
    @BindView(R.id.pullLoadMoreRecyclerView)
    CusPullLoadMoreRecyclerView pullLoadMoreRecyclerView;

    Context mContext;

    int pageIndex = 1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comment_list);

        mContext = this;
        ButterKnife.bind(this);

        back_img.setOnClickListener(view -> onBackPressed());
        //adpter绑定数据
        CustomRecyclerAdapterAdapter customRecyclerAdapterAdapter = new CustomRecyclerAdapterAdapter(dataLists);
        //RecyclerView绑定adpter
        pullLoadMoreRecyclerView.setAdapter(customRecyclerAdapterAdapter);
        pullLoadMoreRecyclerView.getRecyclerView().setLayoutManager(new LinearLayoutManager(mContext));
        pullLoadMoreRecyclerView.addItemDecoration(new DividerItemDecoration(mContext, DividerItemDecoration.VERTICAL));//增加分割线

        pullLoadMoreRecyclerView.setLinearLayout();
        pullLoadMoreRecyclerView.setOnPullLoadMoreListener(new CusPullLoadMoreRecyclerView.PullLoadMoreListener() {
            @Override
            public void onRefresh() {
//                pageIndex = 1;
//                OkHttpInstance.getNewUserList(String.valueOf(pageIndex),selectedGender,(resultString)->{
//                    dataLists = JSON.parseArray(resultString,UserInfoListKtv.class);
//                    customRecyclerAdapterAdapter.setdataLists(dataLists);
//                    customRecyclerAdapterAdapter.notifyDataSetChanged();
//                });


                pullLoadMoreRecyclerView.postDelayed(()->pullLoadMoreRecyclerView.setPullLoadMoreCompleted(),1000);
            }
            @Override
            public void onLoadMore() {
                pageIndex = pageIndex + 1;
                NetworkRequestComment.getMyComment(pageIndex,(resultString)->{
                    if (!resultString.equals("false")){
                        dataLists.addAll(JSON.parseArray(resultString,CommentList.class));
                        customRecyclerAdapterAdapter.swapData(dataLists);
                    }
                });


                pullLoadMoreRecyclerView.postDelayed(()->pullLoadMoreRecyclerView.setPullLoadMoreCompleted(),1000);
            }
        });



        //网络请求
        pullLoadMoreRecyclerView.startWaveLoadingShow();
        NetworkRequestComment.getMyComment(pageIndex,responseString -> {
            if (!responseString.equals("false")){
                dataLists = JSON.parseArray(responseString,CommentList.class);
                customRecyclerAdapterAdapter.swapData(dataLists);

                pullLoadMoreRecyclerView.stopWaveLoadingShow();
            }else {
                pullLoadMoreRecyclerView.noDataShow();
            }
        });
    }


    List<CommentList> dataLists = new ArrayList<>();


    public class CustomRecyclerAdapterAdapter extends RecyclerView.Adapter<ViewHolder>{
        List<CommentList> dataLists = new ArrayList<>();

        public CustomRecyclerAdapterAdapter(List<CommentList> dataLists) {
            this.dataLists = dataLists;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.comment_list_item,parent,false);
            ViewHolder viewHolder = new ViewHolder(view);
            return viewHolder;
        }

        public void setDataLists(List<CommentList> dataLists){
            this.dataLists = dataLists;
            notifyDataSetChanged();
        }
        public void swapData(List<CommentList> dataLists){
            int oldSize = this.dataLists.size();
            int newSize = dataLists.size();
            this.dataLists = dataLists;
            notifyItemRangeInserted(oldSize , newSize);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            Log.d(TAG, "onBindViewHolder: 刷新");

            CommentList currentItem = this.dataLists.get(position);

            Glide.with(mContext).load(currentItem.getPortrait()).into(holder.portrait_niv);
            holder.portrait_niv.setOnClickListener(view -> {
                Intent intent = new Intent(mContext, PersonageActivity.class);
                intent.putExtra("userID",currentItem.getAuthid());
                Log.d(TAG, "onClick: 跳转个人页面");
                mContext.startActivity(intent);
            });
            holder.nickname_tv.setText(currentItem.getNickname());
            holder.nickname_tv.setOnClickListener(view -> {
                Intent intent = new Intent(mContext, PersonageActivity.class);
                intent.putExtra("userID",currentItem.getAuthid());
                Log.d(TAG, "onClick: 跳转个人页面");
                mContext.startActivity(intent);
            });
            holder.comment_tv.setText(currentItem.getComment_text());
            if (currentItem.getMainID().equals("0")){
                holder.description_tv.setText("回复了你的帖子");
            }else{
                holder.description_tv.setText("回复了你的评论");
            }
            holder.time_tv.setText(currentItem.getTime());
            holder.post_title_tv.setText("原贴:"+currentItem.getPosttitle());


            holder.comment_item_constraint.setOnClickListener(view -> {
                Intent intent = new Intent(mContext, SomeonesluntanActivity.class);
                intent.putExtra("authid", Common.myID);
                intent.putExtra("comment", (Serializable)currentItem);
                startActivity(intent);
            });


        }

        @Override
        public int getItemCount() {
            return dataLists.size();
        }
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        @BindView(R.id.comment_item_constraint)
        ConstraintLayout comment_item_constraint;

        @BindView(R.id.portrait_niv)
        NiceImageView portrait_niv;
        @BindView(R.id.nickname_tv)
        TextView nickname_tv;
        @BindView(R.id.comment_tv)
        TextView comment_tv;
        @BindView(R.id.description_tv)
        TextView description_tv;
        @BindView(R.id.time_tv)
        TextView time_tv;
        @BindView(R.id.post_title_tv)
        TextView post_title_tv;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            ButterKnife.bind(this,itemView);
        }
    }
}


//<TableLayout
//        android:layout_width="match_parent"
//                android:layout_height="match_parent">
//<TableRow
//            android:layout_width="match_parent"
//                    android:layout_height="match_parent" >
//<FrameLayout
//                android:layout_width="match_parent"
//                        android:layout_weight="1"
//                        android:layout_height="wrap_content"
//                        android:orientation="horizontal">
//<ImageView
//                    android:id="@+id/back_img"
//                            android:layout_width="40dp"
//                            android:layout_height="40dp"
//                            android:padding="5dp"
//                            android:src="@drawable/ic_my_back_arrow" />
//
//<TextView
//                    android:layout_width="wrap_content"
//                            android:layout_height="40dp"
//                            android:layout_weight="1"
//                            android:text="订单记录"
//                            android:textColor="@color/text_color_1"
//                            android:gravity="center"
//                            android:layout_gravity="center"
//                            android:textSize="20sp"/>
//</FrameLayout>
//</TableRow>
//
//<TableRow
//            android:layout_width="match_parent"
//                    android:layout_height="match_parent" >
//<xin.banghua.pullloadmorerecyclerview.CusPullLoadMoreRecyclerView
//        android:id="@+id/pullLoadMoreRecyclerView"
//        android:layout_width="match_parent"
//        android:layout_height="match_parent"
//        android:background="@color/background"
//        android:layout_weight="1"/>
//</TableRow>
//</TableLayout>