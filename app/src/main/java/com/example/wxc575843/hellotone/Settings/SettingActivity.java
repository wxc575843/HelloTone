package com.example.wxc575843.hellotone.Settings;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import com.example.wxc575843.hellotone.R;
import com.example.wxc575843.hellotone.utils.SharePreferenceUtils;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;

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
    @ViewInject(R.id.setting_postNum)
    TextView tvPostNum;
    @ViewInject(R.id.setting_experience)
    TextView tvExperience;

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
        tvPostNum.setText(postNum+"");
        experience = SharePreferenceUtils.getInt(SettingActivity.this,"experience",0);
        tvExperience.setText(experience+"");
    }

}
