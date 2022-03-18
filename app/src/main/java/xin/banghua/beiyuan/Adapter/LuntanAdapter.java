package xin.banghua.beiyuan.Adapter;

import static xin.banghua.beiyuan.utils.ThreadUtils.runOnUiThread;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.orhanobut.dialogplus.DialogPlus;
import com.zolad.zoominimageview.ZoomInImageView;

import java.io.IOException;
import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import io.agora.chatroom.activity.ChatRoomActivity;
import io.agora.chatroom.util.PortraitFrameView;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import xin.banghua.beiyuan.Main4Branch.ImagerPagerActivity;
import xin.banghua.beiyuan.Main4Branch.PostListActivity;
import xin.banghua.beiyuan.Main4Branch.VideoPlayActivity;
import xin.banghua.beiyuan.Main5Branch.SomeonesluntanActivity;
import xin.banghua.beiyuan.Personage.PersonageActivity;
import xin.banghua.beiyuan.R;
import xin.banghua.beiyuan.Signin.SigninActivity;
import xin.banghua.beiyuan.comment.CommentDialog;
import xin.banghua.beiyuan.custom_ui.CustomVideoView;
import xin.banghua.beiyuan.custom_ui.NiceImageView;
import xin.banghua.beiyuan.Common;
import xin.banghua.beiyuan.utils.OkHttpInstance;
import xin.banghua.beiyuan.utils.OkHttpResponseCallBack;

public class LuntanAdapter extends RecyclerView.Adapter<LuntanAdapter.ViewHolder> {
    private static final String TAG = "LuntanAdapter";


    Integer current_timestamp = Math.round(new Date().getTime()/1000);

