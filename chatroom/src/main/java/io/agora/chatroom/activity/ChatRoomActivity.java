package io.agora.chatroom.activity;

import static android.Manifest.permission.RECORD_AUDIO;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;
import static android.view.View.GONE;
import static android.view.View.VISIBLE;
import static androidx.core.content.PermissionChecker.PERMISSION_GRANTED;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.webkit.JavascriptInterface;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatCheckBox;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SimpleItemAnimator;

import com.alibaba.fastjson.JSON;
import com.elvishew.xlog.LogLevel;
import com.elvishew.xlog.XLog;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnCheckedChanged;
import butterknife.OnClick;
import butterknife.OnEditorAction;
import io.agora.chatroom.Common;
import io.agora.chatroom.FilmTypeFrameLayout;
import io.agora.chatroom.OkHttpInstance;
import io.agora.chatroom.OkHttpResponseCallBack;
import io.agora.chatroom.PIPManager;
import io.agora.chatroom.R;
import io.agora.chatroom.R2;
import io.agora.chatroom.SendBarrageFrameLayout;
import io.agora.chatroom.SwitchRoomActivity;
import io.agora.chatroom.UserInfoDialog;
import io.agora.chatroom.adapter.MessageListAdapter;
import io.agora.chatroom.adapter.NpaLinearLayoutManager;
import io.agora.chatroom.adapter.SeatGridAdapter;
import io.agora.chatroom.gift.GiftDialog;
import io.agora.chatroom.gift.GiftFrameLayout;
import io.agora.chatroom.ktv.KtvFrameLayout;
import io.agora.chatroom.ktv.KtvMusicListDialog;
import io.agora.chatroom.ktv.MemberMusicModel;
import io.agora.chatroom.ktv.MusicPlayer;
import io.agora.chatroom.ktv.OrderMusicFrameLayout;
import io.agora.chatroom.manager.ChatRoomEventListener;
import io.agora.chatroom.manager.ChatRoomManager;
import io.agora.chatroom.manager.RtmManager;
import io.agora.chatroom.manager.SeatManager;
import io.agora.chatroom.model.Channel;
import io.agora.chatroom.model.ChannelData;
import io.agora.chatroom.model.Constant;
import io.agora.chatroom.model.Message;
import io.agora.chatroom.model.Seat;
import io.agora.chatroom.util.AlertUtil;
import io.agora.chatroom.widget.MemberListDialog;
import io.agora.chatroom.widget.VoiceChangerDialog;
import io.agora.rtm.ErrorInfo;
import io.agora.rtm.ResultCallback;
import io.agora.rtm.RtmMessage;
import xyz.doikki.videoplayer.exo.ExoMediaPlayerFactory;
import xyz.doikki.videoplayer.player.VideoViewConfig;
import xyz.doikki.videoplayer.player.VideoViewManager;

public class ChatRoomActivity extends AppCompatActivity implements ChatRoomEventListener, SeatGridAdapter.OnItemClickListener {
    private static final String TAG = "ChatRoomActivity";

    public static final String BUNDLE_KEY_CHANNEL_TYPE = "channelType";//房间类型，不如五子棋，你画我猜等
    public static final String FIVE_CHESS_TYPE = "五子棋";//房间类型，不如五子棋，你画我猜等
    public static final String DRAW_GUESS_TYPE = "你画我猜";//房间类型，不如五子棋，你画我猜等
    public static final String WATCH_FILM_TYPE = "看电影";//房间类型，不如五子棋，你画我猜等
    public static final String KTV_TYPE = "K歌";//房间类型，不如五子棋，你画我猜等
    public static final String COUPLE_TYPE = "处CP";//房间类型，不如五子棋，你画我猜等

    public static final String BUNDLE_KEY_ANCHOR_NAME = "anchorName";
    public static final String BUNDLE_KEY_ANCHOR_PORTRAIT = "anchorPortrait";
    public static final String BUNDLE_KEY_BACKGROUND_RES = "backgroundRes";
    public static final String BUNDLE_KEY_COVER_RES = "coverRes";
    public static final String BUNDLE_KEY_CHANNEL_PASSWORD = "channelPassword";
    public static final String BUNDLE_KEY_CHANNEL_ANNOUNCEMENT = "channelAnnouncement";
    public static final int IMAGE_PICKER_REQUEST_CODE_BACKGROUND = 3961;
    public static final int ACTION_REQUEST_EDITIMAGE_BACKGROUND = 3962;
    public static final int IMAGE_PICKER_REQUEST_CODE_COVER = 3963;
    public static final int ACTION_REQUEST_EDITIMAGE_COVER = 3964;


