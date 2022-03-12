package io.agora.chatroom.ktv;

import static androidx.fragment.app.FragmentPagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT;

import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.HorizontalScrollView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.agora.chatroom.R;
import io.agora.chatroom.R2;

public class KtvMusicListFrameLayout extends FrameLayout {
    private static final String TAG = "FrameLayoutExample";
    public KtvMusicListFrameLayout(@NonNull Context context) {
        super(context);
        init(context);
    }

    public KtvMusicListFrameLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public KtvMusicListFrameLayout(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public KtvMusicListFrameLayout(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context);
    }

    private Context mContext;
    private View mView;



    private void init(Context context) {
        mContext = context;
        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mView = inflater.inflate(R.layout.file_type_dialog, this, true);

        ButterKnife.bind(this, mView);
    }


//    @BindView(R.id.match_chat_view)
//    FrameLayout match_chat_view;//添加布局
    public void initShow(){
        //自定义部分


    }


    @BindView(R2.id.back_btn)
    Button back_btn;
    @BindView(R2.id.add_film_btn)
    Button add_film_btn;
    @BindView(R2.id.menu_scroll)
    HorizontalScrollView menu_scroll;
    @BindView(R2.id.viewpager_menu)
    TabLayout viewpager_menu;
    @BindView(R2.id.viewPager)
    ViewPager viewPager;
    @BindView(R2.id.search_film)
    SearchView search_film;

    private List<Fragment> menuViewPagerView_fragment = new ArrayList<>();
    private List<String> menuViewPagerView_menu = new ArrayList<>();

    FragmentPagerAdapter mFragmentPagerAdapter;


    KtvMusicListFragment ktvMusicListFragment;

    public void initView(AppCompatActivity context){
        back_btn.setOnClickListener(view -> {
            this.setVisibility(GONE);
        });

        ktvMusicListFragment = KtvMusicListFragment.newInstance("");

        menuViewPagerView_fragment.add(ktvMusicListFragment);
        menuViewPagerView_fragment.add(KtvMusicListFragment.newInstance(""));


        menuViewPagerView_menu.add("歌曲");
        menuViewPagerView_menu.add("已点");


        viewpager_menu.setTabMode(TabLayout.MODE_SCROLLABLE);


        //配置ViewPager的适配器
        mFragmentPagerAdapter = new FragmentPagerAdapter(context.getSupportFragmentManager(),BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {
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

        int[] viewLocation = new int[2];

        viewpager_menu.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
//                tab.view.getLocationInWindow(viewLocation);
//                int viewX = viewLocation[0]; // x 坐标
//                int viewY = viewLocation[1]; // y 坐标
//                menu_scroll.scrollTo(viewX,viewY);
//                Log.d(TAG, "getItem: 当前菜单位置"+viewX+"|"+viewY);
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        viewPager.setAdapter(mFragmentPagerAdapter);
        viewpager_menu.setupWithViewPager(viewPager);

        viewPager.setOffscreenPageLimit(3);

        search_film.onActionViewExpanded();
        search_film.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                ktvMusicListFragment.searchName(newText);
                return false;
            }
        });


        add_film_btn.setOnClickListener(v -> {
//            Uri uri = Uri.parse("https://www.oushelun.cn");
//            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
//            mContext.startActivity(intent);
        });
    }
}
