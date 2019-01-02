package com.androidwind.seop.ui.tab;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.androidwind.seop.R;
import com.androidwind.seop.ui.BaseFragment;
import com.ogaclejapan.smarttablayout.SmartTabLayout;

import java.util.Arrays;
import java.util.List;

/**
 * @author ddnosh
 * @website http://blog.csdn.net/ddnosh
 */
public class SmartTabLayoutFragment extends BaseFragment {

    private ViewPager mViewPager;
    private SmartTabLayout mSmartTabLayout;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_ui_tab_smarttablayout, container, false);
        mViewPager = view.findViewById(R.id.vp_tab);
        mSmartTabLayout = view.findViewById(R.id.stl_main_tab);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initViewsAndEvents();
    }

    private void initViewsAndEvents() {
        final List<String> tabs = Arrays.asList(getResources()
                .getStringArray(R.array.tabs));
        mViewPager.setOffscreenPageLimit(tabs.size());
        final PagerAdapter pagerAdapter = new FragmentPagerAdapter(getActivity().getSupportFragmentManager()) {
            @Override
            public int getCount() {
                return null != tabs ? tabs.size() : 0;
            }

            @Override
            public Fragment getItem(int position) {
                Fragment fragment = null;
                switch (position) {
                    case 0:
                        fragment = new TestFragment();
                        break;
                    case 1:
                        fragment = new TestFragment();
                        break;
                    case 2:
                        fragment = new TestFragment();
                        break;
                    default:
                        break;
                }

                return fragment;
            }

            @Override
            public CharSequence getPageTitle(int position) {
                return null != tabs ? tabs.get(position) : null;
            }
        };
        mViewPager.setAdapter(pagerAdapter);
        final LayoutInflater inflater = LayoutInflater.from(mSmartTabLayout.getContext());
        //自定义SmartTabLayout的tab布局
        mSmartTabLayout.setCustomTabView(new SmartTabLayout.TabProvider() {
            @Override
            public View createTabView(ViewGroup container, int position, PagerAdapter adapter) {
                LinearLayout layoutContainer = (LinearLayout) inflater.inflate(R.layout.layout_tab_customized, container, false);
                ImageView icon = (ImageView) layoutContainer.findViewById(R.id.ic_tab);
                TextView tvTabName = (TextView) layoutContainer.findViewById(R.id.tv_tab_name);
                tvTabName.setText(pagerAdapter.getPageTitle(position));
                if (0 == position) {
                    icon.setImageResource(R.mipmap.ic_launcher);
                } else if (1 == position) {
                    icon.setImageResource(R.mipmap.ic_launcher);
                } else {
                    icon.setImageResource(R.mipmap.ic_launcher);
                }
                return layoutContainer;
            }
        });
        //一定要在setCustomTabView之后设置
        mSmartTabLayout.setViewPager(mViewPager);
        mSmartTabLayout.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }
}