    Activity mContext;


    public static final String BUNDLE_KEY_CHANNEL = "channel";
    public static final String BUNDLE_KEY_CHANNEL_ID = "channelId";
    public static final String BUNDLE_KEY_CHANNEL_NAME = "channelName";
    public static final String BUNDLE_KEY_CHANNEL_PORTRAIT = "channelPortrait";
    private final int PERMISSION_REQ_ID = 22;

    public static ChatRoomManager mManager;

    public ChatRoomManager getManager() {
        return mManager;
    }

    private MemberListDialog mMemberDialog = new MemberListDialog();
    private VoiceChangerDialog mChangerDialog = new VoiceChangerDialog();
    private SeatGridAdapter mSeatAdapter;
    private MessageListAdapter mMessageAdapter;
    private String mChannelId;
    private boolean mMuteRemote;
    private boolean isDestroyed;

    @BindView(R2.id.container)
    public ConstraintLayout container;
    @BindView(R2.id.tv_title)
    TextView tv_title;
    @BindView(R2.id.btn_num)
    TextView btn_num;
    @BindView(R2.id.rv_seat_grid)
    RecyclerView rv_seat_grid;
    @BindView(R2.id.rv_message_list)
    RecyclerView rv_message_list;

    AppCompatCheckBox cb_mixing;
    @BindView(R2.id.btn_mic)
    ImageButton btn_mic;
//    @BindView(R2.id.gift)
//    GiftPopView gift;

    @BindView(R2.id.switch_room_btn)
    FloatingActionButton switch_room_btn;
    @BindView(R2.id.switch_room_tv)
    TextView switch_room_tv;


    @BindView(R2.id.screen_layout)
    ConstraintLayout screen_layout;

    public static GiftFrameLayout gift_layout;

    public static ChatRoomActivity chatRoomActivity;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        setContentView(R.layout.activity_chat_room);

        VideoViewManager.setConfig(VideoViewConfig.newBuilder()
                .setPlayerFactory(ExoMediaPlayerFactory.create())
                .build());

        chatRoomActivity = this;

        GiftDialog.gift_layout = findViewById(R.id.gift_layout);

        ButterKnife.bind(this);
        Log.d(TAG, "onCreate: 进入聊天室");

        mContext = this;

        GiftDialog.uniqueInstance = null;

        GiftDialog.getInstance(mContext,true,container,null);

        Intent intent = getIntent();
        mChannelId = getIntent().getStringExtra(BUNDLE_KEY_CHANNEL_ID);
        container.setBackgroundResource(intent.getIntExtra(BUNDLE_KEY_BACKGROUND_RES, R.mipmap.bg_channel_0));

