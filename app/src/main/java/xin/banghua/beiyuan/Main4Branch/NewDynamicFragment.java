package xin.banghua.beiyuan.Main4Branch;


import static xin.banghua.beiyuan.utils.ThreadUtils.runOnUiThread;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.alibaba.fastjson.JSON;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import xin.banghua.beiyuan.Adapter.LuntanList;
import xin.banghua.beiyuan.Main4Branch.tiktok.TikTokController;
import xin.banghua.beiyuan.Main4Branch.tiktok.TikTokRenderViewFactory;
import xin.banghua.beiyuan.Main4Branch.tiktok.Utils;
import xin.banghua.beiyuan.Main4Branch.tiktok.cache.PreloadManager;
import xin.banghua.beiyuan.R;
import xin.banghua.beiyuan.utils.OkHttpInstance;
import xin.banghua.beiyuan.utils.OkHttpResponseCallBack;
import xin.banghua.beiyuan.utils.SampleCoverVideo;
import xyz.doikki.videoplayer.player.VideoView;

/**
 * A simple {@link Fragment} subclass.
 * create an instance of this fragment.
 *
 */
public class NewDynamicFragment extends Fragment {
    private static final String TAG = "VideoPagerBenquan1213";

    NewDynamicFragment newDynamicFragment;

    @BindView(R.id.mViewPager)
    ViewPager2 mViewPager;//PagerSnapHelper+RecyclerView替换成ViewPager2
    @BindView(R.id.video_frameLayout)
    FrameLayout video_frameLayout;


    //第一部分是视频
    public static SampleCoverVideo mVideoView;
    RecyclerView rvPage;
    private List<LuntanList> luntanLists = new ArrayList<>();


    public NewDynamicFragment() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_new_dynamic, container, false);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    /**
     * 当前fragment用户是否可见
//     * @param isVisibleToUser
     */
    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        Log.d(TAG, "setUserVisibleHint: 触发1");
        //防止数据预加载, 只预加载View，不预加载数据
        if (isVisibleToUser) {
            Log.d(TAG, "fragment是否可见: 现在可见"+mCurPos);
            if (sampleCoverVideo_current!=null)
                startPlay(mCurPos);
        }else {
            if (mVideoView_new!=null)
                mVideoView_new.pause();
            if (sampleCoverVideo_current!=null)
                sampleCoverVideo_current.release();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "setUserVisibleHint: 触发2");

        if (mVideoView_new!=null)
            mVideoView_new.release();
    }
    @Override
    public void onPause() {
        super.onPause();
        Log.d(TAG, "setUserVisibleHint: 触发3");

        if (mVideoView_new!=null)
            mVideoView_new.pause();

        Log.d(TAG, "onDestroy: 触发返回1");
    }
    @Override
    public void onStop() {
        super.onStop();
        Log.d(TAG, "setUserVisibleHint: 触发4");
        if (mVideoView_new!=null)
            mVideoView_new.release();
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG, "setUserVisibleHint: 触发5");

        if (mVideoView_new!=null)
            mVideoView_new.resume();

    }



    View mView;
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mView = view;

        ButterKnife.bind(this,view);
        Log.d(TAG, "onViewCreated: 调用");
        //键盘监听
        //EventBus.getDefault().register(this);

        Log.d(TAG, "onCreate: 开始了2");

        newDynamicFragment = this;

        initVideoView();
        initViewPager();

    }

    public int mCurPos;



    /**
     * 初始化视频播放器viewpager
     */
    DouYinViewAdapter douYinViewAdapter;
    public void initViewPager() {
//        DouyinFragment.fromWhere = -1;
        //ViewPage2内部是通过RecyclerView去实现的，它位于ViewPager2的第0个位置
        rvPage = (RecyclerView) mViewPager.getChildAt(0);
        mViewPager.setOffscreenPageLimit(4);
        mViewPager.setOverScrollMode(View.OVER_SCROLL_NEVER);

        luntanLists = new ArrayList<>();

        mVideoView = SampleCoverVideo.getInstance(getActivity());

        douYinViewAdapter = new DouYinViewAdapter(getActivity(),luntanLists);
//                            douYinViewAdapter.setFragment(newDynamicFragment, null);
        douYinViewAdapter.setVideoView(mVideoView);
        douYinViewAdapter.setVideoView_new(mVideoView_new);
        mViewPager.setAdapter(douYinViewAdapter);
        rvPage.scrollBy(0,-1);



        mViewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {

            private int mCurItem;

            /**
             * VerticalViewPager是否反向滑动
             */
            private boolean mIsReverseScroll;

            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                super.onPageScrolled(position, positionOffset, positionOffsetPixels);
                if (position == mCurItem) {
                    return;
                }
                mIsReverseScroll = position < mCurItem;
            }

            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);

                if (position == 0) mViewPager.post(()->startPlay(position));

                if (position == mCurPos) return;

