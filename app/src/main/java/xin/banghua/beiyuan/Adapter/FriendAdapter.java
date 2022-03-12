package xin.banghua.beiyuan.Adapter;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import io.agora.chatroom.util.PortraitFrameView;
import io.rong.imkit.RongIM;
import xin.banghua.beiyuan.CircleImageViewExtension;
import xin.banghua.beiyuan.Common;
import xin.banghua.beiyuan.Personage.PersonageActivity;
import xin.banghua.beiyuan.R;

public class FriendAdapter extends RecyclerView.Adapter<FriendAdapter.ViewHolder> implements Filterable {
    private static final String TAG = "FriendAdapter";
    //用户id,头像，昵称
//    private ArrayList<String> mUserID = new ArrayList<>();
//    private ArrayList<String> mUserPortrait = new ArrayList<>();
//    private ArrayList<String> mUserNickName = new ArrayList<>();


    private List<FriendList> friendList;
    private List<FriendList> friendListFull;

    Integer current_timestamp = Math.round(new Date().getTime()/1000);

    private Context mContext;

    public FriendAdapter(Context mContext, List<FriendList> friendList) {

        this.mContext = mContext;
        this.friendList = friendList;
        this.friendListFull = new ArrayList<>(friendList);
    }
    public void swapData(List<FriendList> friendList) {

        this.friendList = friendList;
        this.friendListFull = new ArrayList<>(friendList);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.recyclerview_haoyou,viewGroup,false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }
    @Override
    public void onViewAttachedToWindow(@NonNull ViewHolder viewHolder) {
        super.onViewAttachedToWindow(viewHolder);
        viewHolder.portraitFrameView.setPortraitFrame(viewHolder.portraitFrameView.getTag().toString());
    }
    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder,final int i) {
        final FriendList currentItem = friendList.get(i);

        viewHolder.userID.setText(currentItem.getmUserID());



//        if (currentItem.getmVip().equals("VIP")) {
//            viewHolder.userPortrait.isVIP(true,mContext.getResources(),true);
//        }
        //现在vip传过来的是时间
        viewHolder.vip_gray.setVisibility(View.VISIBLE);
        if (TextUtils.isEmpty(currentItem.getmSVip())){
            if (TextUtils.isEmpty(currentItem.getmVip())){
                viewHolder.vip_gray.setVisibility(View.VISIBLE);
                Glide.with(mContext)
                        .asBitmap()
                        .load(R.drawable.ic_vip_gray)
                        .into(viewHolder.vip_gray);
            }else {
                int vip_time = Integer.parseInt(currentItem.getmVip());
                if (vip_time > current_timestamp) {
                    //vipicon分级
                    Log.d("会员时长", ((Integer.parseInt(currentItem.getmVip())) - current_timestamp)+"");
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
            int svip_time = Integer.parseInt(currentItem.getmSVip());
            if (svip_time > current_timestamp) {
                //vipicon分级
                Log.d("会员时长", ((Integer.parseInt(currentItem.getmSVip())) - current_timestamp)+"");
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
                if (TextUtils.isEmpty(currentItem.getmVip())){
                    Glide.with(mContext)
                            .asBitmap()
                            .load(R.drawable.ic_vip_gray)
                            .into(viewHolder.vip_gray);
                }else {
                    int vip_time = Integer.parseInt(currentItem.getmVip());
                    if (vip_time > current_timestamp) {
                        //vipicon分级
                        Log.d("会员时长", ((Integer.parseInt(currentItem.getmVip())) - current_timestamp)+"");
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


        Log.d(TAG, "onBindViewHolder: 加载好友头像框"+currentItem.getPortraitframe());
        viewHolder.portraitFrameView.setPortraitFrame(currentItem.getPortraitframe());
        viewHolder.portraitFrameView.setTag(currentItem.getPortraitframe());
        Glide.with(mContext)
                .asBitmap()
                .load(Common.getOssResourceUrl(currentItem.getmUserPortrait()))
                .into(viewHolder.userPortrait);
        viewHolder.userNickName.setText(currentItem.getmUserNickName());
        viewHolder.userLeaveWords.setText("");

        viewHolder.userAttributes.setText(currentItem.getmUserAge()+" | "+currentItem.getmUserGender()+" | "+currentItem.mUserRegion+" | "+currentItem.getmUserProperty());

        viewHolder.haoyouLayout.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: rongyun开始");
                //启动会话界面
                if (RongIM.getInstance() != null) {
                    RongIM.getInstance().startPrivateChat(mContext, currentItem.getmUserID(), currentItem.getmUserNickName());
                }
            }
        });
        viewHolder.userinfo_btn.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: rongyun开始");
                Intent intent = new Intent(v.getContext(), PersonageActivity.class);
                intent.putExtra("userID",currentItem.getmUserID());
                Log.d(TAG, "onClick: 跳转个人页面");
                v.getContext().startActivity(intent);
            }
        });

    }

    @Override
    public int getItemCount() {
        return friendList.size();
    }

    //过滤器
    @Override
    public Filter getFilter() {
        return filter;
    }

    //过滤器
    private Filter filter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            List<FriendList> filteredList = new ArrayList();

            if (constraint == null || constraint.length() == 0){
                filteredList.addAll(friendListFull);
            }else {
                String filterPattern = constraint.toString().trim();
                for (FriendList item : friendListFull){
                    if (item.getmUserNickName().contains(filterPattern)){
                        filteredList.add(item);
                    }
                }
            }
            FilterResults filterResults = new FilterResults();
            filterResults.values = filteredList;
            return filterResults;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            friendList.clear();
            friendList.addAll((List) results.values);
            notifyDataSetChanged();
        }
    };

    public class ViewHolder extends RecyclerView.ViewHolder{

        TextView userID;
        CircleImageViewExtension userPortrait;
        TextView userNickName;
        TextView userLeaveWords;
        Button userinfo_btn;

        TextView userAttributes;

        RelativeLayout haoyouLayout;

        ImageView vip_gray;
        ImageView vip_diamond;
        ImageView vip_black;
        ImageView vip_white;

        PortraitFrameView portraitFrameView;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            portraitFrameView = itemView.findViewById(R.id.portraitFrameView);

            userID = itemView.findViewById(R.id.userID);
            userPortrait = itemView.findViewById(R.id.authportrait);
            userNickName = itemView.findViewById(R.id.userNickName);
            userLeaveWords = itemView.findViewById(R.id.userLeaveWords);
            userinfo_btn = itemView.findViewById(R.id.userinfo_btn);

            userAttributes = itemView.findViewById(R.id.userAttribtes);

            haoyouLayout = itemView.findViewById(R.id.haoyou_layout);

            vip_gray = itemView.findViewById(R.id.vip_gray);
            vip_diamond = itemView.findViewById(R.id.vip_diamond);
            vip_black = itemView.findViewById(R.id.vip_black);
            vip_white = itemView.findViewById(R.id.vip_white);
        }
    }


    /**
     * 根据分类的首字母的Char ascii值获取其第一次出现该首字母的位置
     */
    public int getPositionForSection(int section) {
        for (int i = 0; i < getItemCount(); i++) {
            String sortStr = friendList.get(i).getLetters();
            char firstChar = sortStr.toUpperCase().charAt(0);
            if (firstChar == section) {
                return i;
            }
        }
        return -1;
    }
}
