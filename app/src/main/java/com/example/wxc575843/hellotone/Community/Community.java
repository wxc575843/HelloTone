package com.example.wxc575843.hellotone.Community;


import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.media.Image;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.wxc575843.hellotone.R;
import com.example.wxc575843.hellotone.utils.SharePreferenceUtils;
import com.example.wxc575843.hellotone.Community.RefreshListView;
import com.example.wxc575843.hellotone.Community.RefreshListView.RefreshListener;
import com.github.mikephil.charting.charts.LineChart;
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
import com.model.Comment;
import com.model.CommunityNews;
import com.model.Global;
import com.viewpagerindicator.CirclePageIndicator;

import java.util.ArrayList;
import java.util.List;



/**
 * A simple {@link Fragment} subclass.
 */
public class Community extends Fragment {

    List<ImageView> mImages;
    List<CommunityNews> communityNewses = new ArrayList<CommunityNews>();
    List<CommunityNews> communityTopNewses;
    CirclePageIndicator mIndicator;

    RefreshListView lvNews;
    NewAdapter newAdapter;
    TopNewsAdapter newTopAdapter;
    HorizontalScrollViewPager mViewPager;
    View headerView;

    public Community() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_community, container, false);
        lvNews = (RefreshListView) view.findViewById(R.id.community_lv_news);
        initViews();
        return view;
    }

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        // init List data
        newAdapter = new NewAdapter();
        newTopAdapter = new TopNewsAdapter();
        getNewsData();
    }

    private void initViews(){
        initViewPager();
        initListView();
    }


    private void initListView(){
        // set adapter
        lvNews.addHeaderView(headerView);
        lvNews.setAdapter(newAdapter);
        lvNews.setOnRefreshListener(new RefreshListener() {

            @Override
            public void onRefresh() {
                getNewsData();
            }

            @Override
            public void onLoadMore() {
                getMoreDataFromNet();
            }
        });
        lvNews.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.d("itemClick", "Item点击:" + position);
                if (communityNewses != null) {
                    CommunityNews news = communityNewses.get(position);
                    Log.d("itemClick", "新闻id=" + news.getId() + "; 新闻标题=" + news.getTitle());

                    // 35314,35315,35316
                    String ids = SharePreferenceUtils.getString(getActivity(),
                            "news_read_id", "");

                    if (!ids.contains(news.getId() + "")) {// 只有在不包含该id时才添加
                        ids = ids + news.getId() + ",";
                        SharePreferenceUtils.putString(getActivity(), "news_read_id", ids);// 更新已读id列表
                    }

                    // mNewsAdapter.notifyDataSetChanged();// 刷新ListView
                    newAdapter.changeTextColor(view);// 局部刷新ListView

                    Intent intent = new Intent(getActivity(), CommunityNewsDetail.class);
                    intent.putExtra("news_url", news.getContent());
                    intent.putExtra("id", news.getId() + "");
                    intent.putExtra("title",news.getTitle());
                    Log.d("before", news.getId() + "");
                    startActivity(intent);
                }
            }
        });
    }

    private void getMoreDataFromNet(){
        HttpUtils httpUtils = new HttpUtils();
        RequestParams params = new RequestParams();
        params.addBodyParameter("num",communityNewses.size()+5+"");
        httpUtils.send(HttpRequest.HttpMethod.POST, Global.COMMUNITYMORENEWS, params, new RequestCallBack<String>() {
            @Override
            public void onSuccess(ResponseInfo<String> responseInfo) {
                Gson gson = new Gson();
                communityNewses = gson.fromJson(responseInfo.result,new TypeToken<List<CommunityNews>>(){}.getType());
                newAdapter.notifyDataSetChanged();
                lvNews.onRefreshComplete(false);
            }

            @Override
            public void onFailure(HttpException e, String s) {

            }
        });
    }

    private void getNewsData(){
        HttpUtils httpUtils = new HttpUtils();
        //Log.d("url",Global.COMMUNITYNEWSSERVLET);
        httpUtils.send(HttpRequest.HttpMethod.GET, Global.COMMUNITYNEWSSERVLET, new RequestCallBack<String>() {
            @Override
            public void onSuccess(ResponseInfo<String> responseInfo) {
                String result = responseInfo.result;
                Gson gson = new Gson();
                communityNewses = gson.fromJson(result, new TypeToken<List<CommunityNews>>() {
                }.getType());
                Log.d("result", result);
                newAdapter.notifyDataSetChanged();
                lvNews.onRefreshComplete(true);
            }

            @Override
            public void onFailure(HttpException e, String s) {
                Toast.makeText(getActivity(), "network failed", Toast.LENGTH_SHORT).show();
                lvNews.onRefreshComplete(true);

            }
        });
    }

    private void initViewPager(){
        // init
        headerView = View.inflate(getActivity(),
                R.layout.top_news_header_view, null);
        mIndicator = (CirclePageIndicator) headerView.findViewById(R.id.cpi_tab_detail);
        mViewPager = (HorizontalScrollViewPager) headerView.findViewById(R.id.vp_tab_detail);
        communityTopNewses = new ArrayList<CommunityNews>();

        //init data
        HttpUtils utils = new HttpUtils();
        //Log.d("url",Global.COMMUNITYTOPNEWSSERVLET);
        utils.send(HttpRequest.HttpMethod.GET, Global.COMMUNITYTOPNEWSSERVLET, new RequestCallBack<String>() {

            @Override
            public void onSuccess(ResponseInfo<String> responseInfo) {
                Gson gson = new Gson();
                communityTopNewses = gson.fromJson(responseInfo.result, new TypeToken<List<CommunityNews>>() {
                }.getType());
                newTopAdapter.notifyDataSetChanged();
                //Log.d("result",responseInfo.result);
            }

            @Override
            public void onFailure(HttpException e, String s) {

            }
        });
        
        // set adapter
        mViewPager.setAdapter(newTopAdapter);
        mIndicator.setViewPager(mViewPager);
        mIndicator.setSnap(true);
    }


    class NewAdapter extends BaseAdapter{

        BitmapUtils bitmapUtils;

        public NewAdapter() {
            bitmapUtils = new BitmapUtils(getActivity());
            bitmapUtils.configDefaultLoadingImage(R.mipmap.ic_launcher);
        }

        @Override
        public int getCount() {
            //Log.d("getCount", communityNewses.size() + "");
            if (communityNewses==null){
                Toast.makeText(getActivity(), "network failed please retry", Toast.LENGTH_SHORT).show();
                return 0;
            }
            return communityNewses.size();
        }

        @Override
        public CommunityNews getItem(int position) {
            return communityNewses.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder = new ViewHolder();
            if (convertView == null){
                convertView = View.inflate(getActivity(),R.layout.community_news_items,null);

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

            String ids = SharePreferenceUtils.getString(getActivity(),
                    "news_read_id", "");

            if (ids.contains(getItem(position).getId()+"")) {// 如果再已读列表中
                holder.tvTitle.setTextColor(Color.GRAY);
            } else {
                holder.tvTitle.setTextColor(Color.BLACK);
            }

            return convertView;
        }

        public void changeTextColor(View view) {
            TextView tvTitle = (TextView) view.findViewById(R.id.community_news_title);
            tvTitle.setTextColor(Color.GRAY);
        }
    }

    class ViewHolder {
        public ImageView ivIcon;
        public TextView tvTitle;
        public TextView tvAuthor;
    }

    class TopNewsAdapter extends PagerAdapter{

        private BitmapUtils utils;

        public TopNewsAdapter() {
            utils = new BitmapUtils(getActivity());
            utils.configDefaultBitmapConfig(Bitmap.Config.ARGB_4444);
            utils.configDefaultLoadingImage(R.drawable.topnews_item_default);
        }

        @Override
        public int getCount() {
            //Log.d("TopNewCount",communityTopNewses.size()+"");
            if (communityTopNewses==null){
                return 0;
            }
            return communityTopNewses.size();
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override
        public Object instantiateItem(ViewGroup container, final int position) {
            ImageView image = new ImageView(getActivity());
            image.setScaleType(ImageView.ScaleType.FIT_XY);// 设置图片展现样式为:
            // 宽高填充ImageView(图片可能被拉伸或者缩放)
            image.setImageResource(R.drawable.topnews_item_default);
            container.addView(image);

            utils.display(image, Global.HTTPIP + communityTopNewses.get(position).getPicUrl());// 参1表示ImageView对象,
            image.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    CommunityNews news = communityTopNewses.get(position);
                    Intent intent = new Intent(getActivity(), CommunityNewsDetail.class);
                    intent.putExtra("news_url", news.getContent());
                    intent.putExtra("id", news.getId() + "");
                    intent.putExtra("title",news.getTitle()+"");
//                    Log.d("title+community",news.getTitle());
                    Log.d("before", news.getId() + "");
                    startActivity(intent);
                }
            });
            // 参2表示图片url
            //image.setOnTouchListener(new TopNewsTouchListener());
            return image;

        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((ImageView) object);
        }
    }
}
