package xin.banghua.beiyuan;

import static com.faceunity.nama.post.utils.FileUtilsFU.getExternalFileDir;
import static xin.banghua.beiyuan.utils.ThreadUtils.runOnUiThread;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.Chronometer;

import com.liulishuo.filedownloader.BaseDownloadTask;
import com.liulishuo.filedownloader.FileDownloadListener;
import com.liulishuo.filedownloader.FileDownloader;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

import io.agora.chatroom.activity.ChatRoomService;
import xin.banghua.beiyuan.Adapter.FollowList;
import xin.banghua.beiyuan.Adapter.FriendList;
import xin.banghua.beiyuan.Adapter.LuntanList;
import xin.banghua.beiyuan.Adapter.StoreList;
import xin.banghua.beiyuan.Adapter.UserInfoList;
import xin.banghua.beiyuan.chat.VideoFloatService;
import xin.banghua.beiyuan.chat.VoiceFloatService;
import xin.banghua.beiyuan.utils.OkHttpDownloadCallBack;
import xin.banghua.beiyuan.utils.OkHttpInstance;
import xin.banghua.beiyuan.utils.ZipUtils;

public class Common {


    public static int onlineMatchTimes = -1;

    /**
     * 新朋友num
     */
    public static int newFriendsNum = 0;

    public static String chatFrom = "normal";

    public static String myID = null;
    private static final String TAG = "Common";

    public static int screen_width = 0;
    public static int screen_height = 0;
    public static float screen_density = 0f;
    public static int screen_densityDpi = 0;
    //定位信息
    public static String longitude = "116";
    public static String latitude = "39";

    public static UserInfoList userInfoList = null;


    public static List<FollowList> followList = new ArrayList<>();


    public static List<StoreList> storeLists = new ArrayList<>();

    public static Boolean ifBuySVip = false;


    public static String OSS_PREFIX = "https://oss.banghua.xin/";
    /**
     * 获取网络资源地址，没有oss前缀的要加上，本就是网络资源的直接返回
     * @param resourceUrl
     * @return
     *
     * Common.getOssResourceUrl()
     */
    public static String getOssResourceUrl(String resourceUrl){
        if (TextUtils.isEmpty(resourceUrl))
            return "";
        if (resourceUrl.startsWith("https://moyuanoss.oss-cn-shanghai.aliyuncs.com/")){
            return OSS_PREFIX + resourceUrl.substring(47);
        }if (resourceUrl.startsWith("https://appletattachment.oss-cn-beijing.aliyuncs.com/")){
            return OSS_PREFIX + resourceUrl.substring(53);
        } else if (resourceUrl.startsWith("http")||resourceUrl.startsWith("/")){
            return resourceUrl;//微信头像
        }else {
            return OSS_PREFIX + resourceUrl;//现在的oss只保存后缀，前缀要自己添加
        }
    }



    public static Boolean isFollow(String uid){
        for (int i = 0; i < Common.followList.size(); i++){
            if (Common.followList.get(i).getUserId().equals(uid)){
                return true;
            }
        }
        return false;
    }

    /**
     * 是朋友
     *
     * @param userInfoList 用户信息列表
     * @return {@link Boolean}
     */
    public static Boolean isFriend(UserInfoList userInfoList){
        if (Common.userInfoList!=null) {
            if (("," + userInfoList.getMyfriends() + ",").contains("," + Common.userInfoList.getId() + ",")) {
                return true;
            } else {
                return false;
            }
        }
        return false;
    }

    /**
     * 是黑色列表
     *
     * @param blacklist 用户信息列表
     * @return {@link Boolean}
     */
    public static Boolean isBlackList(String blacklist){
        try {
            if (Common.userInfoList!=null){
                if ((","+blacklist+",").contains(","+Common.userInfoList.getId()+",")) {
                    return true;
                }else {
                    return false;
                }
            }
        }catch (Exception e){
            Log.e(TAG, "isBlackList: 抛出异常");
        }


        return false;
    }


    /**
     * 我是黑名单
     *
     * @param uid uid
     * @return {@link Boolean}
     */
    public static Boolean isBlackListMe(String uid){
        try {
            if (Common.userInfoList!=null){
                if ((","+Common.userInfoList.getMyblacklist()+",").contains(","+uid+",")) {
                    return true;
                }else {
                    return false;
                }
            }
        }catch (Exception e){
            Log.e(TAG, "isBlackList: 抛出异常");
        }


        return false;
    }


