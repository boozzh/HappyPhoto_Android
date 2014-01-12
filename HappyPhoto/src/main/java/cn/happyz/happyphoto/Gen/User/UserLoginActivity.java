package cn.happyz.happyphoto.Gen.User;

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
import android.widget.Toast;

import cn.happyz.happyphoto.DataProvider.User.User;
import cn.happyz.happyphoto.DataProvider.User.UserCollections;
import cn.happyz.happyphoto.DataProvider.User.UserData;
import cn.happyz.happyphoto.Gen.BaseGen;
import cn.happyz.happyphoto.R;
import cn.happyz.happyphoto.Tools.FormatObject;
import cn.happyz.happyphoto.Tools.HttpClientStatus;

/**
 * Created by zcmzc on 13-11-18.
 */
public class UserLoginActivity extends BaseGen {

    private ImageButton btnBack;
    private Button btnUserLogin;
    private EditText txtUserName;
    private EditText txtUserPass;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
        setContentView(R.layout.user_login);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.titlebar);  //titlebar为自己标题栏的布局
        TextView tvTitleBarTitle = (TextView) findViewById(R.id.txtTitleBar);
        tvTitleBarTitle.setText(R.string.user_login_title); //修改title文字



        btnBack = (ImageButton) findViewById(R.id.titlebar_ibtnBack);
        btnBack.setVisibility(0);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        txtUserName = (EditText) findViewById(R.id.user_login_txtUserName);
        txtUserName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //if(txtUserName.getText().equals(R.string.user_login_edittext_username_defaultvalue)){
                //    txtUserName.setText("");
                //}
            }
        });

        txtUserPass = (EditText) findViewById(R.id.user_login_txtUserPass);
        txtUserPass.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD);
        txtUserPass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //txtUserPass.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD);
                //if(txtUserPass.getText().equals(R.string.user_login_edittext_userpass_defaultvalue)){
                //    txtUserPass.setText("");
                //}
            }
        });


        btnUserLogin = (Button) findViewById(R.id.user_login_btnLogin);
        btnUserLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String userLoginUrl = getString(R.string.config_user_login_url);
                userLoginUrl = userLoginUrl.replace("{username}",txtUserName.getText());
                userLoginUrl = userLoginUrl.replace("{userpass}",FormatObject.MD5(txtUserPass.getText().toString()));
                userLoginUrl = userLoginUrl.replace("{siteid}",getString(R.string.config_siteid));

                UserLoginHandler userLoginHandler = new UserLoginHandler();
                UserData userData = new UserData(userLoginUrl,userLoginHandler);
                userData.RequestFromHttp();
            }
        });
    }


    private class UserLoginHandler extends Handler {
        @Override
        public void dispatchMessage(Message msg) {

            HttpClientStatus httpClientStatus = HttpClientStatus.values()[msg.what];

            switch(httpClientStatus){
                case START_GET:
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
                            if(userId>0){
                                Toast.makeText(UserLoginActivity.this, R.string.user_login_result_success, Toast.LENGTH_SHORT).show();

                                SharedPreferences sp = getSharedPreferences("USERINFO", MODE_PRIVATE);
                                sp.edit().putInt("USERID",userId).commit();
                                sp.edit().putString("USERNAME",userName).commit();
                                sp.edit().putString("USERPASS",userPass).commit();
                                sp.edit().putInt("STATE",state).commit();

                            }else{
                                //

                            }
                            //
                        }
                    }
                    UserLoginActivity.this.finish();
                    break;

                case ERROR_GET:
                    Toast.makeText(UserLoginActivity.this, "登录失败", Toast.LENGTH_SHORT).show();
                    break;

                default:
                    System.out.println("nothing to do");
                    break;
            }
        }
    }
}


