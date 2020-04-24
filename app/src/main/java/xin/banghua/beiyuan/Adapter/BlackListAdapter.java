package xin.banghua.beiyuan.Adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import io.rong.imkit.RongIM;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import xin.banghua.beiyuan.Personage.PersonageActivity;
import xin.banghua.beiyuan.R;
import xin.banghua.beiyuan.SharedPreferences.SharedHelper;

public class BlackListAdapter extends RecyclerView.Adapter<BlackListAdapter.ViewHolder> implements Filterable {
    private static final String TAG = "BlackListAdapter";
    //用户id,头像，昵称
//    private ArrayList<String> mUserID = new ArrayList<>();
//    private ArrayList<String> mUserPortrait = new ArrayList<>();
//    private ArrayList<String> mUserNickName = new ArrayList<>();


    private List<FriendList> friendList;
    private List<FriendList> friendListFull;


    private Context mContext;

    public BlackListAdapter(Context mContext, List<FriendList> friendList) {

        this.mContext = mContext;
        this.friendList = friendList;
        this.friendListFull = new ArrayList<>(friendList);
    }

    public void swapData( List<FriendList> friendList) {

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
    public void onBindViewHolder(@NonNull ViewHolder viewHolder,final int i) {
        final FriendList currentItem = friendList.get(i);

        viewHolder.userID.setText(currentItem.getmUserID());
        Glide.with(mContext)
                .asBitmap()
                .load(currentItem.getmUserPortrait())
                .into(viewHolder.userPortrait);
        viewHolder.userNickName.setText(currentItem.getmUserNickName());
        viewHolder.userLeaveWords.setText("");

        viewHolder.userAttributes.setText(currentItem.getmUserAge()+" | "+currentItem.getmUserGender()+" | "+currentItem.mUserRegion+" | "+currentItem.getmUserProperty());

        viewHolder.haoyouLayout.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: rongyun开始");
                //跳转个人信息
                Log.d(TAG, "onClick: rongyun开始");
                Intent intent = new Intent(v.getContext(), PersonageActivity.class);
                intent.putExtra("userID",currentItem.getmUserID());
                Log.d(TAG, "onClick: 跳转个人页面");
                v.getContext().startActivity(intent);
            }
        });

        viewHolder.userinfo_btn.setCompoundDrawables(null,null,null,null);
        viewHolder.userinfo_btn.setText("删");
        viewHolder.userinfo_btn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                //删除黑名单
                deleteBlackList("https://applet.banghua.xin/app/index.php?i=99999&c=entry&a=webapp&do=deleteblacklist&m=socialchat",currentItem.getmUserID());
                if (friendList.size() > 0) {
                    friendList.remove(i);
                    notifyDataSetChanged();
                }
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
        CircleImageView userPortrait;
        TextView userNickName;
        TextView userLeaveWords;
        Button userinfo_btn;

        TextView userAttributes;

        RelativeLayout haoyouLayout;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            userID = itemView.findViewById(R.id.userID);
            userPortrait = itemView.findViewById(R.id.authportrait);
            userNickName = itemView.findViewById(R.id.userNickName);
            userLeaveWords = itemView.findViewById(R.id.userLeaveWords);
            userinfo_btn = itemView.findViewById(R.id.userinfo_btn);

            userAttributes = itemView.findViewById(R.id.userAttribtes);

            haoyouLayout = itemView.findViewById(R.id.haoyou_layout);
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
            if (msg.what==1){
                Toast.makeText(mContext, "删除成功", Toast.LENGTH_LONG).show();
            }
        }
    };
    //TODO okhttp删除黑名单
    public void deleteBlackList(final String url,final String yourid){
        new Thread(new Runnable() {
            @Override
            public void run(){
                SharedHelper shuserinfo = new SharedHelper(mContext);
                String myid = shuserinfo.readUserInfo().get("userID");

                OkHttpClient client = new OkHttpClient();
                RequestBody formBody = new FormBody.Builder()
                        .add("myid", myid)
                        .add("yourid", yourid)
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
