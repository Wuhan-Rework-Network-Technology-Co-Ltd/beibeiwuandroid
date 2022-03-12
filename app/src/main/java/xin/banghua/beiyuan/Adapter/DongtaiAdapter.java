package xin.banghua.beiyuan.Adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.orhanobut.dialogplus.DialogPlus;
import com.orhanobut.dialogplus.OnClickListener;
import com.zolad.zoominimageview.ZoomInImageView;

import java.io.IOException;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import xin.banghua.beiyuan.Personage.PersonageActivity;
import xin.banghua.beiyuan.R;
import xin.banghua.beiyuan.Common;

public class DongtaiAdapter extends RecyclerView.Adapter<DongtaiAdapter.ViewHolder>{
    private static final String TAG = "DongtaiAdapter";

    private Context mContext;

    ViewHolder viewHolder_btn;


    private List<DongtaiList> dongtaiLists;

    public DongtaiAdapter(Context mContext,List<DongtaiList> dongtaiList) {
        this.dongtaiLists = dongtaiList;
        this.mContext = mContext;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.recyclerview_dongtai,viewGroup,false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder viewHolder,final int i) {
        Log.d(TAG, "onBindViewHolder: called");
        final DongtaiList currentItem = dongtaiLists.get(i);

        String authattributes_string = currentItem.getAuthage()+"岁 | "+currentItem.getAuthgender()+" | "+currentItem.getAuthregion()+" | "+currentItem.getAuthproperty();
        viewHolder.authattributes.setText(authattributes_string);



        viewHolder.userID.setText(currentItem.getMyid());
        Glide.with(mContext)
                .asBitmap()
                .load(Common.getOssResourceUrl(currentItem.getMyportrait()))
                .into(viewHolder.userPortrait);
        viewHolder.userPortrait.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), PersonageActivity.class);
                intent.putExtra("userID",currentItem.getMyid());
                v.getContext().startActivity(intent);
            }
        });
        viewHolder.userNickName.setText(currentItem.getMynickname());
        viewHolder.dongtaiWord.setText(currentItem.getContext());

        if (currentItem.getPicture1() != ""){
            Glide.with(mContext)
                    .asBitmap()
                    .load(Common.getOssResourceUrl(currentItem.getPicture1()))
                    .into(viewHolder.dongtaiImage1);
            viewHolder.dongtaiImage1.setVisibility(View.VISIBLE);
            viewHolder.dongtaiImage1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final DialogPlus dialog = DialogPlus.newDialog(mContext)
                            .setAdapter(new BaseAdapter() {
                                @Override
                                public int getCount() {
                                    return 0;
                                }

                                @Override
                                public Object getItem(int position) {
                                    return null;
                                }

                                @Override
                                public long getItemId(int position) {
                                    return 0;
                                }

                                @Override
                                public View getView(int position, View convertView, ViewGroup parent) {
                                    return null;
                                }
                            })
                            .setFooter(R.layout.dialog_original_image)
                            .setExpanded(true)  // This will enable the expand feature, (similar to android L share dialog)
                            .create();
                    dialog.show();
                    View view = dialog.getFooterView();
                    ZoomInImageView originalImage = view.findViewById(R.id.originalImage);
                    Glide.with(mContext)
                            .asBitmap()
                            .load(Common.getOssResourceUrl(currentItem.getPicture1()))
                            .into(originalImage);
                    Button dismissdialog_btn = view.findViewById(R.id.cancel);
                    dismissdialog_btn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialog.dismiss();
                        }
                    });
                }
            });
        }else {
            viewHolder.dongtaiImage1.setVisibility(View.GONE);
        }
        if (currentItem.getPicture2() != ""){
            Glide.with(mContext)
                    .asBitmap()
                    .load(Common.getOssResourceUrl(currentItem.getPicture2()))
                    .into(viewHolder.dongtaiImage2);
            viewHolder.dongtaiImage2.setVisibility(View.VISIBLE);
            viewHolder.dongtaiImage2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final DialogPlus dialog = DialogPlus.newDialog(mContext)
                            .setAdapter(new BaseAdapter() {
                                @Override
                                public int getCount() {
                                    return 0;
                                }

                                @Override
                                public Object getItem(int position) {
                                    return null;
                                }

                                @Override
                                public long getItemId(int position) {
                                    return 0;
                                }

                                @Override
                                public View getView(int position, View convertView, ViewGroup parent) {
                                    return null;
                                }
                            })
                            .setFooter(R.layout.dialog_original_image)
                            .setExpanded(true)  // This will enable the expand feature, (similar to android L share dialog)
                            .create();
                    dialog.show();
                    View view = dialog.getFooterView();
                    ZoomInImageView originalImage = view.findViewById(R.id.originalImage);
                    Glide.with(mContext)
                            .asBitmap()
                            .load(Common.getOssResourceUrl(currentItem.getPicture2()))
                            .into(originalImage);
                    Button dismissdialog_btn = view.findViewById(R.id.cancel);
                    dismissdialog_btn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialog.dismiss();
                        }
                    });
                }
            });
        }else {
            viewHolder.dongtaiImage2.setVisibility(View.GONE);
        }
        if (currentItem.getPicture3() != ""){
            Log.d(TAG, "onBindViewHolder: getPicture3"+currentItem.getPicture3());
            Glide.with(mContext)
                    .asBitmap()
                    .load(Common.getOssResourceUrl(currentItem.getPicture3()))
                    .into(viewHolder.dongtaiImage3);
            viewHolder.dongtaiImage3.setVisibility(View.VISIBLE);
            viewHolder.dongtaiImage3.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final DialogPlus dialog = DialogPlus.newDialog(mContext)
                            .setAdapter(new BaseAdapter() {
                                @Override
                                public int getCount() {
                                    return 0;
                                }

                                @Override
                                public Object getItem(int position) {
                                    return null;
                                }

                                @Override
                                public long getItemId(int position) {
                                    return 0;
                                }

                                @Override
                                public View getView(int position, View convertView, ViewGroup parent) {
                                    return null;
                                }
                            })
                            .setFooter(R.layout.dialog_original_image)
                            .setExpanded(true)  // This will enable the expand feature, (similar to android L share dialog)
                            .create();
                    dialog.show();
                    View view = dialog.getFooterView();
                    ZoomInImageView originalImage = view.findViewById(R.id.originalImage);
                    Glide.with(mContext)
                            .asBitmap()
                            .load(Common.getOssResourceUrl(currentItem.getPicture3()))
                            .into(originalImage);
                    Button dismissdialog_btn = view.findViewById(R.id.cancel);
                    dismissdialog_btn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialog.dismiss();
                        }
                    });
                }
            });
        }else {
            viewHolder.dongtaiImage3.setVisibility(View.GONE);
        }


        viewHolder.dongtaiTime.setText(currentItem.getTime());
        viewHolder.like.setText("赞"+currentItem.getLike());
        viewHolder.like.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: clicked on: " + currentItem.getId());
                //Toast.makeText(mContext, mUserID.get(i) + mUserNickName.get(i), Toast.LENGTH_LONG).show();
                viewHolder_btn = viewHolder;
                like("https://console.banghua.xin/app/index.php?i=99999&c=entry&a=webapp&do=guangchanglike&m=socialchat",currentItem.getId());
            }
        });