        try {
            if (Common.isServiceExisted(this,"io.agora.chatroom.activity.ChatRoomService")) {
                Log.d("触发", "聊天室service已启动");
                stopService(Common.getInstanceChatRoomService());
                if (mManager!=null){
                    if (!mManager.channelId.equals(mChannelId)){
                        mManager.leaveChannel();
                    }
                }
            }else {
                Log.d("触发", "service未启动");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

//        VideoViewManager.setConfig(VideoViewConfig.newBuilder()
//                //使用使用IjkPlayer解码
//                .setPlayerFactory(ExoMediaPlayerFactory.create())
//                .build());

        cb_mixing = findViewById(R.id.cb_mixing);
        SeatManager.cb_mixing = cb_mixing;

        XLog.init(LogLevel.ALL);

        initView();

    }

    @Override
    public void onResume() {
        super.onResume();  // Always call the superclass method first

        Log.d(TAG, "onResume: 重进聊天室rtm");
        if (mManager!=null){
            if (mManager.getRtmManager()!=null){
                if (mManager.getRtmManager().getmRtmChannel()==null){
                    Log.d(TAG, "initManager: 重进聊天室rtm");
                    initManager();
                }
            }
        }

        if (mPIPManager!=null)
            mPIPManager.resume();

        if (SwitchRoomActivity.isSwitchRoom){
            SwitchRoomActivity.isSwitchRoom = false;
            initView();
            mManager.sendMessage("房主切换了房间类型");

            if (mPIPManager!=null)
                mPIPManager.pause();


//            if (ktvView!=null){
//                if (ktvView.getmMusicPlayer()!=null)
//                    ktvView.getmMusicPlayer().pause();
//            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (isFinishing()) {
            destroy();
        }
    }


    @Override
    protected void onDestroy() {
        destroy();
        super.onDestroy();
    }

    private void destroy() {
        if (isDestroyed) return;

        //如果下座位，删除我的全部歌曲，当前播放的我的歌曲结束
        if (isOrderedSong){
            isOrderedSong = false;
            OkHttpInstance.deleteSongUser(Constant.sUserId+"", new OkHttpResponseCallBack() {
                @Override
                public void getResponseString(String responseString) {
                    if (MusicPlayer.mMusicModel!=null && ktvView!=null){
                        if (MusicPlayer.mMusicModel.getUserId().equals(Constant.sUserId+"")){
                            //离开后由频道用户自己处理
                            mManager.sendMessage("歌手离开了座位，开始下一首歌曲");
                        }
                    }
                }
            });
        }

        //mManager.leaveChannel();

        isDestroyed = true;


        if (videoView!=null)
            videoView.unregisteredRtmClientListenerService();

        if (mWebView!=null)
            mWebView.destroy();

        if (mPIPManager!=null)
            mPIPManager.reset();

        if (ktvView!=null){
            if (ktvView.getmMusicPlayer() != null) {
                ktvView.getmMusicPlayer().unregisterPlayerObserver();
                ktvView.getmMusicPlayer().destory();
                ktvView.setmMusicPlayer(null);
            }
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQ_ID) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                mManager.joinChannel(mChannelId);
            else
                AlertUtil.showAlertDialog(this, "No permission", "finish", (dialog, which) -> finish());
        }
    }

    int seatSpanCount = 5;

    public static String mChannelType = "";
    public static String previousChannelType = "";

    private String from;

    public Channel currentChannel;

    private void initView() {



//        if (!TextUtils.isEmpty(intent.getStringExtra(BUNDLE_KEY_BACKGROUND_RES))){
//            container.setBackgroundResource(intent.getIntExtra(BUNDLE_KEY_BACKGROUND_RES, 0));
//        }else {
//            Glide.with(this)
//                    .load(intent.getStringExtra(BUNDLE_KEY_BACKGROUND_RES))
//                    .into(new SimpleTarget<Drawable>() {
//                        @Override
//                        public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition) {
//                            container.setBackground(resource);
//                        }
//                    });
//        }






        OkHttpInstance.getUserAttributes(mChannelId, new OkHttpResponseCallBack() {
            @Override
            public void getResponseString(String responseString) {
                if (!responseString.equals("false")){
                    Log.d(TAG, "getResponseString: 获取房间信息"+responseString);
                    currentChannel = JSON.parseObject(responseString,Channel.class);
                    KtvFrameLayout.currentChannel = currentChannel;
                    mChannelType = currentChannel.getAudioroomtype();

                    if (TextUtils.isEmpty(mChannelType)){
                        mChannelType = COUPLE_TYPE;
                    }
                    adjustUI();
                    tv_title.setText(currentChannel.getAudioroomname());


//                    ktvMusicListDialog = new KtvMusicListDialog(ChatRoomActivity.this);
//                    ktvMusicListDialog.initView(ChatRoomActivity.this,currentChannel);
                    order_music_framelayout.initView(ChatRoomActivity.this, currentChannel, new OkHttpResponseCallBack() {
                        @Override
                        public void getResponseString(String responseString) {
                            adjustUI();
                        }
                    });

                    if (!isRefresh) {
                        initSeatRecyclerView();
                        initMessageRecyclerView();
                        initManager();
                        isRefresh = true;


                        if (mChannelId.equals(Constant.sUserId+"")){
                            switch_room_btn.setVisibility(VISIBLE);
                            switch_room_tv.setVisibility(VISIBLE);
                            switch_room_btn.setOnClickListener(v -> {
                                Intent intent_switch = new Intent(ChatRoomActivity.this, SwitchRoomActivity.class);
                                startActivity(intent_switch);
                            });
                        }
                    }
                }
            }
        });
    }