    /**
     * 是贵宾
     *
     * @param userInfoList 用户信息列表
     * @return {@link Boolean}
     */
    public static Boolean isVip(UserInfoList userInfoList){
        Integer current_timestamp = Math.round(new Date().getTime()/1000);
        int vip_time = Integer.parseInt(userInfoList.getVip()+"");
        if (vip_time > current_timestamp) {
            return true;
        }else {
            return false;
        }
    }

    /**
     * 是svip
     *
     * @param userInfoList 用户信息列表
     * @return {@link Boolean}
     */
    public static Boolean isSVip(UserInfoList userInfoList){
        Integer current_timestamp = Math.round(new Date().getTime()/1000);
        int vip_time = Integer.parseInt(userInfoList.getSvip()+"");
        if (vip_time > current_timestamp) {
            return true;
        }else {
            return false;
        }
    }


    /*
    移除自己的vip头像框
     */
    public static void removeVipPortraitFrameOrVehicle(){
        if ((Common.userInfoList.getPortraitframe().contains("svip") || Common.userInfoList.getPortraitframe().contains("vip")) && !Common.isSVip(Common.userInfoList)){
            if (Common.userInfoList.getPortraitframe().contains("svip")){
                Common.userInfoList.setPortraitframe("");
                OkHttpInstance.equipGoods("-1", responseString -> {
                });
            }
            if (Common.userInfoList.getVeilcel().contains("svip")){
                Common.userInfoList.setVeilcel("");
                OkHttpInstance.equipGoods("-2", responseString -> {
                });
            }

            if (!Common.isVip(Common.userInfoList)){
                if (Common.userInfoList.getPortraitframe().contains("vip")){
                    Common.userInfoList.setPortraitframe("");
                    OkHttpInstance.equipGoods("-1", responseString -> {
                    });
                }
                if (Common.userInfoList.getVeilcel().contains("vip")){
                    Common.userInfoList.setVeilcel("");
                    OkHttpInstance.equipGoods("-2", responseString -> {
                    });
                }
            }
        }
    }


