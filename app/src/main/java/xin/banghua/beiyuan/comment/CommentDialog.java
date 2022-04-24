package xin.banghua.beiyuan.comment;

import static android.content.Context.INPUT_METHOD_SERVICE;
import static android.view.inputmethod.InputMethod.SHOW_FORCED;
import static android.view.inputmethod.InputMethodManager.HIDE_IMPLICIT_ONLY;
import static xin.banghua.beiyuan.Common.myID;
import static xin.banghua.beiyuan.Common.userInfoList;

import android.annotation.SuppressLint;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.drawable.ColorDrawable;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.alibaba.fastjson.JSON;
import com.bumptech.glide.Glide;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import butterknife.BindView;
import butterknife.ButterKnife;
import xin.banghua.beiyuan.Adapter.LuntanList;
import xin.banghua.beiyuan.Adapter.UserInfoList;
import xin.banghua.beiyuan.Main5Branch.SomeonesluntanActivity;
import xin.banghua.beiyuan.Personage.PersonageActivity;
import xin.banghua.beiyuan.R;
import xin.banghua.beiyuan.custom_ui.ad.UIUtils;
import xin.banghua.beiyuan.Common;
import xin.banghua.beiyuan.utils.CommonCallBackInterface;
import xin.banghua.beiyuan.utils.OkHttpInstance;
import xin.banghua.beiyuan.utils.OkHttpResponseCallBack;
import xin.banghua.pullloadmorerecyclerview.CusPullLoadMoreRecyclerView;
import xin.banghua.pullloadmorerecyclerview.NiceImageView;


public class CommentDialog {
    private static final String TAG = "CommentDialog";

    /**  */
    public static final int DIALOG_EXAMPLE = 1;

    public Context mContext;
    private BottomSheetDialog bottomSheetDialog;



