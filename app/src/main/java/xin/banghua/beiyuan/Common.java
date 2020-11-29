package xin.banghua.beiyuan;

import java.util.List;
import java.util.Map;

import xin.banghua.beiyuan.Adapter.FriendList;

public class Common {
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



}
