package io.agora.chatroom;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.alibaba.fastjson.JSON;
import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.agora.chatroom.activity.ChatRoomActivity;
import xin.banghua.pullloadmorerecyclerview.CusPullLoadMoreRecyclerView;
import xin.banghua.pullloadmorerecyclerview.NiceImageView;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link FilmTypeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FilmTypeFragment extends Fragment {
    private static final String TAG = "FilmTypeFragment";
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";

    // TODO: Rename and change types of parameters
    private String mParam1;

    public FilmTypeFragment() {
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
    public static FilmTypeFragment newInstance(String param1) {
        FilmTypeFragment fragment = new FilmTypeFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        fragment.setArguments(args);
        return fragment;
    }


    Context mContext;
    String type = "";
    String filter = "";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            type = (String) getArguments().getString(ARG_PARAM1);
        }

        mContext = getActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_film_type, container, false);
    }



    @BindView(R2.id.pullLoadMoreRecyclerView)
    CusPullLoadMoreRecyclerView pullLoadMoreRecyclerView;

    List<FilmList> dataLists = new ArrayList<>();

    CustomRecyclerAdapterAdapter customRecyclerAdapterAdapter;

    int pageindex = 1;
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);



        ButterKnife.bind(this,view);


        //adpter绑定数据
        customRecyclerAdapterAdapter = new CustomRecyclerAdapterAdapter(dataLists);
        //RecyclerView绑定adpter
        pullLoadMoreRecyclerView.setAdapter(customRecyclerAdapterAdapter);
        pullLoadMoreRecyclerView.getRecyclerView().setLayoutManager(new LinearLayoutManager(mContext));
        pullLoadMoreRecyclerView.addItemDecoration(new DividerItemDecoration(mContext, DividerItemDecoration.VERTICAL));//增加分割线

        pullLoadMoreRecyclerView.setLinearLayout();
        pullLoadMoreRecyclerView.setOnPullLoadMoreListener(new CusPullLoadMoreRecyclerView.PullLoadMoreListener() {
            @Override
            public void onRefresh() {
                pageindex = 1;
                OkHttpInstance.getFilmTopic(pageindex,filter,type, responseString -> {
                    if (!responseString.equals("false")){
                        dataLists = JSON.parseArray(responseString,FilmList.class);
                        customRecyclerAdapterAdapter.swapData(dataLists);

                        pullLoadMoreRecyclerView.stopWaveLoadingShow();
                    }
                });
                pullLoadMoreRecyclerView.postDelayed(()->pullLoadMoreRecyclerView.setPullLoadMoreCompleted(),1000);
            }
            @Override
            public void onLoadMore() {

                pageindex = pageindex + 1;
                OkHttpInstance.getFilmTopic(pageindex,filter,type, responseString -> {
                    if (!responseString.equals("false")){
                        dataLists.addAll(JSON.parseArray(responseString,FilmList.class));
                        customRecyclerAdapterAdapter.setDataLists(dataLists);

                        pullLoadMoreRecyclerView.stopWaveLoadingShow();
                    }
                });

                pullLoadMoreRecyclerView.postDelayed(()->pullLoadMoreRecyclerView.setPullLoadMoreCompleted(),1000);
            }
        });



        //网络请求
        pullLoadMoreRecyclerView.startWaveLoadingShow();
        OkHttpInstance.getFilmTopic(pageindex,filter,type, responseString -> {
            if (!responseString.equals("false")){
                dataLists = JSON.parseArray(responseString,FilmList.class);
                customRecyclerAdapterAdapter.swapData(dataLists);

                pullLoadMoreRecyclerView.stopWaveLoadingShow();
            }
        });
    }

    public void searchName(String key){
        filter = key;

        OkHttpInstance.getFilmTopic(pageindex,filter,type, responseString -> {
            if (!responseString.equals("false")){
                dataLists = JSON.parseArray(responseString,FilmList.class);
                customRecyclerAdapterAdapter.setDataLists(dataLists);

                pullLoadMoreRecyclerView.stopWaveLoadingShow();
            }
        });
    }

    public class CustomRecyclerAdapterAdapter extends RecyclerView.Adapter<ViewHolder>{
        List<FilmList> dataLists = new ArrayList<>();

        public CustomRecyclerAdapterAdapter(List<FilmList> dataLists) {
            this.dataLists = dataLists;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.film_item_layout,parent,false);
            ViewHolder viewHolder = new ViewHolder(view);
            return viewHolder;
        }

        public void setDataLists(List<FilmList> dataLists){
            this.dataLists = dataLists;
            notifyDataSetChanged();
        }
        public void swapData(List<FilmList> dataLists){
            int oldSize = this.dataLists.size();
            int newSize = dataLists.size();
            this.dataLists = dataLists;
            notifyItemRangeInserted(oldSize , newSize);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            Log.d(TAG, "onBindViewHolder: 刷新");

            final FilmList currentItem = this.dataLists.get(position);


            holder.film_container.setOnClickListener(view -> {
                Log.d(TAG, "onBindViewHolder: 选中电影名"+currentItem.getName());
                ChatRoomActivity.videoView.initShow(currentItem);
            });

            Glide.with(mContext).load(currentItem.getCover()).into(holder.film_cover_img);
            holder.film_name.setText(currentItem.getName());
            holder.uploader_name.setText("分享者："+currentItem.getNickname());
        }

        @Override
        public int getItemCount() {
            return dataLists.size();
        }
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        ConstraintLayout film_container;
        NiceImageView film_cover_img;
        TextView film_name;
        TextView uploader_name;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            film_container = itemView.findViewById(R.id.film_container);
            film_cover_img = itemView.findViewById(R.id.film_cover_img);
            film_name = itemView.findViewById(R.id.film_name);
            uploader_name = itemView.findViewById(R.id.uploader_name);
        }
    }
}