package xin.banghua.beiyuan.custom_ui;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.wuxiaolong.pullloadmorerecyclerview.PullLoadMoreRecyclerView;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import xin.banghua.beiyuan.Adapter.UserInfoList;
import xin.banghua.beiyuan.R;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link RecyclerViewFragmentExample#newInstance} factory method to
 * create an instance of this fragment.
 *
 *             <!-- TODO: Update blank fragment layout -->
 *             <com.wuxiaolong.pullloadmorerecyclerview.PullLoadMoreRecyclerView
 *                 android:id="@+id/new_user_rv"
 *                 android:layout_width="match_parent"
 *                 android:layout_height="match_parent"
 *                 android:layout_span="5"
 *                 android:background="#EDF0F5" />
 */
public class RecyclerViewFragmentExample extends Fragment {
    private static final String TAG = "NewUserFragment";
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    // TODO: Rename and change types of parameters

    public RecyclerViewFragmentExample() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment NewUserFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static RecyclerViewFragmentExample newInstance(List<UserInfoList> param1) {
        RecyclerViewFragmentExample fragment = new RecyclerViewFragmentExample();
        Bundle args = new Bundle();
        args.putSerializable(ARG_PARAM1, (Serializable) param1);
        fragment.setArguments(args);
        return fragment;
    }

    List<UserInfoList> arrayList = new ArrayList<>();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            arrayList = (List) getArguments().getSerializable(ARG_PARAM1);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_mine, container, false);
    }



    @BindView(R.id.pullLoadMoreRecyclerView)
    PullLoadMoreRecyclerView pullLoadMoreRecyclerView;


    //xml
//    <com.wuxiaolong.pullloadmorerecyclerview.PullLoadMoreRecyclerView
//      android:id="@+id/new_user_rv"
//      android:layout_width="match_parent"
//      android:layout_height="match_parent"
//      android:layout_span="5"
//      android:background="#EDF0F5" />



    List<UserInfoList> userInfoLists = new ArrayList<>();

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ButterKnife.bind(this,view);


        //adpter绑定数据
        ExampleFragmentAdapter exampleFragmentAdapter = new ExampleFragmentAdapter(userInfoLists);
        //RecyclerView绑定adpter
        pullLoadMoreRecyclerView.setAdapter(exampleFragmentAdapter);
        pullLoadMoreRecyclerView.getRecyclerView().setLayoutManager(new LinearLayoutManager(getActivity()));
        pullLoadMoreRecyclerView.addItemDecoration(new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL));//增加分割线

        pullLoadMoreRecyclerView.setLinearLayout();
        pullLoadMoreRecyclerView.setOnPullLoadMoreListener(new PullLoadMoreRecyclerView.PullLoadMoreListener() {
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

//                pageindex = pageindex + 1;
//                OkHttpInstance.getNewUserList(String.valueOf(pageindex),selectedGender,(resultString)->{
//                    userInfoLists.addAll(JSON.parseArray(resultString,UserInfoList.class));
//                    newUserAdapter.swapData(userInfoLists);
//                });

                pullLoadMoreRecyclerView.postDelayed(()->pullLoadMoreRecyclerView.setPullLoadMoreCompleted(),1000);
            }
        });



        //网络请求

//        OkHttpInstance.getXunYuanLikeList("0", responseString -> {
//            if (!responseString.equals("false")){
//                userInfoLists = JSON.parseArray(responseString,UserInfoList.class);
//                myXunYuanLikeAdapter.swapData(userInfoLists);
//            }
//        });
    }

    public class ExampleFragmentAdapter extends RecyclerView.Adapter<ViewHolder>{
        List<UserInfoList> userInfoLists = new ArrayList<>();

        public ExampleFragmentAdapter(List<UserInfoList> userInfoLists) {
            this.userInfoLists = userInfoLists;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.fragment_mine,parent,false);
            ViewHolder viewHolder = new ViewHolder(view);
            return viewHolder;
        }

        public void setUserInfoLists(List<UserInfoList> userInfoLists){
            this.userInfoLists = userInfoLists;
        }
        public void swapData(List<UserInfoList> userInfoLists){
            int oldSize = this.userInfoLists.size();
            int newSize = userInfoLists.size();
            this.userInfoLists = userInfoLists;
            notifyItemRangeInserted(oldSize , newSize);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            Log.d(TAG, "onBindViewHolder: 刷新");

            UserInfoList currentItem = this.userInfoLists.get(position);

        }

        @Override
        public int getItemCount() {
            return 1;
        }
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        TextView userID;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            userID = itemView.findViewById(R.id.userID);
        }
    }

}