//package xin.banghua.beiyuan.custom_ui.music_selector;
//
//import android.content.Context;
//import android.content.Intent;
//import android.os.Bundle;
//import android.text.TextUtils;
//import android.util.Log;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.Button;
//import android.widget.ImageView;
//import android.widget.TextView;
//
//import androidx.annotation.NonNull;
//import androidx.appcompat.app.AppCompatActivity;
//import androidx.recyclerview.widget.RecyclerView;
//import androidx.recyclerview.widget.StaggeredGridLayoutManager;
//
//import com.alibaba.fastjson.JSON;
//import com.bumptech.glide.Glide;
//import com.faceunity.nama.post.AllEffectActivity;
//
//import java.util.ArrayList;
//import java.util.List;
//
//import butterknife.BindView;
//import butterknife.ButterKnife;
//import xin.banghua.beiyuan.Adapter.LuntanList;
//import xin.banghua.beiyuan.R;
//import xin.banghua.beiyuan.custom_ui.MyMarqueeTextView;
//import xin.banghua.beiyuan.utils.CusPullLoadMoreRecyclerView;
//
//
//public class ZuopinMusicActivity extends AppCompatActivity {
//    private static final String TAG = "ZuopinMusicActivity";
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_zuopin_music);
//
//        ButterKnife.bind(this);
//
//        initialData();
//    }
//
//    @Override
//    protected void onResume() {
//        super.onResume();
//
//        if (zuopinMusicAdapter!=null){
//            ((StaggeredGridLayoutManager)str.getRecyclerView().getLayoutManager()).scrollToPositionWithOffset(zuopinMusicCurrentId,0);
//        }
//    }
//
//    @BindView(R.id.iv_back_left)
//    ImageView iv_back_left;
//    @BindView(R.id.music_textview)
//    TextView music_textview;
//    @BindView(R.id.music_image)
//    ImageView music_image;
//    @BindView(R.id.music_name)
//    MyMarqueeTextView music_name;
//    @BindView(R.id.music_authname)
//    TextView music_authname;
//    @BindView(R.id.used_times)
//    TextView used_times;
//    @BindView(R.id.str)
//    CusPullLoadMoreRecyclerView str;
//
//    @BindView(R.id.participate_music)
//    Button participate_music;
//
//
//    int refreshOrLoadmore = 0;
//
//    public static int zuopinMusicPageIndex = 1;
//    public static List<LuntanList> zuopinMusicLuntanLists = new ArrayList<>();
//    public static int zuopinMusicCurrentId = 0;
//
//    public static String music_id;//音乐id
//
//    public ZuopinMusicAdapter zuopinMusicAdapter;
//
//
//    public static Music music;
//    public void initialData(){
//        music_id = getIntent().getStringExtra("music_id");
//
//        iv_back_left.setOnClickListener(v->onBackPressed());
//
//
//
//        zuopinMusicAdapter = new ZuopinMusicAdapter(ZuopinMusicActivity.this,zuopinMusicLuntanLists);
//
//        str.link(zuopinMusicAdapter,3);
//        str.addAnimation(R.anim.right_to_left);
//        //间距   重复执行时会累加，导致view越窄，所以只执行一次
//        if (refreshOrLoadmore==0) {
//            str.addDecoration(new GridItemDecoration(ZuopinMusicActivity.this, 3));
//        }
//        refreshOrLoadmore = 1;
//        str.addCallbackListener(new LoadMoreAndRefresh() {
//            @Override
//            public void onLoadMore() {
//                OkHttpInstance.getZuopinMusic(responseString -> {
//                    if (!responseString.equals("false")){
//                        zuopinMusicLuntanLists.addAll(JSON.parseArray(responseString,LuntanList.class));
//                        zuopinMusicAdapter.swapData(zuopinMusicLuntanLists);
//                    }
//                });
//            }
//
//            @Override
//            public void onRefresh() {
//
//            }
//        });
//
//
//        OkHttpInstance.getMusicById(music_id, responseString -> {
//            if (!responseString.equals("false")){
//                music = JSON.parseObject(responseString,Music.class);
//                if (TextUtils.isEmpty(music.getImage())){
//                    music_textview.setText(music.getName());
//                    music_textview.setVisibility(View.VISIBLE);
//                    music_image.setVisibility(View.INVISIBLE);
//                }else {
//                    Glide.with(ZuopinMusicActivity.this).load(Common.getOssResourceUrl(music.getImage())).into(music_image);
//                    music_textview.setVisibility(View.INVISIBLE);
//                    music_image.setVisibility(View.VISIBLE);
//                }
//                music_name.setText(music.getName());
//                music_authname.setText(music.getSinger());
//                used_times.setText(music.getUsed_times()+" 次参与");
//
//                participate_music.setOnClickListener(v->{
//                    Common.same_music_path = Common.getOssResourceUrl(music.getPath());
//                    Common.same_music_id = music_id;
//                    Common.same_music_role = music.getName();
//
//                    Intent intent = new Intent();
//                    intent.setClass(ZuopinMusicActivity.this, AllEffectActivity.class);
//                    startActivity(intent);
//
////                    Intent intent = new Intent();
////                    intent.setClass(ZuopinMusicActivity.this, FabutieziActivity.class);
////                    intent.putExtra("form_where",FORM_ZUOPIN_MUSIC_TO_PUBLISH);
////                    intent.putExtra("param",CommonKtv.getOssResourceUrl(music.getPath()));
////                    intent.putExtra("music_id",music_id);
////                    intent.putExtra("music_role",music.getName());
////                    startActivity(intent);
//                });
//
//                OkHttpInstance.getZuopinMusic(responseString1 -> {
//                    if (!responseString1.equals("false")){
//                        zuopinMusicLuntanLists = JSON.parseArray(responseString1,LuntanList.class);
//                        zuopinMusicAdapter.swapDataRefresh(zuopinMusicLuntanLists);
//                    }
//                });
//            }
//        });
//    }
//
//
//
//    public class ZuopinMusicAdapter extends StaggedAdapter {
//        private static final String TAG = "ZuopinMusicAdapter";
//        private Context mContext;
//        List<LuntanList> datas;
//
//        public ZuopinMusicAdapter(Context c, List<LuntanList> datas) {
//            super(c);
//            this.mContext = c;
//            this.datas=datas;
//        }
//
//        //替换数据，并更新
//        public void swapDataRefresh(List<LuntanList> datas){
//            this.datas = datas;
//            notifyDataSetChanged();
//        }
//        //替换数据，并更新
//        public void swapData(List<LuntanList> datas){
//            int oldSize = this.datas.size();
//            int newSize = datas.size();
//            this.datas = datas;
//            notifyItemRangeInserted(oldSize , newSize);
//        }
//
//        @Override
//        public RecyclerView.ViewHolder addViewHolder(ViewGroup viewGroup, int i) {
//            //绑定自定义的viewholder
//            View v = LayoutInflater.from(mContext).inflate(R.layout.adapter_zuopin_music_item,viewGroup,false);
//            return new ZuopinMusicAdapter.MyHolder(v);
//        }
//
//        @Override
//        public void bindView(RecyclerView.ViewHolder viewHolder, int i) {
//            LuntanList currentItem = this.datas.get(i);
//            int currentPosition = i;
//
//            MyHolder myHolder = (MyHolder)viewHolder;
//
//            switch (currentItem.getPosttitle()){
//                case "image":
//                    String[] postPicture = currentItem.getPostpicture().split(",");
//                    if (postPicture.length>0)
//                        Glide.with(mContext).load(Common.getOssResourceUrl(postPicture[0]))
//                                .into(myHolder.img);
//                    break;
//                case "video":
//                    Glide.with(mContext).load(Common.getOssResourceUrl(currentItem.getVideocover()))
//                            .into(myHolder.img);
//                    break;
//                default:
//                    Glide.with(mContext).load(Common.getOssResourceUrl(currentItem.getAuthalbum()))
//                            .into(myHolder.img);
//                    break;
//            }
//
//
//
//            if (!TextUtils.isEmpty(music.getFirst_publish())&&currentItem.getId().equals(music.getFirst_publish())){
//                Log.d(TAG, "bindView: 首发1"+currentItem.getId()+"|"+music.getFirst_publish());
//                myHolder.first_publish.setVisibility(View.VISIBLE);
//            }else {
//                myHolder.first_publish.setVisibility(View.GONE);
//            }
//
//
//            myHolder.img.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    zuopinMusicCurrentId = currentPosition;
//                    Log.d(TAG, "onClick: 来自作品音乐"+zuopinMusicCurrentId);
//                    Intent intent = new Intent(mContext, DouyinNewActivity.class);
//                    intent.putExtra("fromWhere", FROM_ZUOPIN_MUSIC);
//                    mContext.startActivity(intent);
//                }
//            });
//        }
//        @Override
//        public int getItemCount() {
//            Log.d("count","videoadpater获取数量"+datas.size());
//            return datas.size();
//        }
//
//        /**
//         * 下拉刷新
//         * @param datasMore
//         */
//        public void refresh(List datasMore){
//
//            this.datas = datasMore;
//            notifyItemRangeChanged(0, datasMore.size());
//
//        }
//
//        /**
//         * 加载更多
//         * @param datasMore
//         */
//        public void loadMore(List datasMore){
//
//            Log.d("count","loadmore数量"+datas.size()+"|"+datasMore.size());
//
//            this.datas = datasMore;
//            notifyDataSetChanged();
//
//        }
//        /**
//         * 自定义viewholder
//         */
//
//        class MyHolder extends RecyclerView.ViewHolder{
//
//            ImageView img;
//            TextView first_publish;
//
//            public MyHolder(@NonNull View itemView) {
//                super(itemView);
//
//                img = itemView.findViewById(R.id.video_img);
//
//                first_publish = itemView.findViewById(R.id.first_publish);
//            }
//        }
//    }
//}