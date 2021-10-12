package xin.banghua.beiyuan.util;

import android.text.TextUtils;

public class ConstantValue {
    public static String myId = null;

    public static Boolean ifBuySVip = false;



    public static final String OSS_PREFIX = "https://oss.banghua.xin/";
    /**
     * 获取网络资源地址，没有oss前缀的要加上，本就是网络资源的直接返回
     * @param resourceUrl
     * @return
     *
     * ConstantValue.getOssResourceUrl()
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
}