    // split截取后缀名
    public static String lastName(File file) {
        if (file == null) return null;
        String filename = file.getName();
        // split用的是正则，所以需要用 //. 来做分隔符
        String[] split = filename.split("\\.");
        //注意判断截取后的数组长度，数组最后一个元素是后缀名
        if (split.length > 1) {
            return split[split.length - 1];
        } else {
            return "";
        }
    }
    /**
     * 用法：
     * Common.getShortTime(Long.parseLong("1602566550"))
     * @param dateline
     * @return
     */
    public static String getShortTime(long dateline) {
        String shortstring = null;
        String time = timestampToStr(dateline);
        Date date = getDateByString(time);
        if(date == null) return shortstring;

        long now = Calendar.getInstance().getTimeInMillis();
        long deltime = (now - date.getTime())/1000;
        if(deltime > 24*60*60) {
            //shortstring = (int)(deltime/(24*60*60)) + "天前";
            shortstring = timestampToStrYYMMDD(dateline);
        } else if(deltime > 60*60) {
            shortstring = (int)(deltime/(60*60)) + "小时前";
        } else if(deltime > 60) {
            shortstring = (int)(deltime/(60)) + "分前";
        } else if(deltime > 1) {
            shortstring = deltime + "秒前";
        } else {
            shortstring = "刚刚";
        }
        return shortstring;
    }
    public static Date getDateByString(String time) {
        Date date = null;
        if (time == null)
            return date;
        String date_format = "yyyy-MM-dd HH:mm:ss";
        SimpleDateFormat format = new SimpleDateFormat(date_format);
        try {
            date = format.parse(time);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date;
    }

    /**
     * timestampToStr  例如2021-07-13 13:22:14
     * @param dateline
     * @return
     */
    public static String timestampToStr(long dateline){
        Timestamp timestamp = new Timestamp(dateline*1000);
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//定义格式，不显示毫秒
        return df.format(timestamp);
    }

    /**
     * timestampToStr  例如2021-07-13 13:22:14
     * @param dateline
     * @return
     */
    public static String timestampToStrYYMMDD(long dateline){
        Timestamp timestamp = new Timestamp(dateline*1000);
        SimpleDateFormat df = new SimpleDateFormat("yy-MM-dd");//定义格式，不显示毫秒
        return df.format(timestamp);
    }


    /**
     * 用法：
     * Common.getOnlineState(Long.parseLong("1602566550"))
     * @param dateline
     * @return
     */
    public static String getOnlineState(long dateline) {
        String shortstring = "离线";
        String time = timestampToStr(dateline);
        Date date = getDateByString(time);
        if(date == null) return shortstring;

        long now = Calendar.getInstance().getTimeInMillis();
        long deltime = (now - date.getTime())/1000;
        if(deltime > 365*24*60*60) {
            //shortstring = (int)(deltime/(365*24*60*60)) + "年前";
            //shortstring = timestampToStrYYMMDD(dateline);
            shortstring = "离线";
        } else if(deltime > 24*60*60) {
            //shortstring = (int)(deltime/(24*60*60)) + "天前";
            //shortstring = timestampToStrYYMMDD(dateline);
            shortstring = "最近在线";
        } else if(deltime > 60*60) {
            //shortstring = (int)(deltime/(60*60)) + "小时前";
            shortstring = "刚刚在线";
        } else if(deltime > 300) {
            shortstring = (int)(deltime/(60)) + "分前";
            //shortstring = "刚刚在线";
        } else {
            shortstring = "在线";
        }
        return shortstring;
    }



    static Intent intentChatRoomService;//聊天室
    public static void instanceChatRoomService(Context context){
        intentChatRoomService = new Intent(context, ChatRoomService.class);
    }
    public static Intent getInstanceChatRoomService(){
        return intentChatRoomService;
    }


    public static String myInfo = null; //我的信息字符串
    public static Map<String,String> friendsRemarkMap = null;  //好友备注
    public static Map<String,String> friendsTagMap = null;   //好友标签


    public static boolean ifSVIPChat = false;


    public static String vipTime = null;


    public static List<FriendList> friendList = null;


    public static Map<String,FriendList> friendListMap = null;



    public static String conversationSettingUserId = null;//会话设置页面的目标用户id
    public static String conversationSettingUserName = null;//会话设置页面的目标用户名
    public static String conversationSettingUserPortrait = null;//会话设置页面的目标用户头像



    public static Boolean newFriendOrDeleteFriend = false;//同意好友或删除好友，或添加黑名单后需刷新好友列表

    /**
     * 用法：
     * CommonMusic.getAge(CommonMusic.parse("20020912"))
     * @param birthDay
     * @return
     * @throws Exception
     */
    public static  int getAge(Date birthDay){
        Calendar cal = Calendar.getInstance();
        if (cal.before(birthDay)) { //出生日期晚于当前时间，无法计算
            throw new IllegalArgumentException(
                    "The birthDay is before Now.It's unbelievable!");
        }
        int yearNow = cal.get(Calendar.YEAR);  //当前年份
        int monthNow = cal.get(Calendar.MONTH);  //当前月份
        int dayOfMonthNow = cal.get(Calendar.DAY_OF_MONTH); //当前日期
        cal.setTime(birthDay);
        int yearBirth = cal.get(Calendar.YEAR);
        int monthBirth = cal.get(Calendar.MONTH);
        int dayOfMonthBirth = cal.get(Calendar.DAY_OF_MONTH);
        int age = yearNow - yearBirth;   //计算整岁数
        if (monthNow <= monthBirth) {
            if (monthNow == monthBirth) {
                if (dayOfMonthNow < dayOfMonthBirth) age--;//当前日期在生日之前，年龄减一
            }else{
                age--;//当前月份在生日之前，年龄减一
            } } return age;
    }


    public static Date parseStringToDate(String strDate){
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");

        Date date = new Date();
        try {
            date = sdf.parse(strDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return date;
    }


    /**
     * 判断服务是否已运行
     * @param context
     * @param className
     * @return
     */
    public static boolean isServiceExisted(Activity context, String className) {
        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningServiceInfo> serviceList = activityManager.getRunningServices(Integer.MAX_VALUE);
        if(!(serviceList.size() > 0)) {
            return false;
        }
        for(int i = 0; i < serviceList.size(); i++) {
            ActivityManager.RunningServiceInfo serviceInfo = serviceList.get(i);
            ComponentName serviceName = serviceInfo.service;
            if(serviceName.getClassName().equals(className)) {
                return true;
            }
        }
        return false;
    }



    public static int getLevelFromUser(UserInfoList userInfoList){
        //自定义部分
        int lv = Integer.parseInt(userInfoList.getVitality()) + Integer.parseInt(userInfoList.getPost()) + Integer.parseInt(userInfoList.getComment());
        if (lv <= 100){
            return R.mipmap.lv1;
        }else if (lv <= 200){
            return R.mipmap.lv2;
        }else if (lv <= 400){
            return R.mipmap.lv3;
        }else if (lv <= 800){
            return R.mipmap.lv4;
        }else if (lv <= 1600){
            return R.mipmap.lv5;
        }else if (lv <= 3500){
            return R.mipmap.lv6;
        }else if (lv <= 5500){
            return R.mipmap.lv7;
        }else if (lv <= 7500){
            return R.mipmap.lv8;
        }else if (lv <= 10000){
            return R.mipmap.lv9;
        }else if (lv <= 15000){
            return R.mipmap.lv10;
        }else if (lv <= 20000){
            return R.mipmap.lv11;
        }else if (lv <= 25000){
            return R.mipmap.lv12;
        }else if (lv <= 30000){
            return R.mipmap.lv13;
        }else if (lv <= 40000){
            return R.mipmap.lv14;
        }else if (lv <= 50000){
            return R.mipmap.lv15;
        }else if (lv <= 60000){
            return R.mipmap.lv16;
        }else if (lv <= 70000){
            return R.mipmap.lv17;
        }else if (lv <= 80000){
            return R.mipmap.lv18;
        }else if (lv <= 90000){
            return R.mipmap.lv19;
        }else{
           return R.mipmap.lv20;
        }
    }

    public static int getLevelFromPost(LuntanList luntanList){
        //自定义部分
        int lv = Integer.parseInt(luntanList.getVitality()) + Integer.parseInt(luntanList.getPost()) + Integer.parseInt(luntanList.getComment());
        if (lv <= 100){
            return R.mipmap.lv1;
        }else if (lv <= 200){
            return R.mipmap.lv2;
        }else if (lv <= 400){
            return R.mipmap.lv3;
        }else if (lv <= 800){
            return R.mipmap.lv4;
        }else if (lv <= 1600){
            return R.mipmap.lv5;
        }else if (lv <= 3500){
            return R.mipmap.lv6;
        }else if (lv <= 5500){
            return R.mipmap.lv7;
        }else if (lv <= 7500){
            return R.mipmap.lv8;
        }else if (lv <= 10000){
            return R.mipmap.lv9;
        }else if (lv <= 15000){
            return R.mipmap.lv10;
        }else if (lv <= 20000){
            return R.mipmap.lv11;
        }else if (lv <= 25000){
            return R.mipmap.lv12;
        }else if (lv <= 30000){
            return R.mipmap.lv13;
        }else if (lv <= 40000){
            return R.mipmap.lv14;
        }else if (lv <= 50000){
            return R.mipmap.lv15;
        }else if (lv <= 60000){
            return R.mipmap.lv16;
        }else if (lv <= 70000){
            return R.mipmap.lv17;
        }else if (lv <= 80000){
            return R.mipmap.lv18;
        }else if (lv <= 90000){
            return R.mipmap.lv19;
        }else{
            return R.mipmap.lv20;
        }
    }


    /**
     * 隐藏动物
     *
     * @return {@link TranslateAnimation}
     */
    public static TranslateAnimation hideAnimUpToDown(){
        TranslateAnimation hideAnim = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0.0f,
                Animation.RELATIVE_TO_SELF, 0.0f,
                Animation.RELATIVE_TO_SELF, 0.0f,
                Animation.RELATIVE_TO_SELF, 1.0f);
        hideAnim.setDuration(500);
        return hideAnim;
    }

    public static TranslateAnimation hideAnimDownToUp(){
        TranslateAnimation hideAnim = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0.0f,
                Animation.RELATIVE_TO_SELF, 0.0f,
                Animation.RELATIVE_TO_SELF, 0.0f,
                Animation.RELATIVE_TO_SELF, -1.0f);
        hideAnim.setDuration(500);
        return hideAnim;
    }

    public static TranslateAnimation showAnimDownToUp(){
        TranslateAnimation showAnim = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0.0f,
                Animation.RELATIVE_TO_SELF, 0.0f,
                Animation.RELATIVE_TO_SELF, 1.0f,
                Animation.RELATIVE_TO_SELF, 0.0f);
        showAnim.setDuration(500);
        return showAnim;
    }

