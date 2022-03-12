package xin.banghua.beiyuan.custom_ui;


import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.List;

import xin.banghua.beiyuan.R;

/**
 *                         //xml部分
 *                         <LinearLayout
 *                             android:layout_width="match_parent"
 *                             android:layout_height="match_parent"
 *                             android:orientation="vertical"
 *                             android:layout_weight="1">
 *
 *                             <com.google.android.material.tabs.TabLayout
 *                                 android:id="@+id/viewpager_menu"
 *                                 android:layout_width="match_parent"
 *                                 android:layout_height="wrap_content"
 *                                 android:background="@color/background"
 *                                 app:tabSelectedTextColor="@color/color3"
 *                                 app:tabIndicatorColor="@color/alivc_common_bg_orange"
 *                                 app:tabTextColor="@color/color4"
 *                                 app:tabIndicatorHeight="3dp"
 *                                 app:tabMode="fixed"/>
 *
 *                             <androidx.viewpager.widget.ViewPager
 *                                 android:id="@+id/viewpager"
 *                                 android:layout_width="match_parent"
 *                                 android:layout_height="match_parent" />
 *                         </LinearLayout>
 */
public class ViewPagerExample {
    TabLayout viewpager_menu;
    ViewPager viewPager;

    private List<Fragment> menuViewPagerView_fragment = new ArrayList<>();
    private List<String> menuViewPagerView_menu = new ArrayList<>();

    FragmentPagerAdapter mFragmentPagerAdapter;

    View mView;

    AppCompatActivity appCompatActivity;

    public void initShow(){
        viewpager_menu = mView.findViewById(R.id.viewpager_menu);
        viewPager = mView.findViewById(R.id.viewpager);

        menuViewPagerView_fragment.add(new Fragment());
        menuViewPagerView_fragment.add(new Fragment());
        menuViewPagerView_fragment.add(new Fragment());

        menuViewPagerView_menu.add("资料");
        menuViewPagerView_menu.add("作品");
        menuViewPagerView_menu.add("喜欢");

        //配置ViewPager的适配器
        mFragmentPagerAdapter = new FragmentPagerAdapter((FragmentManager) appCompatActivity.getSupportFragmentManager()) {
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

        viewPager.setOffscreenPageLimit(3);
    }
}
