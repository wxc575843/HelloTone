package com.example.wxc575843.hellotone.start;

import android.content.Intent;
import android.media.Image;
import android.media.audiofx.BassBoost;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ActionMenuView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.example.wxc575843.hellotone.R;
import com.example.wxc575843.hellotone.utils.DensityUtils;
import com.example.wxc575843.hellotone.utils.SharePreferenceUtils;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;

import java.util.ArrayList;

public class GuideActivity extends AppCompatActivity {

    ArrayList<ImageView> mImageViewList;
    private static final int[] mImageIds = new int[]{
      R.drawable.guide_picture1,R.drawable.guide_picture2,R.drawable.guide_picture3
    };
    int mPointWidth;

    @ViewInject(R.id.vp_guide)
    private ViewPager vpGuide;
    @ViewInject(R.id.guide_point_group)
    LinearLayout llPointGroup;
    @ViewInject(R.id.guide_red_point)
    View viewRedPoint;
    @ViewInject(R.id.btn_start)
    Button btnStart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guide);
        ViewUtils.inject(this);
        initViews();
        vpGuide.setAdapter(new GuideAdapter());
        vpGuide.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                int len = (int) (mPointWidth * (positionOffset + position));
                RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) viewRedPoint.getLayoutParams();
                params.leftMargin = len;
                viewRedPoint.setLayoutParams(params);
            }

            @Override
            public void onPageSelected(int position) {
                if (position == mImageViewList.size() - 1) {
                    btnStart.setVisibility(View.VISIBLE);
                } else {
                    btnStart.setVisibility(View.GONE);
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        btnStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharePreferenceUtils.putBoolean(GuideActivity.this,
                        SplashActivity.PREF_IS_USER_GUIDE_SHOWED, true);// 记录已经展现过了新手引导页

                Intent intent = new Intent(GuideActivity.this, LoginActivity.class);
                startActivity(intent);

                finish();

            }
        });
    }

    private void initViews(){
        mImageViewList = new ArrayList<ImageView>();
        for (int i = 0; i < mImageIds.length; ++i){
            ImageView image = new ImageView(this);
            image.setBackgroundResource(mImageIds[i]);
            mImageViewList.add(image);
        }

        for(int i = 0; i < mImageIds.length; ++i){
            View point = new View(this);
            point.setBackgroundResource(R.drawable.shape_point_gray);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(DensityUtils.dp2px(this,15),DensityUtils.dp2px(this,15));
            if(i>0){
                params.leftMargin = DensityUtils.dp2px(this,15);
            }
            point.setLayoutParams(params);
            llPointGroup.addView(point);
        }

        llPointGroup.getViewTreeObserver().addOnGlobalLayoutListener(
                new ViewTreeObserver.OnGlobalLayoutListener() {

                    // 完成布局后会回调改方法, 改方法可能会被回调多次
                    @Override
                    public void onGlobalLayout() {
                        // 此方法只需要执行一次就可以: 把当前的监听事件从视图树中移除掉, 以后就不会在回调此事件了.
                        llPointGroup.getViewTreeObserver()
                                .removeGlobalOnLayoutListener(this);

                        mPointWidth = llPointGroup.getChildAt(1).getLeft()
                                - llPointGroup.getChildAt(0).getLeft();

                        System.out.println("间距: " + mPointWidth);
                    }
                });
        btnStart.setVisibility(View.GONE);
    }

    class GuideAdapter extends PagerAdapter{

        @Override
        public int getCount() {
            return mImageIds.length;
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            container.addView(mImageViewList.get(position));
            return mImageViewList.get(position);
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((ImageView)object);
        }
    }


}
