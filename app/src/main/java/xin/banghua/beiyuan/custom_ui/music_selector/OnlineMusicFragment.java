package xin.banghua.beiyuan.custom_ui.music_selector;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.alibaba.fastjson.JSON;
import com.wuxiaolong.pullloadmorerecyclerview.PullLoadMoreRecyclerView;

import java.util.ArrayList;
import java.util.List;

import xin.banghua.beiyuan.Common;
import xin.banghua.beiyuan.R;
import xin.banghua.beiyuan.utils.OkHttpInstance;
import xin.banghua.beiyuan.utils.OkHttpResponseCallBack;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link OnlineMusicFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class OnlineMusicFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public OnlineMusicFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment OnlineMusicFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static OnlineMusicFragment newInstance(String param1, String param2) {
        OnlineMusicFragment fragment = new OnlineMusicFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
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
        return inflater.inflate(R.layout.fragment_online_music, container, false);
    }

    @Override
    public void onStop() {
        super.onStop();
        MusicPlayer.reset();
    }

    int pageindex = 1;
    String query = "";
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        //获取控件
        mListViewMusic=(PullLoadMoreRecyclerView) view.findViewById(R.id.music_listView);
        music_search=(SearchView) view.findViewById(R.id.music_search);
        //设置适配器
        // 读取在线音乐文件

        musicListViewAdapter=new OnlineMusicListViewAdapter(getActivity(),mMusicList);
        mListViewMusic.setAdapter(musicListViewAdapter);

        OkHttpInstance.getOnlineMusicList(String.valueOf(pageindex),query,new OkHttpResponseCallBack() {
            @Override
            public void getResponseString(String responseString) {
                if (responseString!="false"){
                    mMusicList = JSON.parseArray(responseString,Music.class);
                    for (int i=0;i<mMusicList.size();i++){
                        mMusicList.get(i).setPath(Common.getOssResourceUrl(mMusicList.get(i).getPath()));
                        mMusicList.get(i).setImage(Common.getOssResourceUrl(mMusicList.get(i).getImage()));
                    }
                    musicListViewAdapter.swapDataRefresh(mMusicList);
                }
            }
        });



        mListViewMusic.getRecyclerView().setLayoutManager(new LinearLayoutManager(getActivity()));
        mListViewMusic.addItemDecoration(new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL));//增加分割线
        mListViewMusic.setLinearLayout();
        mListViewMusic.setOnPullLoadMoreListener(new PullLoadMoreRecyclerView.PullLoadMoreListener() {
            @Override
            public void onRefresh() {
                mListViewMusic.postDelayed(()->mListViewMusic.setPullLoadMoreCompleted(),1000);

                pageindex = 1;
                query = "";
                OkHttpInstance.getOnlineMusicList(String.valueOf(pageindex),query,new OkHttpResponseCallBack() {
                    @Override
                    public void getResponseString(String responseString) {
                        if (responseString!="false"){
                            List<Music> mMusicListNew = JSON.parseArray(responseString,Music.class);
                            for (int i=0;i<mMusicListNew.size();i++){
                                mMusicListNew.get(i).setPath(Common.getOssResourceUrl(mMusicListNew.get(i).getPath()));
                                mMusicListNew.get(i).setImage(Common.getOssResourceUrl(mMusicListNew.get(i).getImage()));
                            }
                            mMusicList = mMusicListNew;
                            musicListViewAdapter.swapDataRefresh(mMusicList);
                        }
                    }
                });
            }

            @Override
            public void onLoadMore() {
                mListViewMusic.postDelayed(()->mListViewMusic.setPullLoadMoreCompleted(),1000);
                pageindex = pageindex + 1;
                OkHttpInstance.getOnlineMusicList(String.valueOf(pageindex),query,new OkHttpResponseCallBack() {
                    @Override
                    public void getResponseString(String responseString) {
                        if (responseString!="false"){
                            List<Music> mMusicListNew = JSON.parseArray(responseString,Music.class);
                            for (int i=0;i<mMusicListNew.size();i++){
                                mMusicListNew.get(i).setPath(Common.getOssResourceUrl(mMusicListNew.get(i).getPath()));
                                mMusicListNew.get(i).setImage(Common.getOssResourceUrl(mMusicListNew.get(i).getImage()));
                            }
                            mMusicList.addAll(mMusicListNew);
                            musicListViewAdapter.swapData(mMusicList);
                        }
                    }
                });
            }
        });


        initEvent();
    }

    private static final String TAG = "MainActivity";

    //音乐ListView视图
    private SearchView music_search;
    //音乐ListView视图
    private PullLoadMoreRecyclerView mListViewMusic;
    //音乐集合
    private List<Music> mMusicList = new ArrayList<>();
    //ListView的适配器
    private OnlineMusicListViewAdapter musicListViewAdapter;
    //开始播放的进度
    private long startProgress=0;



    private void initEvent() {
        // TODO Auto-generated method stub
        music_search.setIconifiedByDefault(false);
        music_search.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String queryNew) {
                pageindex = 1;
                query = queryNew;
                OkHttpInstance.getOnlineMusicList(String.valueOf(pageindex),query,new OkHttpResponseCallBack() {
                    @Override
                    public void getResponseString(String responseString) {
                        if (responseString!="false"){
                            List<Music> mMusicListNew = JSON.parseArray(responseString,Music.class);
                            for (int i=0;i<mMusicListNew.size();i++){
                                mMusicListNew.get(i).setPath(Common.getOssResourceUrl(mMusicListNew.get(i).getPath()));
                                mMusicListNew.get(i).setImage(Common.getOssResourceUrl(mMusicListNew.get(i).getImage()));
                            }
                            mMusicList = mMusicListNew;
                            musicListViewAdapter.swapDataRefresh(mMusicList);
                        }
                    }
                });
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
        /**
         * 监听ListView中音乐的播放事件
         */
        musicListViewAdapter.setOnplayMusicListener(new OnlineMusicListViewAdapter.OnplayMusicListener() {
            //播放完整歌曲
            @Override
            public void playForFull() {
                // TODO Auto-generated method stub
                if(musicListViewAdapter!=null){
                    musicListViewAdapter.setSelectItem(musicListViewAdapter.getSelectItem());
                    musicListViewAdapter.setHidePlayBtn(true);
                    musicListViewAdapter.notifyDataSetChanged();
                }
                Music music=musicListViewAdapter.getCurrentMusic();
                MusicPlayer.play(getActivity(),music.getPath(), 0);
            }
            //播放片段歌曲
            @Override
            public void playforPart() {
                // TODO Auto-generated method stub
                startProgress=musicListViewAdapter.getCutMusicStartProgress();
                MusicPlayer.endProgress=musicListViewAdapter.getCutMusicEndProgress();
                String path=musicListViewAdapter.getCutMusicPath();
                MusicPlayer.play(getActivity(), path,(int)startProgress);
                MusicPlayer.handler.post(MusicPlayer.run);
            }
        });
        /**
         * 监听ListView子项的点击事件
         */
//        mListViewMusic.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//
//            @Override
//            public void onItemClick(AdapterView<?> parent, View view, int position,
//                                    long id) {
//                // TODO Auto-generated method stub
//
//                if(musicListViewAdapter!=null){
//                    musicListViewAdapter.setHidePlayBtn(false);
//                    musicListViewAdapter.setSelectItem(position);
//                    musicListViewAdapter.notifyDataSetChanged();
//                }
//                MusicPlayer.reset();
//            }
//        });
    }
}