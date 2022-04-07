package io.agora.chatroom.util;

import android.content.Context;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Build;
import android.util.Log;

import java.util.HashMap;

import io.agora.chatroom.R;


/**
 * 播放短音频
 * */
public class SoundUtil {

    private static final String TAG = "SoundUtil";

    //定义一个保存数据的方法  保存响铃设置
    public static final String SOUND_ALL = "all";
    public static final String SOUND_XUNYUAN_LIKE = "xunyuan_like";
    public static final String SOUND_INTERACT = "interact";
    public static final String SOUND_GIFT = "gift";
    public static final String SOUND_REWARD = "reward";
    public static final String SOUND_CHAT = "chat";
    public static final String SOUND_SCRAMBLE = "scramble";
    public static final String SOUND_LOVE_BELL = "love_bell";
    public static final String SOUND_ORDER = "order";
    public static final String SOUND_MATCH = "match";
    /**
     * 上下文
     */
    private Context mContext;
    /**
     * 添加的声音资源参数
     */
    private static HashMap<String, Integer> soundPoolMap;
    /**
     * 声音池
     */
    private SoundPool mSoundPool;
    /**
     * 单例
     */
    private static SoundUtil instance;

    private SoundUtil(Context context) {
        mContext = context;
    }

    public static SoundUtil getInstance(Context context) {
        if (instance == null) {
            instance = new SoundUtil(context);
            instance.init();
        }
        return instance;
    }

    /**
     * 初始化声音
     */
    public void init() {
        //sdk版本21(Android 5.0)是SoundPool 的一个分水岭
        if (Build.VERSION.SDK_INT >= 21){
            SoundPool.Builder builder = new SoundPool.Builder();
            //传入最多播放音频数量,
            builder.setMaxStreams(1);
            //AudioAttributes是一个封装音频各种属性的方法
            AudioAttributes.Builder attrBuilder = new AudioAttributes.Builder();
            //设置音频流的合适的属性
            attrBuilder.setLegacyStreamType(AudioManager.STREAM_MUSIC);
            //加载一个AudioAttributes
            builder.setAudioAttributes(attrBuilder.build());
            mSoundPool = builder.build();
        } else {
            /**
             * 第一个参数：int maxStreams：SoundPool对象的最大并发流数
             * 第二个参数：int streamType：AudioManager中描述的音频流类型
             * 第三个参数：int srcQuality：采样率转换器的质量。 目前没有效果。 使用0作为默认值。
             */
            mSoundPool = new SoundPool(2, AudioManager.STREAM_MUSIC, 0);
        }

        soundPoolMap = new HashMap<>();
        //接收聊天消息
        putSound(SOUND_ALL, R.raw.prompt_message);
        putSound(SOUND_XUNYUAN_LIKE, R.raw.sound_interact);
        putSound(SOUND_INTERACT, R.raw.sound_interact);
        putSound(SOUND_GIFT, R.raw.sound_gift);
        putSound(SOUND_REWARD, R.raw.sound_reward);
        putSound(SOUND_CHAT, R.raw.sound_chat);
        putSound(SOUND_SCRAMBLE, R.raw.sound_scramble);
        putSound(SOUND_ORDER, R.raw.sound_order);
        //匹配成功
        putSound(SOUND_MATCH, R.raw.match_success);

        //匹配成功
        putSound(SOUND_LOVE_BELL, R.raw.sound_scramble);
    }


    private void putSound(String order, int soundRes) {
        // 上下文，声音资源id，优先级
        soundPoolMap.put(order, soundRes);
    }

    /**
     * 根据序号播放声音
     * @param order
     */
    public void playSound(String order) {
        mSoundPool.setOnLoadCompleteListener(new SoundPool.OnLoadCompleteListener() {
            @Override
            public void onLoadComplete(SoundPool soundPool, int sampleId, int status) {
                Log.d(TAG, "onLoadComplete: 加载完成");
                soundPool.play(
                        sampleId,
                        1f,       //左耳道音量【0~1】
                        1f,       //右耳道音量【0~1】
                        0,        //播放优先级【0表示最低优先级】
                        0,        //循环模式【0表示循环一次，-1表示一直循环，
                        //其他表示数字+1表示当前数字对应的循环次数】
                        1         //播放速度【1是正常，范围从0~2】
                );
                //mSoundPool.stop(sampleId);
            }
        });
        mSoundPool.load(mContext,soundPoolMap.get(order),1);

//        Log.d(TAG, "playSound: 响铃");
//        new Thread(new Runnable(){
//            public void run(){
//                try {
//                    Thread.sleep(500);
//                    mSoundPool.play(
//                            soundPoolMap.get(order),
//                            1f,       //左耳道音量【0~1】
//                            1f,       //右耳道音量【0~1】
//                            0,        //播放优先级【0表示最低优先级】
//                            0,        //循环模式【0表示循环一次，-1表示一直循环，
//                            //其他表示数字+1表示当前数字对应的循环次数】
//                            1         //播放速度【1是正常，范围从0~2】
//                    );
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
//            }
//        }).start();
    }



    public static int soundLoopId = -1;
    /**
     * 根据序号播放声音
     * @param order
     */
    public void playSoundLoop(String order) {
        mSoundPool.setOnLoadCompleteListener(new SoundPool.OnLoadCompleteListener() {
            @Override
            public void onLoadComplete(SoundPool soundPool, int sampleId, int status) {
                soundLoopId = sampleId;
                Log.d(TAG, "onLoadComplete: 加载完成");
                soundPool.play(
                        sampleId,
                        1f,       //左耳道音量【0~1】
                        1f,       //右耳道音量【0~1】
                        0,        //播放优先级【0表示最低优先级】
                        1,        //循环模式【0表示循环一次，-1表示一直循环，
                        //其他表示数字+1表示当前数字对应的循环次数】
                        1         //播放速度【1是正常，范围从0~2】
                );
                //mSoundPool.stop(sampleId);
            }
        });
        mSoundPool.load(mContext,soundPoolMap.get(order),1);

//        Log.d(TAG, "playSoundLoop: 播放提示音");
//        new Thread(new Runnable(){
//            public void run(){
//                try {
//                    if (soundPoolMap==null){
//                        init();
//                    }
//                    Thread.sleep(500);//等load完再播放
//                    mSoundPool.play(
//                            soundPoolMap.get(order),
//                            1f,       //左耳道音量【0~1】
//                            1f,       //右耳道音量【0~1】
//                            0,        //播放优先级【0表示最低优先级】
//                            -1,        //循环模式【0表示循环一次，-1表示一直循环，
//                            //其他表示数字+1表示当前数字对应的循环次数】
//                            1         //播放速度【1是正常，范围从0~2】
//                    );
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
//            }
//        }).start();
    }


    public void stopSoundLoop(){
        if (soundLoopId!=-1){
            mSoundPool.stop(soundLoopId);

            soundLoopId = -1;
        }
    }

    /**
     * 释放内存
     */
    public void release() {
        if (mSoundPool != null) {
            mSoundPool.release();
            mSoundPool = null;
        }
        instance = null;
    }

}
