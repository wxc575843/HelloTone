package com.example.wxc575843.hellotone.Community;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.wxc575843.hellotone.R;
import com.example.wxc575843.hellotone.utils.SharePreferenceUtils;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.lidroid.xutils.BitmapUtils;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest;
import com.model.CommunityNews;
import com.model.Global;

import org.apache.http.message.HeaderGroup;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import be.tarsos.dsp.pitch.Goertzel;

/**
 * Created by wxc575843 on 6/11/16.
 */
public class CommunityTest extends AppCompatActivity {

    List<CommunityNews> communityNewses = new ArrayList<CommunityNews>();
    ListView lvNews;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_community);

        lvNews = (ListView) findViewById(R.id.community_lv_news);
        initData();
        lvNews.setAdapter(new NewsAdapter());
    }

    class NewsAdapter extends BaseAdapter{

        BitmapUtils bitmapUtils;

        public NewsAdapter() {
            bitmapUtils = new BitmapUtils(CommunityTest.this);
            bitmapUtils.configDefaultLoadingImage(R.mipmap.ic_launcher);
        }

        @Override
        public int getCount() {
            return communityNewses.size();
        }

        @Override
        public CommunityNews getItem(int position) {
            return communityNewses.get(position);
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder = new ViewHolder();
            if (convertView == null){
                convertView = View.inflate(CommunityTest.this,R.layout.community_news_items,null);

                holder.ivIcon = (ImageView) convertView.findViewById(R.id.community_news_pic);
                holder.tvTitle = (TextView) convertView.findViewById(R.id.community_news_title);
                holder.tvAuthor = (TextView) convertView.findViewById(R.id.community_news_author);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            holder.tvAuthor.setText(getItem(position).getAuthor());
            holder.tvTitle.setText(getItem(position).getTitle());

            bitmapUtils.display(holder.ivIcon,Global.HTTPIP + getItem(position).getPicUrl());

            String ids = SharePreferenceUtils.getString(CommunityTest.this,
                    "news_read_id", "");

            if (ids.contains(getItem(position).getId()+"")) {// 如果再已读列表中
                holder.tvTitle.setTextColor(Color.GRAY);
            } else {
                holder.tvTitle.setTextColor(Color.BLACK);
            }

            return convertView;
        }
    }

    class ViewHolder {
        public ImageView ivIcon;
        public TextView tvTitle;
        public TextView tvAuthor;
    }

    public void initData(){
        HttpUtils httpUtils = new HttpUtils();
        httpUtils.send(HttpRequest.HttpMethod.GET, Global.COMMUNITYNEWSSERVLET, new RequestCallBack<String>() {
            @Override
            public void onSuccess(ResponseInfo<String> responseInfo) {
                Gson gson = new Gson();
                communityNewses = gson.fromJson(responseInfo.result, new TypeToken<List<CommunityNews>>(){}.getType());
            }

            @Override
            public void onFailure(HttpException e, String s) {

            }
        });
    }
}