    public CommentDialog(Context mContext) {
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
        view = View.inflate(mContext, R.layout.comment_dialog, null);

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
                CommentDialog.this.slideOffset = slideOffset;//记录滑动值
            }
        });


        close_img.setOnClickListener(v -> bottomSheetDialog.dismiss());
    }

    private int getWindowHeight() {
        Resources res = mContext.getResources();
        DisplayMetrics displayMetrics = res.getDisplayMetrics();
        return displayMetrics.heightPixels;
    }
    @BindView(R.id.close_img)
    ImageView close_img;
    @BindView(R.id.comment_sum)
    TextView comment_sum;
    @BindView(R.id.comment_recycler_view)
    CusPullLoadMoreRecyclerView pullLoadMoreRecyclerView;
    @BindView(R.id.comment_et)
    EditText comment_et;
    @BindView(R.id.send_comment)
    ImageView send_comment;


    int pageIndex = 1;
    String mainID = "0";
    String mainID_user = "0";
    String subID = "0";
    String sub_nickname = "";
    String subID_comment = "0";
    int currentPosition = 1;
    LuntanList luntanList;

    String ifauthreply = "0";

    public void startLoadComment(){
        SomeonesluntanActivity.selectedComment = null;
        //网络请求
        NetworkRequestComment.getMainComment(luntanList.getId(),pageIndex+"", responseString -> {
            if (!responseString.equals("false")){
                dataLists = JSON.parseArray(responseString,CommentList.class);
                Log.d(TAG, "loadComment: 评论数量"+dataLists.size());
//                List<Integer> items = new ArrayList<>();
//                for (int i=dataLists.size()-1;i>=0;i--){
//                    Log.d(TAG, "loadComment: i="+i);
//                    for (int j=dataLists.size()-1;j>=0;j--){
//                        Log.d(TAG, "loadComment: j="+j);
//                        if (i != j && dataLists.get(i).getId().equals(dataLists.get(j).getId())){
//                            Log.d(TAG, "startLoadComment: "+i+"|"+j+"|"+dataLists.get(i).getId()+"|"+dataLists.get(j).getId());
//                            items.add(i);
//                        }
//                    }
//                }
//                for (int item:items) {
//                    Log.d(TAG, "startLoadComment: 移除"+item);
//                    dataLists.remove(item);
//                }
                mainRecyclerAdapterAdapter.swapData(dataLists);
            }
        });


        slideOffset = 0;
        bottomSheetDialog.show();
    }
    MainRecyclerAdapterAdapter mainRecyclerAdapterAdapter;

    String previous_comment = "";
    public void loadComment(LuntanList luntanList){
        mainID = "0";
        mainID_user = "0";
        subID = "0";
        sub_nickname = "";
        subID_comment = "0";
        pageIndex = 1;
        this.luntanList = luntanList;
        if (luntanList.getAuthid().equals(myID)){
            ifauthreply = "1";
        }else {
            ifauthreply = "0";
        }
        Log.d(TAG, "loadComment: 是不是帖子作者"+ifauthreply);

        dataLists.clear();
        pageIndex = 1;
        Log.d(TAG, "loadComment: postid"+luntanList.getId());
        comment_sum.setText(luntanList.getComment_sum()+"条评论");
        //adpter绑定数据
        mainRecyclerAdapterAdapter = new MainRecyclerAdapterAdapter(dataLists);
        //RecyclerView绑定adpter
        pullLoadMoreRecyclerView.setAdapter(mainRecyclerAdapterAdapter);
        pullLoadMoreRecyclerView.getRecyclerView().setLayoutManager(new LinearLayoutManager(mContext));
        pullLoadMoreRecyclerView.addItemDecoration(new DividerItemDecoration(mContext, DividerItemDecoration.VERTICAL));//增加分割线
        pullLoadMoreRecyclerView.setLinearLayout();
        pullLoadMoreRecyclerView.setPullRefreshEnable(false);
        pullLoadMoreRecyclerView.setHasMore(true);
        pullLoadMoreRecyclerView.setFooterViewText("正在加载更多...");
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
                //网络请求
                NetworkRequestComment.getMainComment(luntanList.getId(),pageIndex+"", responseString -> {
                    if (!responseString.equals("false")){
                        dataLists.addAll(JSON.parseArray(responseString,CommentList.class));
                        Log.d(TAG, "loadComment: 评论数量"+dataLists.size());
//                        List<Integer> items = new ArrayList<>();
//                        for (int i=dataLists.size()-1;i>=0;i--){
//                            Log.d(TAG, "loadComment: i="+i);
//                            for (int j=dataLists.size()-1;j>=0;j--){
//                                Log.d(TAG, "loadComment: j="+j);
//                                if (dataLists.get(i).getId().equals(dataLists.get(j).getId())){
//                                    //items.add(i);
//                                }
//                            }
//                        }
//                        for (int item:items) {
//                            dataLists.remove(item);
//                        }
                        mainRecyclerAdapterAdapter.swapData(dataLists);
                    }else {
                        pullLoadMoreRecyclerView.setFooterViewText("已经到底了");
                        pullLoadMoreRecyclerView.setHasMore(false);
                    }
                });


                pullLoadMoreRecyclerView.postDelayed(()->pullLoadMoreRecyclerView.setPullLoadMoreCompleted(),1000);
            }
        });


        pullLoadMoreRecyclerView.stopWaveLoadingShow();



        if (luntanList.getComment_forbid().equals("1")){
            comment_et.setText("此贴已被作者设置禁止评论!");
            send_comment.setVisibility(View.GONE);
        }
        send_comment.setOnClickListener(view1 -> {
            if (comment_et.getText().toString().equals(previous_comment)){
                Toast.makeText(mContext,"请不要重复发布相同内容",Toast.LENGTH_LONG).show();
                return;
            }
            if (comment_et.getText().length()>100){
                Toast.makeText(mContext,"字数不能超过100字节",Toast.LENGTH_LONG).show();
            }else if (comment_et.getText().length()==0){
                Toast.makeText(mContext,"字数不能为空",Toast.LENGTH_LONG).show();
            }else {
                previous_comment = comment_et.getText().toString();
                OkHttpInstance.getUserAttributes(luntanList.getAuthid(), new OkHttpResponseCallBack() {
                    @Override
                    public void getResponseString(String responseString) {
                        if (!responseString.equals("false")){
                            UserInfoList post_auth = JSON.parseObject(responseString,UserInfoList.class);
                            ArrayList blacklist = new ArrayList();
                            if (post_auth.getMyblacklist()!=null){
                                blacklist = new ArrayList<String>(Arrays.asList(post_auth.getMyblacklist().split(",")));
                            }

                            if (blacklist.contains(myID)){
                                Toast.makeText(mContext,"你已被作者加入黑名单",Toast.LENGTH_LONG).show();
                            }else {
                                NetworkRequestComment.sendComment(comment_et.getText().toString(), luntanList.getId(),luntanList.getAuthid(), mainID,mainID_user, subID, subID_comment,ifauthreply, new OkHttpResponseCallBack() {
                                    @Override
                                    public void getResponseString(String responseString) {
                                        Toast.makeText(mContext,"评论成功，经验+1，积分+5",Toast.LENGTH_SHORT).show();
                                        CommentList commentList = new CommentList();
                                        commentList.setId(responseString);
                                        commentList.setTime(Math.round(new Date().getTime()/1000)+"");
                                        commentList.setAuthid(myID);
                                        commentList.setPostid_user(luntanList.getAuthid());
                                        commentList.setNickname(userInfoList.getNickname());
                                        commentList.setPortrait(userInfoList.getPortrait());
                                        commentList.setComment_text(comment_et.getText().toString());
                                        commentList.setLike("0");
                                        commentList.setSubcomment_num("0");
                                        commentList.setForbid("0");
                                        commentList.setMainID(mainID);
                                        commentList.setMainID_user(mainID_user);
                                        commentList.setSubID(subID);
                                        commentList.setSubID_comment(subID_comment);
                                        commentList.setSub_nickname(sub_nickname);
                                        commentList.setPostid(luntanList.getId());
                                        List<CommentList> newData = new ArrayList<>();
                                        newData.add(commentList);

                                        if (mainID.equals("0")){
                                            newData.addAll(dataLists);
                                            dataLists = newData;
                                            mainRecyclerAdapterAdapter.setDataLists(dataLists);

                                            pullLoadMoreRecyclerView.scrollToTop();
                                        }else {
                                            newData.addAll(sub_dataLists);
                                            sub_dataLists = newData;
                                            //subRecyclerAdapterAdapter.setDataLists(sub_dataLists);
                                            SubCommentView subCommentView = new SubCommentView(mContext);
                                            subCommentView.initShow(commentList,CommentDialog.this,currentPosition);
                                            subCommentView.comment_relative_layout.setOnLongClickListener(new View.OnLongClickListener(){
                                                @Override
                                                public boolean onLongClick(View v) {
                                                    Log.d(TAG, "长按中");
                                                    deleteCommentList = commentList;
                                                    deleteSubCommentView = subCommentView;
                                                    initPopWindow(v,-1, new CommonCallBackInterface() {
                                                        @Override
                                                        public void callBack() {

                                                        }
                                                    });
                                                    return true;
                                                }
                                            });
                                            if (sub_comment_layout!=null)
                                                sub_comment_layout.addView(subCommentView,0);

                                            pullLoadMoreRecyclerView.getRecyclerView().scrollToPosition(currentPosition);
                                        }
                                        hideInput();
                                        comment_et.setText("");
                                    }
                                });
                            }
                        }
                    }
                });
            }
        });

    }


    List<CommentList> selectedComments = new ArrayList<>();
    public void loadSelectedComment(String comment_id){
        NetworkRequestComment.getSelectedComment(comment_id, new OkHttpResponseCallBack() {
            @Override
            public void getResponseString(String responseString) {
                if (!responseString.equals("false")){
                    selectedComments = JSON.parseArray(responseString,CommentList.class);
                    if (selectedComments.size()==3){//回复2级评论
                        List<CommentList> mainComment = new ArrayList<>();
                        mainComment.add(selectedComments.get(0));
                        mainRecyclerAdapterAdapter.swapData(mainComment);

                        close_img.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                SubCommentView subCommentView_one = new SubCommentView(mContext);
                                subCommentView_one.initShow(selectedComments.get(1),CommentDialog.this,currentPosition);
                                if (sub_comment_layout != null)
                                    sub_comment_layout.addView(subCommentView_one);

                                SubCommentView subCommentView = new SubCommentView(mContext);
                                subCommentView.initShow(selectedComments.get(2),CommentDialog.this,currentPosition);
                                if (sub_comment_layout != null)
                                    sub_comment_layout.addView(subCommentView);
                            }
                        },500);
                    }else if(selectedComments.size()==2){//回复1级评论
                        List<CommentList> mainComment = new ArrayList<>();
                        mainComment.add(selectedComments.get(0));
                        mainRecyclerAdapterAdapter.swapData(mainComment);

                        close_img.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                SubCommentView subCommentView = new SubCommentView(mContext);
                                subCommentView.initShow(selectedComments.get(1),CommentDialog.this,currentPosition);
                                if (sub_comment_layout != null)
                                    sub_comment_layout.addView(subCommentView);
                            }
                        },500);

                    }else {//回复帖子
                        mainRecyclerAdapterAdapter.swapData(selectedComments);
                    }
                }
            }
        });

        slideOffset = 0;
        bottomSheetDialog.show();
    }


    public void hideInput() {
        InputMethodManager imm = (InputMethodManager) mContext.getSystemService(INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        mainID = "0";
        mainID_user = "0";
        subID = "0";
        sub_nickname = "";
        subID_comment = "0";
        comment_et.setHint("留下你的精彩评论吧~~(经验+1，积分+5)");
    }
    public void showInput() {
        InputMethodManager imm = (InputMethodManager) mContext.getSystemService(INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(SHOW_FORCED, HIDE_IMPLICIT_ONLY);
    }

    List<CommentList> dataLists = new ArrayList<>();
    public class MainRecyclerAdapterAdapter extends RecyclerView.Adapter<MainViewHolder> {
        
        List<CommentList> dataLists = new ArrayList<>();

        public MainRecyclerAdapterAdapter(List<CommentList> dataLists) {
            this.dataLists = dataLists;
        }

        @NonNull
        @Override
        public MainViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.comment_main_item,parent,false);
            MainViewHolder viewHolder = new MainViewHolder(view);
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
        public void onBindViewHolder(@NonNull MainViewHolder holder, @SuppressLint("RecyclerView") int position) {
            Log.d(TAG, "onBindViewHolder: 刷新");


            CommentList currentItem = this.dataLists.get(position);

            List<CommentList> sub_dataLists_inner = new ArrayList<>();
            SubRecyclerAdapterAdapter subRecyclerAdapterAdapter_inner = new SubRecyclerAdapterAdapter(sub_dataLists_inner);
            holder.subcomment_recycler_view.setLayoutManager(new LinearLayoutManager(mContext));
            holder.subcomment_recycler_view.setAdapter(subRecyclerAdapterAdapter_inner);

            Glide.with(mContext).load(currentItem.getPortrait()).into(holder.iv_header);
            holder.iv_header.setOnClickListener(view1 -> {
                Intent intent = new Intent(mContext, PersonageActivity.class);
                intent.putExtra("userID",currentItem.getAuthid());
                Log.d(TAG, "onClick: 跳转个人页面");
                mContext.startActivity(intent);
            });
            holder.tv_user_name.setText(currentItem.getNickname());
            holder.tv_user_name.setOnClickListener(view1 -> {
                Intent intent = new Intent(mContext, PersonageActivity.class);
                intent.putExtra("userID",currentItem.getAuthid());
                Log.d(TAG, "onClick: 跳转个人页面");
                mContext.startActivity(intent);
            });
            holder.ll_like.setOnClickListener(view1 -> {
                NetworkRequestComment.sendCommentLike(currentItem.getId(),ifauthreply);
                holder.tv_like_count.setText((Integer.parseInt(holder.tv_like_count.getText().toString())+1)+"");
            });
            holder.tv_like_count.setText(currentItem.getLike());

            holder.tv_time.setText(currentItem.getTime());

            if (SomeonesluntanActivity.selectedComment!=null && position==0){
                sub_comment_layout = holder.sub_comment_layout;
            }
            holder.main_comment_layout.setOnClickListener(view1 -> {
                mainID = currentItem.getId();
                mainID_user = currentItem.getAuthid();
                Log.d(TAG, "onBindViewHolder: 回复1级评论"+mainID+currentItem.getId());
                sub_nickname = currentItem.getNickname();
                comment_et.setHint("回复 "+currentItem.getNickname());
                comment_et.requestFocus();
                showInput();

                //subRecyclerAdapterAdapter = subRecyclerAdapterAdapter_inner;

                sub_comment_layout = holder.sub_comment_layout;
                sub_dataLists = sub_dataLists_inner;

                currentPosition = position;
            });



            if (Integer.parseInt(currentItem.getSubcomment_num())>0){
                holder.collapse_tv.setVisibility(View.VISIBLE);
                holder.collapse_tv.setText("--展开"+currentItem.subcomment_num+"条评论");
                AtomicInteger pageIndex = new AtomicInteger();
                collapseOpen(holder.collapse_tv,currentItem,pageIndex,sub_dataLists_inner,holder.sub_comment_layout,position);
            }else {
                holder.collapse_tv.setVisibility(View.GONE);
            }

            if (SomeonesluntanActivity.selectedComment!=null){
                holder.collapse_tv.setVisibility(View.GONE);
            }

            if (currentItem.getAuthid().equals(luntanList.getAuthid())){
                holder.if_auth_tv.setVisibility(View.VISIBLE);
            }else {
                holder.if_auth_tv.setVisibility(View.GONE);
            }
            if (currentItem.getIfauthreply().equals("1")){
                holder.ifauthreply_tv.setVisibility(View.VISIBLE);
            }else {
                holder.ifauthreply_tv.setVisibility(View.GONE);
            }
            if (currentItem.getIfauthlike().equals("1")){
                holder.ifauthlike_tv.setVisibility(View.VISIBLE);
            }else {
                holder.ifauthlike_tv.setVisibility(View.GONE);
            }
            if (Integer.valueOf(currentItem.getForbid()) > Integer.valueOf((int) (System.currentTimeMillis()/1000))){
                holder.tv_content.setText("内容已被屏蔽");
            }else {
                holder.tv_content.setText(currentItem.getComment_text());
            }


            View.OnLongClickListener longClickListener = new View.OnLongClickListener(){
                @Override
                public boolean onLongClick(View v) {
                    Log.d(TAG, "长按中");
                    deleteCommentList = currentItem;
                    initPopWindow(v,position, new CommonCallBackInterface() {
                        @Override
                        public void callBack() {
                            notifyDataSetChanged();
                        }
                    });
                    return true;
                }
            };

            //holder.tv_content.setOnLongClickListener(longClickListener);
            holder.main_comment_layout.setOnLongClickListener(longClickListener);


            //subRecyclerAdapterAdapter_inner.setDataLists(sub_dataLists_inner);
            holder.sub_comment_layout.removeAllViews();
        }

        @Override
        public int getItemCount() {
            return dataLists.size();
        }
    }
    public void collapseOpen(TextView collapse_tv,CommentList currentItem,AtomicInteger pageIndex,List<CommentList> sub_dataLists_inner,
                             LinearLayout sub_comment_layout,int currentPosition){
        Log.d(TAG, "onBindViewHolder: 点击了展开2");

        collapse_tv.setOnClickListener(view1 -> {
            Log.d(TAG, "onBindViewHolder: 点击了展开1");
            mainID = currentItem.getId();
            mainID_user = currentItem.getAuthid();
            pageIndex.set(pageIndex.get() + 1);
            NetworkRequestComment.getSubComment(mainID, pageIndex.get()+"", new OkHttpResponseCallBack() {
                @Override
                public void getResponseString(String responseString) {
                    if (!responseString.equals("false")){
                        sub_dataLists_inner.clear();
                        sub_dataLists_inner.addAll(JSON.parseArray(responseString,CommentList.class));
                        //subRecyclerAdapterAdapter_inner.setDataLists(sub_dataLists_inner);
                        for (CommentList item: sub_dataLists_inner) {
                            SubCommentView subCommentView = new SubCommentView(mContext);
                            subCommentView.initShow(item,CommentDialog.this,currentPosition);
                            subCommentView.comment_relative_layout.setOnLongClickListener(new View.OnLongClickListener(){
                                @Override
                                public boolean onLongClick(View v) {
                                    Log.d(TAG, "长按中");
                                    deleteCommentList = item;
                                    deleteSubCommentView = subCommentView;
                                    initPopWindow(v,-1, new CommonCallBackInterface() {
                                        @Override
                                        public void callBack() {

                                        }
                                    });
                                    return true;
                                }
                            });
                            if (sub_comment_layout != null)
                                sub_comment_layout.addView(subCommentView);
                        }

                        collapseClose(collapse_tv,currentItem,pageIndex,sub_dataLists_inner,sub_comment_layout,currentPosition);
                    }
                }
            });
        });
    }
    public void collapseClose(TextView collapse_tv,CommentList currentItem,AtomicInteger pageIndex,List<CommentList> sub_dataLists_inner,
                              LinearLayout sub_comment_layout,int currentPosition){
        if (Integer.parseInt(currentItem.getSubcomment_num())==sub_comment_layout.getChildCount()){
            collapse_tv.setText("--收起");
            collapse_tv.setOnClickListener(view1 -> {
                sub_dataLists_inner.clear();
                //subRecyclerAdapterAdapter_inner.setDataLists(sub_dataLists_inner);
                sub_comment_layout.removeAllViews();
                collapse_tv.setText("--展开"+currentItem.subcomment_num+"条评论");
                pageIndex.set(0);
                collapseOpen(collapse_tv,currentItem,pageIndex,sub_dataLists_inner,sub_comment_layout,currentPosition);
            });
        }
    }



    public class MainViewHolder extends RecyclerView.ViewHolder{
        @BindView(R.id.main_comment_layout)
        View main_comment_layout;
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
        @BindView(R.id.ifauthreply_tv)
        TextView ifauthreply_tv;
        @BindView(R.id.ifauthlike_tv)
        TextView ifauthlike_tv;
        @BindView(R.id.tv_content)
        TextView tv_content;
        @BindView(R.id.tv_time)
        TextView tv_time;
        @BindView(R.id.tv_reply)
        TextView tv_reply;
        @BindView(R.id.subcomment_recycler_view)
        RecyclerView subcomment_recycler_view;
        @BindView(R.id.collapse_tv)
        TextView collapse_tv;

        @BindView(R.id.sub_comment_layout)
        LinearLayout sub_comment_layout;


        public MainViewHolder(@NonNull View itemView) {
            super(itemView);

            ButterKnife.bind(this,itemView);
        }
    }


    LinearLayout sub_comment_layout;
    List<CommentList> sub_dataLists = new ArrayList<>();
    public class SubRecyclerAdapterAdapter extends RecyclerView.Adapter<SubViewHolder>{
        List<CommentList> dataLists = new ArrayList<>();

        public SubRecyclerAdapterAdapter(List<CommentList> dataLists) {
            this.dataLists = dataLists;
        }

        @NonNull
        @Override
        public SubViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_comment_child_new,parent,false);
            SubViewHolder viewHolder = new SubViewHolder(view);
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
        public void onBindViewHolder(@NonNull SubViewHolder holder, int position) {
            Log.d(TAG, "onBindViewHolder: 刷新");

            CommentList currentItem = this.dataLists.get(position);

            Glide.with(mContext).load(currentItem.getPortrait()).into(holder.iv_header);
            holder.iv_header.setOnClickListener(view1 -> {
                Intent intent = new Intent(mContext, PersonageActivity.class);
                intent.putExtra("userID",currentItem.getAuthid());
                Log.d(TAG, "onClick: 跳转个人页面");
                mContext.startActivity(intent);
            });
            holder.tv_user_name.setText(currentItem.getNickname());
            holder.tv_user_name.setOnClickListener(view1 -> {
                Intent intent = new Intent(mContext, PersonageActivity.class);
                intent.putExtra("userID",currentItem.getAuthid());
                Log.d(TAG, "onClick: 跳转个人页面");
                mContext.startActivity(intent);
            });
            holder.ll_like.setOnClickListener(view1 -> {
                NetworkRequestComment.sendCommentLike(currentItem.getId(),ifauthreply);
                holder.tv_like_count.setText((Integer.parseInt(holder.tv_like_count.getText().toString())+1)+"");
            });
            holder.tv_like_count.setText(currentItem.getLike());

            holder.tv_time.setText(currentItem.getTime());

            holder.sub_comment_layout.setOnClickListener(view1 -> {
                subID = currentItem.getAuthid();
                subID_comment = currentItem.getId();
                Log.d(TAG, "onBindViewHolder: 回复2级评论"+subID+currentItem.getAuthid());
                comment_et.setHint("回复 "+currentItem.getNickname());
                comment_et.requestFocus();
                showInput();
            });


            if (!currentItem.getSubID().equals("0")){
                holder.reply_user_tv.setVisibility(View.VISIBLE);
                holder.reply_user_name.setVisibility(View.VISIBLE);
                holder.reply_user_name.setText(currentItem.getSub_nickname());
                holder.reply_user_name.setOnClickListener(view1 -> {
                    Intent intent = new Intent(mContext, PersonageActivity.class);
                    intent.putExtra("userID",currentItem.getSubID());
                    Log.d(TAG, "onClick: 跳转个人页面");
                    mContext.startActivity(intent);
                });
            }else {
                holder.reply_user_tv.setVisibility(View.GONE);
                holder.reply_user_name.setVisibility(View.GONE);
            }
            if (currentItem.getAuthid().equals(luntanList.getAuthid())){
                holder.if_auth_tv.setVisibility(View.VISIBLE);
            }else {
                holder.if_auth_tv.setVisibility(View.GONE);
            }
            if (currentItem.getIfauthlike().equals("1")){
                holder.ifauthlike_tv.setVisibility(View.VISIBLE);
            }else {
                holder.ifauthlike_tv.setVisibility(View.GONE);
            }
            if (Integer.valueOf(currentItem.getForbid()) > Integer.valueOf((int) (System.currentTimeMillis()/1000))){
                holder.tv_content.setText("内容已被屏蔽");
            }else {
                holder.tv_content.setText(currentItem.getComment_text());
            }
        }

        @Override
        public int getItemCount() {
            return dataLists.size();
        }
    }

    public class SubViewHolder extends RecyclerView.ViewHolder{
        @BindView(R.id.sub_comment_layout)
        View sub_comment_layout;
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


        public SubViewHolder(@NonNull View itemView) {
            super(itemView);

            ButterKnife.bind(this,itemView);
        }
    }



    CommentList deleteCommentList;
    SubCommentView deleteSubCommentView;
    private void initPopWindow(View v,int position,CommonCallBackInterface commonCallBackInterface) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_popip, null, false);
        Button btn_copy = (Button) view.findViewById(R.id.btn_copy);
        Button btn_delete = (Button) view.findViewById(R.id.btn_delete);
        Button btn_tipoff = (Button) view.findViewById(R.id.btn_tipoff);
        if (Integer.parseInt(Common.myID)<10 || Integer.parseInt(Common.myID)==20978 || Common.myID.equals(deleteCommentList.authid)||Common.myID.equals(luntanList.getAuthid())){
            btn_delete.setVisibility(View.VISIBLE);
        }else {
            btn_delete.setVisibility(View.GONE);
        }

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
        btn_delete.setOnClickListener(v1 -> {
            NetworkRequestComment.deleteComment(deleteCommentList.id, new OkHttpResponseCallBack() {
                @Override
                public void getResponseString(String responseString) {
                    try {
                        if (position>=0){//1级评论
                            dataLists.remove(position);
                            commonCallBackInterface.callBack();
                        }else {
                            UIUtils.removeFromParent(deleteSubCommentView);
                        }
                    }catch (Exception e){
                        Log.e(TAG, "onBindViewHolder: 异常"+ e.toString());
                    }
                }
            });
            popWindow.dismiss();
        });

        btn_copy.setOnClickListener(view1 -> {
            ClipboardManager cm = (ClipboardManager) mContext.getSystemService(Context.CLIPBOARD_SERVICE);
            cm.setText(deleteCommentList.getComment_text());
            Toast.makeText(mContext,"复制到剪切板",Toast.LENGTH_LONG).show();
            popWindow.dismiss();
        });

        btn_tipoff.setOnClickListener(v1 -> {
            OkHttpInstance.tipoffComment(deleteCommentList.id, new OkHttpResponseCallBack() {
                @Override
                public void getResponseString(String responseString) {
                    Toast.makeText(mContext,"已举报，等待处理！",Toast.LENGTH_LONG).show();
                }
            });
            popWindow.dismiss();
        });

        Log.d(TAG, "initPopWindow: 长按中显示");
    }
}
