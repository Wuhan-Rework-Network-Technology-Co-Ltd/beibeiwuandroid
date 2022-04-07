package xin.banghua.beiyuan.custom_ui.music_selector;

import android.content.ContentResolver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.util.ArrayList;
import java.util.List;

import xin.banghua.beiyuan.R;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link LocalMusicFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class LocalMusicFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public LocalMusicFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment LocalMusicFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static LocalMusicFragment newInstance(String param1, String param2) {
        LocalMusicFragment fragment = new LocalMusicFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_local_music, container, false);
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //获取控件
        mListViewMusic=(ListView) view.findViewById(R.id.music_listView);
        initData();
        //设置适配器
        musicListViewAdapter=new MusicListViewAdapter(getActivity(), mMusicList);
        mListViewMusic.setAdapter(musicListViewAdapter);
        //监听事件
        initEvent();
    }

    private static final String TAG = "MainActivity";
    //音乐ListView视图
    private ListView mListViewMusic;
    //音乐集合
    private List<Music> mMusicList;
    //ListView的适配器
    private MusicListViewAdapter musicListViewAdapter;
    //开始播放的进度
    private long startProgress=0;

    private void initData() {
        // TODO Auto-generated method stub
        // 读取手机中的音乐文件
        mMusicList = new ArrayList<Music>();
        /*
         * Uri：指明要查询的数据库名称加上表的名称，从MediaStore中我们可以找到相应信息的参数。 Projection:
         * 指定查询数据库表中的哪几列，返回的游标中将包括相应的信息。Null则返回所有信息。 selection: 指定查询条件
         * selectionArgs：参数selection里有
         * ？这个符号是，这里可以以实际值代替这个问号。如果selection这个没有？的话，那么这个String数组可以为null。
         * SortOrder：指定查询结果的排列顺序
         */
        Uri uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        ContentResolver contentResolver = getActivity().getContentResolver();
        String selection = MediaStore.Audio.Media.MIME_TYPE + "=? ";
        String[] selectionArgs = new String[] { "audio/mpeg" };
        // 游标
        Cursor cursor = contentResolver.query(uri, null, null, null,
                MediaStore.Audio.AudioColumns.IS_MUSIC);
        if (cursor != null) {
            while (cursor.moveToNext()) {
                String name = cursor
                        .getString(cursor
                                .getColumnIndexOrThrow(MediaStore.Audio.Media.DISPLAY_NAME));
                String singer = cursor.getString(cursor
                        .getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST));
                String path = cursor.getString(cursor
                        .getColumnIndexOrThrow(MediaStore.Audio.Media.DATA));
                long duration = cursor
                        .getLong(cursor
                                .getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION));
                long size = cursor.getLong(cursor
                        .getColumnIndexOrThrow(MediaStore.Audio.Media.SIZE));

                // 把歌曲名字和歌手切割开
                // if (size > 1000 * 800) {
                if (name.contains("-")) {
                    String[] str = name.split("-");
                    singer = str[0];
                    if (str[1].contains(".")) {
                        name = str[1].substring(0, str[1].indexOf('.'));
                    } else {
                        name = str[1];
                    }
                } else if (name.contains(".")) {
                    name = name.substring(0, name.indexOf('.'));
                }
                Music music = new Music(name, singer, duration, path ,"local");
                mMusicList.add(music);
                // }
            }
        }
        cursor.close();
    }

    private void initEvent() {
        // TODO Auto-generated method stub
        /**
         * 监听ListView中音乐的播放事件
         */
        musicListViewAdapter.setOnplayMusicListener(new MusicListViewAdapter.OnplayMusicListener() {
            //播放完整歌曲
            @Override
            public void playForFull() {
                // TODO Auto-generated method stub
                if(musicListViewAdapter!=null){
                    musicListViewAdapter.setSelectItem(musicListViewAdapter.getSelectItem());
                    musicListViewAdapter.setHidePlayBtn(true);
                    musicListViewAdapter.notifyDataSetChanged();
                }
                Music music=musicListViewAdapter.getCurrentMusic();
                MusicPlayer.play(getActivity(),music.getPath(), 0);
            }
            //播放片段歌曲
            @Override
            public void playforPart() {
                // TODO Auto-generated method stub
                startProgress=musicListViewAdapter.getCutMusicStartProgress();
                MusicPlayer.endProgress=musicListViewAdapter.getCutMusicEndProgress();
                String path=musicListViewAdapter.getCutMusicPath();
                MusicPlayer.play(getActivity(), path,(int)startProgress);
                MusicPlayer.handler.post(MusicPlayer.run);
            }


        });
        /**
         * 监听ListView子项的点击事件
         */
        mListViewMusic.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position,
                                    long id) {
                // TODO Auto-generated method stub

                if(musicListViewAdapter!=null){
                    musicListViewAdapter.setHidePlayBtn(false);
                    musicListViewAdapter.setSelectItem(position);
                    musicListViewAdapter.notifyDataSetChanged();
                }
                MusicPlayer.reset();
            }
        });
    }
}