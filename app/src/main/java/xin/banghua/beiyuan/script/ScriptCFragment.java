package xin.banghua.beiyuan.script;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;

import com.alibaba.fastjson.JSON;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import xin.banghua.beiyuan.Adapter.ScriptCAdapter;
import xin.banghua.beiyuan.Adapter.ScriptList;
import xin.banghua.beiyuan.R;
import xin.banghua.beiyuan.utils.OkHttpInstance;
import xin.banghua.pullloadmorerecyclerview.CusPullLoadMoreRecyclerView;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ScriptCFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ScriptCFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public ScriptCFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ScriptFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ScriptCFragment newInstance(String param1, String param2) {
        ScriptCFragment fragment = new ScriptCFragment();
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
    List<ScriptList> scriptLists = new ArrayList<>();
    @BindView(R.id.pullLoadMoreRecyclerView)
    CusPullLoadMoreRecyclerView pullLoadMoreRecyclerView;

    String pageIndex = "0";
    ScriptCAdapter scriptCAdapter;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_script, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ButterKnife.bind(this,view);



        //adpter绑定数据
        scriptCAdapter = new ScriptCAdapter(scriptLists,getActivity());
        //RecyclerView绑定adpter
        pullLoadMoreRecyclerView.setAdapter(scriptCAdapter);
        pullLoadMoreRecyclerView.getRecyclerView().setLayoutManager(new GridLayoutManager(getActivity(),2));
        pullLoadMoreRecyclerView.setOnPullLoadMoreListener(new CusPullLoadMoreRecyclerView.PullLoadMoreListener() {
            @Override
            public void onRefresh() {

//                pageindex = 1;
//                OkHttpInstance.getNewUserList(String.valueOf(pageindex),selectedGender,(resultString)->{
//                    scriptLists = JSON.parseArray(resultString,ScriptList.class);
//                    newUserAdapter.setScriptLists(scriptLists);
//                    newUserAdapter.notifyDataSetChanged();
//                });

                pullLoadMoreRecyclerView.postDelayed(()->pullLoadMoreRecyclerView.setPullLoadMoreCompleted(),1000);
            }
            @Override
            public void onLoadMore() {

                pageIndex = scriptLists.get(scriptLists.size()-1).getId();

                OkHttpInstance.getScript(pageIndex,"合音",responseString -> {
                    if (!responseString.equals("false")){
                        scriptLists.addAll(JSON.parseArray(responseString,ScriptList.class));
                        scriptCAdapter.swapData(scriptLists);
                    }
                });

                pullLoadMoreRecyclerView.postDelayed(()->pullLoadMoreRecyclerView.setPullLoadMoreCompleted(),1000);
            }
        });



        pullLoadMoreRecyclerView.startWaveLoadingShow();



        //网络请求
        OkHttpInstance.getScript(pageIndex,"合音",responseString -> {
            if (!responseString.equals("false")){
                scriptLists.clear();
                scriptLists.addAll(JSON.parseArray(responseString,ScriptList.class));
                scriptCAdapter.setScriptLists(scriptLists);
                pullLoadMoreRecyclerView.stopWaveLoadingShow();
            }else {
                pullLoadMoreRecyclerView.noDataShow();
            }
        });
    }
}