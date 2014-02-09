package cn.happyz.happyphoto.Gen.User;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.InputType;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import cn.happyz.happyphoto.DataProvider.User.User;
import cn.happyz.happyphoto.DataProvider.User.UserCollections;
import cn.happyz.happyphoto.DataProvider.User.UserData;
import cn.happyz.happyphoto.DataProvider.User.UserDataOperateType;
import cn.happyz.happyphoto.DefaultGen;
import cn.happyz.happyphoto.Gen.BaseGen;
import cn.happyz.happyphoto.R;
import cn.happyz.happyphoto.Tools.HttpClientStatus;
import cn.happyz.happyphoto.Tools.ToastObject;

/**
 * Created by zcmzc on 14-1-13.
 */
public class UserRegisterGen extends BaseGen {

    private ImageButton btnBack;
    private Button btnUserLogin;
    private Button btnUserRegister;
    private EditText txtUserName;
    private EditText txtUserPass;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
        setContentView(R.layout.user_register);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.titlebar);
        TextView tvTitleBarTitle = (TextView) findViewById(R.id.txtTitleBar);
        tvTitleBarTitle.setText(R.string.user_register_title);



        btnBack = (ImageButton) findViewById(R.id.titlebar_ibtnBack);
        btnBack.setVisibility(View.VISIBLE);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(UserRegisterGen.this, DefaultGen.class);
                startActivity(intent);
                finish();
            }
        });

        txtUserName = (EditText) findViewById(R.id.user_register_txtUserName);
        txtUserName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //if(txtUserName.getText().equals(R.string.user_login_edittext_username_defaultvalue)){
                //    txtUserName.setText("");
                //}
            }
        });

        txtUserPass = (EditText) findViewById(R.id.user_register_txtUserPass);
        txtUserPass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //txtUserPass.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD);
                //if(txtUserPass.getText().equals(R.string.user_login_edittext_userpass_defaultvalue)){
                //    txtUserPass.setText("");
                //}
            }
        });


        btnUserLogin = (Button) findViewById(R.id.user_register_btnLogin);
        btnUserLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                btnUserLogin.setEnabled(false);

                Intent intent = new Intent(UserRegisterGen.this, UserLoginGen.class);
                startActivity(intent);
                UserRegisterGen.this.finish();

            }
        });

        btnUserRegister = (Button) findViewById(R.id.user_register_btnRegister);
        btnUserRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                btnUserRegister.setEnabled(false);

                String userName = txtUserName.getText().toString().trim();
                if(userName == null || userName.equals("")){
                    ToastObject.Show(UserRegisterGen.this, getString(R.string.user_register_tips_no_username));
                    btnUserRegister.setEnabled(true);
                    return;
                }
                String userPass = txtUserPass.getText().toString().trim();
                if(userPass == null || userPass.equals("")){
                    ToastObject.Show(UserRegisterGen.this, getString(R.string.user_register_tips_no_userpass));
                    btnUserRegister.setEnabled(true);
                    return;
                }

                String userRegisterUrl = getString(R.string.config_user_register_url);
                userRegisterUrl = userRegisterUrl.replace("{user_name}",txtUserName.getText());
                //注册的时候，密码不用md5加密
                userRegisterUrl = userRegisterUrl.replace("{user_pass}",txtUserPass.getText().toString());
                userRegisterUrl = userRegisterUrl.replace("{site_id}",getString(R.string.config_siteid));

                UserRegisterHandler userRegisterHandler = new UserRegisterHandler();
                UserData userData = new UserData(userRegisterUrl,userRegisterHandler);
                userData.RequestFromHttp(UserDataOperateType.Register);
            }
        });
    }

    private class UserRegisterHandler extends Handler {
        @Override
        public void dispatchMessage(Message msg) {

            HttpClientStatus httpClientStatus = HttpClientStatus.values()[msg.what];

            switch(httpClientStatus){
                case START_GET:
                    ToastObject.Show(UserRegisterGen.this, getString(R.string.user_register_result_begin));
                    break;

                case FINISH_GET:
                    UserCollections userCollections = (UserCollections)msg.obj;
                    for(int i=0;i<userCollections.size();i++){
                        User user = userCollections.get(i);
                        if(user != null){
                            Integer userId = user.getUserId();
                            String userName = user.getUserName();
                            String userPass = user.getUserPass();
                            Integer state = user.getState();
                            int userPoint = user.getUserPoint();
                            if(userId>0){
                                ToastObject.Show(UserRegisterGen.this, getString(R.string.user_register_result_success));

                                SharedPreferences sp = getSharedPreferences("USER_INFO", MODE_PRIVATE);
                                sp.edit().putInt("USER_ID",userId).commit();
                                sp.edit().putString("USER_NAME", userName).commit();
                                sp.edit().putString("USER_PASS",userPass).commit();
                                sp.edit().putInt("STATE", state).commit();
                                sp.edit().putInt("USER_POINT", userPoint).commit();


                                Intent intent = new Intent(UserRegisterGen.this, DefaultGen.class);
                                startActivity(intent);

                                UserRegisterGen.this.finish();
                            }else{
                                btnUserRegister.setEnabled(true);
                                ToastObject.Show(UserRegisterGen.this, getString(R.string.user_register_result_failure));
                            }
                        }
                    }

                    break;

                case ERROR_GET:
                    btnUserRegister.setEnabled(true);
                    ToastObject.Show(UserRegisterGen.this, getString(R.string.user_register_result_failure_for_network));
                    break;

                default:
                    btnUserRegister.setEnabled(true);
                    System.out.println("nothing to do");
                    break;
            }
        }
    }
}
