package com.example.wxc575843.hellotone.Settings;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
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
import com.model.Global;

import java.util.Set;

import be.tarsos.dsp.pitch.Goertzel;

public class SettingActivity extends AppCompatActivity {

    private String email;
    private String nickName;
    private String gender;
    private String country;
    private String chineseLevel;
    private int level;
    private int articleNum;
    private int postNum;
    private int experience;
    private String a;

    @ViewInject(R.id.setting_nickname)
    TextView tvNickName;
    @ViewInject(R.id.setting_email)
    TextView tvEmail;
    @ViewInject(R.id.setting_country)
    TextView tvCountry;
    @ViewInject(R.id.setting_gender)
    TextView tvGender;
    @ViewInject(R.id.setting_chineseLevel)
    TextView tvChineseLevel;
    @ViewInject(R.id.setting_level)
    TextView tvLevel;
    @ViewInject(R.id.setting_articleNum)
    TextView tvArticleNum;
//    @ViewInject(R.id.setting_postNum)
//    TextView tvPostNum;
    @ViewInject(R.id.setting_experience)
    TextView tvExperience;
    @ViewInject(R.id.modify_password)
    Button btnModifyPassword;
    @ViewInject(R.id.rv_like_article)
    RelativeLayout rvLike;
    @ViewInject(R.id.rv_like_record)
    RelativeLayout rvRecord;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        ViewUtils.inject(this);

        email = SharePreferenceUtils.getString(SettingActivity.this,"email",null);
        tvEmail.setText(email);
        nickName = SharePreferenceUtils.getString(SettingActivity.this,"nickname",null);
        tvNickName.setText(nickName);
        gender = SharePreferenceUtils.getString(SettingActivity.this,"gender",null);
        tvGender.setText(gender);

        country = SharePreferenceUtils.getString(SettingActivity.this,"country",null);
        tvCountry.setText(country);
        chineseLevel = SharePreferenceUtils.getString(SettingActivity.this,"chineseLevel",null);
        tvChineseLevel.setText(chineseLevel);
        level = SharePreferenceUtils.getInt(SettingActivity.this, "level", 0);
        tvLevel.setText(level+"");

        articleNum = SharePreferenceUtils.getInt(SettingActivity.this, "articleNum", 0);
        tvArticleNum.setText(articleNum+"");
        postNum = SharePreferenceUtils.getInt(SettingActivity.this,"postNum",0);
//        tvPostNum.setText(postNum+"");
        experience = SharePreferenceUtils.getInt(SettingActivity.this,"experience",0);
        tvExperience.setText(experience+"");

        initClick();
    }

    private void initClick(){
        tvNickName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(SettingActivity.this);
                builder.setIcon(R.drawable.ic_menu_manage);
                builder.setTitle("请输入用户名和密码");
                //    通过LayoutInflater来加载一个xml的布局文件作为一个View对象
                View view = LayoutInflater.from(SettingActivity.this).inflate(R.layout.dialog, null);
                //    设置我们自己定义的布局文件作为弹出框的Content
                builder.setView(view);

                final EditText username = (EditText) view.findViewById(R.id.nickname);

                builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        a = username.getText().toString().trim();
                        HttpUtils httpUtils = new HttpUtils();
                        RequestParams requestParams = new RequestParams();
                        requestParams.addBodyParameter("nickname",a);
                        requestParams.addBodyParameter("email",tvEmail.getText().toString());
                        httpUtils.send(HttpRequest.HttpMethod.POST, Global.NICKNAMEMODIFYSERVLET, requestParams, new RequestCallBack<String>() {
                            @Override
                            public void onSuccess(ResponseInfo<String> responseInfo) {
                                tvNickName.setText(a);
                                Toast.makeText(SettingActivity.this,"更新成功",Toast.LENGTH_SHORT).show();
                            }

                            @Override
                            public void onFailure(HttpException e, String s) {
                                Toast.makeText(SettingActivity.this,R.string.register_failed_network,Toast.LENGTH_SHORT).show();
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

        btnModifyPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SettingActivity.this, ModifyPassword.class);
                startActivityForResult(intent, 1);
            }
        });

        rvLike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SettingActivity.this, LikeArticle.class);
                startActivity(intent);
            }
        });

        rvRecord.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SettingActivity.this, LikeRecord.class);
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode , Intent data) {
        String result = data.getExtras().getString("result");//得到新Activity 关闭后返回的数据
        if (result.equals("success")){
            this.finish();
        }
    }

}
