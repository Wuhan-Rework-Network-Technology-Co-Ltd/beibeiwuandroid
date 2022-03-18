package xin.banghua.beiyuan;

import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import java.io.File;
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
import xin.banghua.beiyuan.Adapter.StoreList;
import xin.banghua.beiyuan.Adapter.UserInfoList;
import xin.banghua.beiyuan.utils.OkHttpInstance;

public class Common {
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
            shortstring = "今天在线";
        } else if(deltime > 300) {
            //shortstring = (int)(deltime/(60)) + "分前";
            shortstring = "刚刚在线";
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
    public static boolean isServiceExisted(AppCompatActivity context, String className) {
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
}
