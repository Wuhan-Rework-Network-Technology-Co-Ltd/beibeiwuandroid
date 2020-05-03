package xin.banghua.beiyuan.Adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.daimajia.slider.library.Animations.DescriptionAnimation;
import com.daimajia.slider.library.SliderLayout;
import com.daimajia.slider.library.SliderTypes.BaseSliderView;
import com.daimajia.slider.library.SliderTypes.TextSliderView;
import com.daimajia.slider.library.Tricks.ViewPagerEx;
import com.orhanobut.dialogplus.DialogPlus;
import com.zolad.zoominimageview.ZoomInImageView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
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
import xin.banghua.beiyuan.SliderWebViewActivity;

public class LuntanSliderAdapter extends RecyclerView.Adapter  implements  ViewPagerEx.OnPageChangeListener {
    private static final String TAG = "LuntanAdapter";

    Integer current_timestamp = Math.round(new Date().getTime()/1000);
    //幻灯片
    SliderLayout mDemoSlider;
    JSONArray jsonArray;
    ViewHolder viewHolder_btn;
    private List<LuntanList> luntanLists;
    private Context mContext;
    private final static int TYPE_HEAD = 0;
    private final static int TYPE_CONTENT = 1;
    public LuntanSliderAdapter(List<LuntanList> luntanLists,JSONArray jsonArray, Context mContext) {
        this.jsonArray = jsonArray;
        Log.d(TAG, "LuntanAdapter: start");
        this.luntanLists = luntanLists;
        this.mContext = mContext;
    }
    //替换数据，并更新
    public void swapData(List<LuntanList> luntanLists){
        this.luntanLists = luntanLists;
        notifyDataSetChanged();
    }
    @Override
    public int getItemViewType(int position) {
        Log.d(TAG, "getItemViewType: position"+position);
        if ( position == 0){ // 头部
            Log.d(TAG, "getItemViewType: 头");
            return TYPE_HEAD;
        }else{
            Log.d(TAG, "getItemViewType: 身");
            return TYPE_CONTENT;
        }
    }
    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup,final int i) {
        Log.d(TAG, "onCreateViewHolder: 进入");
        if (i == TYPE_HEAD){
            View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.recyclerview_slider,viewGroup,false);
            LuntanSliderAdapter.SliderHolder viewHolder = new LuntanSliderAdapter.SliderHolder(view);
            return viewHolder;
        }else if(i == TYPE_CONTENT){
            View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.recyclerview_luntan,viewGroup,false);
            LuntanSliderAdapter.ViewHolder viewHolder = new LuntanSliderAdapter.ViewHolder(view);
            return viewHolder;
        }
        return null;

    }

    @Override
    public void onBindViewHolder(@NonNull final RecyclerView.ViewHolder viewHolder,final int i) {
        if (viewHolder instanceof LuntanSliderAdapter.SliderHolder){
            HashMap<String,String> url_maps = new HashMap<String, String>();
            if (jsonArray.length()>0){
                for (int j=0;j<jsonArray.length();j++){
                    try {
                       final JSONObject jsonObject = jsonArray.getJSONObject(j);
                        //url_maps.put(jsonObject.getString("slidename"), jsonObject.getString("slidepicture"));
                        TextSliderView textSliderView = new TextSliderView(mContext);
                        // initialize a SliderLayout
                        textSliderView
                                .description(jsonObject.getString("slidename"))
                                .image(jsonObject.getString("slidepicture"))
                                .setScaleType(BaseSliderView.ScaleType.Fit)
                                .setOnSliderClickListener(new BaseSliderView.OnSliderClickListener() {
                                    @Override
                                    public void onSliderClick(BaseSliderView slider) {
                                        try {
                                            if (!jsonObject.getString("slideurl").isEmpty()){
                                                Log.d(TAG, "onSliderClick: 链接是"+jsonObject.getString("slideurl"));
                                                Intent intent = new Intent(mContext, SliderWebViewActivity.class);
                                                intent.putExtra("slidername",jsonObject.getString("slidename"));
                                                intent.putExtra("sliderurl",jsonObject.getString("slideurl"));
                                                mContext.startActivity(intent);
                                            }
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                });

                        //add your extra information
                        textSliderView.bundle(new Bundle());
                        textSliderView.getBundle()
                                .putString("extra",jsonObject.getString("slidename"));

                        mDemoSlider.addSlider(textSliderView);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
            }


            mDemoSlider.setMinimumHeight(100);
            mDemoSlider.setPresetTransformer(SliderLayout.Transformer.Accordion);
            mDemoSlider.setPresetIndicator(SliderLayout.PresetIndicators.Center_Bottom);
            mDemoSlider.setCustomAnimation(new DescriptionAnimation());
            mDemoSlider.setDuration(4000);
            mDemoSlider.addOnPageChangeListener(this);
        }else if (viewHolder instanceof LuntanSliderAdapter.ViewHolder){
            final LuntanList currentItem = luntanLists.get(i-1);

            String authattributes_string = currentItem.getAuthage()+"岁 | "+currentItem.getAuthgender()+" | "+currentItem.getAuthregion()+" | "+currentItem.getAuthproperty();
            ((ViewHolder) viewHolder).authattributes.setText(authattributes_string);

            ((ViewHolder) viewHolder).id.setText(currentItem.getId());
            ((ViewHolder) viewHolder).plateid.setText(currentItem.getPlateid());
            ((ViewHolder) viewHolder).platename.setText(currentItem.getPlatename());
            ((ViewHolder) viewHolder).authid.setText(currentItem.getAuthid());
            ((ViewHolder) viewHolder).authnickname.setText(currentItem.getAuthnickname());


            Glide.with(mContext)
                    .asBitmap()
                    .load(currentItem.getAuthportrait())
                    .into(((ViewHolder) viewHolder).authportrait);





            //GOTO  会员标识
            //现在vip传过来的是时间
            if (currentItem.getAuthvip().isEmpty()||currentItem.getAuthvip()=="null"){
                ((ViewHolder) viewHolder).vip_diamond.setVisibility(View.INVISIBLE);
                ((ViewHolder) viewHolder).vip_black.setVisibility(View.INVISIBLE);
                ((ViewHolder) viewHolder).vip_white.setVisibility(View.INVISIBLE);
                ((ViewHolder) viewHolder).vip_gray.setVisibility(View.VISIBLE);
            }else {
                Log.d("会员时间",currentItem.getAuthvip()+"");
                int vip_time = Integer.parseInt(currentItem.getAuthvip()+"");
                if (vip_time > current_timestamp) {
                    //vipicon分级
                    Log.d("会员时长", ((vip_time - current_timestamp) + ""));
                    if ((vip_time - current_timestamp) < 3600 * 24 * 30) {
                        ((ViewHolder) viewHolder).vip_black.setVisibility(View.INVISIBLE);
                        ((ViewHolder) viewHolder).vip_white.setVisibility(View.INVISIBLE);
                        ((ViewHolder) viewHolder).vip_gray.setVisibility(View.INVISIBLE);
                        ((ViewHolder) viewHolder).vip_diamond.setVisibility(View.VISIBLE);
                    } else if ((vip_time - current_timestamp) < 3600 * 24 * 180) {
                        ((ViewHolder) viewHolder).vip_diamond.setVisibility(View.INVISIBLE);
                        ((ViewHolder) viewHolder).vip_white.setVisibility(View.INVISIBLE);
                        ((ViewHolder) viewHolder).vip_gray.setVisibility(View.INVISIBLE);
                        ((ViewHolder) viewHolder).vip_black.setVisibility(View.VISIBLE);
                    } else {
                        ((ViewHolder) viewHolder).vip_diamond.setVisibility(View.INVISIBLE);
                        ((ViewHolder) viewHolder).vip_black.setVisibility(View.INVISIBLE);
                        ((ViewHolder) viewHolder).vip_gray.setVisibility(View.INVISIBLE);
                        ((ViewHolder) viewHolder).vip_white.setVisibility(View.VISIBLE);
                    }
                } else {
                    ((ViewHolder) viewHolder).vip_diamond.setVisibility(View.INVISIBLE);
                    ((ViewHolder) viewHolder).vip_black.setVisibility(View.INVISIBLE);
                    ((ViewHolder) viewHolder).vip_white.setVisibility(View.INVISIBLE);
                    ((ViewHolder) viewHolder).vip_gray.setVisibility(View.VISIBLE);
                }
            }









            ((ViewHolder) viewHolder).posttip.setText(currentItem.getPosttip().isEmpty()?"":currentItem.getPosttip());
            if (currentItem.getPosttip().equals("加精")){
                Resources resources = mContext.getResources();
                Drawable drawable = resources.getDrawable(R.drawable.ic_essence,null);
                drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
                ((ViewHolder) viewHolder).posttip.setCompoundDrawables(drawable,null,null,null);
            }else if(currentItem.getPosttip().equals("置顶")){
                Resources resources = mContext.getResources();
                Drawable drawable = resources.getDrawable(R.drawable.ic_ontop,null);
                drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
                ((ViewHolder) viewHolder).posttip.setCompoundDrawables(drawable,null,null,null);
            }else if (currentItem.getPosttip().equals("置顶,加精")){
                Resources resources = mContext.getResources();
                Drawable drawable1 = resources.getDrawable(R.drawable.ic_essence,null);
                drawable1.setBounds(0, 0, drawable1.getMinimumWidth(), drawable1.getMinimumHeight());
                Drawable drawable2 = resources.getDrawable(R.drawable.ic_ontop,null);
                drawable2.setBounds(0, 0, drawable2.getMinimumWidth(), drawable2.getMinimumHeight());
                ((ViewHolder) viewHolder).posttip.setCompoundDrawables(drawable1,null,drawable2,null);
            }else {
                //必须加上，否则会错乱
                ((ViewHolder) viewHolder).posttip.setCompoundDrawables(null,null,null,null);
            }
            ((ViewHolder) viewHolder).posttitle.setText(currentItem.getPosttitle());
            if (currentItem.getPosttext().length()>50) {
                ((ViewHolder) viewHolder).posttext.setText(currentItem.getPosttext().substring(0, 50)+"......");
                ((ViewHolder) viewHolder).detail_content.setVisibility(View.VISIBLE);
                ((ViewHolder) viewHolder).detail_content.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ((ViewHolder) viewHolder).posttext.setText(currentItem.getPosttext());
                        ((ViewHolder) viewHolder).detail_content.setVisibility(View.GONE);
                    }
                });
            }else {
                ((ViewHolder) viewHolder).posttext.setText(currentItem.getPosttext());
                ((ViewHolder) viewHolder).detail_content.setVisibility(View.GONE);
            }

            if (currentItem.getPostpicture().length!=1){
                ((ViewHolder) viewHolder).postpicture.setVisibility(View.GONE);
            }else if (currentItem.getPostpicture().length==1){
                Glide.with(mContext)
                        .asBitmap()
                        .load(currentItem.getPostpicture()[0])
                        .into(((ViewHolder) viewHolder).postpicture);
                ((ViewHolder) viewHolder).postpicture.setVisibility(View.VISIBLE);
                ((ViewHolder) viewHolder).postpicture.setOnClickListener(new View.OnClickListener() {
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
                                .load(currentItem.getPostpicture()[0])
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
            }
            if (currentItem.getPostpicture().length<2){
                ((ViewHolder) viewHolder).postpicture1.setVisibility(View.GONE);
            }else {
                Glide.with(mContext)
                        .asBitmap()
                        .load(currentItem.getPostpicture()[0])
                        .into(((ViewHolder) viewHolder).postpicture1);
                ((ViewHolder) viewHolder).postpicture1.setVisibility(View.VISIBLE);
                ((ViewHolder) viewHolder).postpicture1.setOnClickListener(new View.OnClickListener() {
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
                                .load(currentItem.getPostpicture()[0])
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
            }
            if (currentItem.getPostpicture().length<2){
                ((ViewHolder) viewHolder).postpicture2.setVisibility(View.GONE);
            }else {
                Glide.with(mContext)
                        .asBitmap()
                        .load(currentItem.getPostpicture()[1])
                        .into(((ViewHolder) viewHolder).postpicture2);
                ((ViewHolder) viewHolder).postpicture2.setVisibility(View.VISIBLE);
                ((ViewHolder) viewHolder).postpicture2.setOnClickListener(new View.OnClickListener() {
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
                                .load(currentItem.getPostpicture()[1])
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
            }
            if (currentItem.getPostpicture().length<3){
                ((ViewHolder) viewHolder).postpicture3.setVisibility(View.GONE);
            }else {
                Glide.with(mContext)
                        .asBitmap()
                        .load(currentItem.getPostpicture()[2])
                        .into(((ViewHolder) viewHolder).postpicture3);
                ((ViewHolder) viewHolder).postpicture3.setVisibility(View.VISIBLE);
                ((ViewHolder) viewHolder).postpicture3.setOnClickListener(new View.OnClickListener() {
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
                                .load(currentItem.getPostpicture()[2])
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
            }

            ((ViewHolder) viewHolder).like.setText("赞"+currentItem.getLike());
            ((ViewHolder) viewHolder).like.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    Log.d(TAG, "onClick: clicked on: " + currentItem.getId());
                    //Toast.makeText(mContext, mUserID.get(i) + mUserNickName.get(i), Toast.LENGTH_LONG).show();
                    viewHolder_btn = (ViewHolder) viewHolder;
                    like("https://applet.banghua.xin/app/index.php?i=99999&c=entry&a=webapp&do=luntanlike&m=socialchat",currentItem.getId());
                }
            });
            ((ViewHolder) viewHolder).favorite.setText(currentItem.getFavorite());
            ((ViewHolder) viewHolder).time.setText(currentItem.getTime());


            ((ViewHolder) viewHolder).authnickname.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(v.getContext(), PersonageActivity.class);
                    intent.putExtra("userID",currentItem.getAuthid());
                    v.getContext().startActivity(intent);
                }
            });
            ((ViewHolder) viewHolder).authportrait.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(v.getContext(), PersonageActivity.class);
                    intent.putExtra("userID",currentItem.getAuthid());
                    v.getContext().startActivity(intent);
                }
            });

//            ((ViewHolder) viewHolder).posttitle.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    intentPostlist(v,currentItem);
//                }
//            });
//            ((ViewHolder) viewHolder).posttext.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    intentPostlist(v,currentItem);
//                }
//            });
//            ((ViewHolder) viewHolder).postpicture.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    intentPostlist(v,currentItem);
//                }
//            });
//
//            ((ViewHolder) viewHolder).luntanLayout.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    intentPostlist(v,currentItem);
//                }
//            });

            ((ViewHolder) viewHolder).menu_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    InformBlacklistAdapter adapter = new InformBlacklistAdapter(mContext,"post",currentItem.getId(),currentItem.getAuthid());
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

        }
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
        return luntanLists.size()+1;
    }



    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {

    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    private class SliderHolder extends RecyclerView.ViewHolder{
        private static final String TAG = "SliderHolder";

        public SliderHolder(View itemView) {
            super(itemView);
            Log.d(TAG, "SliderHolder: 进入");
            mDemoSlider = itemView.findViewById(R.id.recyclerview_slider);
        }
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
        ZoomInImageView postpicture;
        ZoomInImageView postpicture1;
        ZoomInImageView postpicture2;
        ZoomInImageView postpicture3;
        TextView like;
        TextView favorite;
        TextView time;
        Button detail_content;

        TextView authattributes;
        Button menu_btn;

        RelativeLayout luntanLayout;

        ImageView vip_gray;
        ImageView vip_diamond;
        ImageView vip_black;
        ImageView vip_white;

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
            postpicture1 = itemView.findViewById(R.id.postpicture1);
            postpicture2 = itemView.findViewById(R.id.postpicture2);
            postpicture3 = itemView.findViewById(R.id.postpicture3);
            like = itemView.findViewById(R.id.like);
            favorite = itemView.findViewById(R.id.favorite);
            time = itemView.findViewById(R.id.time);

            luntanLayout = itemView.findViewById(R.id.luntanLayout);

            authattributes = itemView.findViewById(R.id.authattributes);
            menu_btn = itemView.findViewById(R.id.menu_btn);

            detail_content = itemView.findViewById(R.id.detail_content);


            vip_gray = itemView.findViewById(R.id.vip_gray);
            vip_diamond = itemView.findViewById(R.id.vip_diamond);
            vip_black = itemView.findViewById(R.id.vip_black);
            vip_white = itemView.findViewById(R.id.vip_white);
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
