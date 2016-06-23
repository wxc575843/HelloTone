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
import android.widget.Toast;

import com.example.wxc575843.hellotone.R;
import com.example.wxc575843.hellotone.utils.SharePreferenceUtils;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest;
import com.model.Global;
import com.model.PracticeCategroy;
import com.model.Record;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import be.tarsos.dsp.util.fft.ScaledHammingWindow;

public class Practice_select extends AppCompatActivity {

    private static final String TAG = "PRACTICE_SELECT";

    List<Record> list;
    Gson gson = new Gson();
    MyAdapter adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_practice_select);
        list = new ArrayList<Record>();
        adapter = new MyAdapter();

        Log.d("Practice_select",Global.RECORDLISTSERVLET);
        HttpUtils httpUtils = new HttpUtils();
        httpUtils.send(HttpRequest.HttpMethod.GET, Global.RECORDLISTSERVLET, new RequestCallBack<String>() {
            @Override
            public void onSuccess(ResponseInfo<String> responseInfo) {
                String result = (String) responseInfo.result;
                list = gson.fromJson(result, new TypeToken<List<Record>>() {
                }.getType());
                Log.d("res",result);
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onFailure(HttpException e, String s) {
                Toast.makeText(Practice_select.this,"network failed",Toast.LENGTH_SHORT).show();
            }
        });
        ListView lv = (ListView) findViewById(R.id.lv_practice);
        lv.setAdapter(adapter);
//        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                Intent intent = new Intent(Practice_select.this, PracticeMain.class);
//            }
//        });
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(Practice_select.this,PracticeMain.class);
                Toast.makeText(Practice_select.this,"click",Toast.LENGTH_SHORT).show();
                intent.putExtra("id", list.get(position).getId()+"");
                Log.d("id", list.get(position).getId() + "");
                intent.putExtra("picture", list.get(position).getPicturePath());
                intent.putExtra("example", list.get(position).getExamplePath());
                intent.putExtra("explain",list.get(position).getExplainPath());
                downloadVoice(list.get(position).getId(),intent);
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

    private void downloadVoice(final int id, final Intent intent){
        String path = SharePreferenceUtils.getString(Practice_select.this,"VoiceFilePath",null);
        final String filePath = path+"/"+id+".mp3";
        Log.d("VoiceFilePath",filePath);
        final File file = new File(filePath);
        intent.putExtra("filePath",filePath);
        if (!file.exists()){
            String gender = SharePreferenceUtils.getString(Practice_select.this,"gender",null);
            String downloadUrl = Global.VOICEDOWNLOADPATH;
            if (gender.equals("0")){
                downloadUrl=downloadUrl+"/male/"+id+".mp3";
            } else {
                downloadUrl=downloadUrl+"/female/"+id+".mp3";
            }
            HttpUtils httpUtils = new HttpUtils();
            httpUtils.download(downloadUrl, filePath, true, true, new RequestCallBack<File>() {
                @Override
                public void onSuccess(ResponseInfo<File> responseInfo) {
                    Toast.makeText(Practice_select.this,"success",Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onFailure(HttpException e, String s) {

                }
            });
        }

    }
}
