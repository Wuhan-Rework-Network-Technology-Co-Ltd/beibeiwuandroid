package xin.banghua.beiyuan.custom_ui.music_selector;

import android.media.AudioManager;
import android.media.MediaPlayer;
import android.util.Log;

public class SingletonMusicPlayer extends MediaPlayer {
    private static final String TAG = "SingletonMusicPlayer";
    private static SingletonMusicPlayer instance = new SingletonMusicPlayer();
    private SingletonMusicPlayer (){
        Log.d(TAG, "SingletonMusicPlayer: 创建实例");
        this.setAudioStreamType(AudioManager.STREAM_MUSIC);
        this.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                //mp.setLooping(false);
            }
        });
        this.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                //mp.start();
            }
        });
    }
    public static SingletonMusicPlayer getInstance() {
        return instance;
    }

    public Music music;
}
