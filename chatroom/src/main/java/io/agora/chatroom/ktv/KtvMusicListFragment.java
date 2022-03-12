package io.agora.chatroom.ktv;

import static io.agora.chatroom.ktv.KtvFrameLayout.ktvView;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.util.ObjectsCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.alibaba.fastjson.JSON;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.agora.chatroom.OkHttpInstance;
import io.agora.chatroom.OkHttpResponseCallBack;
import io.agora.chatroom.R;
import io.agora.chatroom.R2;
import io.agora.chatroom.activity.ChatRoomActivity;
import io.agora.chatroom.model.Channel;
import xin.banghua.pullloadmorerecyclerview.CusPullLoadMoreRecyclerView;

/**
 * A simple {@link Fragment} subclass.
 * create an instance of this fragment.
 *
 *             <!-- TODO: Update blank fragment layout -->
 */

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
//                            android:text="接单设置"
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

public class KtvMusicListFragment extends Fragment {
    private static final String TAG = "KtvMusicListFragment";
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    // TODO: Rename and change types of parameters

    public KtvMusicListFragment() {
        // Required empty public constructor
    }
    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @return A new instance of fragment FilmTypeFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static KtvMusicListFragment newInstance(String param1) {
        KtvMusicListFragment fragment = new KtvMusicListFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        fragment.setArguments(args);
        return fragment;
    }


    Context mContext;

    List<MemberMusicModel> paramDataLists = new ArrayList<>();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            key_word = (String) getArguments().getString(ARG_PARAM1);
        }

        mContext = this.getContext();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_film_type, container, false);
    }



    @BindView(R2.id.pullLoadMoreRecyclerView)
    CusPullLoadMoreRecyclerView pullLoadMoreRecyclerView;

    List<MemberMusicModel> dataLists = new ArrayList<>();


    String key_word = "";
    int pageIndex = 1;
    CustomRecyclerAdapterAdapter customRecyclerAdapterAdapter;
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Log.d(TAG, "onViewCreated: 初始化");


        ButterKnife.bind(this,view);


        //adpter绑定数据
        customRecyclerAdapterAdapter = new CustomRecyclerAdapterAdapter(dataLists);
        //RecyclerView绑定adpter
        pullLoadMoreRecyclerView.setAdapter(customRecyclerAdapterAdapter);
        pullLoadMoreRecyclerView.getRecyclerView().setLayoutManager(new LinearLayoutManager(mContext));
        pullLoadMoreRecyclerView.addItemDecoration(new DividerItemDecoration(mContext, DividerItemDecoration.VERTICAL));//增加分割线


        pullLoadMoreRecyclerView.setIsRefresh(false);

        pullLoadMoreRecyclerView.setLinearLayout();
        pullLoadMoreRecyclerView.setOnPullLoadMoreListener(new CusPullLoadMoreRecyclerView.PullLoadMoreListener() {
            @Override
            public void onRefresh() {
//                pageIndex = 1;
//                OkHttpInstance.getNewUserList(String.valueOf(pageIndex),selectedGender,(resultString)->{
//                    dataLists = JSON.parseArray(resultString,KtvMusicListKtv.class);
//                    customRecyclerAdapterAdapter.setdataLists(dataLists);
//                    customRecyclerAdapterAdapter.notifyDataSetChanged();
//                });


                pullLoadMoreRecyclerView.postDelayed(()->pullLoadMoreRecyclerView.setPullLoadMoreCompleted(),1000);
            }
            @Override
            public void onLoadMore() {
                pageIndex = pageIndex + 1;
                OkHttpInstance.getKtvMusicList(key_word,pageIndex+"", responseString -> {
                    if (!responseString.equals("false")){
                        dataLists.addAll(JSON.parseArray(responseString,MemberMusicModel.class));
                        customRecyclerAdapterAdapter.swapData(dataLists);
                    }
                });


                pullLoadMoreRecyclerView.postDelayed(()->pullLoadMoreRecyclerView.setPullLoadMoreCompleted(),1000);
            }
        });


        searchName(key_word);

    }

    public void searchName(String key){
        key_word = key;
        pageIndex = 1;
        //网络请求
        pullLoadMoreRecyclerView.startWaveLoadingShow();
        OkHttpInstance.getKtvMusicList(key_word,pageIndex+"", responseString -> {
            if (!responseString.equals("false")){
                dataLists = JSON.parseArray(responseString,MemberMusicModel.class);
                customRecyclerAdapterAdapter.setDataLists(dataLists);

                pullLoadMoreRecyclerView.stopWaveLoadingShow();
            }
        });
    }


    Channel currentChannel;

    public void setCurrentChannel(Channel currentChannel) {
        this.currentChannel = currentChannel;
    }



    public class CustomRecyclerAdapterAdapter extends RecyclerView.Adapter<ViewHolder>{
        List<MemberMusicModel> dataLists = new ArrayList<>();

        public CustomRecyclerAdapterAdapter(List<MemberMusicModel> dataLists) {
            this.dataLists = dataLists;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.ktv_item_choose_song_list,parent,false);
            ViewHolder viewHolder = new ViewHolder(view);
            return viewHolder;
        }

        public void setDataLists(List<MemberMusicModel> dataLists){
            this.dataLists = dataLists;
            notifyDataSetChanged();
        }
        public void swapData(List<MemberMusicModel> dataLists){
            int oldSize = this.dataLists.size();
            int newSize = dataLists.size();
            this.dataLists = dataLists;
            notifyItemRangeInserted(oldSize , newSize);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            Log.d(TAG, "onBindViewHolder: 刷新");

            MemberMusicModel currentItem = this.dataLists.get(position);



            holder.tvName.setText(currentItem.getName() + "-" + currentItem.getSinger());

            Glide.with(holder.itemView)
                    .load(currentItem.getPoster())
                    .apply(RequestOptions.bitmapTransform(new RoundedCorners(10)))
                    .into(holder.iv);


            holder.btChooseSong.setOnClickListener(view -> {
                OkHttpInstance.addSong(currentChannel.getId(), currentItem.getId(), new OkHttpResponseCallBack() {
                    @Override
                    public void getResponseString(String responseString) {
                        if (ktvView.mMusicPlayer!=null){
                            if (ktvView.memberMusicModels.size()==0){
                                Log.d(TAG, "onMessageAdded: 播放新歌5");
                                ktvView.playNewSong();
                            }
                        }
                    }
                });
                holder.btChooseSong.setText("已点");
                holder.btChooseSong.setEnabled(false);
            });



            if (isInMusicOrderList(currentItem)) {
                holder.btChooseSong.setEnabled(false);
                holder.btChooseSong.setText(R.string.ktv_room_choosed_song);
            } else {
                holder.btChooseSong.setEnabled(true);
                holder.btChooseSong.setText(R.string.ktv_room_choose_song);
            }

        }

        @Override
        public int getItemCount() {
            return dataLists.size();
        }
    }

    public boolean isInMusicOrderList(MemberMusicModel item) {
        for (MemberMusicModel music : ChatRoomActivity.choosedSong) {
            if (ObjectsCompat.equals(music.getMusicId(), item.getMusicId())) {
                return true;
            }
        }
        return false;
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        @BindView(R2.id.tvName)
        TextView tvName;
        @BindView(R2.id.iv)
        ImageView iv;
        @BindView(R2.id.btChooseSong)
        TextView btChooseSong;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            ButterKnife.bind(this,itemView);
        }
    }

}