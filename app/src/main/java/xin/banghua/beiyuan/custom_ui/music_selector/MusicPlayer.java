package xin.banghua.beiyuan.custom_ui.music_selector;

import android.content.Context;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnPreparedListener;
import android.os.Handler;

/**
 * 音乐播放器
 * @author admin
 *
 */
public class MusicPlayer {
	//多媒体播放器
	private static MediaPlayer mediaPlayer;

	public static MediaPlayer getMediaPlayer() {
		return mediaPlayer;
	}
	public static void setMediaPlayer(MediaPlayer mediaPlayer) {
		MusicPlayer.mediaPlayer = mediaPlayer;
	}


	//当歌曲播放到结束位置时，使用Handler来停止播放
	public static long endProgress=0;//结束进度
	public static Handler handler = new Handler();
	public static Runnable run = new Runnable() {

		public void run() {
			if(endProgress!=0){
				if(MusicPlayer.getMediaPlayer().isPlaying()&&MusicPlayer.getMediaPlayer().getCurrentPosition()>=endProgress){
					MusicPlayer.reset();
					handler.removeCallbacks(run);
				}
				handler.postDelayed(run, 1000);
			}

		}
	};
	/**
	 * 播放
	 */
	public static void play(Context context,String path,final int progress){
		if(mediaPlayer==null){
			//初始化
			mediaPlayer=new MediaPlayer();
		}
		//重置音乐
		if(mediaPlayer.isPlaying()){
			mediaPlayer.reset();
		}
		try {
			mediaPlayer.setDataSource(path);
			//让MediaPlayer进入到准备状态
			mediaPlayer.prepareAsync();
			//播放音乐
			mediaPlayer.setOnPreparedListener(new OnPreparedListener() {

				@Override
				public void onPrepared(MediaPlayer mp) {
					// TODO Auto-generated method stub
					if(progress!=0){
						mediaPlayer.seekTo(progress);
					}
					mediaPlayer.start();
					//循环播放
					mediaPlayer.setLooping(true);
				}
			});
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	/**
	 * 播放（一般用于暂停后重新播放）
	 */
	public static void reStart(){
		if(mediaPlayer!=null){
			mediaPlayer.start();
		}
	}
	/**
	 * 暂停
	 */
	public static void pause(){
		if(mediaPlayer!=null&&mediaPlayer.isPlaying()){
			mediaPlayer.pause();
		}
	}
	/**
	 * 停止
	 */
	public static void stop(){
		if(handler!=null){
			handler.removeCallbacks(run);
		}
		if(mediaPlayer!=null&&mediaPlayer.isPlaying()){
			mediaPlayer.stop();
			mediaPlayer.release();
			mediaPlayer=null;
		}
	}
	/**
	 * 重置
	 */
	public static void reset(){
		if(mediaPlayer!=null&&mediaPlayer.isPlaying()){
			mediaPlayer.reset();
		}
	}
}
