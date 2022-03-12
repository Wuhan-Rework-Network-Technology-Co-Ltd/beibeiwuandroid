package io.agora.chatroom.activity;

import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.alibaba.fastjson.JSON;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.agora.chatroom.CreateRoomActivity;
import io.agora.chatroom.OkHttpInstance;
import io.agora.chatroom.OkHttpResponseCallBack;
import io.agora.chatroom.R;
import io.agora.chatroom.R2;
import io.agora.chatroom.adapter.ChannelGridAdapter;
import io.agora.chatroom.model.Channel;

public class ChannelGridActivity extends AppCompatActivity implements ChannelGridAdapter.OnItemClickListener {

    @BindView(R2.id.rv_channel_grid)
    RecyclerView rv_channel_grid;
    @BindView(R2.id.create_room_btn)
    FloatingActionButton create_room_btn;

    @BindView(R2.id.back_img)
    ImageView back_img;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_channel_grid);
        ButterKnife.bind(this);

        initRecyclerView();

        back_img.setOnClickListener(v -> {
            onBackPressed();
        });

        create_room_btn.setOnClickListener(v -> {
            Intent intent = new Intent(ChannelGridActivity.this, CreateRoomActivity.class);
            startActivity(intent);
        });
    }

    private void initRecyclerView() {
        rv_channel_grid.setHasFixedSize(true);

        OkHttpInstance.getSystemRoom(new OkHttpResponseCallBack() {
            @Override
            public void getResponseString(String responseString) {
                if (!responseString.equals("false")){
                    List<Channel> mChannelList = JSON.parseArray(responseString,Channel.class);

                    ChannelGridAdapter adapter = new ChannelGridAdapter(ChannelGridActivity.this);
                    adapter.initData(mChannelList);
                    adapter.setOnItemClickListener(ChannelGridActivity.this);
                    rv_channel_grid.setAdapter(adapter);

                    rv_channel_grid.setLayoutManager(new GridLayoutManager(ChannelGridActivity.this, 2));

                    int spacing = getResources().getDimensionPixelSize(R.dimen.item_channel_spacing);
                    rv_channel_grid.addItemDecoration(new RecyclerView.ItemDecoration() {
                        @Override
                        public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
                            super.getItemOffsets(outRect, view, parent, state);
                            outRect.set(spacing, spacing, spacing, spacing);
                        }
                    });
                }
            }
        });


    }

    @Override
    public void onItemClick(View view, int position, Channel channel) {
        Intent intent = new Intent(ChannelGridActivity.this, ChatRoomActivity.class);
        intent.putExtra(ChatRoomActivity.BUNDLE_KEY_CHANNEL_ID, channel.getId());
        intent.putExtra(ChatRoomActivity.BUNDLE_KEY_CHANNEL_TYPE, channel.getAudioroomtype());
        intent.putExtra(ChatRoomActivity.BUNDLE_KEY_BACKGROUND_RES, R.mipmap.bg_channel_0);
        startActivity(intent);
    }

}
