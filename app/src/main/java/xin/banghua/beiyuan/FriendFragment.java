package xin.banghua.beiyuan;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by Lm on 2018/3/29.
 * Email:1002464056@qq.com
 */

public class FriendFragment extends Fragment {
    public static FriendFragment instance = null;   // 单例模式
    private View mView;

    public static FriendFragment getInstance() {
        if (instance == null) {
            instance = new FriendFragment();
        }
        return instance;
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_friend, container, false);

        return mView;

    }
}
