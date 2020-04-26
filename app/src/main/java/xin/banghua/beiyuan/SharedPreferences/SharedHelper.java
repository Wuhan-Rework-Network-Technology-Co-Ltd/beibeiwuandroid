package xin.banghua.beiyuan.SharedPreferences;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import java.util.HashMap;
import java.util.Map;

public class SharedHelper {
    private static final String TAG = "SharedHelper";

    private Context mContext;

    public SharedHelper() {
    }

    public SharedHelper(Context mContext) {
        this.mContext = mContext;
    }


    //定义一个保存数据的方法   保存用户信息
    public void saveUserInfo(String userID, String userNickName,String userPortrait,String userAge,String userGender,String userProperty,String userRegion) {
        SharedPreferences sp = mContext.getSharedPreferences("userInfo", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString("userID", userID);
        editor.putString("userNickName", userNickName);
        editor.putString("userPortrait", userPortrait);
        editor.putString("userAge", userAge);
        editor.putString("userGender", userGender);
        editor.putString("userProperty", userProperty);
        editor.putString("userRegion", userRegion);
        editor.commit();
        Log.d(TAG, "saveUserInfo: 保存的用户名："+userNickName);
        //Toast.makeText(mContext, "信息已写入SharedPreference中", Toast.LENGTH_SHORT).show();
    }

    //定义一个读取SP文件的方法
    public Map<String, String> readUserInfo() {
        Map<String, String> data = new HashMap<String, String>();
        SharedPreferences sp = mContext.getSharedPreferences("userInfo", Context.MODE_PRIVATE);
        data.put("userID", sp.getString("userID", ""));
        data.put("userNickName", sp.getString("userNickName", ""));
        data.put("userPortrait", sp.getString("userPortrait", ""));
        data.put("userAge", sp.getString("userAge", ""));
        data.put("userGender", sp.getString("userGender", ""));
        data.put("userProperty", sp.getString("userProperty", ""));
        data.put("userRegion", sp.getString("userRegion", ""));
        Log.d(TAG, "readUserInfo: 读取的用户名"+data.toString());
        return data;
    }


    //定义一个保存数据的方法  保存定位信息
    public void saveLocation(String latitude, String longitude) {
        SharedPreferences sp = mContext.getSharedPreferences("location", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString("latitude", latitude);
        editor.putString("longitude", longitude);
        editor.commit();
        //Toast.makeText(mContext, "信息已写入SharedPreference中", Toast.LENGTH_SHORT).show();
    }

    //定义一个读取SP文件的方法
    public Map<String, String> readLocation() {
        Map<String, String> data = new HashMap<String, String>();
        SharedPreferences sp = mContext.getSharedPreferences("location", Context.MODE_PRIVATE);
        data.put("latitude", sp.getString("latitude", ""));
        data.put("longitude", sp.getString("longitude", ""));
        return data;
    }

    //定义一个保存数据的方法  保存选取的好友信息
    public void saveValue(String value) {
        SharedPreferences sp = mContext.getSharedPreferences("value", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString("value", value);
        editor.commit();
        //Toast.makeText(mContext, "信息已写入SharedPreference中", Toast.LENGTH_SHORT).show();
    }

    //定义一个读取SP文件的方法
    public Map<String, String> readValue() {
        Map<String, String> data = new HashMap<String, String>();
        SharedPreferences sp = mContext.getSharedPreferences("value", Context.MODE_PRIVATE);
        data.put("value", sp.getString("value", ""));
        return data;
    }

    //定义一个保存数据的方法  保存定位信息
    public void saveRongtoken(String Rongtoken) {
        SharedPreferences sp = mContext.getSharedPreferences("Rongtoken", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString("Rongtoken", Rongtoken);
        editor.commit();
        //Toast.makeText(mContext, "信息已写入SharedPreference中", Toast.LENGTH_SHORT).show();
    }

    //定义一个读取SP文件的方法
    public Map<String, String> readRongtoken() {
        Map<String, String> data = new HashMap<String, String>();
        SharedPreferences sp = mContext.getSharedPreferences("Rongtoken", Context.MODE_PRIVATE);
        data.put("Rongtoken", sp.getString("Rongtoken", ""));
        return data;
    }

    //定义一个保存数据的方法  保存选取的好友信息
    public void saveNewFriendApplyNumber(String value) {
        SharedPreferences sp = mContext.getSharedPreferences("NewFriendApplyNumber", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();

        editor.putString("newFriendApplyNumber", value);
        editor.commit();
        //Toast.makeText(mContext, "信息已写入SharedPreference中", Toast.LENGTH_SHORT).show();
    }

    //定义一个读取SP文件的方法
    public String readNewFriendApplyNumber() {
        Map<String, String> data = new HashMap<String, String>();
        SharedPreferences sp = mContext.getSharedPreferences("NewFriendApplyNumber", Context.MODE_PRIVATE);

        return sp.getString("newFriendApplyNumber", "");
    }


    //定义一个保存数据的方法  保存响铃设置
    public void saveSoundSet(String value) {
        SharedPreferences sp = mContext.getSharedPreferences("SoundSet", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();

        editor.putString("SoundSet", value);
        editor.commit();
        //Toast.makeText(mContext, "信息已写入SharedPreference中", Toast.LENGTH_SHORT).show();
    }

    //定义一个读取SP文件的方法
    public String readSoundSet() {
        SharedPreferences sp = mContext.getSharedPreferences("SoundSet", Context.MODE_PRIVATE);

        return sp.getString("SoundSet", "");
    }

    //定义一个保存数据的方法   保存用户信息
    public void saveOnestart(int num) {
        SharedPreferences sp = mContext.getSharedPreferences("onestart", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putInt("onestart", num);
        editor.commit();
        //Toast.makeText(mContext, "信息已写入SharedPreference中", Toast.LENGTH_SHORT).show();
    }

    //定义一个读取SP文件的方法
    public int readOnestart() {
        SharedPreferences sp = mContext.getSharedPreferences("onestart", Context.MODE_PRIVATE);
        return sp.getInt("onestart", 0);
    }
}
