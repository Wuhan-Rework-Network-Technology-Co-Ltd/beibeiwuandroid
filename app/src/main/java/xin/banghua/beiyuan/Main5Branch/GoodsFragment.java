package xin.banghua.beiyuan.Main5Branch;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

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
import de.hdodenhof.circleimageview.CircleImageView;
import io.agora.chatroom.gift.GiftDialog;
import io.agora.chatroom.model.Member;
import io.agora.chatroom.util.PortraitFrameView;
import xin.banghua.beiyuan.Adapter.StoreList;
import xin.banghua.beiyuan.Common;
import xin.banghua.beiyuan.R;
import xin.banghua.beiyuan.utils.OkHttpInstance;
import xin.banghua.beiyuan.utils.OkHttpResponseCallBack;
import xin.banghua.pullloadmorerecyclerview.CusPullLoadMoreRecyclerView;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link GoodsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class GoodsFragment extends Fragment {
    private static final String TAG = "GoodsFragment";
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public GoodsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment GoodsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static GoodsFragment newInstance(String param1, String param2) {
        GoodsFragment fragment = new GoodsFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_goods, container, false);
    }

    List<StoreList> arrayList = new ArrayList<>();
    @BindView(R.id.pullLoadMoreRecyclerView)
    CusPullLoadMoreRecyclerView pullLoadMoreRecyclerView;


    @BindView(R.id.container)
    ConstraintLayout container;
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ButterKnife.bind(this,view);


        //adpter绑定数据
        ExampleFragmentAdapter exampleFragmentAdapter = new ExampleFragmentAdapter(arrayList);
        //RecyclerView绑定adpter
        pullLoadMoreRecyclerView.setAdapter(exampleFragmentAdapter);
        pullLoadMoreRecyclerView.getRecyclerView().setLayoutManager(new LinearLayoutManager(getActivity()));
        pullLoadMoreRecyclerView.addItemDecoration(new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL));//增加分割线

        pullLoadMoreRecyclerView.setLinearLayout();
        pullLoadMoreRecyclerView.setIsLoadMore(false);
        pullLoadMoreRecyclerView.setOnPullLoadMoreListener(new CusPullLoadMoreRecyclerView.PullLoadMoreListener() {
            @Override
            public void onRefresh() {
                pullLoadMoreRecyclerView.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        pullLoadMoreRecyclerView.setPullLoadMoreCompleted();
                    }
                },500);
            }

            @Override
            public void onLoadMore() {
                pullLoadMoreRecyclerView.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        pullLoadMoreRecyclerView.setPullLoadMoreCompleted();
                    }
                },500);
            }
        });

        //网络请求
        pullLoadMoreRecyclerView.startWaveLoadingShow();
        OkHttpInstance.getStore(mParam1, responseString -> {
            if (!responseString.equals("false")){
                pullLoadMoreRecyclerView.stopWaveLoadingShow();
                arrayList = JSON.parseArray(responseString,StoreList.class);
                exampleFragmentAdapter.setUserInfoLists(arrayList);
            }else {
                pullLoadMoreRecyclerView.noDataShow();
            }
        });
    }

    public class ExampleFragmentAdapter extends RecyclerView.Adapter<ViewHolder>{
        List<StoreList> arrayList1 = new ArrayList<>();

        public ExampleFragmentAdapter(List<StoreList> userInfoLists) {
            this.arrayList1 = userInfoLists;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.store_item_layout,parent,false);
            ViewHolder viewHolder = new ViewHolder(view);
            return viewHolder;
        }

        public void setUserInfoLists(List<StoreList> arrayList){
            this.arrayList1 = arrayList;
            notifyDataSetChanged();
        }
        public void swapData(List<StoreList> arrayList){
            int oldSize = this.arrayList1.size();
            int newSize = arrayList.size();
            this.arrayList1 = arrayList;
            notifyItemRangeInserted(oldSize , newSize);
        }

        @Override
        public void onViewAttachedToWindow(@NonNull ViewHolder holder) {
            super.onViewAttachedToWindow(holder);
            holder.portraitFrameView.setPortraitFrame(holder.portraitFrameView.getTag().toString());
        }


        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            Log.d(TAG, "onBindViewHolder: 刷新");

            StoreList currentItem = this.arrayList1.get(position);

            holder.portraitFrameView.setPortraitFrame(currentItem.getSvga());
            holder.portraitFrameView.setTag(currentItem.getSvga());
            holder.name_tv.setText(currentItem.getName());
            holder.description_tv.setText(currentItem.getDescription());

            holder.buy_btn.setOnClickListener(v -> {
                OkHttpInstance.buyStoreGoods(currentItem.getId(), responseString -> {
                    Toast.makeText(getActivity(),responseString,Toast.LENGTH_SHORT).show();
                    if (responseString.equals("购买成功！")){
                        holder.buy_btn.setText("装备");
                        holder.buy_btn.setBackgroundResource(R.drawable.red_round);
                        holder.buy_btn.setOnClickListener(v1 -> {
                            if (currentItem.getType().equals("头像框")){
                                Common.userInfoList.setPortraitframe(currentItem.getSvga());
                            }else if (currentItem.getType().equals("坐骑")){
                                Common.userInfoList.setVeilcel(currentItem.getSvga());
                            }
                            OkHttpInstance.equipGoods(currentItem.getId(), new OkHttpResponseCallBack() {
                                @Override
                                public void getResponseString(String responseString) {
                                    notifyDataSetChanged();
                                }
                            });
                        });
                    }else {
                        GiftDialog.getInstance(getActivity(),false, container,null)
                                .initShow(new Member(Common.userInfoList.getId(), Common.userInfoList.getNickname(),
                                        Common.userInfoList.getPortrait(),Common.userInfoList.getGender(),Common.userInfoList.getProperty(),
                                        Common.userInfoList.getPortraitframe(),Common.userInfoList.getVeilcel()));
                    }
                });
            });

            holder.buy_btn.setText("购买");
            holder.buy_btn.setBackgroundResource(R.drawable.bd_bg_square_round_corner_blue);


            if (!currentItem.getCurrency().equals("任务")){
                holder.price_tv.setVisibility(View.VISIBLE);
                holder.price_tv.setText(currentItem.getPrice()+"  "+currentItem.getCurrency());
                holder.buy_btn.setVisibility(View.VISIBLE);
            }else {
                holder.price_tv.setVisibility(View.GONE);
                holder.buy_btn.setVisibility(View.GONE);
            }

            if (!Common.userInfoList.getRp_verify_time().equals("0") && (currentItem.getDescription().equals("实名认证专属头像框") || currentItem.getDescription().equals("实名认证专属坐骑"))){
                holder.buy_btn.setVisibility(View.VISIBLE);
                holder.buy_btn.setClickable(true);
                holder.buy_btn.setText("装备");
                holder.buy_btn.setBackgroundResource(R.drawable.red_round);
                holder.buy_btn.setOnClickListener(v -> {
                    if (currentItem.getType().equals("头像框")){
                        Common.userInfoList.setPortraitframe(currentItem.getSvga());
                    }else if (currentItem.getType().equals("坐骑")){
                        Common.userInfoList.setVeilcel(currentItem.getSvga());
                    }
                    OkHttpInstance.equipGoods(currentItem.getId(), new OkHttpResponseCallBack() {
                        @Override
                        public void getResponseString(String responseString) {
                            notifyDataSetChanged();
                        }
                    });
                });
            }
            if (Common.isSVip(Common.userInfoList) && (currentItem.getDescription().equals("svip专属头像框") || currentItem.getDescription().equals("svip专属坐骑") || currentItem.getDescription().equals("vip专属头像框") || currentItem.getDescription().equals("vip专属坐骑"))){
                holder.buy_btn.setVisibility(View.VISIBLE);
                holder.buy_btn.setClickable(true);
                holder.buy_btn.setText("装备");
                holder.buy_btn.setBackgroundResource(R.drawable.red_round);
                holder.buy_btn.setOnClickListener(v -> {
                    if (currentItem.getType().equals("头像框")){
                        Common.userInfoList.setPortraitframe(currentItem.getSvga());
                    }else if (currentItem.getType().equals("坐骑")){
                        Common.userInfoList.setVeilcel(currentItem.getSvga());
                    }
                    OkHttpInstance.equipGoods(currentItem.getId(), new OkHttpResponseCallBack() {
                        @Override
                        public void getResponseString(String responseString) {
                            notifyDataSetChanged();
                        }
                    });
                });
            }
            if (Common.isVip(Common.userInfoList) && (currentItem.getDescription().equals("vip专属头像框") || currentItem.getDescription().equals("vip专属坐骑"))){
                holder.buy_btn.setVisibility(View.VISIBLE);
                holder.buy_btn.setClickable(true);
                holder.buy_btn.setText("装备");
                holder.buy_btn.setBackgroundResource(R.drawable.red_round);
                holder.buy_btn.setOnClickListener(v -> {
                    if (currentItem.getType().equals("头像框")){
                        Common.userInfoList.setPortraitframe(currentItem.getSvga());
                    }else if (currentItem.getType().equals("坐骑")){
                        Common.userInfoList.setVeilcel(currentItem.getSvga());
                    }
                    OkHttpInstance.equipGoods(currentItem.getId(), new OkHttpResponseCallBack() {
                        @Override
                        public void getResponseString(String responseString) {
                            notifyDataSetChanged();
                        }
                    });
                });
            }

            int lv = Integer.parseInt(Common.userInfoList.getVitality()) + Integer.parseInt(Common.userInfoList.getPost()) + Integer.parseInt(Common.userInfoList.getComment());
            Log.d(TAG, "onBindViewHolder: 等级数值"+lv);
            if (lv > 800 && (currentItem.getDescription().equals("5级专属头像框") || currentItem.getDescription().equals("5级专属坐骑"))){
                holder.buy_btn.setVisibility(View.VISIBLE);
                holder.buy_btn.setClickable(true);
                holder.buy_btn.setText("装备");
                holder.buy_btn.setBackgroundResource(R.drawable.red_round);
                holder.buy_btn.setOnClickListener(v -> {
                    if (currentItem.getType().equals("头像框")){
                        Common.userInfoList.setPortraitframe(currentItem.getSvga());
                    }else if (currentItem.getType().equals("坐骑")){
                        Common.userInfoList.setVeilcel(currentItem.getSvga());
                    }
                    OkHttpInstance.equipGoods(currentItem.getId(), new OkHttpResponseCallBack() {
                        @Override
                        public void getResponseString(String responseString) {
                            notifyDataSetChanged();
                        }
                    });
                });
            }
            if (lv > 10000 && (currentItem.getDescription().equals("10级专属头像框") || currentItem.getDescription().equals("10级专属坐骑"))){
                holder.buy_btn.setVisibility(View.VISIBLE);
                holder.buy_btn.setClickable(true);
                holder.buy_btn.setText("装备");
                holder.buy_btn.setBackgroundResource(R.drawable.red_round);
                holder.buy_btn.setOnClickListener(v -> {
                    if (currentItem.getType().equals("头像框")){
                        Common.userInfoList.setPortraitframe(currentItem.getSvga());
                    }else if (currentItem.getType().equals("坐骑")){
                        Common.userInfoList.setVeilcel(currentItem.getSvga());
                    }
                    OkHttpInstance.equipGoods(currentItem.getId(), new OkHttpResponseCallBack() {
                        @Override
                        public void getResponseString(String responseString) {
                            notifyDataSetChanged();
                        }
                    });
                });
            }
            if (lv > 40000 && (currentItem.getDescription().equals("15级专属头像框") || currentItem.getDescription().equals("15级专属坐骑"))){
                holder.buy_btn.setVisibility(View.VISIBLE);
                holder.buy_btn.setClickable(true);
                holder.buy_btn.setText("装备");
                holder.buy_btn.setBackgroundResource(R.drawable.red_round);
                holder.buy_btn.setOnClickListener(v -> {
                    if (currentItem.getType().equals("头像框")){
                        Common.userInfoList.setPortraitframe(currentItem.getSvga());
                    }else if (currentItem.getType().equals("坐骑")){
                        Common.userInfoList.setVeilcel(currentItem.getSvga());
                    }
                    OkHttpInstance.equipGoods(currentItem.getId(), new OkHttpResponseCallBack() {
                        @Override
                        public void getResponseString(String responseString) {
                            notifyDataSetChanged();
                        }
                    });
                });
            }
            if (lv > 90000 && (currentItem.getDescription().equals("20级专属头像框") || currentItem.getDescription().equals("20级专属坐骑"))){
                holder.buy_btn.setVisibility(View.VISIBLE);
                holder.buy_btn.setClickable(true);
                holder.buy_btn.setText("装备");
                holder.buy_btn.setBackgroundResource(R.drawable.red_round);
                holder.buy_btn.setOnClickListener(v -> {
                    if (currentItem.getType().equals("头像框")){
                        Common.userInfoList.setPortraitframe(currentItem.getSvga());
                    }else if (currentItem.getType().equals("坐骑")){
                        Common.userInfoList.setVeilcel(currentItem.getSvga());
                    }
                    OkHttpInstance.equipGoods(currentItem.getId(), new OkHttpResponseCallBack() {
                        @Override
                        public void getResponseString(String responseString) {
                            notifyDataSetChanged();
                        }
                    });
                });
            }

            if (currentItem.getSvga().equals(Common.userInfoList.getPortraitframe()) || currentItem.getSvga().equals(Common.userInfoList.getVeilcel())){
                holder.buy_btn.setText("已装备");
                holder.buy_btn.setBackgroundResource(R.drawable.corner_gray_bg);
                holder.buy_btn.setOnClickListener(v -> {
                    if (currentItem.getType().equals("头像框")){
                        Common.userInfoList.setPortraitframe("");
                        OkHttpInstance.equipGoods("-1", new OkHttpResponseCallBack() {
                            @Override
                            public void getResponseString(String responseString) {
                                notifyDataSetChanged();
                            }
                        });
                    }else if (currentItem.getType().equals("坐骑")){
                        Common.userInfoList.setVeilcel("");
                        OkHttpInstance.equipGoods("-2", new OkHttpResponseCallBack() {
                            @Override
                            public void getResponseString(String responseString) {
                                notifyDataSetChanged();
                            }
                        });
                    }
                });
            }else {
                holder.buy_btn.setClickable(true);
                for (StoreList storeList : Common.storeLists){
                    if (storeList.getGoods_id().equals(currentItem.getId())){
                        holder.buy_btn.setText("装备");
                        holder.buy_btn.setBackgroundResource(R.drawable.red_round);
                        holder.buy_btn.setOnClickListener(v -> {
                            if (currentItem.getType().equals("头像框")){
                                Common.userInfoList.setPortraitframe(currentItem.getSvga());
                            }else if (currentItem.getType().equals("坐骑")){
                                Common.userInfoList.setVeilcel(currentItem.getSvga());
                            }
                            OkHttpInstance.equipGoods(currentItem.getId(), new OkHttpResponseCallBack() {
                                @Override
                                public void getResponseString(String responseString) {
                                    notifyDataSetChanged();
                                }
                            });
                        });
                    }
                }
            }









            if (currentItem.getType().equals("头像框")){
                holder.authportrait.setVisibility(View.VISIBLE);
                Glide.with(getActivity())
                        .asBitmap()
                        .load(Common.getOssResourceUrl(Common.userInfoList.getPortrait()))
                        .into(holder.authportrait);
            }else {
                holder.authportrait.setVisibility(View.GONE);
            }
        }

        @Override
        public int getItemCount() {
            return arrayList1.size();
        }
    }

    public class ViewHolder extends RecyclerView.ViewHolder{


        PortraitFrameView portraitFrameView;
        TextView name_tv;
        TextView description_tv;
        TextView price_tv;
        Button buy_btn;


        CircleImageView authportrait;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            authportrait = itemView.findViewById(R.id.authportrait);
            portraitFrameView = itemView.findViewById(R.id.portraitFrameView);
            name_tv = itemView.findViewById(R.id.name_tv);
            description_tv = itemView.findViewById(R.id.description_tv);
            price_tv = itemView.findViewById(R.id.price_tv);
            buy_btn = itemView.findViewById(R.id.buy_btn);
        }
    }
}