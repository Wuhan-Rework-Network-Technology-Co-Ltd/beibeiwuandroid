package xin.banghua.beiyuan.custom_ui.music_selector;

import android.content.Context;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.liulishuo.filedownloader.BaseDownloadTask;
import com.wang.avi.AVLoadingIndicatorView;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import xin.banghua.beiyuan.Common;
import xin.banghua.beiyuan.R;
import xin.banghua.beiyuan.publish.BaseFaceUnityActivity;
import xin.banghua.beiyuan.publish.EditVideoActivity;
import xin.banghua.beiyuan.utils.OkHttpDownloadCallBack;

public class MusicListViewAdapter extends BaseAdapter {
	private static final String TAG = "MusicListViewAdapter";
	private Context context;
	private List<Music> mMusicList;
	private MusicCutPopupDialog musicCutPopupWindow;
	private Music currentMusic;

	public Music getCurrentMusic() {
		return currentMusic;
	}

	public void setCurrentMusic(Music currentMusic) {
		this.currentMusic = currentMusic;
	}

	public MusicListViewAdapter(Context context, List<Music> mMusicList) {
		super();
		this.context = context;
		this.mMusicList = mMusicList;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return mMusicList.size();
	}

	@Override
	public Object getItem(int arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public long getItemId(int arg0) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		final Music music = mMusicList.get(position);
		final int currentId=position;
		// 加载布局
		View view;
		final ViewHolder viewHolder;
		if (convertView == null) {
			view = LayoutInflater.from(context).inflate(
					R.layout.music_listview_item, null);
			viewHolder = new ViewHolder();
			// 获取控件
			viewHolder.textViewName = (TextView) view
					.findViewById(R.id.music_tv_name);
			viewHolder.textViewSinger = (TextView) view
					.findViewById(R.id.music_tv_singer);
			viewHolder.textViewDuration = (TextView) view
					.findViewById(R.id.music_tv_duration);
			viewHolder.imageViewPlay = (ImageView) view
					.findViewById(R.id.music_iv_play);
			viewHolder.imageViewPic = (ImageView) view
					.findViewById(R.id.music_iv_pic);
			viewHolder.imageViewCut = (ImageView) view
					.findViewById(R.id.music_iv_cut);
			viewHolder.loading_view = (AVLoadingIndicatorView) view
					.findViewById(R.id.loading_view);
			viewHolder.item_layout = (LinearLayout) view
					.findViewById(R.id.item_layout);
			view.setTag(viewHolder);
		} else {
			view = convertView;
			// 重新获取ViewHolder
			viewHolder = (ViewHolder) view.getTag();
		}
		// 设置控件
		viewHolder.textViewName.setText(music.getName());
		viewHolder.textViewSinger.setText(music.getSinger());
		viewHolder.textViewDuration.setText(formatTime(music.getDuration()));

		if (selectItem != -1 && selectItem == position) {
			viewHolder.imageViewPic.setEnabled(false);
			if (hidePlayBtn) {
				viewHolder.imageViewPlay.setVisibility(View.GONE);
			} else {
				viewHolder.imageViewPlay.setVisibility(View.VISIBLE);
			}
		} else {
			viewHolder.imageViewPic.setEnabled(true);
			viewHolder.imageViewPlay.setVisibility(View.VISIBLE);
		}

		if (!TextUtils.isEmpty(music.getImage())){
			Glide.with(context).load(music.getImage()).into(viewHolder.imageViewPic);
		}else {
			Glide.with(context).load(R.drawable.selector_music_play_pic).into(viewHolder.imageViewPic);
		}

		// 判断播放图标是否显示
		/*
		 * if(viewHolder.imageViewPlay.getVisibility()==View.GONE){
		 * viewHolder.imageViewPic.setEnabled(false); }else{
		 * viewHolder.imageViewPic.setEnabled(true); }
		 */
		// 播放音乐按钮
		viewHolder.imageViewPlay.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View view) {
				// TODO Auto-generated method stub
				if (onplayMusicListener != null) {
					currentMusic = music;
					onplayMusicListener.playForFull();
					selectItem = currentId;
				}
			}
		});

		viewHolder.imageViewCut.setVisibility(View.VISIBLE);
		viewHolder.loading_view.setVisibility(View.GONE);
		// 音乐剪辑
		viewHolder.item_layout.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View view) {
				// TODO Auto-generated method stub
				currentMusic = music;

				if (currentMusic.getLocalOrOnline().equals("online")){
					if (!(new File(Common.getAccthmentPath("Music",currentMusic.getName()))).exists()) { // 如果不存在.
						viewHolder.imageViewCut.setVisibility(View.GONE);
						viewHolder.loading_view.setVisibility(View.VISIBLE);

						Common.downloadAttachment("Music", currentMusic.getName(), currentMusic.getPath(),new OkHttpDownloadCallBack() {
							@Override
							public void getBaseDownloadTask(BaseDownloadTask task) {
								Log.d(TAG, "getBaseDownloadTask: 下载完成后地址"+task.getPath()+"|"+task.getTargetFilePath());
								viewHolder.imageViewCut.setVisibility(View.VISIBLE);
								viewHolder.loading_view.setVisibility(View.GONE);

								currentMusic.setPath(Common.getAccthmentPath("Music",currentMusic.getName()));

								musicCutPopupWindow = new MusicCutPopupDialog(context,
										itemsOnClick);
								// 设置音乐信息
								musicCutPopupWindow.setMusicInfo(currentMusic.getSinger()
												+ " - " + currentMusic.getName(),
										currentMusic.getDuration());
								// 显示窗口
								musicCutPopupWindow.showAtLocation();

								//直接播放
								cutMusicStartProgress=(long)((int)musicCutPopupWindow.getMinRule()*1000);
								cutMusicEndProgress=(long)((int)musicCutPopupWindow.getMaxRule()*1000);
								cutMusicPath=currentMusic.getPath();
								if(onplayMusicListener!=null){
									onplayMusicListener.playforPart();
									musicCutPopupWindow.startChronometer(cutMusicStartProgress);
								}
								try {
									if (BaseFaceUnityActivity.music_tv!=null){
										BaseFaceUnityActivity.music_tv.setText(music.getName());
									}
									if (EditVideoActivity.music_tv != null){
										EditVideoActivity.music_tv.setText(music.getName());
									}
									SingletonMusicPlayer.getInstance().music = music;
									SingletonMusicPlayer.getInstance().reset();
									SingletonMusicPlayer.getInstance().setDataSource(cutMusicPath);
									SingletonMusicPlayer.getInstance().prepare();
								} catch (IOException e) {
									e.printStackTrace();
								}
							}
						});
					}else {
						currentMusic.setPath(Common.getAccthmentPath("Music",currentMusic.getName()));

						musicCutPopupWindow = new MusicCutPopupDialog(context,
								itemsOnClick);
						// 设置音乐信息
						musicCutPopupWindow.setMusicInfo(currentMusic.getSinger()
										+ " - " + currentMusic.getName(),
								currentMusic.getDuration());
						// 显示窗口
						musicCutPopupWindow.showAtLocation();


						//直接播放
						cutMusicStartProgress=(long)((int)musicCutPopupWindow.getMinRule()*1000);
						cutMusicEndProgress=(long)((int)musicCutPopupWindow.getMaxRule()*1000);
						cutMusicPath=currentMusic.getPath();
						if(onplayMusicListener!=null){
							onplayMusicListener.playforPart();
							musicCutPopupWindow.startChronometer(cutMusicStartProgress);
						}
						try {
							if (BaseFaceUnityActivity.music_tv!=null){
								BaseFaceUnityActivity.music_tv.setText(music.getName());
							}
							if (EditVideoActivity.music_tv != null){
								EditVideoActivity.music_tv.setText(music.getName());
							}
							SingletonMusicPlayer.getInstance().music = music;
							SingletonMusicPlayer.getInstance().reset();
							SingletonMusicPlayer.getInstance().setDataSource(cutMusicPath);
							SingletonMusicPlayer.getInstance().prepare();
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
				}else {
					musicCutPopupWindow = new MusicCutPopupDialog(context,
							itemsOnClick);
					// 设置音乐信息
					musicCutPopupWindow.setMusicInfo(currentMusic.getSinger()
									+ " - " + currentMusic.getName(),
							currentMusic.getDuration());
					// 显示窗口
					musicCutPopupWindow.showAtLocation();


					//直接播放
					cutMusicStartProgress=(long)((int)musicCutPopupWindow.getMinRule()*1000);
					cutMusicEndProgress=(long)((int)musicCutPopupWindow.getMaxRule()*1000);
					cutMusicPath=currentMusic.getPath();
					if(onplayMusicListener!=null){
						onplayMusicListener.playforPart();
						musicCutPopupWindow.startChronometer(cutMusicStartProgress);
					}
					try {
						if (BaseFaceUnityActivity.music_tv!=null){
							BaseFaceUnityActivity.music_tv.setText(music.getName());
						}

						if (EditVideoActivity.music_tv != null){
							EditVideoActivity.music_tv.setText(music.getName());
						}
						SingletonMusicPlayer.getInstance().music = music;
						SingletonMusicPlayer.getInstance().reset();
						SingletonMusicPlayer.getInstance().setDataSource(cutMusicPath);
						SingletonMusicPlayer.getInstance().prepare();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		});

		return view;

	}

	//为弹出窗口实现监听类
	private OnClickListener  itemsOnClick = new OnClickListener(){

		public void onClick(View v) {

			switch (v.getId()) {
				//确定按钮
				case R.id.popupWindow_music_cut_btn_sure:
					//获取最小值
					float min=musicCutPopupWindow.getMinRule();
					//获取最大值
					float max=musicCutPopupWindow.getMaxRule();
					//开始剪切音乐
					if(currentMusic==null){
						return;
					}
					//要剪切的音乐路径
					String inputPath=currentMusic.getPath();
					Date date=Calendar.getInstance().getTime();
					SimpleDateFormat sdf= new SimpleDateFormat("yyyyMMddHHmmss");
					String str=sdf.format(date);
					//剪切后音乐文件路径
					File sdDir = null;
					boolean sdCardExist = Environment.getExternalStorageState().equals(
							android.os.Environment.MEDIA_MOUNTED);// 判断sd卡是否存在
					if (!sdCardExist) {
						Toast.makeText(context, "SD卡不存在，保存失败", Toast.LENGTH_SHORT).show();
						return;

					}else{
						sdDir = Environment.getExternalStorageDirectory();// 获取根目录
					}
					File flie=new File(sdDir +"/audio");
					//目录不存在就自动创建
					if(!flie.exists()){
						flie.mkdirs();
					}
					String outputPath = flie.getPath()+"/new"+"_"+currentMusic.getName()+".mp3";
					File audioPath = new File(outputPath);
					//文件存在就先删除
					if(audioPath.exists()){
						try {
							audioPath.delete();
							audioPath.createNewFile();
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}

					boolean flag;
					flag = MusicUtil.clipMp3(inputPath, outputPath, (int)min*1000, (int)max*1000);
					if(flag){
						//Toast.makeText(context, "剪辑成功,保存路径："+outputPath, Toast.LENGTH_SHORT).show();

						try {
							SingletonMusicPlayer.getInstance().music.setPath(outputPath);
							SingletonMusicPlayer.getInstance().reset();
							SingletonMusicPlayer.getInstance().setDataSource(outputPath);
							SingletonMusicPlayer.getInstance().prepare();
						} catch (IOException e) {
							e.printStackTrace();
						}

						//传地址给发帖,关闭音乐选择器
//						FabutieziActivity.voicebutton.setPlayPath(outputPath,context);
//						FabutieziActivity.voicebutton.setVisibility(VISIBLE);
//						FabutieziActivity.musicSelectorView.setVisibility(View.GONE);
//						FabutieziActivity.voice_role = currentMusic.getName();

						musicCutPopupWindow.dismiss();
					}else{
						Toast.makeText(context, "剪辑失败", Toast.LENGTH_SHORT).show();
					}
					break;
				//取消按钮
				case R.id.popupWindow_music_cut_btn_cancel:
					//关闭弹窗
					musicCutPopupWindow.dismiss();
					//重置音乐
					MusicPlayer.reset();
					break;
				//试听按钮
				case R.id.popupWindow_music_cut_tv_musicTest:
					cutMusicStartProgress=(long)((int)musicCutPopupWindow.getMinRule()*1000);
					cutMusicEndProgress=(long)((int)musicCutPopupWindow.getMaxRule()*1000);
					cutMusicPath=currentMusic.getPath();
					if(onplayMusicListener!=null){
						onplayMusicListener.playforPart();
						musicCutPopupWindow.startChronometer(cutMusicStartProgress);
					}
					break;
			}

		}

	};

	class ViewHolder{
		TextView textViewName;
		TextView textViewSinger;
		TextView textViewDuration;
		ImageView imageViewPlay;
		ImageView imageViewPic;
		ImageView imageViewCut;

		AVLoadingIndicatorView loading_view;

		LinearLayout item_layout;
	}

	private Integer selectItem=-1;

	public Integer getSelectItem() {
		return selectItem;
	}

	public void setSelectItem(Integer selectItem) {
		this.selectItem = selectItem;
	}
	//标识是否隐藏播放按钮
	private boolean hidePlayBtn;

	public void setHidePlayBtn(boolean hidePlayBtn) {
		this.hidePlayBtn = hidePlayBtn;
	}
	/**
	 * 转换歌曲时间的格式
	 * @param time
	 * @return
	 */
	public static String formatTime(long time) {
		if (time / 1000 % 60 < 10) {
			String t = time / 1000 / 60 + ":0" + time / 1000 % 60;
			return t;
		} else {
			String t = time / 1000 / 60 + ":" + time / 1000 % 60;
			return t;
		}
	}

	// 剪辑歌曲路径
	private String cutMusicPath;
	// 剪辑歌曲开始进度
	private long cutMusicStartProgress;
	// 剪辑歌曲结束进度
	private long cutMusicEndProgress;
	public String getCutMusicPath() {
		return cutMusicPath;
	}
	public void setCutMusicPath(String cutMusicPath) {
		this.cutMusicPath = cutMusicPath;
	}
	public long getCutMusicStartProgress() {
		return cutMusicStartProgress;
	}
	public void setCutMusicStartProgress(long cutMusicStartProgress) {
		this.cutMusicStartProgress = cutMusicStartProgress;
	}
	public long getCutMusicEndProgress() {
		return cutMusicEndProgress;
	}
	public void setCutMusicEndProgress(long cutMusicEndProgress) {
		this.cutMusicEndProgress = cutMusicEndProgress;
	}
	/**
	 * 自定义用于播放音乐的接口
	 * @author admin
	 *
	 */
	private OnplayMusicListener onplayMusicListener;

	public void setOnplayMusicListener(
			OnplayMusicListener onplayMusicListener) {
		this.onplayMusicListener = onplayMusicListener;
	}

	public interface OnplayMusicListener{
		//播放完整歌曲
		void playForFull();
		//播放片段歌曲
		void playforPart();
	}
}