//                停止播放声音
//                SingletonMusicPlayer.getInstance().reset();
                rvPage.postDelayed(()->startPlay(position),300);
                mCurPos = position;
                Log.d(TAG, "onPageSelected: 翻页了"+position+"|"+ luntanLists.size()+"|"+mIsReverseScroll);

                if (position + 5 == luntanLists.size()){
                    Log.d(TAG, "onPageSelected: 翻页了，加载更多"+luntanLists.get(luntanLists.size()-1).getId());
                    pageIndex = luntanLists.get(luntanLists.size()-1).getId();
                    initData();
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                super.onPageScrollStateChanged(state);
                if (state == ViewPager2.SCROLL_STATE_DRAGGING) {
                    mCurItem = mViewPager.getCurrentItem();
                }
            }
        });


        initData();


    }


    String pageIndex = "0";
    public void initData(){
        Log.d(TAG, "onPageSelected: 翻页了，1加载更多"+pageIndex);
        OkHttpInstance.getVideo("",pageIndex,new OkHttpResponseCallBack() {
            @Override
            public void getResponseString(String responseString) {
                Log.d(TAG, "onPageSelected: 翻页了，2加载更多");
                if (!responseString.equals("false")){
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Log.d(TAG, "run: 获取推荐帖子"+responseString);

                            luntanLists.addAll(JSON.parseArray(responseString,LuntanList.class));
                            douYinViewAdapter.swap(luntanLists);
                            Log.d(TAG, "onScrollStateChanged: 触发更多1"+luntanLists.size()+"|"+responseString);
                        }
                    });
                }else {
                    //initData();
                }
            }
        });
    }


    public static VideoView mVideoView_new;
    private TikTokController mController;
    private PreloadManager mPreloadManager;

    /**
     * 初始化新的带缓存的视频播放器
     */
    public void initVideoView() {
        if (mVideoView_new==null){
            mVideoView_new = new VideoView(getActivity());

            mVideoView_new.setLooping(true);
            //以下只能二选一，看你的需求
            mVideoView_new.setRenderViewFactory(TikTokRenderViewFactory.create());
//        mVideoView.setScreenScaleType(VideoView.SCREEN_SCALE_CENTER_CROP);
        }
        mController = new TikTokController(getActivity());
        mVideoView_new.setVideoController(mController);

        mPreloadManager = PreloadManager.getInstance(getActivity());
    }

    SampleCoverVideo sampleCoverVideo_current;
    /**
     * 播放视频
     * @param position
     */
    public void startPlay(int position) {
        if (rvPage==null)
            return;

        if (mVideoView_new!=null)
            mVideoView_new.release();

        Log.d(TAG, "startPlay: 开始播放");
//        if (TextUtils.isEmpty(luntanLists.get(position).getVideourl())){
//            Log.d(TAG, "startPlay: 不是视频");
////            mVideoView.release();
//            return;
//        }

        DouYinViewAdapter.VideoViewHolder viewHolder = (DouYinViewAdapter.VideoViewHolder)rvPage.findViewHolderForAdapterPosition(position);
        if (viewHolder==null){
//            initViewPager();
            return;
        }

        if (sampleCoverVideo_current!=null)
            sampleCoverVideo_current.showPauseCover();


//        Common.currentSelectedUserID = luntanLists.get(position).getAuthid();//当前用户id，用于右划跳转个人界面,和评论中判断是否是作者
//        Common.currentPostId = luntanLists.get(position).getId();//当前postid，用于右划跳转个人界面时显示刚才查看的作品
        LikeLayout.ifVideo = true;
        Log.d(TAG, "startPlay: 播放2");



        Utils.removeViewFormParent(mVideoView_new);
        String playUrl = mPreloadManager.getPlayUrl(luntanLists.get(position).getPostvideo());
        Log.d(TAG, "startPlay: startPlay: " + "position: " + position + "  url: " + playUrl + " getPostvideo"+luntanLists.get(position).getPostvideo());
        mVideoView_new.setUrl(playUrl);
        //请点进去看isDissociate的解释
        mController.addControlComponent(viewHolder.mTikTokView, true);
        viewHolder.mPlayerContainer.addView(mVideoView_new, 0);
        mVideoView_new.start();

        viewHolder.sampleCoverVideo.startPlayLogic();
        sampleCoverVideo_current = viewHolder.sampleCoverVideo;

    }
}