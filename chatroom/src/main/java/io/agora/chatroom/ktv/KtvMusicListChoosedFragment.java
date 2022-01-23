package io.agora.chatroom.ktv;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.util.ObjectsCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.alibaba.fastjson.JSON;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.agora.chatroom.OkHttpInstance;
import io.agora.chatroom.R;
import io.agora.chatroom.R2;
import io.agora.chatroom.activity.ChatRoomActivity;
import io.agora.chatroom.model.Channel;

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

public class KtvMusicListChoosedFragment extends Fragment {
    private static final String TAG = "KtvMusicListFragment";
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    // TODO: Rename and change types of parameters

    public KtvMusicListChoosedFragment() {
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
    public static KtvMusicListChoosedFragment newInstance(String param1) {
        KtvMusicListChoosedFragment fragment = new KtvMusicListChoosedFragment();
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
        return inflater.inflate(R.layout.ktv_fragment_song_order_list, container, false);
    }

    @BindView(R2.id.swipeRefreshLayout)
    SwipeRefreshLayout swipeRefreshLayout;

    @BindView(R2.id.list)
    RecyclerView list;

    @BindView(R2.id.llEmpty)
    LinearLayout llEmpty;

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
        list.setLayoutManager(new LinearLayoutManager(mContext));
        list.setAdapter(customRecyclerAdapterAdapter);

        swipeRefreshLayout.setEnabled(false);
        llEmpty.setVisibility(View.GONE);


        Log.d(TAG, "playNewSong: 获取房间音乐2");
        OkHttpInstance.getSongList(currentChannel.getId(),responseString -> {
            if (!responseString.equals("false")){
                dataLists = JSON.parseArray(responseString,MemberMusicModel.class);
                customRecyclerAdapterAdapter.setDataLists(dataLists);
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG, "playNewSong: 获取房间音乐3");
        OkHttpInstance.getSongList(currentChannel.getId(),responseString -> {
            if (!responseString.equals("false")){
                dataLists = JSON.parseArray(responseString,MemberMusicModel.class);
                customRecyclerAdapterAdapter.setDataLists(dataLists);
            }else {
                dataLists.clear();
                customRecyclerAdapterAdapter.setDataLists(dataLists);
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
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.ktv_item_choosed_song_list,parent,false);
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

            if (currentItem == null) {
                return;
            }

            holder.tvNo.setText(String.valueOf(position + 1));
            holder.tvName.setText(currentItem.getName() + "-" + currentItem.getSinger());

            Glide.with(holder.itemView)
                    .load(currentItem.getPoster())
                    .apply(RequestOptions.bitmapTransform(new RoundedCorners(10)))
                    .into(holder.iv);

            if (position == 0) {
                holder.tvSing.setVisibility(View.VISIBLE);
            } else {
                holder.tvSing.setVisibility(View.GONE);
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

        @BindView(R2.id.tvNo)
        TextView tvNo;
        @BindView(R2.id.tvName)
        TextView tvName;
        @BindView(R2.id.iv)
        ImageView iv;
        @BindView(R2.id.tvSing)
        TextView tvSing;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            ButterKnife.bind(this,itemView);
        }
    }

}