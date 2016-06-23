package com.example.wxc575843.hellotone.start;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.wxc575843.hellotone.Practice.PracticeMain;
import com.example.wxc575843.hellotone.R;
import com.example.wxc575843.hellotone.utils.SharePreferenceUtils;
import com.google.gson.Gson;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.model.Global;
import com.model.User;

public class LoginActivity extends AppCompatActivity {

    private final String TAG = "LoginActivity";

    private String email;
    private String password;

    @ViewInject(R.id.login_email)
    EditText etMail;

    @ViewInject(R.id.login_password)
    EditText etPassword;

    @ViewInject(R.id.btnLogin)
    Button btnLogin;

    @ViewInject(R.id.link_to_register)
    TextView tvLink2Register;

    @ViewInject(R.id.develop)
    Button btnDevelop;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ViewUtils.inject(this);

        tvLink2Register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(intent);
                finish();
            }
        });

        btnLogin.setOnClickListener(loginLinster);

        btnDevelop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });
    }

    Button.OnClickListener loginLinster = new Button.OnClickListener() {
        @Override
        public void onClick(View v) {
            email = etMail.getText().toString().trim();
            password = etPassword.getText().toString();
            Log.d(TAG+"email",email);
            Log.d(TAG+"password",password);
            if (email.isEmpty() || password.isEmpty()){
                Toast.makeText(LoginActivity.this,R.string.register_failed_sth_empty,Toast.LENGTH_SHORT).show();
            }else {
                HttpUtils httpUtils = new HttpUtils();
                RequestParams requestParams = new RequestParams();
                requestParams.addBodyParameter("email",email);
                requestParams.addBodyParameter("password",password);
                httpUtils.send(HttpRequest.HttpMethod.POST, Global.LOGINSERVLET, requestParams, new RequestCallBack<String>() {
                    @Override
                    public void onSuccess(ResponseInfo<String> responseInfo) {
                        String rs = responseInfo.result;
                        Log.d(TAG,rs);
                        Gson gson = new Gson();
                        User user = gson.fromJson(rs, User.class);
                        Log.d(TAG,user.getStateCode()+"");
                        if (user.getStateCode()==1){
                            SharePreferenceUtils.putString(LoginActivity.this,"email",user.getEmail());
                            SharePreferenceUtils.putString(LoginActivity.this,"nickname",user.getNickName());
                            SharePreferenceUtils.putString(LoginActivity.this,"gender",user.getGender());
                            SharePreferenceUtils.putString(LoginActivity.this,"country",user.getCountry());
                            SharePreferenceUtils.putString(LoginActivity.this,"chineseLevel",user.getChineseLevel());
                            SharePreferenceUtils.putInt(LoginActivity.this, "level", user.getLevel());
                            SharePreferenceUtils.putString(LoginActivity.this, "headPicture", user.getHeadPicture());
                            SharePreferenceUtils.putInt(LoginActivity.this, "articleNum", user.getArticleNum());
                            SharePreferenceUtils.putInt(LoginActivity.this,"postNum",user.getPostNum());
                            SharePreferenceUtils.putInt(LoginActivity.this,"experience",user.getExperience());
                            SharePreferenceUtils.putString(LoginActivity.this, "password", password);
                            SharePreferenceUtils.putString(LoginActivity.this,"id",user.getId());

                            SharePreferenceUtils.putBoolean(LoginActivity.this,"loginState",true);

                            Log.d(TAG,user.getChineseLevel());

                            Intent intent = new Intent(LoginActivity.this,MainActivity.class);
                            startActivity(intent);
                            finish();
                        } else {
                            Toast.makeText(LoginActivity.this,R.string.login_wrong,Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(HttpException e, String s) {
                        Toast.makeText(LoginActivity.this,R.string.register_failed_network,Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }
    };
}