    public void adjustUI(){
        if (mChannelType.equals(FIVE_CHESS_TYPE)){
            Log.d(TAG, "initView:房间类型 FIVE_CHESS_TYPE");
            screen_layout.setVisibility(VISIBLE);
            initWebView("0","0");
            rv_seat_grid.getLayoutParams().height = ChatRoomActivity.this.getResources().getDimensionPixelSize(R.dimen.dimen_80);
        }else if (mChannelType.equals(DRAW_GUESS_TYPE)){
            Log.d(TAG, "initView:房间类型 DRAW_GUESS_TYPE");
            screen_layout.setVisibility(VISIBLE);
            initWebView("0","0");
            rv_seat_grid.getLayoutParams().height = ChatRoomActivity.this.getResources().getDimensionPixelSize(R.dimen.dimen_80);
        }else if (mChannelType.equals(WATCH_FILM_TYPE)){
            Log.d(TAG, "initView:房间类型 WATCH_FILM_TYPE");
            screen_layout.setVisibility(VISIBLE);
            initVideoView();
            rv_seat_grid.getLayoutParams().height = ChatRoomActivity.this.getResources().getDimensionPixelSize(R.dimen.dimen_80);
        }else if (mChannelType.equals(COUPLE_TYPE)){
            Log.d(TAG, "initView:房间类型 COUPLE_TYPE");
            screen_layout.setVisibility(GONE);
            initCpRoom();
            rv_seat_grid.getLayoutParams().height = ChatRoomActivity.this.getResources().getDimensionPixelSize(R.dimen.dimen_175);
        }else if (mChannelType.equals(KTV_TYPE)){
            Log.d(TAG, "initView:房间类型 COUPLE_TYPE");
            screen_layout.setVisibility(VISIBLE);
            initKtvView();
            rv_seat_grid.getLayoutParams().height = ChatRoomActivity.this.getResources().getDimensionPixelSize(R.dimen.dimen_80);
        }
    }

