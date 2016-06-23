package com.example.wxc575843.hellotone.Settings;

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
import android.widget.Toast;

import com.example.wxc575843.hellotone.Practice.PracticeMain;
import com.example.wxc575843.hellotone.R;
import com.example.wxc575843.hellotone.utils.SharePreferenceUtils;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.model.Global;
import com.model.Record;

import java.util.ArrayList;
import java.util.List;

public class LikeRecord extends AppCompatActivity {

    List<Record> list;

    @ViewInject(R.id.lv_like_record)
    ListView likeRecord;

    MyAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_like_record);
        ViewUtils.inject(this);
        list = new ArrayList<Record>();
        adapter = new MyAdapter();

        initData();
        initView();
    }

    private void initData(){
        HttpUtils httpUtils = new HttpUtils();
        RequestParams params = new RequestParams();
        params.addBodyParameter("userid", SharePreferenceUtils.getString(LikeRecord.this,"id",null));
        httpUtils.send(HttpRequest.HttpMethod.POST, Global.RECORDLIKELISTSERVLET, params, new RequestCallBack<String>() {
            @Override
            public void onSuccess(ResponseInfo<String> responseInfo) {
                String result = (String) responseInfo.result;
                Gson gson = new Gson();
                list = gson.fromJson(result, new TypeToken<List<Record>>() {
                }.getType());
                Log.d("res", result);
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onFailure(HttpException e, String s) {

            }
        });
    }

    private void initView(){
        likeRecord.setAdapter(adapter);
        likeRecord.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(LikeRecord.this,PracticeMain.class);
                Toast.makeText(LikeRecord.this, "click", Toast.LENGTH_SHORT).show();
                intent.putExtra("id", list.get(position).getId()+"");
                Log.d("id",list.get(position).getId()+"");
                intent.putExtra("picture",list.get(position).getPicturePath());
                intent.putExtra("example",list.get(position).getExamplePath());
                intent.putExtra("explain",list.get(position).getExplainPath());
                startActivity(intent);
            }
        });
    }

    class MyAdapter extends BaseAdapter{

        @Override
        public int getCount() {
            return list.size();
        }

        @Override
        public Record getItem(int position) {
            return list.get(position);
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            Record r = list.get(position);
            String s = r.getName() +" " + r.getRecordClass();
            View v = null;
            //判断条目是否有缓存
            if(convertView == null){
                //把布局文件填充成一个View对象
                v = View.inflate(LikeRecord.this, R.layout.practice_categroy_items, null);
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
