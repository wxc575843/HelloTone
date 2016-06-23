package com.example.wxc575843.hellotone.Settings;

import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.wxc575843.hellotone.Community.Community;
import com.example.wxc575843.hellotone.Community.CommunityNewsDetail;
import com.example.wxc575843.hellotone.R;
import com.example.wxc575843.hellotone.utils.SharePreferenceUtils;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.lidroid.xutils.BitmapUtils;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.model.CommunityNews;
import com.model.Global;

import java.util.ArrayList;
import java.util.List;

public class LikeArticle extends AppCompatActivity {

    @ViewInject(R.id.lv_like_article)
    ListView lvLikeArticle;

    List<CommunityNews> list = new ArrayList<CommunityNews>();
    LikeAdpter likeAdpter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_like_article);

        likeAdpter = new LikeAdpter();
        ViewUtils.inject(this);

        HttpUtils httpUtils = new HttpUtils();
        RequestParams params = new RequestParams();
        String tmpUserId = SharePreferenceUtils.getString(LikeArticle.this, "id", null);
        params.addBodyParameter("userId", tmpUserId);
        Log.d("userId", tmpUserId);
        httpUtils.send(HttpRequest.HttpMethod.POST, Global.LIKEARTICLELISTSERVLET, params, new RequestCallBack<String>() {
            @Override
            public void onSuccess(ResponseInfo<String> responseInfo) {
                Gson gson = new Gson();
                list = gson.fromJson(responseInfo.result, new TypeToken<List<CommunityNews>>() {
                }.getType());
                likeAdpter.notifyDataSetChanged();
                Log.d("LikeList", list.size() + "");
                Log.d("LikeList", responseInfo.result);
            }

            @Override
            public void onFailure(HttpException e, String s) {
                Toast.makeText(LikeArticle.this, R.string.register_failed_network, Toast.LENGTH_SHORT).show();
            }
        });
        lvLikeArticle.setAdapter(likeAdpter);
        lvLikeArticle.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.d("itemClick", "Item点击:" + position);
                if (list != null) {
                    CommunityNews news = list.get(position);
                    Log.d("itemClick", "新闻id=" + news.getId() + "; 新闻标题=" + news.getTitle());

//                    // 35314,35315,35316
//                    String ids = SharePreferenceUtils.getString(getActivity(),
//                            "news_read_id", "");

//                    if (!ids.contains(news.getId()+"")) {// 只有在不包含该id时才添加
//                        ids = ids + news.getId() + ",";
//                        SharePreferenceUtils.putString(getActivity(), "news_read_id", ids);// 更新已读id列表
//                    }

                    // mNewsAdapter.notifyDataSetChanged();// 刷新ListView
//                    newAdapter.changeTextColor(view);// 局部刷新ListView

                    Intent intent = new Intent(LikeArticle.this, CommunityNewsDetail.class);
                    intent.putExtra("news_url", news.getContent());
                    intent.putExtra("id",news.getId()+"");
                    Log.d("before",news.getId()+"");
                    startActivity(intent);
                }
            }
        });
    }

    class LikeAdpter extends BaseAdapter{

        BitmapUtils bitmapUtils;

        public LikeAdpter(){
            bitmapUtils = new BitmapUtils(LikeArticle.this);
            bitmapUtils.configDefaultLoadingImage(R.mipmap.ic_launcher);
        }

        @Override
        public int getCount() {
            Log.d("LikeArticle Count",list.size()+"");
            return list.size();
        }

        @Override
        public CommunityNews getItem(int position) {
            return list.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder = new ViewHolder();
            if (convertView == null){
                convertView = View.inflate(LikeArticle.this,R.layout.community_news_items,null);

                holder.ivIcon = (ImageView) convertView.findViewById(R.id.community_news_pic);
                holder.tvTitle = (TextView) convertView.findViewById(R.id.community_news_title);
                holder.tvAuthor = (TextView) convertView.findViewById(R.id.community_news_author);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            holder.tvAuthor.setText(getItem(position).getAuthor());
            holder.tvTitle.setText(getItem(position).getTitle());

            bitmapUtils.display(holder.ivIcon, Global.HTTPIP + getItem(position).getPicUrl());

            holder.tvTitle.setTextColor(Color.BLACK);
//            String ids = SharePreferenceUtils.getString(LikeArticle.this,
//                    "news_read_id", "");
//
//            if (ids.contains(getItem(position).getId()+"")) {// 如果再已读列表中
//                holder.tvTitle.setTextColor(Color.GRAY);
//            } else {
//                holder.tvTitle.setTextColor(Color.BLACK);
//            }

            return convertView;
        }
    }

    class ViewHolder {
        public ImageView ivIcon;
        public TextView tvTitle;
        public TextView tvAuthor;
    }
}
