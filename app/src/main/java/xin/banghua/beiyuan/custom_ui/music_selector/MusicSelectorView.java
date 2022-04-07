package xin.banghua.beiyuan.custom_ui.music_selector;

import static androidx.fragment.app.FragmentPagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.List;

import xin.banghua.beiyuan.R;

public class MusicSelectorView extends FrameLayout {
    private static final String TAG = "MusicSelectorView";
    public MusicSelectorView(@NonNull Context context) {
        super(context);
    }

    public MusicSelectorView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public MusicSelectorView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    public MusicSelectorView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context, attrs);
    }

    private Context mContext;
    private View mView;

    private void init(Context context, AttributeSet attrs) {
        mContext = context;
        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mView = inflater.inflate(R.layout.music_selector_view, this, true);


        viewpager_menu = mView.findViewById(R.id.viewpager_menu);
        viewPager = mView.findViewById(R.id.viewpager);
    }


    private List<Fragment> menuViewPagerView_fragment = new ArrayList<>();
    private List<String> menuViewPagerView_menu = new ArrayList<>();

    FragmentPagerAdapter mFragmentPagerAdapter;

    TabLayout viewpager_menu;
    ViewPager viewPager;

    Boolean ifInitialed = false;
    public void initShow(AppCompatActivity appCompatActivity){
        if (ifInitialed)
            return;


        ifInitialed = true;
        //自定义部分
        menuViewPagerView_fragment.add(new OnlineMusicFragment());
        menuViewPagerView_fragment.add(new LocalMusicFragment());
        //menuViewPagerView_fragment.add(new RecordMusicFragment());

        menuViewPagerView_menu.add("在线");
        menuViewPagerView_menu.add("本地");
        //menuViewPagerView_menu.add("录音");


        mFragmentPagerAdapter = new FragmentPagerAdapter(appCompatActivity.getSupportFragmentManager(),BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {
            @Override
            public Fragment getItem(int position) {
                return menuViewPagerView_fragment.get(position);
            }

            @Override
            public int getCount() {
                return menuViewPagerView_fragment.size();
            }

            @Nullable
            @Override
            public CharSequence getPageTitle(int position) {
                return menuViewPagerView_menu.get(position);
            }
        };

        viewPager.setAdapter(mFragmentPagerAdapter);
        viewpager_menu.setupWithViewPager(viewPager);

        viewPager.setOffscreenPageLimit(2);
    }
}
