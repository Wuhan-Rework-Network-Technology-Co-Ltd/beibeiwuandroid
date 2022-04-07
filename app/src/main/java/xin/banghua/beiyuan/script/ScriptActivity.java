package xin.banghua.beiyuan.script;

import static androidx.fragment.app.FragmentPagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT;

import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.List;

import xin.banghua.beiyuan.R;

public class ScriptActivity extends AppCompatActivity {


    private List<Fragment> menuViewPagerView_fragment = new ArrayList<>();
    private List<String> menuViewPagerView_menu = new ArrayList<>();

    FragmentPagerAdapter mFragmentPagerAdapter;

    TabLayout viewpager_menu;
    ViewPager viewPager;

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish(); // back button
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_script2);


        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("配音&对合音");
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeButtonEnabled(true); //设置返回键可用
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);



        viewpager_menu = findViewById(R.id.viewpager_menu);
        viewPager = findViewById(R.id.viewpager);

        menuViewPagerView_fragment.add(ScriptKFragment.newInstance("k音","k音"));
        menuViewPagerView_fragment.add(ScriptCFragment.newInstance("合音","合音"));

        menuViewPagerView_menu.add("配音");
        menuViewPagerView_menu.add("合音");

        //配置ViewPager的适配器
        mFragmentPagerAdapter = new FragmentPagerAdapter(getSupportFragmentManager(),BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {
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