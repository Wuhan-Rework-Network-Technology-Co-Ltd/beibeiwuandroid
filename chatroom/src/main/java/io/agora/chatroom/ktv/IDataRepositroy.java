package io.agora.chatroom.ktv;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.io.File;
import java.util.List;

import io.reactivex.Completable;
import io.reactivex.Observable;

/**
 * 数据仓库接口
 *
 * @author chenhengfei(Aslanchen)
 */
public interface IDataRepositroy {

    Observable<List<MemberMusicModel>> getMusics(@Nullable String searchKey);

    Observable<MemberMusicModel> getMusic(@NonNull String musicId);

    Completable download(@NonNull File file, @NonNull String url);
}
