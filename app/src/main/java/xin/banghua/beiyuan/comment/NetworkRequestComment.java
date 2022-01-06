package xin.banghua.beiyuan.comment;

import android.util.Log;

import java.io.IOException;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import xin.banghua.beiyuan.utils.Common;
import xin.banghua.beiyuan.utils.OkHttpInstance;
import xin.banghua.beiyuan.utils.OkHttpResponseCallBack;

import static xin.banghua.beiyuan.utils.ThreadUtils.runOnUiThread;

public class NetworkRequestComment {
    private static final String TAG = "NetworkRequestComment";
    private static OkHttpClient uniqueInstance = new OkHttpClient();
    //Singleton类只有一个构造方法并且是被private修饰的，所以用户无法通过new方法创建该对象实例
    private NetworkRequestComment(){}
    public static OkHttpClient getInstance(){
        return uniqueInstance;
    }
    /**
     * 点赞评论
     * @param commentID
     * @param ifauthlike
     */
    public static void sendCommentLike(String commentID,String ifauthlike) {
        new Thread(new Runnable() {
            @Override
            public void run(){
                OkHttpClient client = OkHttpInstance.getInstance();
                RequestBody formBody = new FormBody.Builder()
                        .add("myID", Common.myID)
                        .add("commentID", commentID)
                        .add("ifauthlike",ifauthlike)
                        .build();
                Request request = new Request.Builder()
                        .url("https://console.banghua.xin/app/index.php?i=99999&c=entry&a=webapp&do=sendCommentLike&m=socialchat")
                        .post(formBody)
                        .build();
                try (Response response = client.newCall(request).execute()) {
                    if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);
                }catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }
    /**
     * 发布评论
     * @param comment_text
     * @param postID
     * @param mainID
     * @param subID
     * @param ifauthreply
     */
    public static void sendComment(String comment_text,String postID,String postid_user,String mainID,String mainID_user,String subID,String subID_comment,String ifauthreply,OkHttpResponseCallBack okHttpResponseCallBack) {
        new Thread(new Runnable() {
            @Override
            public void run(){
                OkHttpClient client = OkHttpInstance.getInstance();
                RequestBody formBody = new FormBody.Builder()
                        .add("myID", Common.myID)
                        .add("comment_text", comment_text)
                        .add("postID",postID)
                        .add("postid_user",postid_user)
                        .add("mainID",mainID)
                        .add("mainID_user",mainID_user)
                        .add("subID",subID)
                        .add("subID_comment",subID_comment)
                        .add("ifauthreply",ifauthreply)
                        .build();
                Request request = new Request.Builder()
                        .url("https://console.banghua.xin/app/index.php?i=99999&c=entry&a=webapp&do=sendComment&m=socialchat")
                        .post(formBody)
                        .build();
                Log.d(TAG, "run: * 发布评论"+mainID+subID);
                try (Response response = client.newCall(request).execute()) {
                    if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);
                    if (okHttpResponseCallBack!=null){
                        String resultString = response.body().string();
                        Log.d(TAG, "发布评论" + resultString);
                        runOnUiThread(()->okHttpResponseCallBack.getResponseString(resultString));
                    }
                }catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    /**
     * 删除评论
     * @param comment_id
     * @param okHttpResponseCallBack
     */
    public static void deleteComment(String comment_id,OkHttpResponseCallBack okHttpResponseCallBack) {
        new Thread(new Runnable() {
            @Override
            public void run(){
                OkHttpClient client = OkHttpInstance.getInstance();
                RequestBody formBody = new FormBody.Builder()
                        .add("comment_id",comment_id)
                        .build();
                Request request = new Request.Builder()
                        .url("https://console.banghua.xin/app/index.php?i=99999&c=entry&a=webapp&do=deleteComment&m=socialchat")
                        .post(formBody)
                        .build();

                try (Response response = client.newCall(request).execute()) {
                    if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);
                    if (okHttpResponseCallBack!=null){
                        String resultString = response.body().string();
                        Log.d(TAG, "删除评论" + comment_id +resultString);
                        runOnUiThread(()->okHttpResponseCallBack.getResponseString(resultString));
                    }
                }catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }


    /**
     * 获取1级评论
     * @param postID
     * @param pageIndex
     * @param okHttpResponseCallBack
     */
    public static void getMainComment(String postID,String pageIndex,OkHttpResponseCallBack okHttpResponseCallBack) {
        new Thread(new Runnable() {
            @Override
            public void run(){
                OkHttpClient client = OkHttpInstance.getInstance();
                RequestBody formBody = new FormBody.Builder()
                        .add("postID",postID)
                        .add("pageIndex",pageIndex)
                        .build();
                Request request = new Request.Builder()
                        .url("https://console.banghua.xin/app/index.php?i=99999&c=entry&a=webapp&do=getMainComment&m=socialchat")
                        .post(formBody)
                        .build();

                try (Response response = client.newCall(request).execute()) {
                    if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);
                    if (okHttpResponseCallBack!=null){
                        String resultString = response.body().string();
                        Log.d(TAG, "获取1级评论" +postID + resultString+pageIndex);
                        runOnUiThread(()->okHttpResponseCallBack.getResponseString(resultString));
                    }
                }catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    /**
     * 获取2级评论
     * @param mainID
     * @param pageIndex
     * @param okHttpResponseCallBack
     */
    public static void getSubComment(String mainID,String pageIndex,OkHttpResponseCallBack okHttpResponseCallBack) {
        new Thread(new Runnable() {
            @Override
            public void run(){
                OkHttpClient client = OkHttpInstance.getInstance();
                RequestBody formBody = new FormBody.Builder()
                        .add("mainID",mainID)
                        .add("pageIndex",pageIndex)
                        .build();
                Request request = new Request.Builder()
                        .url("https://console.banghua.xin/app/index.php?i=99999&c=entry&a=webapp&do=getSubComment&m=socialchat")
                        .post(formBody)
                        .build();

                try (Response response = client.newCall(request).execute()) {
                    if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);
                    if (okHttpResponseCallBack!=null){
                        String resultString = response.body().string();
                        Log.d(TAG, "获取2级评论" + resultString);
                        runOnUiThread(()->okHttpResponseCallBack.getResponseString(resultString));
                    }
                }catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }


    /**
     * 获取选中评论
     * @param comment_id
     * @param okHttpResponseCallBack
     */
    public static void getSelectedComment(String comment_id,OkHttpResponseCallBack okHttpResponseCallBack) {
        new Thread(new Runnable() {
            @Override
            public void run(){
                OkHttpClient client = OkHttpInstance.getInstance();
                RequestBody formBody = new FormBody.Builder()
                        .add("comment_id",comment_id)
                        .build();
                Request request = new Request.Builder()
                        .url("https://console.banghua.xin/app/index.php?i=99999&c=entry&a=webapp&do=getSelectedComment&m=socialchat")
                        .post(formBody)
                        .build();

                try (Response response = client.newCall(request).execute()) {
                    if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);
                    if (okHttpResponseCallBack!=null){
                        String resultString = response.body().string();
                        Log.d(TAG, "获取选中评论" + resultString);
                        runOnUiThread(()->okHttpResponseCallBack.getResponseString(resultString));
                    }
                }catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }



    /**
     * 获取与自己有关的评论
     * @param okHttpResponseCallBack
     */
    public static void getMyComment(int pageIndex,OkHttpResponseCallBack okHttpResponseCallBack) {
        new Thread(new Runnable() {
            @Override
            public void run(){
                OkHttpClient client = OkHttpInstance.getInstance();
                RequestBody formBody = new FormBody.Builder()
                        .add("myid",Common.myID)
                        .add("pageIndex",pageIndex+"")
                        .build();
                Request request = new Request.Builder()
                        .url("https://console.banghua.xin/app/index.php?i=99999&c=entry&a=webapp&do=getMyComment&m=socialchat")
                        .post(formBody)
                        .build();

                try (Response response = client.newCall(request).execute()) {
                    if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);
                    if (okHttpResponseCallBack!=null){
                        String resultString = response.body().string();
                        Log.d(TAG, "获取与自己有关的评论" + resultString);
                        runOnUiThread(()->okHttpResponseCallBack.getResponseString(resultString));
                    }
                }catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }
}