    boolean isRefresh = false;
    private void initSeatRecyclerView() {
        rv_seat_grid.setHasFixedSize(true);

        RecyclerView.ItemAnimator animator = rv_seat_grid.getItemAnimator();
        if (animator instanceof SimpleItemAnimator)
            ((SimpleItemAnimator) animator).setSupportsChangeAnimations(false);

        mSeatAdapter = new SeatGridAdapter(this);
        mSeatAdapter.setOnItemClickListener(this);
        rv_seat_grid.setAdapter(mSeatAdapter);

        rv_seat_grid.setLayoutManager(new GridLayoutManager(this, seatSpanCount));


        int spacing = getResources().getDimensionPixelSize(R.dimen.item_seat_spacing);
        rv_seat_grid.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
                super.getItemOffsets(outRect, view, parent, state);
                outRect.set(spacing, spacing, spacing, spacing);
            }
        });
    }

    private void initMessageRecyclerView() {
        mMessageAdapter = new MessageListAdapter(this);
        rv_message_list.setAdapter(mMessageAdapter);

        rv_message_list.setLayoutManager(new NpaLinearLayoutManager(this));

        int spacing = getResources().getDimensionPixelSize(R.dimen.item_message_spacing);
        rv_message_list.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
                super.getItemOffsets(outRect, view, parent, state);
                outRect.set(spacing, 0, spacing, spacing);
            }
        });

    }

    public void initManager() {
        mManager = ChatRoomManager.instance(this);
        mManager.setListener(this);
        if (checkPermission())
            mManager.joinChannel(mChannelId);


        btn_num.postDelayed(new Runnable() {
            @Override
            public void run() {
                btn_num.setText(String.valueOf(mManager.getChannelData().getMemberList().size()));
            }
        },1000);

    }

    private boolean checkPermission() {
        if (ContextCompat.checkSelfPermission(this, RECORD_AUDIO) != PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(this, WRITE_EXTERNAL_STORAGE) != PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{RECORD_AUDIO, WRITE_EXTERNAL_STORAGE}, PERMISSION_REQ_ID);
            return false;
        }
        return true;
    }

    public void givingGift(View view) {
        GiftDialog.getInstance(mContext,true,container,null).initShow(null);
        //mManager.givingGift();
    }

    KtvMusicListDialog ktvMusicListDialog;


    public static boolean isOrderedSong = false;

    @BindView(R2.id.order_music_framelayout)
    OrderMusicFrameLayout order_music_framelayout;
    @OnCheckedChanged({R2.id.cb_mixing})
    public void onCheckedChanged(CompoundButton view, boolean isChecked) {
        if (view.getId() == R.id.cb_mixing) {

            isOrderedSong = true;
//            if (isChecked) {
//                ktvMusicListDialog.showDialog();
//                //mManager.getRtcManager().startAudioMixing("/assets/mixing.mp3");
//            } else {
//                ktvMusicListDialog.dismissDialog();
//                //mManager.getRtcManager().stopAudioMixing();
//            }

            //ktvMusicListDialog.showDialog();
            order_music_framelayout.setVisibility(VISIBLE);

            screen_layout.setVisibility(VISIBLE);
            initKtvView();
            rv_seat_grid.getLayoutParams().height = ChatRoomActivity.this.getResources().getDimensionPixelSize(R.dimen.dimen_80);
        }
    }
    @Override
    public void onBackPressed () {
        //super.onBackPressed();
        zoom();
        //finish();
    }
    @OnClick({R2.id.btn_exit, R2.id.btn_num, R2.id.btn_changer, R2.id.btn_mic, R2.id.btn_speaker})
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.btn_exit) {
            zoom();
            //finish();
        } else if (id == R.id.btn_num) {
            mMemberDialog.show(getSupportFragmentManager(), null);
        } else if (id == R.id.btn_changer) {
            mChangerDialog.show(getSupportFragmentManager(), null);
        } else if (id == R.id.btn_mic) {
            String myUserId = String.valueOf(Constant.sUserId);
            mManager.muteMic(myUserId, !mManager.getChannelData().isUserMuted(myUserId));
        } else if (id == R.id.btn_speaker) {
            mMuteRemote = !mMuteRemote;
            ((ImageButton) view).setImageResource(mMuteRemote ? R.mipmap.ic_speaker_off : R.mipmap.ic_speaker_on);
            mManager.getRtcManager().muteAllRemoteAudioStreams(mMuteRemote);
        }
    }

    @OnEditorAction({R2.id.et_input})
    public void onEditorAction(TextView v, int actionId, KeyEvent event) {
        if (actionId == EditorInfo.IME_ACTION_SEND || event.getKeyCode() == KeyEvent.KEYCODE_ENTER) {
            String message = v.getText().toString();
            if (TextUtils.isEmpty(message)) return;
            mManager.sendMessage(message);
            v.setText("");
        }
    }

    @Override
    public void onItemClick(View view, int position, Seat seat) {
        ChannelData channelData = mManager.getChannelData();
        boolean isAnchor = channelData.isAnchorMyself();
        if (seat != null) {
            if (seat.isClosed()) {
                if (isAnchor)
                    showSeatPop(view, new int[]{R.id.open_seat}, null, position);
                return;
            } else {
                String userId = seat.getUserId();
                if (channelData.isUserOnline(userId)) {
                    if (isAnchor) {
                        boolean muted = channelData.isUserMuted(userId);
                        showSeatPop(view, new int[]{
                                R.id.to_audience,
                                muted ? R.id.turn_on_mic : R.id.turn_off_mic,
                                R.id.close_seat
                        }, userId, position);
                    } else {
                        if (Constant.isMyself(userId))
                            showSeatPop(view, new int[]{R.id.to_audience}, userId, position);
                    }
                    UserInfoDialog userInfoDialog = new UserInfoDialog(mContext);
                    userInfoDialog.initShow(userId);
                    return;
                }
            }
        }
        showSeatPop(
                view,
                isAnchor ? new int[]{R.id.to_broadcast, R.id.close_seat} : new int[]{R.id.to_broadcast},
                String.valueOf(Constant.sUserId),
                position
        );
    }

    private void showSeatPop(View view, int[] ids, String userId, int position) {
        AlertUtil.showPop(this, view, ids, (index, menu) -> {
            if (menu.getId() == R.id.to_broadcast) {
                mManager.toBroadcaster(String.valueOf(Constant.sUserId), position);
            } else if (menu.getId() == R.id.to_audience) {
                mManager.toAudience(userId, null);
            } else if (menu.getId() == R.id.turn_off_mic) {
                mManager.muteMic(userId, true);
            } else if (menu.getId() == R.id.turn_on_mic) {
                mManager.muteMic(userId, false);
            } else if (menu.getId() == R.id.close_seat) {
                mManager.closeSeat(position);
            } else if (menu.getId() == R.id.open_seat) {
                mManager.openSeat(position);
            }
            return true;
        }, null);
    }

    @Override
    public void onSeatUpdated(int position) {
        runOnUiThread(() -> {
            //座位更新
            if (mSeatAdapter!=null)
                mSeatAdapter.notifyItemChanged(position);
            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.O_MR1) {//27就是8.1
                //做一些处理  webview打开
                if (mChannelType.equals(FIVE_CHESS_TYPE) && position < 2){
//                    ChannelData mChannelData = ChatRoomManager.instance(this).getChannelData();
//                    if (mChannelData.getSeatArray()[0]!=null&&mChannelData.getSeatArray()[1]!=null){
//                        initWebView(mChannelData.getSeatArray()[0].getUserId(),mChannelData.getSeatArray()[1].getUserId());
//                    }
                }else if (mChannelType.contains(DRAW_GUESS_TYPE)){
                    //init(DRAW_GUESS_TYPE +2497);
                }
            }
        });
    }

    @Override
    public void onUserGivingGift(String userId) {
        //runOnUiThread(() -> gift.show(userId));
    }

    @Override
    public void onMessageAdded(int position, Message message) {
        runOnUiThread(() -> {
            if (message.getContent().equals("房主切换了房间类型")){
                if (!mChannelId.equals(Constant.sUserId+"") && mChannelId.equals(message.getSendId())){
                    initView();
                    if (mChannelType.equals(FIVE_CHESS_TYPE)){
//                        ChannelData mChannelData = mManager.getChannelData();
//                        if (mChannelData.getSeatArray()[0]!=null&&mChannelData.getSeatArray()[1]!=null){
//                            initWebView(mChannelData.getSeatArray()[0].getUserId(),mChannelData.getSeatArray()[1].getUserId());
//                        }
                    }else if (mChannelType.contains(DRAW_GUESS_TYPE)){
                        //init(DRAW_GUESS_TYPE +2497);
                    }
                    if (mPIPManager!=null)
                        mPIPManager.pause();

//                    if (ktvView!=null){
//                        if (ktvView.getmMusicPlayer()!=null)
//                            ktvView.getmMusicPlayer().pause();
//                    }

                }
            }else if (message.getContent().equals("歌手离开了座位，开始下一首歌曲")){
                if (ktvView.getmMusicPlayer().mMusicModel!=null){
                    if (ktvView.getmMusicPlayer().mMusicModel.getUserId().equals(message.getSendId())){
                        ktvView.playNewSong();
                    }
                }
            }else {
                mMessageAdapter.notifyItemInserted(position);
                rv_message_list.scrollToPosition(position);
            }
        });
    }

    @Override
    public void onMemberListUpdated(String userId) {
        runOnUiThread(() -> {
            OkHttpInstance.setChannelMemXiaobei(mChannelId,mManager.getChannelData().getMemberList().size()+"");
            btn_num.setText(String.valueOf(mManager.getChannelData().getMemberList().size()));
            if (mSeatAdapter!=null)
                mSeatAdapter.notifyItemChanged(userId, false);
            mMemberDialog.notifyDataSetChanged();
        });
    }

    @Override
    public void onUserStatusChanged(String userId, Boolean muted) {
        runOnUiThread(() -> {
            if (Constant.isMyself(userId)) {
                if (muted != null && muted)
                    btn_mic.setImageResource(R.mipmap.ic_mic_off);
                else
                    btn_mic.setImageResource(R.mipmap.ic_mic_on);
            }
            if (mSeatAdapter!=null)
                mSeatAdapter.notifyItemChanged(userId, false);

            if (mMemberDialog!=null)
                mMemberDialog.notifyItemChangedByUserId(userId);
        });
    }

    @Override
    public void onAudioMixingStateChanged(boolean isPlaying) {
        //runOnUiThread(() -> cb_mixing.setChecked(isPlaying));
    }

    @Override
    public void onAudioVolumeIndication(String userId, int volume) {
        if (mSeatAdapter!=null)
            runOnUiThread(() -> mSeatAdapter.notifyItemChanged(userId, true));
    }




    private void initCpRoom(){
        videoView = findViewById(R.id.videoView);
        videoView.setVisibility(GONE);
        mWebView.setVisibility(GONE);
        ktvView = findViewById(R.id.ktvView);
        ktvView.setVisibility(GONE);
        ktvView.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (mManager!=null){
                    ktvView.initShow(ChatRoomActivity.this,mManager,currentChannel);
                }
            }
        },1000);
    }


    //region 看电影屏幕
    public SendBarrageFrameLayout videoView;
    @BindView(R2.id.film_type_framelayout)
    FilmTypeFrameLayout film_type_framelayout;

    private PIPManager mPIPManager;
    private void initVideoView(){
        videoView = findViewById(R.id.videoView);

        videoView.setVisibility(VISIBLE);
        mWebView.setVisibility(GONE);
        ktvView = findViewById(R.id.ktvView);
        ktvView.setVisibility(GONE);
        ktvView.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (mManager!=null){
                    ktvView.initShow(ChatRoomActivity.this,mManager,currentChannel);
                }
            }
        },1000);

        mPIPManager = PIPManager.getInstance();

        videoView.setAppCompatActivity(this);
        videoView.setFilm_type_framelayout(film_type_framelayout);
        videoView.setChannel_id(mChannelId);
        if (mChannelId.equals(Constant.sUserId+"")){
            //房主则不直接播放
        }else if (!mPIPManager.isStartFloatWindow()){
            RtmMessage message = RtmManager.instance(mContext).getRtmClient().createMessage();
            message.setText("{\"type\":\"syn_anchor_film\"}");
            RtmManager.instance(mContext).getRtmClient().sendMessageToPeer(mChannelId, message, RtmManager.instance(mContext).getSendMessageOptions(), new ResultCallback<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    Log.d(TAG, "onSuccess: 通过声网消息通知对面更新声网用户属性");
                }
                @Override
                public void onFailure(ErrorInfo errorInfo) {
                }
            });
        }

    }
    //endregion

    //region webview游戏屏幕
    @BindView(R2.id.mWebView)
    WebView mWebView;

    @SuppressLint({"SetJavaScriptEnabled", "JavascriptInterface"})
    private void initWebView(String user_one, String user_two) {

        videoView = findViewById(R.id.videoView);
        videoView.setVisibility(GONE);
        ktvView = findViewById(R.id.ktvView);
        ktvView.setVisibility(GONE);
        ktvView.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (mManager!=null){
                    ktvView.initShow(ChatRoomActivity.this,mManager,currentChannel);
                }
            }
        },1000);

        mWebView.setVisibility(VISIBLE);


        String url = "https://console.banghua.xin/app/index.php?i=99999&c=entry&do=Game_fivechess&m=socialchat&channel_id="+mChannelId+"&user_one="+ user_one + "&user_two="+user_two+"&myId="+ Constant.sUserId;
        if (mChannelType.equals(FIVE_CHESS_TYPE)){
            url = "https://console.banghua.xin/app/index.php?i=99999&c=entry&do=Game_fivechess&m=socialchat&channel_id="+mChannelId+"&user_one="+ user_one + "&user_two="+user_two+"&myId="+ Constant.sUserId;
        }else if (mChannelType.equals(DRAW_GUESS_TYPE)){
            url = "https://console.banghua.xin/app/index.php?i=99999&c=entry&do=Game_drawguess&m=socialchat&channel_id="+mChannelId+"&myId="+ Constant.sUserId;
        }
        Log.d(TAG, "init: 游戏地址"+url);

        mWebView.setVisibility(VISIBLE);
        // 开启JavaScript支持
        mWebView.getSettings().setJavaScriptEnabled(true);

        // 设置背景色
        mWebView.setBackgroundColor(0);
        // 设置填充透明度
        mWebView.getBackground().setAlpha(0);

        mWebView.addJavascriptInterface(new InJavaScriptLocalObj(), "java_obj");

        // 设置WebView是否支持使用屏幕控件或手势进行缩放，默认是true，支持缩放
        mWebView.getSettings().setSupportZoom(true);

        // 设置WebView是否使用其内置的变焦机制，该机制集合屏幕缩放控件使用，默认是false，不使用内置变焦机制。
        mWebView.getSettings().setBuiltInZoomControls(true);

        // 设置是否开启DOM存储API权限，默认false，未开启，设置为true，WebView能够使用DOM storage API
        mWebView.getSettings().setDomStorageEnabled(true);

        // 触摸焦点起作用.如果不设置，则在点击网页文本输入框时，不能弹出软键盘及不响应其他的一些事件。
        mWebView.requestFocus();

        // 设置此属性,可任意比例缩放,设置webview推荐使用的窗口
        mWebView.getSettings().setUseWideViewPort(true);

        // 设置webview加载的页面的模式,缩放至屏幕的大小
        mWebView.getSettings().setLoadWithOverviewMode(true);

        //禁止缩放
        mWebView.getSettings().setBuiltInZoomControls(false);
        mWebView.getSettings().setSupportZoom(false);
        mWebView.getSettings().setDisplayZoomControls(false);

        // 加载链接
        mWebView.loadUrl(url);