    public List<LuntanList> luntanLists;
    private Context mContext;
    ViewHolder viewHolder_btn;
    public LuntanAdapter(List<LuntanList> luntanLists, Context mContext) {
        Log.d(TAG, "LuntanAdapter: start");
        this.luntanLists = luntanLists;
        this.mContext = mContext;
        luntanAdapter = this;
    }
    //替换数据，并更新
    public void swapData(List<LuntanList> luntanLists){
        this.luntanLists = luntanLists;
        notifyDataSetChanged();
    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.guangchang_fragment_adapter,viewGroup,false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    HashMap<Integer, LuntanAdapter.ViewHolder> viewHolders = new HashMap<Integer, LuntanAdapter.ViewHolder>();
    public LuntanAdapter.ViewHolder getViewHolder(int position){
        return viewHolders.get(position);
    }

    @Override
    public void onViewAttachedToWindow(@NonNull ViewHolder viewHolder) {
        super.onViewAttachedToWindow(viewHolder);
        viewHolder.portraitFrameView.setPortraitFrame(viewHolder.portraitFrameView.getTag().toString());
    }
    LuntanAdapter luntanAdapter;
    @Override
    public void onBindViewHolder(@NonNull final ViewHolder viewHolder, int i) {
        int currentPosition = i;
        viewHolders.put(i,((LuntanAdapter.ViewHolder) viewHolder));
        final LuntanList currentItem = luntanLists.get(i);

        String authattributes_string = currentItem.getAuthage()+"岁 | "+currentItem.getAuthgender()+" | "+currentItem.getAuthregion()+" | "+currentItem.getAuthproperty();
        //viewHolder.authattributes.setText(authattributes_string);

//        viewHolder.id.setText(currentItem.getId());
//        viewHolder.plateid.setText(currentItem.getPlateid());
//        viewHolder.platename.setText(currentItem.getPlatename());
//        viewHolder.authid.setText(currentItem.getAuthid());
        viewHolder.authnickname.setText(currentItem.getAuthnickname());

        viewHolder.portraitFrameView.setPortraitFrame(currentItem.getPortraitframe());
        viewHolder.portraitFrameView.setTag(currentItem.getPortraitframe());
        Glide.with(mContext)
                .asBitmap()
                .load(Common.getOssResourceUrl(currentItem.getAuthportrait()))
                .into(viewHolder.authportrait);

        viewHolder.online_tv.setText(currentItem.getOnline());
        if (currentItem.getOnline().equals("在线")){
            Glide.with(mContext).load(R.mipmap.green_point).into(viewHolder.online_img);
        }else {
            Glide.with(mContext).load(R.mipmap.gray_point).into(viewHolder.online_img);
        }

        if (currentItem.platename.equals("招募令")){
            viewHolder.goToRoomBtn.setVisibility(View.VISIBLE);
            viewHolder.goToRoomBtn.setOnClickListener(v -> {
                Intent intent = new Intent(mContext, ChatRoomActivity.class);
                intent.putExtra(ChatRoomActivity.BUNDLE_KEY_CHANNEL_ID, currentItem.getAuthid());
                intent.putExtra(ChatRoomActivity.BUNDLE_KEY_CHANNEL_TYPE, "处CP");
                intent.putExtra(ChatRoomActivity.BUNDLE_KEY_BACKGROUND_RES, io.agora.chatroom.R.mipmap.bg_channel_0);
                //intent.putExtra(ChatRoomActivity.BUNDLE_KEY_BACKGROUND_RES, Constant.sRoomBG);
                mContext.startActivity(intent);
            });
        }else {
            viewHolder.goToRoomBtn.setVisibility(View.GONE);
        }

        //GOTO  会员标识
        //现在vip传过来的是时间
        viewHolder.vip_gray.setVisibility(View.VISIBLE);
        if (TextUtils.isEmpty(currentItem.getAuthsvip())){
            if (TextUtils.isEmpty(currentItem.getAuthvip())){

                Glide.with(mContext)
                        .asBitmap()
                        .load(R.drawable.ic_vip_gray)
                        .into(viewHolder.vip_gray);
            }else {
                Log.d("会员时间",currentItem.getAuthvip()+"");
                int vip_time = Integer.parseInt(currentItem.getAuthvip()+"");
                if (vip_time > current_timestamp) {
                    //vipicon分级
                    Log.d("会员时长", ((vip_time - current_timestamp) + ""));
                    if ((vip_time - current_timestamp) < 3600 * 24 * 30) {
                        Glide.with(mContext)
                                .asBitmap()
                                .load(R.drawable.ic_vip_diamond)
                                .into(viewHolder.vip_gray);
                    } else if ((vip_time - current_timestamp) < 3600 * 24 * 180) {
                        Glide.with(mContext)
                                .asBitmap()
                                .load(R.drawable.ic_vip_black)
                                .into(viewHolder.vip_gray);
                    } else {
                        Glide.with(mContext)
                                .asBitmap()
                                .load(R.drawable.ic_vip_white)
                                .into(viewHolder.vip_gray);
                    }
                } else {
                    Glide.with(mContext)
                            .asBitmap()
                            .load(R.drawable.ic_vip_gray)
                            .into(viewHolder.vip_gray);
                }
            }
        }else {
            Log.d("会员时间",currentItem.getAuthsvip()+"");
            int svip_time = Integer.parseInt(currentItem.getAuthsvip()+"");
            if (svip_time > current_timestamp) {
                //vipicon分级
                Log.d("会员时长", ((svip_time - current_timestamp) + ""));
                if ((svip_time - current_timestamp) < 3600 * 24 * 30) {
                    Glide.with(mContext)
                            .asBitmap()
                            .load(R.drawable.ic_svip_diamond)
                            .into(viewHolder.vip_gray);
                } else if ((svip_time - current_timestamp) < 3600 * 24 * 180) {
                    Glide.with(mContext)
                            .asBitmap()
                            .load(R.drawable.ic_svip_black)
                            .into(viewHolder.vip_gray);
                } else {
                    Glide.with(mContext)
                            .asBitmap()
                            .load(R.drawable.ic_svip_white)
                            .into(viewHolder.vip_gray);
                }
            } else {
                if (TextUtils.isEmpty(currentItem.getAuthvip())){
                    viewHolder.vip_gray.setVisibility(View.VISIBLE);
                    Glide.with(mContext)
                            .asBitmap()
                            .load(R.drawable.ic_vip_gray)
                            .into(viewHolder.vip_gray);
                }else {
                    Log.d("会员时间",currentItem.getAuthvip()+"");
                    int vip_time = Integer.parseInt(currentItem.getAuthvip()+"");
                    if (vip_time > current_timestamp) {
                        //vipicon分级
                        Log.d("会员时长", ((vip_time - current_timestamp) + ""));
                        if ((vip_time - current_timestamp) < 3600 * 24 * 30) {
                            Glide.with(mContext)
                                    .asBitmap()
                                    .load(R.drawable.ic_vip_diamond)
                                    .into(viewHolder.vip_gray);
                        } else if ((vip_time - current_timestamp) < 3600 * 24 * 180) {
                            Glide.with(mContext)
                                    .asBitmap()
                                    .load(R.drawable.ic_vip_black)
                                    .into(viewHolder.vip_gray);
                        } else {
                            Glide.with(mContext)
                                    .asBitmap()
                                    .load(R.drawable.ic_vip_white)
                                    .into(viewHolder.vip_gray);
                        }
                    } else {
                        Glide.with(mContext)
                                .asBitmap()
                                .load(R.drawable.ic_vip_gray)
                                .into(viewHolder.vip_gray);
                    }
                }
            }
        }

        
        
        
        
        
        
        
        
        viewHolder.posttip.setText(currentItem.getPosttip().isEmpty()?"":currentItem.getPosttip());
        if (currentItem.getPosttip().equals("加精")){
//            Resources resources = mContext.getResources();
//            Drawable drawable = resources.getDrawable(R.drawable.ic_essence,null);
//            drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
//            viewHolder.posttip.setCompoundDrawables(drawable,null,null,null);
            viewHolder.posttip.setVisibility(View.VISIBLE);
            viewHolder.posttop.setVisibility(View.GONE);
        }else if(currentItem.getPosttip().equals("置顶")){
//            Resources resources = mContext.getResources();
//            Drawable drawable = resources.getDrawable(R.drawable.ic_ontop,null);
//            drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
//            viewHolder.posttip.setCompoundDrawables(drawable,null,null,null);
            viewHolder.posttip.setVisibility(View.GONE);
            viewHolder.posttop.setVisibility(View.VISIBLE);
        }else if (currentItem.getPosttip().equals("置顶,加精")){
//            Resources resources = mContext.getResources();
//            Drawable drawable1 = resources.getDrawable(R.drawable.ic_essence,null);
//            drawable1.setBounds(0, 0, drawable1.getMinimumWidth(), drawable1.getMinimumHeight());
//            Drawable drawable2 = resources.getDrawable(R.drawable.ic_ontop,null);
//            drawable2.setBounds(0, 0, drawable2.getMinimumWidth(), drawable2.getMinimumHeight());
//            viewHolder.posttip.setCompoundDrawables(drawable1,null,drawable2,null);
            viewHolder.posttip.setVisibility(View.VISIBLE);
            viewHolder.posttop.setVisibility(View.VISIBLE);
        }else {
            //必须加上，否则会错乱
            //viewHolder.posttip.setCompoundDrawables(null,null,null,null);
            viewHolder.posttip.setVisibility(View.GONE);
            viewHolder.posttop.setVisibility(View.GONE);
        }
        if (TextUtils.isEmpty(currentItem.getPosttitle())){
            viewHolder.posttitle.setVisibility(View.GONE);
        }else {
            viewHolder.posttitle.setVisibility(View.VISIBLE);
            viewHolder.posttitle.setText(currentItem.getPosttitle());
        }
        if (currentItem.getPosttext().length()>50) {
            viewHolder.posttext.setText(currentItem.getPosttext().substring(0, 50)+"......");
            viewHolder.detail_content.setVisibility(View.VISIBLE);
            viewHolder.detail_content.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    viewHolder.posttext.setText(currentItem.getPosttext());
                    viewHolder.detail_content.setVisibility(View.GONE);
                }
            });
        }else {
            viewHolder.posttext.setText(currentItem.getPosttext());
            viewHolder.detail_content.setVisibility(View.GONE);
        }
        if (currentItem.getPosttext().length()>50) {
            viewHolder.posttext.setText(currentItem.getPosttext().substring(0, 50)+"  查看全文");
            viewHolder.posttext.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    viewHolder.posttext.setText(currentItem.getPosttext());
                }
            });
        }else {
            viewHolder.posttext.setText(currentItem.getPosttext());
        }
        if (currentItem.getPostpicture().length!=1){
            viewHolder.postpicture.setVisibility(View.GONE);
        }else if (currentItem.getPostpicture().length==1){
            Glide.with(mContext)
                    .asBitmap()
                    .load(Common.getOssResourceUrl(currentItem.getPostpicture()[0]))
                    .into(viewHolder.postpicture);
            viewHolder.postpicture.setVisibility(View.VISIBLE);
            viewHolder.postpicture.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(v.getContext(), ImagerPagerActivity.class);
                    intent.putExtra("luntanlist", currentItem);
                    intent.putExtra("image_index", 0);
                    v.getContext().startActivity(intent);
                }
            });
        }
        if (currentItem.getPostpicture().length<2){
            viewHolder.postpicture1.setVisibility(View.GONE);
        }else {
            Glide.with(mContext)
                    .asBitmap()
                    .load(Common.getOssResourceUrl(currentItem.getPostpicture()[0]))
                    .into(viewHolder.postpicture1);
            viewHolder.postpicture1.setVisibility(View.VISIBLE);
            viewHolder.postpicture1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(v.getContext(), ImagerPagerActivity.class);
                    intent.putExtra("luntanlist", currentItem);
                    intent.putExtra("image_index", 0);
                    v.getContext().startActivity(intent);
                }
            });
        }
        if (currentItem.getPostpicture().length<2){
            viewHolder.postpicture2.setVisibility(View.GONE);
        }else {
            Glide.with(mContext)
                    .asBitmap()
                    .load(Common.getOssResourceUrl(currentItem.getPostpicture()[1]))
                    .into(viewHolder.postpicture2);
            viewHolder.postpicture2.setVisibility(View.VISIBLE);
            viewHolder.postpicture2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(v.getContext(), ImagerPagerActivity.class);
                    intent.putExtra("luntanlist", currentItem);
                    intent.putExtra("image_index", 1);
                    v.getContext().startActivity(intent);
                }
            });
        }
        if (currentItem.getPostpicture().length<3){
            viewHolder.postpicture3.setVisibility(View.GONE);
        }else {
            Glide.with(mContext)
                    .asBitmap()
                    .load(Common.getOssResourceUrl(currentItem.getPostpicture()[2]))
                    .into(viewHolder.postpicture3);
            viewHolder.postpicture3.setVisibility(View.VISIBLE);
            viewHolder.postpicture3.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(v.getContext(), ImagerPagerActivity.class);
                    intent.putExtra("luntanlist", currentItem);
                    intent.putExtra("image_index", 2);
                    v.getContext().startActivity(intent);
                }
            });
        }



        viewHolder.like.setText(""+currentItem.getLike());
        viewHolder.like_layout.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: clicked on: " + currentItem.getId());
                //Toast.makeText(mContext, mUserID.get(i) + mUserNickName.get(i), Toast.LENGTH_LONG).show();
                viewHolder_btn = viewHolder;
                like("https://console.banghua.xin/app/index.php?i=99999&c=entry&a=webapp&do=luntanlike&m=socialchat",currentItem.getId());
            }
        });
        //viewHolder.favorite.setText(currentItem.getFavorite());
        viewHolder.time.setText(currentItem.getTime());


        viewHolder.authnickname.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), PersonageActivity.class);
                intent.putExtra("userID",currentItem.getAuthid());
                v.getContext().startActivity(intent);
            }
        });
        viewHolder.authportrait.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), PersonageActivity.class);
                intent.putExtra("userID",currentItem.getAuthid());
                v.getContext().startActivity(intent);
            }
        });

