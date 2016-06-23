package com.example.wxc575843.hellotone.Community;

import android.annotation.SuppressLint;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.webkit.WebSettings.TextSize;
import android.widget.Toast;

import com.example.wxc575843.hellotone.R;
import com.example.wxc575843.hellotone.utils.SharePreferenceUtils;
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

@SuppressWarnings("deprecation")
public class CommunityNewsDetail extends AppCompatActivity implements View.OnClickListener {

    protected static final String TAG = CommunityNewsDetail.class
            .getSimpleName();

    @ViewInject(R.id.btn_text_size)
    private ImageButton btnTextSize;

//    @ViewInject(R.id.btn_share)
//    private ImageButton btnShare;

    @ViewInject(R.id.btn_news_to_comment)
    private Button btn2Comment;

    @ViewInject(R.id.btn_back)
    private ImageButton btnBack;

    @ViewInject(R.id.btn_menu)
    private ImageButton btnMenu;

    @ViewInject(R.id.ll_control)
    private LinearLayout llControl;

    @ViewInject(R.id.wv_webview)
    private WebView mWebView;

    @ViewInject(R.id.pb_news_detail)
    private ProgressBar mProgress;

    @ViewInject(R.id.likebtn_commnitynews_detail)
    private LikeButton likeButton;

    @ViewInject(R.id.favourite_btn_commnitynews_detail)
    private LikeButton btnFavourite;

//    private ImageButton btnFavourite;

    private String mUrl;
    private String title;