//        String postData= "myid="+ Common.myID +"&sign="+ MD5Tool.getSign(myID)+"&type=";
//        mWebView.postUrl(url,postData.getBytes());

        mWebView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                // 在开始加载网页时会回调
                super.onPageStarted(view, url, favicon);
            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                // 拦截 url 跳转,在里边添加点击链接跳转或者操作
//                if( url.startsWith("http:") || url.startsWith("https:") ) {
//                    return false;
//                }
                //view.loadUrl(url);
                return false;
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                // 在结束加载网页时会回调

                // 获取页面内容
                view.loadUrl("javascript:window.java_obj.showSource("
                        + "document.getElementsByTagName('html')[0].innerHTML);");

                // 获取解析<meta name="share-description" content="获取到的值">
                view.loadUrl("javascript:window.java_obj.showDescription("
                        + "document.querySelector('meta[name=\"share-description\"]').getAttribute('content')"
                        + ");");
                super.onPageFinished(view, url);
            }

            @Override
            public void onReceivedError(WebView view, int errorCode,
                                        String description, String failingUrl) {
                // 加载错误的时候会回调，在其中可做错误处理，比如再请求加载一次，或者提示404的错误页面
                super.onReceivedError(view, errorCode, description, failingUrl);
            }

            @Override
            public WebResourceResponse shouldInterceptRequest(WebView view,
                                                              WebResourceRequest request) {
                // 在每一次请求资源时，都会通过这个函数来回调
                return super.shouldInterceptRequest(view, request);
            }

        });
    }
    public final class InJavaScriptLocalObj
    {
        @JavascriptInterface
        public void showSource(String html) {
            System.out.println("====>html=" + html);
        }

        @JavascriptInterface
        public void showDescription(String str) {
            System.out.println("====>html=" + str);
        }
    }
    //endregion





    //region KTV
    public KtvFrameLayout ktvView;

    public static List<MemberMusicModel> choosedSong = new ArrayList<>();
    public void initKtvView(){
        videoView = findViewById(R.id.videoView);
        videoView.setVisibility(GONE);
        mWebView.setVisibility(GONE);
        ktvView = findViewById(R.id.ktvView);
        ktvView.setVisibility(VISIBLE);

        ktvView.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (mManager!=null){
                    ktvView.initShow(ChatRoomActivity.this,mManager,currentChannel);
                }
            }
        },1000);

    }
    //endregion



    public void zoom () {

        Common.instanceChatRoomService(this);

//        if (mChannelType.equals(WATCH_FILM_TYPE)){
//            videoView.floatingStart();
//            return;
//        }

        Log.d(TAG, "zoom: 开启悬浮窗");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!Settings.canDrawOverlays(this)) {
                Toast.makeText(this, "当前无权限，请授权", Toast.LENGTH_SHORT);

                new AlertDialog.Builder(mContext)
                        .setIcon(android.R.drawable.ic_dialog_info)
                        .setTitle("提示")
                        .setMessage("检测到您还没有开启悬浮窗权限")
                        .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                if (mManager!=null)
                                    mManager.leaveChannel();


                                finish();
                            }
                        })
                        .setPositiveButton("开启", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                                startActivityForResult(new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:" + getPackageName())), 33493);
                                dialogInterface.dismiss();
                            }
                        })
                        .show();
            } else {
                Log.d(TAG, "服务zoom触发");
                Intent intent = Common.getInstanceChatRoomService();
                //hasBind = bindService(intent, mVideoServiceConnection, Context.BIND_AUTO_CREATE);
                intent.putExtra(ChatRoomActivity.BUNDLE_KEY_CHANNEL, (Serializable) currentChannel);
                startService(intent);

                finish();
            }
        }
    }


    @Override
    public void onActivityResult ( int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 33493 && resultCode == RESULT_OK) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (!Settings.canDrawOverlays(this)) {
                    Toast.makeText(this, "授权失败", Toast.LENGTH_SHORT).show();
                } else {
                    //开启房间服务
                    Log.d(TAG, "服务33493触发");
                    //Intent intent = new Intent(this, ChatRoomService.class);
                    Intent intent = Common.getInstanceChatRoomService();
                    //hasBind = bindService(intent, mVideoServiceConnection, Context.BIND_AUTO_CREATE);
                    intent.putExtra(ChatRoomActivity.BUNDLE_KEY_CHANNEL, (Serializable) currentChannel);
                    startService(intent);

                    finish();
                }
            }
        }
    }
}
