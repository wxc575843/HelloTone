package com.example.wxc575843.hellotone.Community;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

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
import com.model.Comment;
import com.model.Global;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class CommentsActivity extends AppCompatActivity {

    List<Comment> comments;
    String id;

    @ViewInject(R.id.comment_title)
    TextView tvTitle;

    @ViewInject(R.id.et_comment)
    EditText etComment;

    @ViewInject(R.id.comment_list)
    ListView lvComments;

    CommentAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comments);
        ViewUtils.inject(this);
        comments = new ArrayList<Comment>();
        adapter = new CommentAdapter();
        initData();
        String title = getIntent().getStringExtra("title");
        Log.d("title", title);
        tvTitle.setText(title);
        clickComment();
        Log.d("afterclick","yes");
        lvComments.setAdapter(adapter);
    }

    private void initData(){
        id = getIntent().getStringExtra("id");
        HttpUtils httpUtils = new HttpUtils();
        RequestParams params = new RequestParams();
        params.addBodyParameter("id",id);
        httpUtils.send(HttpRequest.HttpMethod.POST, Global.GETCOMMENTLISTSERVLET, params, new RequestCallBack<String >() {
            @Override
            public void onSuccess(ResponseInfo<String> responseInfo) {
                Gson gson = new Gson();
                comments = gson.fromJson(responseInfo.result,new TypeToken<List<Comment>>(){}.getType());
                Log.d("comment",responseInfo.result);
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onFailure(HttpException e, String s) {
                Toast.makeText(CommentsActivity.this,R.string.register_failed_network,Toast.LENGTH_SHORT);
            }
        });
    }

    class CommentAdapter extends BaseAdapter{

        BitmapUtils bitmapUtils;

        public CommentAdapter(){
            bitmapUtils = new BitmapUtils(CommentsActivity.this);
        }

        @Override
        public int getCount() {
            return comments.size();
        }

        @Override
        public Comment getItem(int position) {
            return comments.get(position);
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder = new ViewHolder();
            if (convertView == null){
                convertView = View.inflate(CommentsActivity.this,R.layout.comment_item,null);

                holder.tvAuthor = (TextView) convertView.findViewById(R.id.tv_comment_author);
                holder.tvContent = (TextView) convertView.findViewById(R.id.tv_comment_content);
                holder.tvDate = (TextView) convertView.findViewById(R.id.tv_comment_date);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            holder.tvAuthor.setText(getItem(position).getNickname());
            holder.tvDate.setText(getItem(position).getDate());
            holder.tvContent.setText(getItem(position).getContent());

            return convertView;
        }
    }

    class ViewHolder {
        public TextView tvContent;
        public TextView tvDate;
        public TextView tvAuthor;
    }

    private void clickComment(){
        etComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(CommentsActivity.this);
                builder.setIcon(R.drawable.ic_menu_manage);
                builder.setTitle("请输入评论信息");
                //    通过LayoutInflater来加载一个xml的布局文件作为一个View对象
                View view = LayoutInflater.from(CommentsActivity.this).inflate(R.layout.dialog, null);

                //    设置我们自己定义的布局文件作为弹出框的Content
                builder.setView(view);
                final EditText username = (EditText) view.findViewById(R.id.nickname);
                username.setHint("请输入评论内容");

                builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String str = username.getText().toString().trim();
                        Log.d("Commnet click",str);
                        HttpUtils httpUtils = new HttpUtils();
                        RequestParams params = new RequestParams();
                        params.addBodyParameter("userId", SharePreferenceUtils.getString(CommentsActivity.this,"id",null));
                        params.addBodyParameter("articleId",id);
                        params.addBodyParameter("comment",str);
                        Log.d("servlet",Global.ADDCOMMENTSERVLET);
                        httpUtils.send(HttpRequest.HttpMethod.POST, Global.ADDCOMMENTSERVLET, params, new RequestCallBack<String>() {
                            @Override
                            public void onSuccess(ResponseInfo<String> responseInfo) {
                                if (responseInfo.result.equals("success")){
                                    Toast.makeText(CommentsActivity.this,"评论成功",Toast.LENGTH_SHORT).show();
                                    Comment comment = new Comment();
                                    comment.setContent(username.getText().toString().trim());
                                    SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");//设置日期格式
                                    System.out.println(df.format(new Date()));// new Date()为获取当前系统时间
                                    comment.setDate(df.format(new Date()).toString());
                                    comment.setNickname(SharePreferenceUtils.getString(CommentsActivity.this,"nickname",null));
                                    comments.add(comment);
                                    adapter.notifyDataSetChanged();
                                } else {
                                    Toast.makeText(CommentsActivity.this,R.string.register_failed_network,Toast.LENGTH_SHORT);
                                }
                            }

                            @Override
                            public void onFailure(HttpException e, String s) {
                                Toast.makeText(CommentsActivity.this,R.string.register_failed_network,Toast.LENGTH_SHORT);
                            }
                        });
                    }
                });
                builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
                builder.show();
            }
        });
    }
}
