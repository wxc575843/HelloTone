package com.example.wxc575843.hellotone.Practice;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.wxc575843.hellotone.R;
import com.example.wxc575843.hellotone.utils.SharePreferenceUtils;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.lidroid.xutils.BitmapUtils;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.like.LikeButton;
import com.like.OnLikeListener;
import com.model.Global;
import com.model.Record;

import java.io.IOException;
import java.util.ArrayList;

public class PracticeMain extends AppCompatActivity implements UIHelper{

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

    String id = "";
    String picture="";
    String example="";
    String explain="";
    String voiceFilePath="";
    MediaPlayer mPlayer;

    @ViewInject(R.id.practice_main_pinyin)
    ImageView ivPinYin;

    @ViewInject(R.id.vp_practice_main)
    ViewPager vpPracticeMain;

    @ViewInject(R.id.practice_main_btn_record)
    ImageButton btnRecord;

    @ViewInject(R.id.practice_main_btn_play)
    ImageButton btnPlay;

    @ViewInject(R.id.favourite_btn_practice)
    LikeButton btnAdd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_practice_main);
        ViewUtils.inject(this);
        id = getIntent().getStringExtra("id");
        picture = getIntent().getStringExtra("picture");
        example = getIntent().getStringExtra("example");
        explain = getIntent().getStringExtra("explain");
        voiceFilePath = getIntent().getStringExtra("filePath");
        Log.d("id", getIntent().getStringExtra("id"));


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

        BitmapUtils utils = new BitmapUtils(PracticeMain.this);
        utils.display(ivPinYin, Global.HTTPIP + picture);// 参1表示ImageView对象,
        mImageViewList = new ArrayList<ImageView>();
        ImageView image = new ImageView(this);
        utils.display(image, Global.HTTPIP + explain);
        mImageViewList.add(image);
        Log.d("explian", Global.HTTPIP + explain);
        utils.display(image, Global.HTTPIP + example);
        Log.d("example", Global.HTTPIP + example);
        mImageViewList.add(image);

        initBtnAdd();

        mPlayer = new MediaPlayer();
        btnPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri myUri = Uri.parse(voiceFilePath);
                Log.d("uri",myUri.toString());
                mPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
                try {
                    mPlayer.setDataSource(getApplicationContext(), myUri);
                } catch (IllegalArgumentException e) {
//                    Toast.makeText(getApplicationContext(), e.toString(), Toast.LENGTH_SHORT).show();
                } catch (SecurityException e) {
//                    Toast.makeText(getApplicationContext(), e.toString(), Toast.LENGTH_SHORT).show();
                } catch (IllegalStateException e) {
//                    Toast.makeText(getApplicationContext(), e.toString(), Toast.LENGTH_SHORT).show();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                try {
                    mPlayer.prepare();
                } catch (IllegalStateException e) {
//                    Toast.makeText(getApplicationContext(), "You might not set the URI correctly!", Toast.LENGTH_SHORT).show();
                } catch (IOException e) {
//                    Toast.makeText(getApplicationContext(), "You might not set the URI correctly!", Toast.LENGTH_SHORT).show();
                }
                mPlayer.start();
                Log.d("play", "yes");
                HttpUtils httpUtils = new HttpUtils();
                RequestParams params = new RequestParams();
                params.addBodyParameter("userId",SharePreferenceUtils.getString(PracticeMain.this,"id",null));
                params.addBodyParameter("recordId",id+"");
                httpUtils.send(HttpRequest.HttpMethod.POST, Global.LEARNRECORDSERVLET, params, new RequestCallBack<String>() {
                    @Override
                    public void onSuccess(ResponseInfo<String> responseInfo) {
                        Log.d("exp+2333",responseInfo.result);
                        if (responseInfo.result.equals("new")){
                            int exp = SharePreferenceUtils.getInt(PracticeMain.this,"experience",0);
                            SharePreferenceUtils.putInt(PracticeMain.this,"experience",exp+20);
                            Log.d("exp+2333",exp+"");
//                            int level = SharePreferenceUtils.getInt(PracticeMain.this,"level",0);
                            SharePreferenceUtils.putInt(PracticeMain.this,"level",exp/100);
                        }
                    }

                    @Override
                    public void onFailure(HttpException e, String s) {

                    }
                });
            }
        });
    }

    private void initBtnAdd() {

        HttpUtils utils = new HttpUtils();
        RequestParams params = new RequestParams();
        params.addBodyParameter("state","view");
        params.addBodyParameter("userId",SharePreferenceUtils.getString(PracticeMain.this,"id",null));
        params.addBodyParameter("recordid",id+"");
        utils.send(HttpRequest.HttpMethod.POST, Global.RECORDLIKESERVLET, params, new RequestCallBack<String>() {
            @Override
            public void onSuccess(ResponseInfo<String> responseInfo) {
                if (responseInfo.result.equals("yes")) {
                    btnAdd.setLiked(true);
                } else {
                    btnAdd.setLiked(false);
                }
            }

            @Override
            public void onFailure(HttpException e, String s) {

            }
        });

        btnAdd.setOnLikeListener(new OnLikeListener() {
            @Override
            public void liked(LikeButton likeButton) {
                HttpUtils httpUtils = new HttpUtils();
                RequestParams params = new RequestParams();
                params.addBodyParameter("userId", SharePreferenceUtils.getString(PracticeMain.this, "id", null));
                params.addBodyParameter("recordid", id);
                params.addBodyParameter("state", "favourite");
                httpUtils.send(HttpRequest.HttpMethod.POST, Global.RECORDLIKESERVLET, params, new RequestCallBack<String>() {
                    @Override
                    public void onSuccess(ResponseInfo<String> responseInfo) {

                    }

                    @Override
                    public void onFailure(HttpException e, String s) {
                        Toast.makeText(PracticeMain.this, R.string.register_failed_network, Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void unLiked(LikeButton likeButton) {
                HttpUtils httpUtils = new HttpUtils();
                RequestParams params = new RequestParams();
                params.addBodyParameter("userId", SharePreferenceUtils.getString(PracticeMain.this, "id", null));
                params.addBodyParameter("recordid", id);
                params.addBodyParameter("state", "unfavourite");
                httpUtils.send(HttpRequest.HttpMethod.POST, Global.RECORDLIKESERVLET, params, new RequestCallBack<String>() {
                    @Override
                    public void onSuccess(ResponseInfo<String> responseInfo) {

                    }

                    @Override
                    public void onFailure(HttpException e, String s) {
                        Toast.makeText(PracticeMain.this, R.string.register_failed_network, Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }

    class PracticeMainPageAdapter extends PagerAdapter{

        @Override
        public int getCount() {
            return 3;
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view==object;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            if (position == 0){
                ViewGroup v = (ViewGroup) mChart.getParent();
                if(v!=null){
                    v.removeView(mChart);
                }
                container.addView(mChart);
                return mChart;
            }else {
                Log.d("postion-1",position-1+"");
                Log.d("size", mImageViewList.size() + "");
                ViewGroup v = (ViewGroup) mImageViewList.get(position-1).getParent();
                if(v!=null){
                    v.removeView(mImageViewList.get(position-1));
                }
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
