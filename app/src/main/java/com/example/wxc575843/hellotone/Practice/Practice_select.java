package com.example.wxc575843.hellotone.Practice;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.example.wxc575843.hellotone.R;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest;
import com.model.Global;
import com.model.PracticeCategroy;

import java.util.ArrayList;
import java.util.List;

public class Practice_select extends AppCompatActivity {

    private static final String TAG = "PRACTICE_SELECT";

    List<PracticeCategroy> list = new ArrayList<PracticeCategroy>();
    Gson gson = new Gson();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_practice_select);


        HttpUtils httpUtils = new HttpUtils();
        httpUtils.send(HttpRequest.HttpMethod.GET, Global.PRACTICCATEGROYESERVLET, new RequestCallBack<String>() {
            @Override
            public void onSuccess(ResponseInfo<String> responseInfo) {
                String result = (String) responseInfo.result;
                list = gson.fromJson(result, new TypeToken<List<PracticeCategroy>>() {
                }.getType());
            }

            @Override
            public void onFailure(HttpException e, String s) {

            }
        });
        ListView lv = (ListView) findViewById(R.id.lv_practice);
        lv.setAdapter(new MyAdapter());
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(Practice_select.this,PracticeMain.class);
            }
        });
    }

    class MyAdapter extends BaseAdapter{

        @Override
        public int getCount() {
            return list.size();
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            String s = list.get(position).getItem();
            View v = null;
            //判断条目是否有缓存
            if(convertView == null){
                //把布局文件填充成一个View对象
                v = View.inflate(Practice_select.this, R.layout.practice_categroy_items, null);
            }
            else{
                v = convertView;
            }
            TextView tv_name = (TextView) v.findViewById(R.id.tv_practiceCategroyItem);
            tv_name.setText(s);
            return v;
        }
    }
}
