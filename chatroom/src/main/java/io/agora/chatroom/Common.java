package io.agora.chatroom;

import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.text.TextUtils;
import android.view.ViewGroup;
import android.view.ViewParent;

import androidx.appcompat.app.AppCompatActivity;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import io.agora.chatroom.activity.ChatRoomService;
import xyz.doikki.videoplayer.player.VideoView;

public class Common {

    public static int screen_width = 0;
    public static int screen_height = 0;
    public static float screen_density = 0f;
    public static int screen_densityDpi = 0;

    public static UserInfoList myUserInfoList = new UserInfoList();

    public static String[] filterString = {};

    public static String[] referralList = {};

    static Intent intentChatRoomService;//聊天室
    public static void instanceChatRoomService(Context context){
        intentChatRoomService = new Intent(context, ChatRoomService.class);
    }
    public static Intent getInstanceChatRoomService(){
        return intentChatRoomService;
    }

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
     * 转变大于10000的数字单位为w，例如24234：2.4w
     * @param num
     * @return
     */
    public static String changeNumberFormatIntoW(Double num) {
        if(num<10000){
            return String.valueOf(num);
        }else{
            double n = (double)num/10000;
            return String.format("%.1f",n)+"w";
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
    public static ViewGroup removeFromParent(VideoView videoView) {
        if (videoView != null) {
            ViewParent parent = videoView.getParent();
            if (parent != null) {
                ViewGroup group = (ViewGroup) parent;
                group.removeView(videoView);
                return group;
            }
        }
        return null;
    }

    /**
     * 用法：
     * CommonKtv.getShortTime(Long.parseLong("1602566550"))
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
            shortstring = "1秒前";
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


    /*
     *    get image from network
     *    @param [String]imageURL
     *    @return [BitMap]image
     */
    public static Bitmap returnBitMap(String url){
        URL myFileUrl = null;
        Bitmap bitmap = null;
        try {
            myFileUrl = new URL(url);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        try {
            HttpURLConnection conn = (HttpURLConnection) myFileUrl.openConnection();
            conn.setDoInput(true);
            conn.connect();
            InputStream is = conn.getInputStream();
            bitmap = BitmapFactory.decodeStream(is);
            is.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bitmap;
    }
    /*
    bitmap转圆形
     */
    public static Bitmap makeRoundCorner(Bitmap bitmap)
    {
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        int left = 0, top = 0, right = width, bottom = height;
        float roundPx = height/2;
        if (width > height) {
            left = (width - height)/2;
            top = 0;
            right = left + height;
            bottom = height;
        } else if (height > width) {
            left = 0;
            top = (height - width)/2;
            right = width;
            bottom = top + width;
            roundPx = width/2;
        }

        Bitmap output = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);
        int color = 0xff424242;
        Paint paint = new Paint();
        Rect rect = new Rect(left, top, right, bottom);
        RectF rectF = new RectF(rect);

        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);
        canvas.drawRoundRect(rectF, roundPx, roundPx, paint);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, paint);
        return output;
    }

}
