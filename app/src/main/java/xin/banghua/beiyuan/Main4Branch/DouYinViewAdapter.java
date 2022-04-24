package xin.banghua.beiyuan.Main4Branch;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;
import static xin.banghua.beiyuan.Common.changeNumberFormatIntoW;

import android.content.Intent;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.like.LikeButton;
import com.like.OnLikeListener;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import kotlin.Unit;
import kotlin.jvm.functions.Function0;
import xin.banghua.beiyuan.Adapter.FollowList;
import xin.banghua.beiyuan.Adapter.LuntanList;
import xin.banghua.beiyuan.Common;
import xin.banghua.beiyuan.Main4Branch.tiktok.TikTokView;
import xin.banghua.beiyuan.Main4Branch.tiktok.cache.PreloadManager;
import xin.banghua.beiyuan.Personage.PersonageActivity;
import xin.banghua.beiyuan.R;
import xin.banghua.beiyuan.comment.CommentDialog;
import xin.banghua.beiyuan.custom_ui.MyMarqueeTextView;
import xin.banghua.beiyuan.custom_ui.PortraitFrameView;
import xin.banghua.beiyuan.utils.OkHttpInstance;
import xin.banghua.beiyuan.utils.OkHttpResponseCallBack;
import xin.banghua.beiyuan.utils.SampleCoverVideo;
import xin.banghua.onekeylogin.login.OneKeyLoginActivity;
import xyz.doikki.videoplayer.player.VideoView;

public class DouYinViewAdapter extends RecyclerView.Adapter<DouYinViewAdapter.VideoViewHolder> {
    private static final String TAG = "DouYinViewAdapter";
    private FragmentActivity mContext;
    private SampleCoverVideo mVideoView;

    private VideoView mVideoView_new;

    private Fragment fragment;

    //帖子列表
    List<LuntanList> list = new ArrayList<>();

    private String currentPostId = "";


    public DouYinViewAdapter(FragmentActivity mContext, List<LuntanList> list) {
        this.mContext = mContext;
        this.list = list;
    }

    public void setVideoView(SampleCoverVideo mVideoView){
        this.mVideoView = mVideoView;
    }

    public void setVideoView_new(VideoView mVideoView){
        this.mVideoView_new = mVideoView;
    }


    //替换数据，并更新
    public void swap(List<LuntanList> list){
        int oldSize = this.list.size();
        int newSize = list.size();
        this.list = list;
        notifyItemRangeInserted(oldSize , newSize);
    }

    public void swap(int currentSize,List<LuntanList> list){
        this.list = list;
        notifyItemRangeChanged(currentSize, list.size());
    }

    @NonNull
    @Override
    public VideoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_dynamic_pager_item, parent, false);
        return new VideoViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull VideoViewHolder holder, int position) {
        LuntanList currentItem = list.get(position);

        Log.d(TAG, "2测试位置"+position);

        ViewGroup.LayoutParams layoutParams = holder.itemView.getLayoutParams();
        layoutParams.height = MATCH_PARENT;

//        if (currentItem.getPosttitle().equals("video")){
//            //holder.image_pager_slider.setVisibility(View.GONE);
////            holder.douyinImageView.setVisibility(View.GONE);
////            holder.btn_intent_album.setVisibility(View.GONE);
//
//        }
        holder.mp_video.setVisibility(View.VISIBLE);
        holder.jz_video_frameLayout.setVisibility(View.VISIBLE);

        Glide.with(mContext).load(currentItem.getCover()).into(holder.mp_video);

        //开始预加载
        Log.d(TAG, "onBindViewHolder: 开始预加载"+currentItem.getPostvideo());
        PreloadManager.getInstance(mContext).addPreloadTask(Common.getOssResourceUrl(currentItem.getCover()), position);
        Glide.with(mContext)
                .load(Common.getOssResourceUrl(currentItem.getCover()))
                .placeholder(android.R.color.white)
                .into(holder.mThumb);



        holder.tv_text.setMovementMethod(LinkMovementMethod.getInstance());
        holder.tv_text.setText(currentItem.getPosttext());



        //评论
        holder.tv_comment.setText(currentItem.getComment_sum());
        CommentDialog commentDialog = new CommentDialog(mContext);
        holder.comment_view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                commentDialog.loadComment(currentItem);
                commentDialog.startLoadComment();
            }
        });



        holder.tv_like.setTextColor(mContext.getColor(R.color.white));
        holder.likeButton.setLiked(false);
        //点赞,评论和分享
