package xin.banghua.beiyuan.custom_ui;

import static androidx.fragment.app.FragmentPagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.List;


public class ViewPagerFragmentWithTabLayout {
    private List<Fragment> menuViewPagerView_fragment = new ArrayList<>();
    private List<String> menuViewPagerView_menu = new ArrayList<>();

    FragmentPagerAdapter mFragmentPagerAdapter;

    TabLayout viewpager_menu;
    ViewPager viewPager;

    public void initView(AppCompatActivity appCompatActivity){
//        viewpager_menu = mView.findViewById(R.id.viewpager_menu);
//        viewPager = mView.findViewById(R.id.viewpager);

        //menuViewPagerView_fragment.add(StaggerdRecyclerViewTaskListFragment.newInstance());

        menuViewPagerView_menu.add("任务");

        //配置ViewPager的适配器
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

        viewPager.setOffscreenPageLimit(4);
    }


    //XML
//    <LinearLayout
//    android:layout_width="match_parent"
//    android:layout_height="match_parent"
//    android:orientation="vertical"
//    android:layout_weight="1">
//
//    <com.google.android.material.tabs.TabLayout
//    android:id="@+id/viewpager_menu"
//    android:layout_width="match_parent"
//    android:layout_height="wrap_content"
//    android:background="@color/background"
//    app:tabSelectedTextColor="@color/color3"
//    app:tabIndicatorColor="@color/alivc_common_bg_orange"
//    app:tabTextColor="@color/color4"
//    app:tabIndicatorHeight="3dp"
//    app:tabMode="fixed"/>
//
//    <androidx.viewpager.widget.ViewPager
//    android:id="@+id/viewpager"
//    android:layout_width="match_parent"
//    android:layout_height="match_parent" />
//    </LinearLayout>
}
