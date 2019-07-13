package com.yuqingsen.yximalaya;

import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.os.Bundle;

import com.yuqingsen.yximalaya.adapters.IndicatorAdapter;
import com.yuqingsen.yximalaya.adapters.MainContentAdapter;
import com.yuqingsen.yximalaya.utils.LogUtil;

import net.lucode.hackware.magicindicator.MagicIndicator;
import net.lucode.hackware.magicindicator.ViewPagerHelper;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.CommonNavigator;


public class MainActivity extends FragmentActivity {

    private String TAG = "MainActivity";
    public MagicIndicator magicIndicator;
    public ViewPager contentPager;
    public IndicatorAdapter indicatorAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        initEvent();
    }

    private void initEvent() {
        indicatorAdapter.setOnIndicatorTabClickListenr(new IndicatorAdapter.OnIndicatorTabClickListener() {
            @Override
            public void onTabClick(int i) {
                LogUtil.d(TAG,"click index is"+i);
                if (contentPager!=null){
                    contentPager.setCurrentItem(i);
                }
            }
        });
    }

    private void initView() {
        magicIndicator = findViewById(R.id.main_indicator);
        magicIndicator.setBackgroundColor(this.getResources().getColor(R.color.main_color));
        //创建适配器
        indicatorAdapter = new IndicatorAdapter(this);
        CommonNavigator commonNavigator = new CommonNavigator(this);
        commonNavigator.setAdjustMode(true);
        commonNavigator.setAdapter(indicatorAdapter);


        //获取ViewPager
        contentPager = this.findViewById(R.id.content_pager);

        //创建内容适配器
        FragmentManager supportFragmentManager = getSupportFragmentManager();
        MainContentAdapter mainContentAdapter = new MainContentAdapter(supportFragmentManager);

        contentPager.setAdapter(mainContentAdapter);

        //绑定
        magicIndicator.setNavigator(commonNavigator);
        ViewPagerHelper.bind(magicIndicator,contentPager);
    }
}
