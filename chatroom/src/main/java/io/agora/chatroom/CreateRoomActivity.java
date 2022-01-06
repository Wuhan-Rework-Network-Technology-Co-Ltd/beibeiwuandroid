package io.agora.chatroom;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.donkingliang.imageselector.utils.ImageSelector;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.agora.chatroom.activity.ChatRoomActivity;
import io.agora.chatroom.model.Constant;

import static io.agora.chatroom.RoomSetDialog.IMAGE_ROOM_BG;
import static io.agora.chatroom.RoomSetDialog.IMAGE_ROOM_COVER;
import static io.agora.chatroom.activity.ChatRoomActivity.COUPLE_TYPE;
import static io.agora.chatroom.activity.ChatRoomActivity.DRAW_GUESS_TYPE;
import static io.agora.chatroom.activity.ChatRoomActivity.FIVE_CHESS_TYPE;
import static io.agora.chatroom.activity.ChatRoomActivity.WATCH_FILM_TYPE;

public class CreateRoomActivity extends AppCompatActivity {
    private static final String TAG = "CreateRoomActivity";


    @BindView(R2.id.create_room_ktv)
    LinearLayout create_room_ktv;
    @BindView(R2.id.create_room_film)
    LinearLayout create_room_film;
    @BindView(R2.id.create_room_cp)
    LinearLayout create_room_cp;
    @BindView(R2.id.create_room_five_chess)
    LinearLayout create_room_five_chess;
    @BindView(R2.id.create_room_draw_guess)
    LinearLayout create_room_draw_guess;


    @BindView(R2.id.room_set_btn)
    Button room_set_btn;

    @BindView(R2.id.back_img)
    ImageView back_img;

