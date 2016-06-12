package com.example.wxc575843.hellotone.Community;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

public class HorizontalScrollViewPager extends ViewPager {

	float startX;
	float startY;

	public HorizontalScrollViewPager(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public HorizontalScrollViewPager(Context context) {
		super(context);
	}

	/**
	 * 上下滑动时, 父控件拦截事件
	 * 
	 * 向右滑动, 当前页是第一页时, 父控件拦截事件; 向左滑动, 当前是最后一页时, 父控件拦截事件
	 */
	@Override
	public boolean dispatchTouchEvent(MotionEvent ev) {
		switch (ev.getAction()) {
		case MotionEvent.ACTION_DOWN:
			// 请求父控件不要拦截触摸事件
			getParent().requestDisallowInterceptTouchEvent(true);

			startX = ev.getX();
			startY = ev.getY();
			break;
		case MotionEvent.ACTION_MOVE:
			float endX = ev.getX();
			float endY = ev.getY();

			float dx = endX - startX;
			float dy = endY - startY;

			// 判断是会否左右滑动
			if (Math.abs(dx) > Math.abs(dy)) {
				// 向右滑动
				if (dx > 0) {
					if (getCurrentItem() == 0) {
						getParent().requestDisallowInterceptTouchEvent(false);
					}
				} else {
					if (getCurrentItem() == getAdapter().getCount() - 1) {
						getParent().requestDisallowInterceptTouchEvent(false);
					}
				}
			} else {
				getParent().requestDisallowInterceptTouchEvent(false);
			}

			break;
		default:
			break;
		}

		return super.dispatchTouchEvent(ev);
	}

}