//        viewHolder.dongtaiLayout.setOnClickListener(new View.OnClickListener(){
//            @Override
//            public void onClick(View v) {
//                Log.d(TAG, "onClick: clicked on: "+currentItem.getId());
//                //Toast.makeText(mContext,mUserID.get(i)+mUserNickName.get(i),Toast.LENGTH_LONG).show();
//            }
//        });
        viewHolder.menu_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InformBlacklistAdapter adapter = new InformBlacklistAdapter(mContext,"circle",currentItem.getId(),currentItem.getMyid());
                final DialogPlus dialog = DialogPlus.newDialog(mContext)
                        .setAdapter(adapter)
                        .setFooter(R.layout.inform_blacklist_foot)
                        .setOnClickListener(new OnClickListener() {
                            @Override
                            public void onClick(DialogPlus dialog, View view) {
                                dialog.dismiss();
                            }
                        })
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
    }

    @Override
    public int getItemCount() {
        return dongtaiLists.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView authattributes;
        Button menu_btn;

        TextView userID;
        CircleImageView userPortrait;
        TextView userNickName;
        TextView dongtaiWord;
        ZoomInImageView dongtaiImage1;
        ZoomInImageView dongtaiImage2;
        ZoomInImageView dongtaiImage3;
        TextView dongtaiTime;
        TextView like;
        RelativeLayout dongtaiLayout;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            userID = itemView.findViewById(R.id.userID);
            userPortrait = itemView.findViewById(R.id.authportrait);
            userNickName = itemView.findViewById(R.id.userNickName);
            dongtaiWord = itemView.findViewById(R.id.dongtaiWord);
            dongtaiImage1 = itemView.findViewById(R.id.dongtaiImage1);
            dongtaiImage2 = itemView.findViewById(R.id.dongtaiImage2);
            dongtaiImage3 = itemView.findViewById(R.id.dongtaiImage3);
            dongtaiTime = itemView.findViewById(R.id.dongtaiTime);
            like = itemView.findViewById(R.id.like);

            dongtaiLayout = itemView.findViewById(R.id.dongtai_layout);

            authattributes = itemView.findViewById(R.id.authattributes);
            menu_btn = itemView.findViewById(R.id.menu_btn);
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
    public void like(final String url,final String circleid){
        new Thread(new Runnable() {
            @Override
            public void run(){
                OkHttpClient client = new OkHttpClient();
                RequestBody formBody = new FormBody.Builder()
                        .add("circleid", circleid)
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