//        if (currentItem.isLiked()){
//            holder.tv_like.setTextColor(mContext.getColor(R.color.alivc_common_bg_red));
//            holder.likeButton.setLiked(true);
//        }else {
//            holder.tv_like.setTextColor(mContext.getColor(R.color.white));
//            holder.likeButton.setLiked(false);
//        }

        holder.likeLayout.setOnPauseListener(new Function0<Unit>() {
            @Override
            public Unit invoke() {
                if (mVideoView_new.isPlaying()){
                    mVideoView_new.pause();
                }else {
                    mVideoView_new.resume();
                }
                return null;
            }
        });
        holder.likeLayout.setOnLikeListener(new Function0<Unit>() {
            @Override
            public Unit invoke() {
                if (!holder.likeButton.isLiked())
                    holder.likeButton.performClick();


                currentItem.setComment_sum((Integer.parseInt(currentItem.getComment_sum())+1)+"");
                holder.tv_like.setText(changeNumberFormatIntoW(Integer.parseInt(currentItem.getComment_sum())));

                holder.tv_like.setTextColor(mContext.getColor(R.color.alivc_common_bg_red));

                OkHttpInstance.like(currentItem.getId(), new OkHttpResponseCallBack() {
                    @Override
                    public void getResponseString(String responseString) {

                    }
                });
                return null;
            }
        });


        if (TextUtils.isEmpty(currentItem.getLike())){
            currentItem.setLike("0");
        }
        final int[] likeNum = {Integer.parseInt(currentItem.getLike())};
        holder.tv_like.setText(changeNumberFormatIntoW((likeNum[0])));

        if (Common.myID==null){
            holder.likeButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intentSignin = new Intent(mContext, OneKeyLoginActivity.class);
                    mContext.startActivity(intentSignin);
                    return;
                }
            });
        }else {
            holder.likeButton.setOnLikeListener(new OnLikeListener() {
                @Override
                public void liked(LikeButton likeButton) {
                    likeButton.setLiked(true);

                    //currentItem.setLiked(true);

                    currentItem.setComment_sum((Integer.parseInt(currentItem.getComment_sum())+1)+"");
                    holder.tv_like.setText(changeNumberFormatIntoW(Integer.parseInt(currentItem.getComment_sum())));

                    holder.tv_like.setTextColor(mContext.getColor(R.color.alivc_common_bg_red));

                    OkHttpInstance.like(currentItem.getId(), new OkHttpResponseCallBack() {
                        @Override
                        public void getResponseString(String responseString) {

                        }
                    });
                    //OkHttpInstance.addLunTanLike(mContext);

                    //推荐界面的点赞缓存
                    //OkHttpInstance.addMoYuanPostLike();
                }

                @Override
                public void unLiked(LikeButton likeButton) {
                    likeButton.setLiked(true);

                    likeNum[0] = likeNum[0] + 1;
                    holder.tv_like.setText(changeNumberFormatIntoW((likeNum[0])));

                    holder.tv_like.setTextColor(mContext.getColor(R.color.alivc_common_bg_red));

                    //currentItem.setLiked(false);

//                    likeNum[0] = likeNum[0] - 1;
//                    holder.tv_like.setText(changeNumberFormatIntoW((likeNum[0])));
//
//                    holder.tv_like.setTextColor(mContext.getColor(R.color.white));

                    //OkHttpInstance.cancelLunTanLike(currentItem.getId());
                }
            });
        }

        //头像名称
        Glide.with(mContext).load(Common.getOssResourceUrl(currentItem.getAuthportrait()))
                .into(holder.authportrait);
        holder.authportrait.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), PersonageActivity.class);
                Log.d(TAG, "onClick: 用户id"+currentItem.getAuthid());
                intent.putExtra("userID", currentItem.getAuthid());
                v.getContext().startActivity(intent);
            }
        });

        holder.portraitFrameView.setPortraitFrame(currentItem.getPortraitframe());
        //holder.vip_gray.setImageResource(PortraitFrame.getPortraitFrame(currentItem.getAuthportraitframe()));

        holder.tv_nickname.setText("@"+currentItem.getAuthnickname());
        holder.tv_nickname.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), PersonageActivity.class);
                Log.d(TAG, "onClick: 用户id"+currentItem.getAuthid());
                intent.putExtra("userID", currentItem.getAuthid());
                v.getContext().startActivity(intent);
            }
        });


        //holder.tv_poi.setText(currentItem.get);

        //关注按钮,判断是否已关注

        holder.add_follow_img.setClickable(false);
        holder.add_follow_img.setImageResource(R.drawable.ic_plus_red);
        holder.add_follow_img.setVisibility(View.VISIBLE);
        holder.add_follow_img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Common.myID==null){
                    Intent intentSignin = new Intent(mContext, OneKeyLoginActivity.class);
                    mContext.startActivity(intentSignin);
                    return;
                }


                holder.add_follow_img.setClickable(false);
                holder.add_follow_img.setImageResource(R.drawable.ic_tick_red);
                holder.add_follow_img.postDelayed(() -> holder.add_follow_img.setVisibility(View.GONE), 1500);

                OkHttpInstance.follow(currentItem.getAuthid(), responseString -> {
                    Common.followList.add(new FollowList(currentItem.getAuthid()));
                });
            }
        });
        for (int i = 0; i < Common.followList.size(); i++){
            if (Common.followList.get(i).getUserId().equals(Common.userInfoList.getId())){
                holder.add_follow_img.setVisibility(View.GONE);
            }
        }
        holder.add_follow_img.setClickable(true);


        //打赏
        holder.pay_reward_rank.setOnClickListener(v->{

        });
    }

    @Override
    public int getItemCount() {
        Log.d(TAG, "getItemCount: 抖音条目总数"+list.size());
        return list != null ? list.size() : 0;
    }

    public class VideoViewHolder extends RecyclerView.ViewHolder{

        public View rootView;

        public TextView tv_nickname;
        public TextView tv_text;


        CircleImageView authportrait;

        //新头像框
        PortraitFrameView portraitFrameView;
        //ImageView vip_gray;

        ImageView add_follow_img;

        //视频部分
        public ImageView mp_video;
        FrameLayout jz_video_frameLayout;


        //投票
        public CardView vote_card;
        public LinearLayout vote_layout;
        public TextView vote_title_tv;

        TextView tv_poi;


        //点赞,评论和分享
        LikeLayout likeLayout;
        LikeButton likeButton;
        TextView tv_like;
        View comment_view;
        TextView tv_comment;
        View share_view;
        TextView tv_share;

        //打赏
        ImageView pay_reward_rank;

        //音乐名称
        MyMarqueeTextView tv_music_name;
        LinearLayout layout_music_name;



        SampleCoverVideo sampleCoverVideo;


        //带缓存的视频
        public TextView mTitle;//标题
        public ImageView mThumb;//封面图
        public TikTokView mTikTokView;
        public FrameLayout mPlayerContainer;


        public VideoViewHolder(@NonNull View itemView) {
            super(itemView);
            Log.d(TAG, "3测试位置");
            this.rootView = rootView;

            this.mp_video = itemView.findViewById(R.id.jz_video);
            this.jz_video_frameLayout  = itemView.findViewById(R.id.jz_video_frameLayout);

            this.tv_nickname = itemView.findViewById(R.id.tv_nickname);
            this.tv_text = itemView.findViewById(R.id.tv_text);

            this.authportrait = itemView.findViewById(R.id.authportrait);
            this.portraitFrameView = itemView.findViewById(R.id.portraitFrameView);
            //this.vip_gray = itemView.findViewById(R.id.vip_gray);
            this.add_follow_img = itemView.findViewById(R.id.add_follow_img);

//            this.image_pager_slider = itemView.findViewById(R.id.mediaImageSliderView);

            this.tv_poi = itemView.findViewById(R.id.tv_poi);

            this.likeLayout  = itemView.findViewById(R.id.likeLayout);
            this.likeButton = itemView.findViewById(R.id.likeButton);
            this.tv_like = itemView.findViewById(R.id.tv_like);
            this.comment_view = itemView.findViewById(R.id.comment_view);
            this.tv_comment = itemView.findViewById(R.id.tv_comment);
            this.share_view = itemView.findViewById(R.id.share_view);
            this.tv_share = itemView.findViewById(R.id.tv_share);

//            this.eyes_toggle_img = itemView.findViewById(R.id.eyes_toggle_img);
//            this.umExpandLayout = itemView.findViewById(R.id.umExpandLayout);
//
//
//            this.btn_intent_album = itemView.findViewById(R.id.btn_intent_album);
//            this.douyinImageView = itemView.findViewById(R.id.douyinImageView);
//
//            this.douyinAudioView = itemView.findViewById(R.id.douyinAudioView);

            this.pay_reward_rank = itemView.findViewById(R.id.pay_reward_rank);


            this.tv_music_name = itemView.findViewById(R.id.tv_music_name);

            this.layout_music_name = itemView.findViewById(R.id.layout_music_name);

            this.sampleCoverVideo = itemView.findViewById(R.id.sampleCoverVideo);


            //带缓存的视频
            mTikTokView = itemView.findViewById(R.id.tiktok_View);
            mTitle = mTikTokView.findViewById(R.id.tv_title);
            mThumb = mTikTokView.findViewById(R.id.iv_thumb);
            mPlayerContainer = itemView.findViewById(R.id.jz_video_frameLayout);
        }
    }
}
