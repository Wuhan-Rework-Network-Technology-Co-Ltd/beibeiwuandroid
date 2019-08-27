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
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.io.IOException;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import xin.banghua.beiyuan.Main4Branch.PostListActivity;
import xin.banghua.beiyuan.Personage.PersonageActivity;
import xin.banghua.beiyuan.R;

public class LuntanAdapter extends RecyclerView.Adapter<LuntanAdapter.ViewHolder> {
    private static final String TAG = "LuntanAdapter";

    private List<LuntanList> luntanLists;
    private Context mContext;
    ViewHolder viewHolder_btn;
    public LuntanAdapter(List<LuntanList> luntanLists, Context mContext) {
        Log.d(TAG, "LuntanAdapter: start");
        this.luntanLists = luntanLists;
        this.mContext = mContext;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.recyclerview_luntan,viewGroup,false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder viewHolder, int i) {
        final LuntanList currentItem = luntanLists.get(i);

        viewHolder.id.setText(currentItem.getId());
        viewHolder.plateid.setText(currentItem.getPlateid());
        viewHolder.platename.setText(currentItem.getPlatename());
        viewHolder.authid.setText(currentItem.getAuthid());
        viewHolder.authnickname.setText(currentItem.getAuthnickname());
        Glide.with(mContext)
                .asBitmap()
                .load(currentItem.getAuthportrait())
                .into(viewHolder.authportrait);
        viewHolder.posttip.setText(currentItem.getPosttip().isEmpty()?"":currentItem.getPosttip());
        if ((currentItem.getPosttip().isEmpty()?"":currentItem.getPosttip()).equals("加精")){
            Resources resources = mContext.getResources();
            Drawable drawable = resources.getDrawable(R.drawable.jiajing,null);
            viewHolder.posttip.setForeground(drawable);
        }else if((currentItem.getPosttip().isEmpty()?"":currentItem.getPosttip()).equals("置顶")){
            Resources resources = mContext.getResources();
            Drawable drawable = resources.getDrawable(R.drawable.zhiding,null);
            viewHolder.posttip.setForeground(drawable);
        }
        viewHolder.posttitle.setText(currentItem.getPosttitle());
        viewHolder.posttext.setText(currentItem.getPosttext());
        if (currentItem.getPostpicture().length==0){
            viewHolder.postpicture.setVisibility(View.GONE);
        }else {
            Glide.with(mContext)
                    .asBitmap()
                    .load(currentItem.getPostpicture()[0])
                    .into(viewHolder.postpicture);
        }
        viewHolder.like.setText("赞"+currentItem.getLike());
        viewHolder.like.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: clicked on: " + currentItem.getId());
                //Toast.makeText(mContext, mUserID.get(i) + mUserNickName.get(i), Toast.LENGTH_LONG).show();
                viewHolder_btn = viewHolder;
                like("https://applet.banghua.xin/app/index.php?i=99999&c=entry&a=webapp&do=luntanlike&m=socialchat",currentItem.getId());
            }
        });
        viewHolder.favorite.setText(currentItem.getFavorite());
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

        viewHolder.posttitle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intentPostlist(v,currentItem);
            }
        });
        viewHolder.posttext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intentPostlist(v,currentItem);
            }
        });
        viewHolder.postpicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intentPostlist(v,currentItem);
            }
        });

//        viewHolder.luntanLayout.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                intentPostlist(v,currentItem);
//            }
//        });

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
        TextView posttitle;
        TextView posttext;
        ImageView postpicture;
        TextView like;
        TextView favorite;
        TextView time;

        RelativeLayout luntanLayout;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            id = itemView.findViewById(R.id.id);
            plateid = itemView.findViewById(R.id.plateid);
            platename = itemView.findViewById(R.id.platename);
            authid = itemView.findViewById(R.id.authid);
            authnickname = itemView.findViewById(R.id.authnickname);
            authportrait = itemView.findViewById(R.id.authportrait);
            posttip = itemView.findViewById(R.id.posttip);
            posttitle = itemView.findViewById(R.id.posttitle);
            posttext = itemView.findViewById(R.id.posttext);
            postpicture = itemView.findViewById(R.id.postpicture);
            like = itemView.findViewById(R.id.like);
            favorite = itemView.findViewById(R.id.favorite);
            time = itemView.findViewById(R.id.time);

            luntanLayout = itemView.findViewById(R.id.luntanLayout);


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
                    viewHolder_btn.like.setText("赞"+msg.obj.toString());
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
