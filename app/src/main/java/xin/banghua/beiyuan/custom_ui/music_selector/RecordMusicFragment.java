//package xin.banghua.beiyuan.custom_ui.music_selector;
//
//import android.os.Bundle;
//
//import androidx.annotation.NonNull;
//import androidx.annotation.Nullable;
//import androidx.fragment.app.Fragment;
//
//import android.util.Log;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//
//
//import static android.view.View.VISIBLE;
//
///**
// * A simple {@link Fragment} subclass.
// * Use the {@link RecordMusicFragment#newInstance} factory method to
// * create an instance of this fragment.
// */
//public class RecordMusicFragment extends Fragment {
//
//    // TODO: Rename parameter arguments, choose names that match
//    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
//    private static final String ARG_PARAM1 = "param1";
//    private static final String ARG_PARAM2 = "param2";
//
//    // TODO: Rename and change types of parameters
//    private String mParam1;
//    private String mParam2;
//
//    public RecordMusicFragment() {
//        // Required empty public constructor
//    }
//
//    /**
//     * Use this factory method to create a new instance of
//     * this fragment using the provided parameters.
//     *
//     * @param param1 Parameter 1.
//     * @param param2 Parameter 2.
//     * @return A new instance of fragment RecordMusicFragment.
//     */
//    // TODO: Rename and change types and number of parameters
//    public static RecordMusicFragment newInstance(String param1, String param2) {
//        RecordMusicFragment fragment = new RecordMusicFragment();
//        Bundle args = new Bundle();
//        args.putString(ARG_PARAM1, param1);
//        args.putString(ARG_PARAM2, param2);
//        fragment.setArguments(args);
//        return fragment;
//    }
//
//    @Override
//    public void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        if (getArguments() != null) {
//            mParam1 = getArguments().getString(ARG_PARAM1);
//            mParam2 = getArguments().getString(ARG_PARAM2);
//        }
//    }
//
//    @Override
//    public View onCreateView(LayoutInflater inflater, ViewGroup container,
//                             Bundle savedInstanceState) {
//        // Inflate the layout for this fragment
//        return inflater.inflate(R.layout.fragment_record_music, container, false);
//    }
//
//
//    AudioRecordButton audio_record_button;
//
//    @Override
//    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
//        super.onViewCreated(view, savedInstanceState);
//
//        audio_record_button = view.findViewById(R.id.audio_record_button);
//
//        audio_record_button.setOnAudioListener(new AudioListener() {
//            @Override
//            public void onStop(RecordingItem recordingItem) {
//                Log.d("测试",":"+recordingItem.getFilePath());
//                //传地址给发帖,关闭音乐选择器
//                FabutieziActivity.voicebutton.setPlayPath(recordingItem.getFilePath(),getActivity());
//                FabutieziActivity.voicebutton.setVisibility(VISIBLE);
//                FabutieziActivity.musicSelectorView.setVisibility(View.GONE);
//                FabutieziActivity.voice_role = "";
//            }
//
//            @Override
//            public void onCancel() {
//
//            }
//
//            @Override
//            public void onError(Exception e) {
//
//            }
//        });
//    }
//}