    public static TranslateAnimation showAnimUpToDown(){
        TranslateAnimation showAnim = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0.0f,
                Animation.RELATIVE_TO_SELF, 0.0f,
                Animation.RELATIVE_TO_SELF, -1.0f,
                Animation.RELATIVE_TO_SELF, 0.0f);
        showAnim.setDuration(500);
        return showAnim;
    }




    static Intent intentVoiceFloatService;//语音聊天
    public static void instanceVoiceFloatService(Context context){
        intentVoiceFloatService = new Intent(context, VoiceFloatService.class);
    }
    public static Intent getInstanceVoiceFloatService(){
        return intentVoiceFloatService;
    }

    static Intent intentVideoFloatService;//视频聊天
    public static void instanceVideoFloatService(Context context){
        intentVideoFloatService = new Intent(context, VideoFloatService.class);
    }
    public static Intent getInstanceVideoFloatService(){
        return intentVideoFloatService;
    }



    /**
     *
     * @param cmt  Chronometer控件
     * @return 小时+分钟+秒数  的所有秒数
     */
    public static int getChronometerSeconds(Chronometer cmt) {
        int totalss = 0;
        String string = cmt.getText().toString();
        if(string.length()==7){

            String[] split = string.split(":");
            String string2 = split[0];
            int hour = Integer.parseInt(string2);
            int Hours =hour*3600;
            String string3 = split[1];
            int min = Integer.parseInt(string3);
            int Mins =min*60;
            int  SS =Integer.parseInt(split[2]);
            totalss = Hours+Mins+SS;
            return totalss;
        }

        else if(string.length()==5){

            String[] split = string.split(":");
            String string3 = split[0];
            int min = Integer.parseInt(string3);
            int Mins =min*60;
            int  SS =Integer.parseInt(split[1]);

            totalss =Mins+SS;
            return totalss;
        }
        return totalss;
    }



    /**
     * 下载相芯美颜特效
     * 下载并解压faceunity到data/data/xin.banghua.beiyuan/files/faceunity
     */
    public static void downloadFU(){
        Log.d(TAG, "动画下载pending: ");
        //下载
        FileDownloader.getImpl().create(getOssResourceUrl("attachment/faceunity.zip"))
                .setPath("data/data/xin.banghua.beiyuan/files/faceunity.zip")
                .setListener(new FileDownloadListener() {
                    @Override
                    protected void pending(BaseDownloadTask task, int soFarBytes, int totalBytes) {
                        Log.d(TAG, "动画下载pending: ");
                    }

                    @Override
                    protected void connected(BaseDownloadTask task, String etag, boolean isContinue, int soFarBytes, int totalBytes) {
                        Log.d(TAG, "动画下载connected: ");
                    }

                    @Override
                    protected void progress(BaseDownloadTask task, int soFarBytes, int totalBytes) {
                        Log.d(TAG, "动画下载progress: ");
                    }

                    @Override
                    protected void blockComplete(BaseDownloadTask task) {
                        Log.d(TAG, "动画下载blockComplete: ");
                    }

                    @Override
                    protected void retry(final BaseDownloadTask task, final Throwable ex, final int retryingTimes, final int soFarBytes) {
                        Log.d(TAG, "动画下载retry: ");
                    }

                    @Override
                    protected void completed(BaseDownloadTask task) {
                        Log.d(TAG, "动画下载completed: ");
                        //下载完成后解压zip
                        try {
                            ZipUtils.UnZipFolder("data/data/" + "xin.banghua.beiyuan" + "/files/faceunity.zip","data/data/" + "xin.banghua.beiyuan" + "/files/faceunity");
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    protected void paused(BaseDownloadTask task, int soFarBytes, int totalBytes) {
                        Log.d(TAG, "动画下载paused: ");
                    }

                    @Override
                    protected void error(BaseDownloadTask task, Throwable e) {
                        Log.d(TAG, "动画下载error: "+e.toString());
                        //出错后重新下载
                        downloadFU();
                    }

                    @Override
                    protected void warn(BaseDownloadTask task) {
                        Log.d(TAG, "warn: ");
                    }
                }).start();
    }


    public static String getAccthmentPath(String type, String fileName){
        if (type.equals("Music")){
            return "data/data/xin.banghua.beiyuan/files/"+type+"/"+fileName+".mp3";
        }else if (type.equals("Gif")){
            return "data/data/xin.banghua.beiyuan/files/"+type+"/"+fileName+".gif";
        }else {
            return "data/data/xin.banghua.beiyuan/files/"+type+"/"+fileName+".mp4";
        }
    }

    public static String getAssetsCacheFile(Context context, String fileName) {
        Context mContext = App.getApplication();
        File cacheFile = new File(mContext.getCacheDir(), fileName);

        try {
            InputStream inputStream = mContext.getAssets().open(fileName);
            try {
                FileOutputStream outputStream = new FileOutputStream(cacheFile);

                try {
                    byte[] buf = new byte[1024];
                    int len;
                    while ((len = inputStream.read(buf)) > 0) {
                        outputStream.write(buf, 0, len);
                    }
                } finally {
                    outputStream.close();
                }
            } finally {
                inputStream.close();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        return cacheFile.getAbsolutePath();
    }

    /**
     * 附加文件下载，如在线音乐等
     * @param type         类型，如:audio,image,etc
     * @param fileName     文件名，如：红色高跟鞋.mp3
     * @param okHttpDownloadCallBack 下载后的回调
     */
    public static void downloadAttachment(String type, String fileName, String downloadUrl, OkHttpDownloadCallBack okHttpDownloadCallBack){
        //下载

        String downloadPath = getAccthmentPath(type,fileName);
        FileDownloader.getImpl().create(downloadUrl)
                .setPath(downloadPath)
                .setListener(new FileDownloadListener() {
                    @Override
                    protected void pending(BaseDownloadTask task, int soFarBytes, int totalBytes) {
                        Log.d(TAG, "pending: ");
                    }

                    @Override
                    protected void connected(BaseDownloadTask task, String etag, boolean isContinue, int soFarBytes, int totalBytes) {
                        Log.d(TAG, "connected: ");
                    }

                    @Override
                    protected void progress(BaseDownloadTask task, int soFarBytes, int totalBytes) {
                        Log.d(TAG, "progress: ");
                    }

                    @Override
                    protected void blockComplete(BaseDownloadTask task) {
                        Log.d(TAG, "blockComplete: ");
                    }

                    @Override
                    protected void retry(final BaseDownloadTask task, final Throwable ex, final int retryingTimes, final int soFarBytes) {
                        Log.d(TAG, "retry: ");
                    }

                    @Override
                    protected void completed(BaseDownloadTask task) {
                        Log.d(TAG, "completed: ");

                        runOnUiThread(()->okHttpDownloadCallBack.getBaseDownloadTask(task));
                    }

                    @Override
                    protected void paused(BaseDownloadTask task, int soFarBytes, int totalBytes) {
                        Log.d(TAG, "paused: ");
                    }

                    @Override
                    protected void error(BaseDownloadTask task, Throwable e) {
                        Log.d(TAG, "error: "+e.toString());
                        //出错后重新下载
                        downloadAttachment(type,fileName,downloadUrl,okHttpDownloadCallBack);
                    }

                    @Override
                    protected void warn(BaseDownloadTask task) {
                        Log.d(TAG, "warn: ");
                    }
                }).start();
    }


    public static String imageTranslateUri(Context context, int resId){
        Uri uri = Uri.parse(
                ContentResolver.SCHEME_ANDROID_RESOURCE + "://"
                        + context.getResources().getResourcePackageName(resId) + "/"
                        + context.getResources().getResourceTypeName(resId) + "/"
                        + context.getResources().getResourceEntryName(resId)
        );
        return uri.toString();
    }


    /**
     * 将Assets文件拷贝到应用作用域存储
     *
     * @param context    Context
     * @param assetsPath String
     * @param fileName   String
     */
    public static String copyAssetsToExternalFilesDir(Context context, String assetsPath, String fileName) {
        File fileDir = new File(getExternalFileDir(context).getPath() + File.separator + "assets");
        if (!fileDir.exists()) {
            fileDir.mkdirs();
        }
        File file = new File(fileDir, fileName);
        if (file.exists()) {
            return file.getAbsolutePath();
        }
        try {
            InputStream inputStream = context.getAssets().open(assetsPath);
            FileOutputStream fos = new FileOutputStream(file);
            BufferedInputStream bis = new BufferedInputStream(inputStream);
            BufferedOutputStream bos = new BufferedOutputStream(fos);
            byte[] byteArray = new byte[1024];
            int bytes = bis.read(byteArray);
            while (bytes > 0) {
                bos.write(byteArray, 0, bytes);
                bos.flush();
                bytes = bis.read(byteArray);
            }
            bos.close();
            fos.close();
            return file.getAbsolutePath();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }


    /**
     * 转变大于10000的数字单位为w，例如24234：2.4w
     * @param num
     * @return
     */
    public static String changeNumberFormatIntoW(int num) {
        if(num<10000){
            return String.valueOf(num);
        }else{
            double n = (double)num/10000;
            return String.format("%.1f",n)+"w";
        }
    }
}