    private int mCurrentSizeIndex = -1;// 当前选择的字体
    private int mSelectedSizeIndex = 2;// 当前已选的字体, 默认是正常字体, 值为2
    private String id="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_community_news_detail);
        ViewUtils.inject(this);
        mUrl = getIntent().getStringExtra("news_url");
        id = getIntent().getStringExtra("id");
        title = getIntent().getStringExtra("title");
        Log.d("url",mUrl);
        Log.d("title",title);
        Log.d("begin",id);
//        btnFavourite = (ImageButton) findViewById(R.id.btn_like);
        initView();
    }

    @SuppressLint("SetJavaScriptEnabled")
    private void initView() {
        llControl.setVisibility(View.VISIBLE);// 显示字体大小和分享按钮
        btnBack.setVisibility(View.VISIBLE);
        btnMenu.setVisibility(View.GONE);

        btnTextSize.setOnClickListener(this);
//        btnShare.setOnClickListener(this);
        btnBack.setOnClickListener(this);

        if (!TextUtils.isEmpty(mUrl)) {
            WebSettings settings = mWebView.getSettings();

            settings.setJavaScriptEnabled(true);// 打开js功能
            settings.setBuiltInZoomControls(true);// 显示放大缩小的按钮
            settings.setUseWideViewPort(true);// 双击缩放

            // mWebView.loadUrl("http://www.itcast.cn");
            mWebView.setWebViewClient(new WebViewClient() {

                // 监听网页加载结束的事件
                @Override
                public void onPageFinished(WebView view, String url) {
                    mProgress.setVisibility(View.GONE);
                }
            });

            mWebView.loadUrl(mUrl);
        }


        initFavouriteBtn();




        btn2Comment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CommunityNewsDetail.this, CommentsActivity.class);
                intent.putExtra("title", title);
                intent.putExtra("id", id + "");
                Log.d("title", title);
                startActivity(intent);
            }
        });

        initLikeBtn();


    }

    private void initFavouriteBtn() {

        HttpUtils utils = new HttpUtils();
        RequestParams params = new RequestParams();
        params.addBodyParameter("state","view");
        params.addBodyParameter("userId",SharePreferenceUtils.getString(CommunityNewsDetail.this,"id",null));
        params.addBodyParameter("id",id+"");
        utils.send(HttpRequest.HttpMethod.POST, Global.FAVOURITEARTICLESERVLET, params, new RequestCallBack<String>() {
            @Override
            public void onSuccess(ResponseInfo<String> responseInfo) {
                if (responseInfo.result.equals("yes")) {
                    btnFavourite.setLiked(true);

                } else {
                    btnFavourite.setLiked(false);
                }
            }

            @Override
            public void onFailure(HttpException e, String s) {

            }
        });

        btnFavourite.setOnLikeListener(new OnLikeListener() {
            @Override
            public void liked(LikeButton likeButton) {
                HttpUtils httpUtils = new HttpUtils();
                RequestParams params = new RequestParams();
                params.addBodyParameter("id", id);
                params.addBodyParameter("state", "favourite");
                params.addBodyParameter("userId", SharePreferenceUtils.getString(CommunityNewsDetail.this, "id", null));
                httpUtils.send(HttpRequest.HttpMethod.POST, Global.FAVOURITEARTICLESERVLET, params, new RequestCallBack<String>() {
                    @Override
                    public void onSuccess(ResponseInfo<String> responseInfo) {
                        int num = SharePreferenceUtils.getInt(CommunityNewsDetail.this,"articleNum",0);
                        SharePreferenceUtils.putInt(CommunityNewsDetail.this,"articleNum",num+1);
//                        if (responseInfo.result.equals("success")) {
//                            Toast.makeText(CommunityNewsDetail.this, "收藏成功", Toast.LENGTH_SHORT).show();
//                            int num = SharePreferenceUtils.getInt(CommunityNewsDetail.this, "articleNum", 0);
//                            SharePreferenceUtils.putInt(CommunityNewsDetail.this, "articleNum", num + 1);
//                        } else {
//                            Toast.makeText(CommunityNewsDetail.this, "已收藏", Toast.LENGTH_SHORT).show();
//                        }
                    }

                    @Override
                    public void onFailure(HttpException e, String s) {
                        Toast.makeText(CommunityNewsDetail.this, R.string.register_failed_network, Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void unLiked(LikeButton likeButton) {
                HttpUtils httpUtils = new HttpUtils();
                RequestParams params = new RequestParams();
                params.addBodyParameter("id", id);
                Log.d("2333", id);
                params.addBodyParameter("state", "unfavourite");
                Log.d("2333", Global.FAVOURITEARTICLESERVLET);
                params.addBodyParameter("userId", SharePreferenceUtils.getString(CommunityNewsDetail.this, "id", null));
                httpUtils.send(HttpRequest.HttpMethod.POST, Global.FAVOURITEARTICLESERVLET, params, new RequestCallBack<String>() {
                    @Override
                    public void onSuccess(ResponseInfo<String> responseInfo) {
                        int num = SharePreferenceUtils.getInt(CommunityNewsDetail.this,"articleNum",0);
                        SharePreferenceUtils.putInt(CommunityNewsDetail.this,"articleNum",num-1);
//                        if (responseInfo.result.equals("success")) {
//                            Toast.makeText(CommunityNewsDetail.this, "收藏成功", Toast.LENGTH_SHORT).show();
//                            int num = SharePreferenceUtils.getInt(CommunityNewsDetail.this, "articleNum", 0);
//                            SharePreferenceUtils.putInt(CommunityNewsDetail.this, "articleNum", num + 1);
//                        } else {
//                            Toast.makeText(CommunityNewsDetail.this, "已收藏", Toast.LENGTH_SHORT).show();
//                        }
                    }

                    @Override
                    public void onFailure(HttpException e, String s) {
                        Toast.makeText(CommunityNewsDetail.this, R.string.register_failed_network, Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_back:
                finish();
                break;
            case R.id.btn_text_size:
                showChangeSizeDialog();
                break;
//            case R.id.btn_share:
////                showShare();
//                break;

            default:
                break;
        }
    }

    /**
     * 展示修改字体的对话框
     */
    private void showChangeSizeDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        String[] items = new String[] { "超大号字体", "大号字体", "正常字体", "小号字体",
                "超小号字体" };

        // 设置单选对话框
        builder.setSingleChoiceItems(items, mSelectedSizeIndex,
                new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Log.d(TAG, "选中:" + which);
                        mCurrentSizeIndex = which;
                    }
                });

        builder.setTitle("字体设置");// 设置标题
        builder.setPositiveButton("确定", new AlertDialog.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (mCurrentSizeIndex) {
                    case 0:
                        mWebView.getSettings().setTextSize(WebSettings.TextSize.LARGEST);// 设置WebView中字体的大小
                        break;
                    case 1:
                        mWebView.getSettings().setTextSize(WebSettings.TextSize.LARGER);
                        break;
                    case 2:
                        mWebView.getSettings().setTextSize(WebSettings.TextSize.NORMAL);
                        break;
                    case 3:
                        mWebView.getSettings().setTextSize(TextSize.SMALLER);
                        break;
                    case 4:
                        mWebView.getSettings().setTextSize(TextSize.SMALLEST);
                        break;

                    default:
                        break;
                }

                mSelectedSizeIndex = mCurrentSizeIndex;
            }
        });

        builder.setNegativeButton("取消", null);
        builder.show();
    }

//    /**
//     * 一键分享
//     */
//    private void showShare() {
//        ShareSDK.initSDK(this);
//        OnekeyShare oks = new OnekeyShare();
//        oks.setTheme(OnekeyShareTheme.SKYBLUE);//设置主题样式
//        // 关闭sso授权
//        oks.disableSSOWhenAuthorize();//单点登录需要签名验证, 所以不要轻易打开
//
//        // 分享时Notification的图标和文字
//        oks.setNotification(R.drawable.icon_150,
//                getString(R.string.app_name));
//        // title标题，印象笔记、邮箱、信息、微信、人人网和QQ空间使用
//        oks.setTitle(getString(R.string.share));
//        // titleUrl是标题的网络链接，仅在人人网和QQ空间使用
//        oks.setTitleUrl("http://sharesdk.cn");
//        // text是分享文本，所有平台都需要这个字段
//        oks.setText("我是分享文本");
//        // imagePath是图片的本地路径，Linked-In以外的平台都支持此参数
//        oks.setImagePath("/sdcard/test.jpg");// 确保SDcard下面存在此张图片
//        // url仅在微信（包括好友和朋友圈）中使用
//        oks.setUrl("http://sharesdk.cn");
//        // comment是我对这条分享的评论，仅在人人网和QQ空间使用
//        oks.setComment("我是测试评论文本");
//        // site是分享此内容的网站名称，仅在QQ空间使用
//        oks.setSite(getString(R.string.app_name));
//        // siteUrl是分享此内容的网站地址，仅在QQ空间使用
//        oks.setSiteUrl("http://sharesdk.cn");
//
//        // 启动分享GUI
//        oks.show(this);
//    }


    private void initLikeBtn(){

        HttpUtils utils = new HttpUtils();
        RequestParams params = new RequestParams();
        params.addBodyParameter("isLike","view");
        params.addBodyParameter("userId",SharePreferenceUtils.getString(CommunityNewsDetail.this,"id",null));
        params.addBodyParameter("articleId",id+"");
        utils.send(HttpRequest.HttpMethod.POST, Global.LIKEARTICLESERVLET, params, new RequestCallBack<String>() {
            @Override
            public void onSuccess(ResponseInfo<String> responseInfo) {
                if (responseInfo.result.equals("yes")) {
                    likeButton.setLiked(true);
                } else {
                    likeButton.setLiked(false);
                }
            }

            @Override
            public void onFailure(HttpException e, String s) {

            }
        });

        likeButton.setOnLikeListener(new OnLikeListener() {
            @Override
            public void liked(LikeButton likeButton) {
                HttpUtils utils = new HttpUtils();
                RequestParams params = new RequestParams();
                params.addBodyParameter("isLike", "like");
                params.addBodyParameter("userId", SharePreferenceUtils.getString(CommunityNewsDetail.this, "id", null));
                params.addBodyParameter("articleId", id + "");
                utils.send(HttpRequest.HttpMethod.POST, Global.LIKEARTICLESERVLET, params, new RequestCallBack<String>() {
                    @Override
                    public void onSuccess(ResponseInfo<String> responseInfo) {

                    }

                    @Override
                    public void onFailure(HttpException e, String s) {

                    }
                });
            }

            @Override
            public void unLiked(LikeButton likeButton) {
                HttpUtils utils = new HttpUtils();
                RequestParams params = new RequestParams();
                params.addBodyParameter("isLike", "unlike");
                params.addBodyParameter("userId", SharePreferenceUtils.getString(CommunityNewsDetail.this, "id", null));
                params.addBodyParameter("articleId", id + "");
                utils.send(HttpRequest.HttpMethod.POST, Global.LIKEARTICLESERVLET, params, new RequestCallBack<String>() {
                    @Override
                    public void onSuccess(ResponseInfo<String> responseInfo) {

                    }

                    @Override
                    public void onFailure(HttpException e, String s) {

                    }
                });
            }
        });
    }
}
