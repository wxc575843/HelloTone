package com.example.wxc575843.hellotone.Practice;

import android.graphics.Color;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.example.wxc575843.hellotone.R;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;

import java.util.ArrayList;

public class PracticeMain extends AppCompatActivity implements UIHelper{

    private int[] mImageIds = new int[]{R.drawable.explain,R.drawable.example};
    ArrayList<ImageView> mImageViewList;
    private LineChart mChart;
    private Thread audioThread;
    Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            float tmpData;
            tmpData = Float.valueOf(String.valueOf(msg.obj));
            addEntry(tmpData);
            return true;
        }
    });

    @ViewInject(R.id.vp_practice_main)
    ViewPager vpPracticeMain;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_practice_main);
        ViewUtils.inject(this);
        initChart();
        initViews();

        vpPracticeMain.setAdapter(new PracticeMainPageAdapter());
        startHook();
    }

    private void initChart(){
        mChart = new LineChart(this);
        mChart.setBackgroundColor(Color.GRAY);
        // enable touch gestures
        mChart.setTouchEnabled(true);

        // enable scaling and dragging
        mChart.setDragEnabled(true);
        mChart.setScaleEnabled(true);
        mChart.setDrawGridBackground(false);
        LineData data = new LineData();
        data.setValueTextColor(Color.WHITE);
        mChart.setDescription("");
        mChart.setData(data);
        setLegend();
    }

    private void setLegend(){
        Legend l = mChart.getLegend();
        // modify the legend ...
        // l.setPosition(LegendPosition.LEFT_OF_CHART);
        l.setForm(Legend.LegendForm.LINE);
        l.setTextColor(Color.WHITE);

        YAxis leftAxis = mChart.getAxisLeft();
        leftAxis.setTextColor(Color.WHITE);
        leftAxis.setAxisMaxValue(500f);
        leftAxis.setAxisMinValue(0f);
        leftAxis.setDrawGridLines(true);
        leftAxis.setLabelCount(30, true);

        YAxis rightAxis = mChart.getAxisRight();
        rightAxis.setEnabled(false);
        XAxis xl = mChart.getXAxis();
        xl.setEnabled(false);
    }

    private void initViews(){
        mImageViewList = new ArrayList<ImageView>();
        for (int i = 0; i < mImageIds.length; ++i){
            ImageView image = new ImageView(this);
            image.setBackgroundResource(mImageIds[i]);
            mImageViewList.add(image);
        }

    }

    class PracticeMainPageAdapter extends PagerAdapter{

        @Override
        public int getCount() {
            return mImageIds.length+1;
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view==object;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            if (position == 0){
                container.addView(mChart);
                return mChart;
            }else {
                container.addView(mImageViewList.get(position-1));
                return mImageViewList.get(position-1);
            }
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            if(position==0){
                container.removeView((LineChart) object);
            } else {
                container.removeView((ImageView) object);
            }

        }
    }

    public void display(final double pitch) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Message msg = new Message();
                msg.obj = pitch;
                handler.sendMessage(msg);
                //textView.setText(Double.toString(pitch));
            }
        });
    }

    private void addEntry(float newEntry) {
        LineData data = mChart.getData();

        if (data != null) {
            ILineDataSet set = data.getDataSetByIndex(0);
            // set.addEntry(...); // can be called as well

            if (set == null) {
                set = createSet();
                data.addDataSet(set);
            }

            // add a new x-value first
            data.addXValue("1");
            data.addEntry(new Entry(newEntry, set.getEntryCount()), 0);

            // let the chart know it's data has changed
            mChart.notifyDataSetChanged();
            // limit the number of visible entries
            mChart.setVisibleXRangeMaximum(50);
            // mChart.setVisibleYRange(30, AxisDependency.LEFT);
            // move to the latest entry
            mChart.moveViewToX(data.getXValCount() - 51);

            // this automatically refreshes the chart (calls invalidate())
            // mChart.moveViewTo(data.getXValCount()-7, 55f,
            // AxisDependency.LEFT);
        }
    }

    private LineDataSet createSet() {

        LineDataSet set = new LineDataSet(null, "Dynamic Data");
        set.setAxisDependency(YAxis.AxisDependency.LEFT);
        set.setColor(ColorTemplate.getHoloBlue());
        set.setCircleColor(Color.WHITE);
        set.setLineWidth(2f);
        set.setCircleRadius(4f);
        set.setFillAlpha(65);
        set.setFillColor(ColorTemplate.getHoloBlue());
        set.setHighLightColor(Color.rgb(244, 117, 117));
        set.setValueTextColor(Color.WHITE);
        set.setValueTextSize(9f);
        set.setDrawValues(false);
        return set;
    }

    @Override
    protected void onPause() {
        endHook();
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        startHook();
    }

    private void endHook() {
        AndroidRecorder.deinit();
        try {
            audioThread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (Exception ex) {
            ex.printStackTrace();
            System.exit(-1);
        }
    }

    private void startHook() {
        AndroidRecorder.init(this);
        launchPitcha();
    }

    private void launchPitcha() {
        audioThread = new Thread(new Runnable() {
            public void run() {
                android.os.Process.setThreadPriority(android.os.Process.THREAD_PRIORITY_DEFAULT);
                AndroidRecorder.run();
            }
        });
        audioThread.start();
    }
}
