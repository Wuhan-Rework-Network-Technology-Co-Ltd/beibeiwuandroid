package io.agora.chatroom.ktv;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.io.File;
import java.util.List;

import io.reactivex.Completable;
import io.reactivex.Observable;

public class DataRepositroy implements IDataRepositroy {
    private volatile static DataRepositroy instance;

    private Context mContext;

    private final IDataRepositroy mIDataRepositroy;

    private DataRepositroy(Context context) {
        mContext = context.getApplicationContext();

        mIDataRepositroy = new DataRepositroyImpl();
    }

    public static synchronized DataRepositroy Instance(Context context) {
        if (instance == null) {
            synchronized (DataRepositroy.class) {
                if (instance == null)
                    instance = new DataRepositroy(context);
            }
        }
        return instance;
    }

    @Override
    public Observable<List<MemberMusicModel>> getMusics(@Nullable String searchKey) {
        return mIDataRepositroy.getMusics(searchKey);
    }

    @Override
    public Observable<MemberMusicModel> getMusic(@NonNull String musicId) {
        return mIDataRepositroy.getMusic(musicId);
    }

    @Override
    public Completable download(@NonNull File file, @NonNull String url) {
        return mIDataRepositroy.download(file, url);
    }
}
