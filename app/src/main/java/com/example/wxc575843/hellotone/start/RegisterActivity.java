package com.example.wxc575843.hellotone.start;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.wxc575843.hellotone.R;
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

public class RegisterActivity extends AppCompatActivity {

    private final String TAG = "RegisterActivity";
    //0 for male, 1 for female
    private int GENDER = 0;
    String eMail = "";
    String password = "";
    String repeatPass = "";
    String nickName = "";

    @ViewInject(R.id.link_to_login)
    TextView tvLink2Login;

    @ViewInject(R.id.register_gender_male)
    RadioButton rdMale;

    @ViewInject(R.id.register_gender_female)
    RadioButton rdFemale;

    @ViewInject(R.id.register_gender_group)
    RadioGroup rgGender;

    @ViewInject(R.id.btn_register)
    Button btnRegister;

    @ViewInject(R.id.register_email)
    EditText etMail;

    @ViewInject(R.id.register_password)
    EditText etPassword;

    @ViewInject(R.id.register_repeat_password)
    EditText etRepeatPassword;

    @ViewInject(R.id.register_nickname)
    EditText etNickname;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        ViewUtils.inject(this);
        tvLink2Login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });

        rgGender.setOnCheckedChangeListener(ccLinster);
        rdMale.setChecked(true);
        btnRegister.setOnClickListener(rgLinster);
    }

    private Button.OnClickListener rgLinster = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            eMail = etMail.getText().toString().trim();
            password = etPassword.getText().toString();
            repeatPass = etRepeatPassword.getText().toString();
            nickName = etNickname.getText().toString().trim();

            if(eMail.isEmpty() || password.isEmpty() || repeatPass.isEmpty() || nickName.isEmpty()){
                Toast.makeText(RegisterActivity.this,R.string.register_failed_sth_empty,Toast.LENGTH_SHORT).show();
            }else {
                if(!password.equals(repeatPass)){
                    Toast.makeText(RegisterActivity.this,R.string.register_failed_password_diff,Toast.LENGTH_SHORT).show();
                }else {
                    RequestParams requestParams = new RequestParams();
                    requestParams.addBodyParameter("email",eMail);
                    requestParams.addBodyParameter("password",password);
                    requestParams.addBodyParameter("nickname",nickName);
                    requestParams.addBodyParameter("gender",GENDER+"");
                    HttpUtils httpUtils = new HttpUtils();
                    httpUtils.send(HttpRequest.HttpMethod.POST, Global.REGISTERSERVLET, requestParams,new RequestCallBack<String>() {
                        @Override
                        public void onSuccess(ResponseInfo<String> responseInfo) {
                            String rs = responseInfo.result;
                            Log.d(TAG,rs);
                            if(rs.equals("success")){
                                Toast.makeText(RegisterActivity.this,R.string.register_success,Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(RegisterActivity.this,LoginActivity.class);
                                startActivity(intent);
                                finish();
                            }else {
                                Toast.makeText(RegisterActivity.this,R.string.register_failed_already_exist,Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onFailure(HttpException e, String s) {
                            Toast.makeText(RegisterActivity.this,R.string.register_failed_network,Toast.LENGTH_SHORT).show();
                            Log.d(TAG,s);
                        }
                    });
                }
            }
        }
    };

    private RadioGroup.OnCheckedChangeListener ccLinster = new RadioGroup.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(RadioGroup group, int checkedId) {
            int id = group.getCheckedRadioButtonId();
            switch (id){
                case R.id.register_gender_male:
                    GENDER = 0;
                    break;
                case R.id.register_gender_female:
                    GENDER = 1;
                    break;
                default:
                    GENDER = 0;
                    break;
            }
        }
    };
}
