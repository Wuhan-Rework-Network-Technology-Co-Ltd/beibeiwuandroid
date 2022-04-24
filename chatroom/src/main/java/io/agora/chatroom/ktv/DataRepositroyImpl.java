package io.agora.chatroom.ktv;

import android.text.TextUtils;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.alibaba.fastjson.JSON;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import io.agora.chatroom.OkHttpInstance;
import io.reactivex.Completable;
import io.reactivex.CompletableEmitter;
import io.reactivex.CompletableOnSubscribe;
import io.reactivex.Observable;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class DataRepositroyImpl implements IDataRepositroy {
    private static final String TAG = "DataRepositroyImpl";
    private Gson mGson = new GsonBuilder()
            .create();

    @Override
    public Observable<List<MemberMusicModel>> getMusics(@Nullable String searchKey) {
//        LCQuery<LCObject> mLCQuery = LCQuery.getQuery(MusicModel.TABLE_NAME)
//                .limit(500);
//        if (TextUtils.isEmpty(searchKey) == false) {
//            mLCQuery.whereContains(MusicModel.COLUMN_NAME, searchKey);
//        }
//        return mLCQuery.findInBackground()
//                .subscribeOn(Schedulers.io())
//                .flatMap(new Function<List<LCObject>, ObservableSource<List<MusicModel>>>() {
//                    @Override
//                    public ObservableSource<List<MusicModel>> apply(@NonNull List<LCObject> LCObjects) throws Exception {
//                        List<MusicModel> list = new ArrayList<>();
//                        for (LCObject object : LCObjects) {
//                            MusicModel item = mGson.fromJson(object.toJSONObject().toJSONString(), MusicModel.class);
//                            list.add(item);
//                        }
//                        return Observable.just(list);
//                    }
//                });

        final Boolean[] isFinish = {false};

        final String[] resultString = {"{}"};

        if (TextUtils.isEmpty(searchKey)) {
            searchKey = "";
        }

        OkHttpClient client = OkHttpInstance.getInstance();
        RequestBody formBody = new FormBody.Builder()
                .add("searchKey", searchKey)
                .build();
        Request request = new Request.Builder()
                .url("https://redis.banghua.xin/app/index.php?i=888&c=entry&a=webapp&do=GetMusics&m=rediscache")
                .post(formBody)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {

            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                resultString[0] = response.body().string();
                isFinish[0] = true;
            }
        });

        while (!isFinish[0]){
            Log.d(TAG, "1getMusic: 加载歌曲中");
        }

        if (resultString[0].equals("false")){
            return Observable.just(new ArrayList<>());
        }
        List<MemberMusicModel> list = JSON.parseArray(resultString[0],MemberMusicModel.class);
        return Observable.just(list);
    }

    @Override
    public Observable<MemberMusicModel> getMusic(@NonNull String musicId) {
//        Map<String, Object> dicParameters = new HashMap<>();
//        dicParameters.put("id", musicId);
//
//        return LCCloud.callFunctionWithCacheInBackground(
//                "getMusic",
//                dicParameters,
//                LCQuery.CachePolicy.CACHE_ELSE_NETWORK,
//                30000)
//                .flatMap(new Function<Object, ObservableSource<MusicModel>>() {
//                    @Override
//                    public ObservableSource<MusicModel> apply(@NonNull Object o) throws Exception {
//                        return Observable.just(mGson.fromJson(String.valueOf(o), MusicModel.class));
//                    }
//                });


        final Boolean[] isFinish = {false};

        final String[] resultString = {"{}"};

        OkHttpClient client = OkHttpInstance.getInstance();
        RequestBody formBody = new FormBody.Builder()
                .add("musicId", musicId)
                .build();
        Request request = new Request.Builder()
                .url("https://redis.banghua.xin/app/index.php?i=888&c=entry&a=webapp&do=GetMusic&m=rediscache")
                .post(formBody)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {

            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                resultString[0] = response.body().string();
                isFinish[0] = true;
            }
        });

        while (!isFinish[0]){
            Log.d(TAG, "2getMusic: 加载歌曲中");
        }
        return Observable.just(mGson.fromJson(resultString[0], MemberMusicModel.class));
    }

    private OkHttpClient okHttpClient = OkHttpInstance.getInstance();

    @Override
    public Completable download(@NonNull File file, @NonNull String url) {
        return Completable.create(new CompletableOnSubscribe() {
            @Override
            public void subscribe(@NonNull CompletableEmitter emitter) throws Exception {
                Log.d(TAG, file.getName() + ", url: " + url);

                if (file.isDirectory()) {
                    emitter.onError(new Throwable("file is a Directory"));
                    return;
                }

                Request request = new Request.Builder().url(url).build();
                okHttpClient.newCall(request).enqueue(new Callback() {
                    @Override
                    public void onFailure(@NonNull Call call, @NonNull IOException e) {
                        emitter.onError(e);
                    }

                    @Override
                    public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                        ResponseBody body = response.body();
                        if (body == null) {
                            emitter.onError(new Throwable("body is empty"));
                            return;
                        }

                        long total = body.contentLength();

                        if (file.exists() && file.length() == total) {
                            emitter.onComplete();
                            return;
                        }

                        InputStream is = null;
                        byte[] buf = new byte[2048];
                        int len = 0;
                        FileOutputStream fos = null;
                        try {
                            is = body.byteStream();
                            fos = new FileOutputStream(file);
                            long sum = 0;
                            while ((len = is.read(buf)) != -1) {
                                fos.write(buf, 0, len);
                                sum += len;
                                int progress = (int) (sum * 1.0f / total * 100);
                                Log.d(TAG, file.getName() + ", progress: " + progress);
                            }
                            fos.flush();
                            // 下载完成
                            Log.d(TAG, file.getName() + " onComplete");
                            emitter.onComplete();
                        } catch (Exception e) {
                            emitter.onError(e);
                        } finally {
                            try {
                                if (is != null)
                                    is.close();
                            } catch (IOException e) {
                            }
                            try {
                                if (fos != null)
                                    fos.close();
                            } catch (IOException e) {
                            }
                        }
                    }
                });
            }
        });
    }
}
