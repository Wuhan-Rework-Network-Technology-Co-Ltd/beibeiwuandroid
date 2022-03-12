package io.agora.chatroom.model;

import android.content.Intent;
import android.text.TextUtils;

public class Constant {

    public static int sUserId = 0;

    public static boolean isMyself(String userId) {
        return TextUtils.equals(userId, String.valueOf(Constant.sUserId));
    }

    public static String sName = "";
    public static final int sAvatarIndex = 0;
    public static String sPortrait = "";
    public static String sGender = "";
    public static String sProperty = "";

    public static String sRoomName = "";
    public static String sRoomCover = "";
    public static String sRoomBG = "";
    public static String vip = "";
    public static String svip = "";

    public static Intent goToPersonalPage;
}
