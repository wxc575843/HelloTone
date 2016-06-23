package com.example.wxc575843.hellotone.Settings;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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

public class ModifyPassword extends AppCompatActivity {

    String email="";
    String oldPassword="";
    String newPassword="";
    String repeatPassword="";
    String rightPassword="";

    @ViewInject(R.id.old_password)
    EditText etOldPassword;
    @ViewInject(R.id.new_password)
    EditText etNewPassword;
    @ViewInject(R.id.repeat_password)
    EditText etRepeatPassword;
    @ViewInject(R.id.modify_btn_password)
    Button btnSubmit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modify_password);
        ViewUtils.inject(this);

        initData();
    }

    private void initData(){

        rightPassword = SharePreferenceUtils.getString(ModifyPassword.this, "password", null);
        email = SharePreferenceUtils.getString(ModifyPassword.this, "email",null);
        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                oldPassword = etOldPassword.getText().toString().trim();
                newPassword = etNewPassword.getText().toString();
                repeatPassword = etRepeatPassword.getText().toString();

                Log.d("oldPass",oldPassword);
                Log.d("rightPass",rightPassword);
                if (oldPassword.isEmpty()||newPassword.isEmpty()||repeatPassword.isEmpty()){
                    Toast.makeText(ModifyPassword.this,"信息不能为空",Toast.LENGTH_SHORT).show();
                } else if (!oldPassword.equals(rightPassword)){
                    Toast.makeText(ModifyPassword.this,"原密码输入错误",Toast.LENGTH_SHORT).show();
                } else if (!newPassword.equals(repeatPassword)) {
                    Toast.makeText(ModifyPassword.this,"两次密码不一致",Toast.LENGTH_SHORT).show();
                } else {
                    HttpUtils httpUtils = new HttpUtils();
                    RequestParams params = new RequestParams();
                    params.addBodyParameter("email",email);
                    params.addBodyParameter("password",newPassword);
                    httpUtils.send(HttpRequest.HttpMethod.POST, Global.MODIFYPASSWORDSERVLET, params, new RequestCallBack<String>() {
                        @Override
                        public void onSuccess(ResponseInfo<String> responseInfo) {
                            if (responseInfo.result.equals("success")){
                                Toast.makeText(ModifyPassword.this,"密码修改成功",Toast.LENGTH_SHORT).show();
                                SharePreferenceUtils.putString(ModifyPassword.this, "password", newPassword);
                                Intent intent2 = new Intent();
                                intent2.putExtra("result","success");
                                ModifyPassword.this.setResult(RESULT_OK,intent2);
                                Intent intent = new Intent(ModifyPassword.this,SettingActivity.class);
                                startActivity(intent);
                                finish();

                            } else {
                                Toast.makeText(ModifyPassword.this,R.string.register_failed_network,Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onFailure(HttpException e, String s) {
                            Toast.makeText(ModifyPassword.this,"密码修改失败",Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });
    }
}
