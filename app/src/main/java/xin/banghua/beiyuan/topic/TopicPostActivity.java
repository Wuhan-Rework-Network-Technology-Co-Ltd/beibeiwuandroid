package xin.banghua.beiyuan.topic;

import static androidx.fragment.app.FragmentPagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT;

import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.RelativeLayout;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import xin.banghua.beiyuan.R;

public class TopicPostActivity extends AppCompatActivity {
    private static final String TAG = "TopicPostActivity";
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

    TopicList topicList;


    @BindView(R.id.header_layout)
    RelativeLayout header_layout;
    @BindView(R.id.nestedScrollView)
    NestedScrollView nestedScrollView;

    int menuY = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_topic_post);


        ButterKnife.bind(this);

        topicList = (TopicList) getIntent().getSerializableExtra("topicList");


        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle(topicList.getTopic());
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeButtonEnabled(true); //设置返回键可用
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);



        viewpager_menu = findViewById(R.id.viewpager_menu);
        viewPager = findViewById(R.id.viewpager);

        TopicPostFragment topicPostFragment = TopicPostFragment.newInstance(topicList.getTopic(),"最新");
        topicPostFragment.topicPostActivity = this;
        menuViewPagerView_fragment.add(topicPostFragment);

        menuViewPagerView_menu.add("最新");

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


        viewPager.setCurrentItem(getIntent().getIntExtra("type",0));

        header_layout.post(new Runnable() {
            @Override
            public void run() {
                menuY = header_layout.getHeight()+toolbar.getHeight();
                Log.d(TAG, "onCreate: bar高度"+menuY);
            }
        });
    }
}