package io.agora.chatroom.ktv;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SwitchCompat;
import androidx.constraintlayout.widget.ConstraintLayout;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.agora.chatroom.R;
import io.agora.chatroom.R2;
import io.agora.lrcview.LrcView;
import io.agora.lrcview.PitchView;

/**
 * 歌词控制View
 *
 * @author chenhengfei(Aslanchen)
 * @date 2021/7/16
 */
public class LrcControlView extends FrameLayout implements View.OnClickListener {


    public LrcView getLrcView() {
        return lrcView;
    }

    public PitchView getPitchView() {
        return pitchView;
    }


    public enum Role {
        Singer, Listener
    }

    private Role mRole = Role.Listener;
    private MemberMusicModel mMusic;
    private OnLrcActionListener mOnLrcActionListener;

    public LrcControlView(@NonNull Context context) {
        super(context);
        init(context);
    }

    public LrcControlView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public LrcControlView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }


    private View mView;

    @BindView(R2.id.llNoSing)
    LinearLayout llNoSing;//添加布局
    @BindView(R2.id.active_layout)
    ConstraintLayout active_layout;//添加布局

    @BindView(R2.id.lrcView)
    LrcView lrcView;//添加布局
    @BindView(R2.id.pitchView)
    PitchView pitchView;//添加布局
    @BindView(R2.id.rlMusicControlMenu)
    RelativeLayout rlMusicControlMenu;//添加布局
    @BindView(R2.id.tvOriginalLabel)
    TextView tvOriginalLabel;//添加布局
    @BindView(R2.id.switchOriginal)
    SwitchCompat switchOriginal;//添加布局
    @BindView(R2.id.ivMusicMenu)
    ImageView ivMusicMenu;//添加布局
    @BindView(R2.id.ivMusicStart)
    ImageView ivMusicStart;//添加布局
    @BindView(R2.id.ivChangeSong)
    ImageView ivChangeSong;//添加布局

    @BindView(R2.id.clActive)
    ConstraintLayout clActive;//添加布局
    @BindView(R2.id.prepare_layout)
    FrameLayout ilPrepare;//添加布局

    @BindView(R2.id.tvMusicName)
    TextView tvMusicName;//添加布局

    private void init(Context context) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mView = inflater.inflate(R.layout.ktv_layout_lrc_control_view, this, true);

        ButterKnife.bind(this, mView);

        llNoSing.setVisibility(View.VISIBLE);
        active_layout.setVisibility(View.GONE);

        initListener();
    }

    private void initListener() {
        switchOriginal.setOnClickListener(this);
        ivMusicMenu.setOnClickListener(this);
        ivMusicStart.setOnClickListener(this);
        ivChangeSong.setOnClickListener(this);
    }

    public void setOnLrcClickListener(OnLrcActionListener mOnLrcActionListener) {
        this.mOnLrcActionListener = mOnLrcActionListener;
        lrcView.setActionListener(this.mOnLrcActionListener);
    }

    public void onPrepareStatus() {
        llNoSing.setVisibility(View.GONE);
        clActive.setVisibility(View.VISIBLE);
        ilPrepare.setVisibility(View.VISIBLE);
        active_layout.setVisibility(View.GONE);

        if (this.mRole == Role.Singer) {
            lrcView.setEnableDrag(true);
            rlMusicControlMenu.setVisibility(View.VISIBLE);
            switchOriginal.setChecked(true);
        } else if (this.mRole == Role.Listener) {
            lrcView.setEnableDrag(false);
            rlMusicControlMenu.setVisibility(View.GONE);
        }
    }

    public void onPlayStatus() {
        llNoSing.setVisibility(View.GONE);
        clActive.setVisibility(View.VISIBLE);
        ilPrepare.setVisibility(View.GONE);
        active_layout.setVisibility(View.VISIBLE);

        ivMusicStart.setImageResource(R.mipmap.ktv_room_music_pause);
    }

    public void onPauseStatus() {
        llNoSing.setVisibility(View.GONE);
        clActive.setVisibility(View.VISIBLE);
        ilPrepare.setVisibility(View.GONE);
        active_layout.setVisibility(View.VISIBLE);

        ivMusicStart.setImageResource(R.mipmap.ktv_room_music_play);
    }

    public void onIdleStatus() {
        llNoSing.setVisibility(View.VISIBLE);
        clActive.setVisibility(View.GONE);
        ilPrepare.setVisibility(View.GONE);
        active_layout.setVisibility(View.GONE);
    }

    public void setRole(@NonNull Role mRole) {
        this.mRole = mRole;

        if (this.mRole == Role.Singer) {
            lrcView.setEnableDrag(true);
            rlMusicControlMenu.setVisibility(View.VISIBLE);
            switchOriginal.setChecked(true);
        } else if (this.mRole == Role.Listener) {
            lrcView.setEnableDrag(false);
            rlMusicControlMenu.setVisibility(View.GONE);
        }
    }

    public void setMusic(@NonNull MemberMusicModel mMusic) {
        lrcView.reset();
        pitchView.reset();

        this.mMusic = mMusic;
        tvMusicName.setText(this.mMusic.getName());
    }

    public void setLrcViewBackground(@DrawableRes int resId) {
//        Bitmap mBitmap = BitmapFactory.decodeResource(getResources(), resId);
//        Palette.from(mBitmap).generate(new Palette.PaletteAsyncListener() {
//            @Override
//            public void onGenerated(@Nullable Palette palette) {
//                if (palette == null) {
//                    return;
//                }
//
//                int defaultColor = ContextCompat.getColor(getContext(), R.color.ktv_lrc_highligh);
//                lrcView.setCurrentColor(palette.getLightVibrantColor(defaultColor));
//
//                defaultColor = ContextCompat.getColor(getContext(), R.color.ktv_lrc_nomal);
//                lrcView.setNormalColor(palette.getLightMutedColor(defaultColor));
//            }
//        });
        clActive.setBackgroundResource(resId);
    }

    @Override
    public void onClick(View v) {
        if (v == switchOriginal) {
            mOnLrcActionListener.onSwitchOriginalClick();
        } else if (v == ivMusicMenu) {
            mOnLrcActionListener.onMenuClick();
        } else if (v == ivMusicStart) {
            mOnLrcActionListener.onPlayClick();
        } else if (v == ivChangeSong) {
            mOnLrcActionListener.onChangeMusicClick();
        }
    }

    public void setSwitchOriginalChecked(boolean checked) {
        switchOriginal.setChecked(checked);
    }

    public interface OnLrcActionListener extends LrcView.OnActionListener {
        void onSwitchOriginalClick();

        void onMenuClick();

        void onPlayClick();

        void onChangeMusicClick();
    }
}