//        viewHolder.posttitle.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                intentPostlist(v,currentItem);
//            }
//        });
//        viewHolder.posttext.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                intentPostlist(v,currentItem);
//            }
//        });
//        viewHolder.postpicture.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                intentPostlist(v,currentItem);
//            }
//        });

//        viewHolder.luntanLayout.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                intentPostlist(v,currentItem);
//            }
//        });
        viewHolder.menu_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InformBlacklistAdapter adapter = new InformBlacklistAdapter(mContext, "post", currentItem.getId(), currentItem.getAuthid(), new OkHttpResponseCallBack() {
                    @Override
                    public void getResponseString(String responseString) {
                        runOnUiThread(()->{
                            luntanLists.remove(currentPosition);
                            luntanAdapter.notifyDataSetChanged();
                        });
                    }
                });
                final DialogPlus dialog = DialogPlus.newDialog(mContext)
                        .setAdapter(adapter)
                        .setFooter(R.layout.inform_blacklist_foot)
                        .setExpanded(true)  // This will enable the expand feature, (similar to android L share dialog)
                        .create();
                dialog.show();
                View view = dialog.getFooterView();
                Button cancel = view.findViewById(R.id.inform_blacklist_cancel_btn);
                cancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });
            }
        });

        CommentDialog commentDialog = new CommentDialog(mContext);
        viewHolder.comment_tv.setText(""+currentItem.getComment_sum());
        viewHolder.comment_layout.setOnClickListener(view -> {
            if (Common.myID==null){
                android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(mContext)
                        .setTitle("登录以查看更多内容！")
                        .setPositiveButton("去登录", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                Intent intent = new Intent(mContext, SigninActivity.class);
                                mContext.startActivity(intent);
                            }
                        }).setNegativeButton("取消", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.dismiss();
                            }
                        });
                builder.create().show();
            }else {
                commentDialog.loadComment(currentItem);
                commentDialog.startLoadComment();
            }
        });

        if (SomeonesluntanActivity.selectedComment!=null){
            commentDialog.loadComment(currentItem);
            commentDialog.loadSelectedComment(SomeonesluntanActivity.selectedComment.getId());
        }


        viewHolder.userAge.setText(" "+currentItem.getAuthage());
        viewHolder.userRegion.setText(currentItem.getAuthregion());
        viewHolder.userProperty.setText(currentItem.getAuthproperty());
        if (currentItem.getAuthgender().equals("男")){
            Resources resources = mContext.getResources();
            Drawable drawable = resources.getDrawable(R.mipmap.bboy,null);
            //((UserinfoHolder) viewHolder).userGender.setForeground(drawable);
            drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
            viewHolder.userAge.setCompoundDrawables(drawable, null, null, null);
            viewHolder.userAge.setBackgroundResource(R.mipmap.h_sex_male);
        }else {
            Resources resources = mContext.getResources();
            Drawable drawable = resources.getDrawable(R.mipmap.ggirl,null);
            //((UserinfoHolder) viewHolder).userGender.setForeground(drawable);
            drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
            viewHolder.userAge.setCompoundDrawables(drawable, null, null, null);
            viewHolder.userAge.setBackgroundResource(R.mipmap.h_sex);
        }


        Log.d(TAG, "onBindViewHolder: 帖子视频"+currentItem.getPostvideo());
        try {
            if (!currentItem.getPostvideo().equals("https://oss.banghua.xin/0")){
                try {
                    if (Integer.parseInt(currentItem.getHeight())>=Integer.parseInt(currentItem.getWidth())){
                        viewHolder.video_layout.getLayoutParams().height = 700;
                        int width = Math.round(700*((Float.parseFloat(currentItem.getWidth())/Float.parseFloat(currentItem.getHeight()))));
                        viewHolder.video_layout.getLayoutParams().width = width;

                        viewHolder.cover_view.getLayoutParams().height = 700;
                        viewHolder.cover_view.getLayoutParams().width = width;
                    }else {
                        viewHolder.video_layout.getLayoutParams().width = 700;
                        int height = Math.round(700*((Float.parseFloat(currentItem.getHeight())/Float.parseFloat(currentItem.getWidth()))));
                        viewHolder.video_layout.getLayoutParams().height = height;

                        viewHolder.cover_view.getLayoutParams().width = 700;
                        viewHolder.cover_view.getLayoutParams().height = height;
                    }
                }catch (Exception e){
                    Log.e(TAG, "onBindViewHolder: 异常"+e.toString());
                }

                Glide.with(mContext).load(currentItem.getCover()).into(viewHolder.cover_view);

                viewHolder.video_layout.setOnClickListener(v -> {
                    Log.d(TAG, "luntanList.getPlay_once(): "+currentItem.getPlay_once());
                    Intent intent = new Intent(mContext, VideoPlayActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.putExtra("luntanList", (Serializable) currentItem);
                    mContext.startActivity(intent);
                });
                viewHolder.video_layout.setVisibility(View.VISIBLE);
                if (currentItem.getStartVideo()){
                    viewHolder.video_layout.addView(CustomVideoView.getInstance(mContext,currentItem),2);
                }
            }else {
                viewHolder.video_layout.setVisibility(View.GONE);
            }
        }catch (Exception e){
            Log.e(TAG, "onBindViewHolder: 异常"+ e.toString());
        }


        viewHolder.follow_img.setVisibility(View.VISIBLE);
        Log.d(TAG, "onBindViewHolder: 关注的人"+Common.followList.size());
        for (int j = 0;j < Common.followList.size();j++){
            Log.d(TAG, "onBindViewHolder: 关注的人"+Common.followList.get(j));
            if (Common.followList.get(j).getUserId().equals(currentItem.getAuthid())){
                viewHolder.follow_img.setVisibility(View.GONE);
            }
        }

        viewHolder.follow_img.setOnClickListener(v -> {
            viewHolder.follow_img.setVisibility(View.GONE);
            Toast.makeText(mContext,"关注成功",Toast.LENGTH_SHORT).show();
            OkHttpInstance.follow(currentItem.getAuthid(), responseString -> {
                Common.followList.add(new FollowList(currentItem.getAuthid()));
                notifyDataSetChanged();
            });
        });
    }
    public void intentPostlist(View v,LuntanList currentItem){
        Intent intent = new Intent(v.getContext(), PostListActivity.class);
        intent.putExtra("postid",currentItem.getId());
        intent.putExtra("plateid",currentItem.getPlateid());
        intent.putExtra("platename",currentItem.getPlatename());
        intent.putExtra("authid",currentItem.getAuthid());
        intent.putExtra("authnickname",currentItem.getAuthnickname());
        intent.putExtra("authportrait",currentItem.getAuthportrait());
        intent.putExtra("posttip",currentItem.getPosttip());
        intent.putExtra("posttitle",currentItem.getPosttitle());
        intent.putExtra("posttext",currentItem.getPosttext());
        intent.putExtra("postpicture",currentItem.getPostpicture());
        intent.putExtra("like",currentItem.getLike());
        intent.putExtra("favorite",currentItem.getFavorite());
        intent.putExtra("time",currentItem.getTime());
        v.getContext().startActivity(intent);
    }


    @Override
    public int getItemCount() {
        return luntanLists.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView id;
        TextView plateid;
        TextView platename;
        TextView authid;
        TextView authnickname;
        CircleImageView authportrait;
        TextView posttip;
        LinearLayout posttop;
        TextView posttitle;
        TextView posttext;
        ZoomInImageView postpicture;
        ZoomInImageView postpicture1;
        ZoomInImageView postpicture2;
        ZoomInImageView postpicture3;
        TextView like;
        TextView comment_tv;
        TextView favorite;
        TextView time;

        RelativeLayout luntanLayout;
        Button detail_content;
        TextView authattributes;
        Button menu_btn;


        ImageView vip_gray;
        ImageView vip_diamond;
        ImageView vip_black;
        ImageView vip_white;



        TextView userAge;
        TextView userProperty;
        TextView userRegion;

        Button goToRoomBtn;

        LinearLayout comment_layout,like_layout;

        public CustomVideoView videoView;


        public FrameLayout video_layout;


        NiceImageView cover_view;
        ImageView film_cover_img;
        ImageView cover_img;


        TextView online_tv;
        ImageView online_img;
        ImageView follow_img;

        PortraitFrameView portraitFrameView;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            portraitFrameView = itemView.findViewById(R.id.portraitFrameView);
//            id = itemView.findViewById(R.id.id);
//            plateid = itemView.findViewById(R.id.plateid);
//            platename = itemView.findViewById(R.id.platename);
//            authid = itemView.findViewById(R.id.authid);
            comment_layout = itemView.findViewById(R.id.comment_layout);
            like_layout = itemView.findViewById(R.id.like_layout);
            authnickname = itemView.findViewById(R.id.authnickname);
            authportrait = itemView.findViewById(R.id.authportrait);
            posttip = itemView.findViewById(R.id.posttip);
            posttop = itemView.findViewById(R.id.posttop);
            posttitle = itemView.findViewById(R.id.posttitle);
            posttext = itemView.findViewById(R.id.posttext);
            postpicture = itemView.findViewById(R.id.postpicture);
            postpicture1 = itemView.findViewById(R.id.postpicture1);
            postpicture2 = itemView.findViewById(R.id.postpicture2);
            postpicture3 = itemView.findViewById(R.id.postpicture3);
            like = itemView.findViewById(R.id.like);
            comment_tv = itemView.findViewById(R.id.comment_tv);
            favorite = itemView.findViewById(R.id.favorite);
            time = itemView.findViewById(R.id.time);

            luntanLayout = itemView.findViewById(R.id.luntanLayout);

            //authattributes = itemView.findViewById(R.id.authattributes);
            menu_btn = itemView.findViewById(R.id.menu_btn);

            detail_content = itemView.findViewById(R.id.detail_content);


            vip_gray = itemView.findViewById(R.id.vip_gray);
//            vip_diamond = itemView.findViewById(R.id.vip_diamond);
//            vip_black = itemView.findViewById(R.id.vip_black);
//            vip_white = itemView.findViewById(R.id.vip_white);


            userAge = itemView.findViewById(R.id.userAge);
            userProperty = itemView.findViewById(R.id.userProperty);
            userRegion = itemView.findViewById(R.id.userRegion);

            goToRoomBtn = itemView.findViewById(R.id.goToRoomBtn);

            videoView = itemView.findViewById(R.id.player);

            video_layout = itemView.findViewById(R.id.video_layout);
            cover_view = itemView.findViewById(R.id.cover_view);
            film_cover_img = itemView.findViewById(R.id.film_cover_img);
            cover_img = itemView.findViewById(R.id.cover_img);

            online_tv = itemView.findViewById(R.id.online_tv);
            online_img = itemView.findViewById(R.id.online_img);
            follow_img = itemView.findViewById(R.id.follow_img);
        }
    }

    //网络数据部分
//处理返回的数据
    @SuppressLint("HandlerLeak")
    private Handler handler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            //1是用户数据，2是幻灯片
            switch (msg.what){
                case 1:
                    viewHolder_btn.like.setText(""+msg.obj.toString());
                    break;
            }
        }
    };
    //TODO okhttp点赞
    public void like(final String url,final String postid){
        new Thread(new Runnable() {
            @Override
            public void run(){
                OkHttpClient client = new OkHttpClient();
                RequestBody formBody = new FormBody.Builder()
                        .add("postid", postid)
                        .build();
                Request request = new Request.Builder()
                        .url(url)
                        .post(formBody)
                        .build();

                try (Response response = client.newCall(request).execute()) {
                    if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);

                    Message message=handler.obtainMessage();
                    message.obj=response.body().string();
                    message.what=1;
                    Log.d(TAG, "run: Userinfo发送的值"+message.obj.toString());
                    handler.sendMessageDelayed(message,10);
                }catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

}
