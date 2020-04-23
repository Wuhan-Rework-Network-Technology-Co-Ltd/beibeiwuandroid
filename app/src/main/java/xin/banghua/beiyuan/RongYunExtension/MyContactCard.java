package xin.banghua.beiyuan.RongYunExtension;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import io.rong.contactcard.ContactCardExtensionModule;
import io.rong.contactcard.IContactCardInfoProvider;
import io.rong.imkit.IExtensionModule;
import io.rong.imkit.RongExtensionManager;
import io.rong.imlib.model.UserInfo;
import xin.banghua.beiyuan.Personage.PersonageActivity;

public class MyContactCard {
    private static final String TAG = "MyContactCard";

    private static Boolean ifHaveDone = false;

    List<UserInfo> userInfoList = new ArrayList<>();

    public void setUserInfoList(List<UserInfo> userInfoList) {
        this.userInfoList = userInfoList;
    }


    public void initContactCard(){
        ContactCardExtensionModule contactCardExtensionModule = new ContactCardExtensionModule(new IContactCardInfoProvider() {
            /**
             * 获取所有通讯录用户
             *
             * @param contactInfoCallback
             */
            @Override
            public void getContactAllInfoProvider(IContactCardInfoCallback contactInfoCallback) {
                Log.d(TAG, "getContactAllInfoProvider: 呵呵");
                contactInfoCallback.getContactCardInfoCallback(userInfoList);
            }

            /**
             * 获取单一用户
             *
             * @param userId
             * @param name
             * @param portrait
             * @param contactInfoCallback
             */
            @Override
            public void getContactAppointedInfoProvider(String userId, String name, String portrait, IContactCardInfoCallback contactInfoCallback) {
                Log.d(TAG, "getContactAppointedInfoProvider: 哈哈");
                //imInfoProvider.getContactUserInfo(userId, contactInfoCallback);
            }
        }, (view, content) -> {
            Context activityContext = view.getContext();
            // 点击名片进入到个人详细界面
            Intent intent = new Intent(activityContext, PersonageActivity.class);
            intent.putExtra("userID", content.getId());
            activityContext.startActivity(intent);
        });
        List<IExtensionModule> moduleList = RongExtensionManager.getInstance().getExtensionModules();
        IExtensionModule defaultModule = null;
        if (moduleList != null) {
            for (IExtensionModule module : moduleList) {
                if (module instanceof ContactCardExtensionModule) {
                    defaultModule = module;
                    break;
                }
            }
            if (defaultModule != null) {
                RongExtensionManager.getInstance().unregisterExtensionModule(defaultModule);
            }
        }

        RongExtensionManager.getInstance().registerExtensionModule(contactCardExtensionModule);
    }
}
