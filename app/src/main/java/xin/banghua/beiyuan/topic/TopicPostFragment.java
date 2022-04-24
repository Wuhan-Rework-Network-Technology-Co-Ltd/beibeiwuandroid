package xin.banghua.beiyuan.topic;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.alibaba.fastjson.JSON;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import xin.banghua.beiyuan.Adapter.LuntanAdapter;
import xin.banghua.beiyuan.Adapter.LuntanList;
import xin.banghua.beiyuan.R;
import xin.banghua.beiyuan.utils.OkHttpInstance;
import xin.banghua.pullloadmorerecyclerview.CusPullLoadMoreRecyclerView;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link TopicPostFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class TopicPostFragment extends Fragment {
    private static final String TAG = "TopicPostFragment";
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public TopicPostFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment TopicPostFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static TopicPostFragment newInstance(String param1, String param2) {
        TopicPostFragment fragment = new TopicPostFragment();
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
        return inflater.inflate(R.layout.fragment_topic_post, container, false);
    }


    List<LuntanList> luntanLists = new ArrayList<>();

    @BindView(R.id.pullLoadMoreRecyclerView)
    CusPullLoadMoreRecyclerView pullLoadMoreRecyclerView;


    String pageIndex = "0";
    LuntanAdapter exampleFragmentAdapter;


    public TopicPostActivity topicPostActivity;
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ButterKnife.bind(this,view);

        //adpter绑定数据
        exampleFragmentAdapter = new LuntanAdapter(luntanLists,getActivity());
        //RecyclerView绑定adpter
        pullLoadMoreRecyclerView.setAdapter(exampleFragmentAdapter);
        pullLoadMoreRecyclerView.getRecyclerView().setLayoutManager(new LinearLayoutManager(getActivity()));
        pullLoadMoreRecyclerView.addItemDecoration(new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL));//增加分割线

        pullLoadMoreRecyclerView.setLinearLayout();
        pullLoadMoreRecyclerView.setOnPullLoadMoreListener(new CusPullLoadMoreRecyclerView.PullLoadMoreListener() {
            @Override
            public void onRefresh() {

//                pageindex = 1;
//                OkHttpInstance.getNewUserList(String.valueOf(pageindex),selectedGender,(resultString)->{
//                    userInfoLists = JSON.parseArray(resultString,UserInfoList.class);
//                    newUserAdapter.setUserInfoLists(userInfoLists);
//                    newUserAdapter.notifyDataSetChanged();
//                });

                pullLoadMoreRecyclerView.postDelayed(()->pullLoadMoreRecyclerView.setPullLoadMoreCompleted(),1000);
            }
            @Override
            public void onLoadMore() {

                pageIndex = luntanLists.get(luntanLists.size()-1).getId();
                if (mParam2.equals("最新")){
                    OkHttpInstance.getTopicPost(mParam1,pageIndex, responseString -> {
                        if (!responseString.equals("false")){
                            luntanLists.addAll(JSON.parseArray(responseString,LuntanList.class));
                            exampleFragmentAdapter.swapData(luntanLists);
                        }
                    });
                }else if (mParam2.equals("粉丝")){
                    OkHttpInstance.getTopicPost(mParam1,pageIndex, responseString -> {
                        if (!responseString.equals("false")){
                            luntanLists.addAll(JSON.parseArray(responseString,LuntanList.class));
                            exampleFragmentAdapter.swapData(luntanLists);
                        }
                    });
                }

                pullLoadMoreRecyclerView.postDelayed(()->pullLoadMoreRecyclerView.setPullLoadMoreCompleted(),1000);
            }
        });



        pullLoadMoreRecyclerView.startWaveLoadingShow();
        pullLoadMoreRecyclerView.getRecyclerView().addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                //topicPostActivity.nestedScrollView.scrollTo(dx,dy-topicPostActivity.nestedScrollView.getHeight());
                Log.d(TAG, "onScrolled: ");
            }

            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);

            }
        });
    }


    @Override
    public void onResume() {
        super.onResume();

        pullLoadMoreRecyclerView.startWaveLoadingShow();
        //网络请求
        if (mParam2.equals("最新")){
            OkHttpInstance.getTopicPost(mParam1,pageIndex, responseString -> {
                if (!responseString.equals("false")){
                    luntanLists.clear();
                    luntanLists.addAll(JSON.parseArray(responseString,LuntanList.class));
                    exampleFragmentAdapter.setLuntanLists(luntanLists);
                    pullLoadMoreRecyclerView.stopWaveLoadingShow();
                }else {
                    pullLoadMoreRecyclerView.noDataShow();
                }
            });
        }else if (mParam2.equals("粉丝")){
            OkHttpInstance.getTopicPost(mParam1,pageIndex, responseString -> {
                if (!responseString.equals("false")){
                    luntanLists.addAll(JSON.parseArray(responseString,LuntanList.class));
                    exampleFragmentAdapter.setLuntanLists(luntanLists);
                    pullLoadMoreRecyclerView.stopWaveLoadingShow();
                }else {
                    pullLoadMoreRecyclerView.noDataShow();
                }
            });
        }
    }
}