    RoomSetDialog roomSetDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_room);

        ButterKnife.bind(this);

        back_img.setOnClickListener(v -> {
            onBackPressed();
        });

        create_room_cp.setOnClickListener(view1 -> {
            Toast.makeText(CreateRoomActivity.this,"正在创建房间...",Toast.LENGTH_SHORT).show();
            OkHttpInstance.createRoom(COUPLE_TYPE, responseString -> {
                if (responseString.equals("ok")){
                    create_room_draw_guess.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            Intent intent = new Intent(CreateRoomActivity.this, ChatRoomActivity.class);
                            intent.putExtra(ChatRoomActivity.BUNDLE_KEY_CHANNEL_ID, Constant.sUserId+"");
                            intent.putExtra(ChatRoomActivity.BUNDLE_KEY_CHANNEL_TYPE, COUPLE_TYPE);
                            intent.putExtra(ChatRoomActivity.BUNDLE_KEY_BACKGROUND_RES, R.mipmap.bg_channel_0);
                            //intent.putExtra(ChatRoomActivity.BUNDLE_KEY_BACKGROUND_RES, Constant.sRoomBG);
                            startActivity(intent);
                        }
                    },1500);
                }
            });
        });
        create_room_film.setOnClickListener(view1 -> {
            Toast.makeText(CreateRoomActivity.this,"正在创建房间...",Toast.LENGTH_LONG).show();
            OkHttpInstance.createRoom(WATCH_FILM_TYPE, responseString -> {
                if (responseString.equals("ok")){
                    create_room_draw_guess.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            Intent intent = new Intent(CreateRoomActivity.this, ChatRoomActivity.class);
                            intent.putExtra(ChatRoomActivity.BUNDLE_KEY_CHANNEL_ID, Constant.sUserId+"");
                            intent.putExtra(ChatRoomActivity.BUNDLE_KEY_CHANNEL_TYPE, WATCH_FILM_TYPE);
                            intent.putExtra(ChatRoomActivity.BUNDLE_KEY_BACKGROUND_RES, R.mipmap.bg_channel_0);
                            //intent.putExtra(ChatRoomActivity.BUNDLE_KEY_BACKGROUND_RES, Constant.sRoomBG);
                            startActivity(intent);
                        }
                    },1500);
                }
            });
        });
        create_room_five_chess.setOnClickListener(view1 -> {
            Toast.makeText(CreateRoomActivity.this,"正在创建房间...",Toast.LENGTH_LONG).show();
            OkHttpInstance.createRoom(FIVE_CHESS_TYPE, responseString -> {
                if (responseString.equals("ok")){
                    create_room_draw_guess.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            Intent intent = new Intent(CreateRoomActivity.this, ChatRoomActivity.class);
                            intent.putExtra(ChatRoomActivity.BUNDLE_KEY_CHANNEL_ID, Constant.sUserId+"");
                            intent.putExtra(ChatRoomActivity.BUNDLE_KEY_CHANNEL_TYPE, FIVE_CHESS_TYPE);
                            intent.putExtra(ChatRoomActivity.BUNDLE_KEY_BACKGROUND_RES, R.mipmap.bg_channel_0);
                            //intent.putExtra(ChatRoomActivity.BUNDLE_KEY_BACKGROUND_RES, Constant.sRoomBG);
                            startActivity(intent);
                        }
                    },1500);
                }
            });
        });
        create_room_draw_guess.setOnClickListener(view1 -> {
            Toast.makeText(CreateRoomActivity.this,"正在创建房间...",Toast.LENGTH_LONG).show();
            OkHttpInstance.createRoom(DRAW_GUESS_TYPE, responseString -> {
                if (responseString.equals("ok")){
                    create_room_draw_guess.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            Intent intent = new Intent(CreateRoomActivity.this, ChatRoomActivity.class);
                            intent.putExtra(ChatRoomActivity.BUNDLE_KEY_CHANNEL_ID, Constant.sUserId+"");
                            intent.putExtra(ChatRoomActivity.BUNDLE_KEY_CHANNEL_TYPE, DRAW_GUESS_TYPE);
                            intent.putExtra(ChatRoomActivity.BUNDLE_KEY_BACKGROUND_RES, R.mipmap.bg_channel_0);
                            //intent.putExtra(ChatRoomActivity.BUNDLE_KEY_BACKGROUND_RES, Constant.sRoomBG);
                            startActivity(intent);
                        }
                    },1500);
                }
            });
        });


        room_set_btn.setOnClickListener(v -> {
            roomSetDialog = new RoomSetDialog(CreateRoomActivity.this);
            roomSetDialog.initShow(CreateRoomActivity.this);
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            //从图片选择器回来
            case IMAGE_ROOM_COVER:
                if (data != null) {
                    ArrayList<String> images = data.getStringArrayListExtra(ImageSelector.SELECT_RESULT);
                    boolean isCameraImage = data.getBooleanExtra(ImageSelector.IS_CAMERA_IMAGE, false);
                    boolean isFull = data.getBooleanExtra(ImageSelector.IS_FULL, false);
                    Log.d("isCameraImage", "是否是拍照图片：" + isCameraImage);
                    if (images == null || images.size() <= 0) {
                        Toast.makeText(this, "取消设置", Toast.LENGTH_SHORT).show();
                    }
                    for (String image : images){
                        Log.d(TAG, "onActivityResult: 图片地址"+image);
                        String mPath = image;
//                        portrait.setImageURI(Uri.fromFile(new File(image)));

                        RoomSetDialog.room_cover = mPath;

                        roomSetDialog.setCover(mPath);
                    }
                }
                break;
            case IMAGE_ROOM_BG:
                if (data != null) {
                    ArrayList<String> images = data.getStringArrayListExtra(ImageSelector.SELECT_RESULT);
                    boolean isCameraImage = data.getBooleanExtra(ImageSelector.IS_CAMERA_IMAGE, false);
                    boolean isFull = data.getBooleanExtra(ImageSelector.IS_FULL, false);
                    Log.d("isCameraImage", "是否是拍照图片：" + isCameraImage);
                    if (images == null || images.size() <= 0) {
                        Toast.makeText(this, "取消设置", Toast.LENGTH_SHORT).show();
                    }
                    for (String image : images){
                        Log.d(TAG, "onActivityResult: 图片地址"+image);
                        String mPath = image;
//                        portrait.setImageURI(Uri.fromFile(new File(image)));

                        RoomSetDialog.room_bg = mPath;

                        roomSetDialog.setBG(mPath);
                    }
                }
                break;
        }

    }
}