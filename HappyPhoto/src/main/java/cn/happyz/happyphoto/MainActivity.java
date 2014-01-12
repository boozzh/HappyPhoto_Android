package cn.happyz.happyphoto;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Message;
import android.os.Bundle;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;



import cn.happyz.happyphoto.DataProvider.DocumentNews.DocumentNews;
import cn.happyz.happyphoto.DataProvider.DocumentNews.DocumentNewsCollections;
import cn.happyz.happyphoto.DataProvider.DocumentNews.DocumentNewsData;
import cn.happyz.happyphoto.DataProvider.User.UserAlbumTypeCollections;
import cn.happyz.happyphoto.Gen.BaseGen;
import cn.happyz.happyphoto.Gen.User.UserAlbumCreateActivity;
import cn.happyz.happyphoto.Gen.User.UserAlbumGameActivity;
import cn.happyz.happyphoto.Gen.User.UserAlbumListAllActivity;
import cn.happyz.happyphoto.Gen.User.UserAlbumListOfMineActivity;
import cn.happyz.happyphoto.Gen.User.UserInfoActivity;
import cn.happyz.happyphoto.Gen.User.UserLoginActivity;
import cn.happyz.happyphoto.Tools.AsyncImageLoader;
import cn.happyz.happyphoto.Tools.HttpClientStatus;


public class MainActivity extends BaseGen {

    private ViewFlipper viewFlipper;
    private FrameLayout btnUserAlbumShow;
    private FrameLayout btnTakeAndUpload;
    private FrameLayout btnGotoGame;
    private FrameLayout btnMyPhoto;
    private FrameLayout btnOldGame;
    private FrameLayout btnMoney;
    public static UserAlbumTypeCollections globalUserAlbumTypeCollections;

    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
        setContentView(R.layout.activity_main);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.titlebar);

        viewFlipper = (ViewFlipper) findViewById(R.id.viewFlipper1);
        // 开启自动启动并设置时间间隔
        viewFlipper.setAutoStart(true);
        viewFlipper.setFlipInterval(4000);

        btnUserAlbumShow = (FrameLayout) findViewById(R.id.btnUserAlbumShow);
        btnTakeAndUpload = (FrameLayout) findViewById(R.id.btnTakeAndUpload);
        btnGotoGame = (FrameLayout) findViewById(R.id.btnGotoGame);
        btnMyPhoto = (FrameLayout) findViewById(R.id.btnMyPhoto);
        btnOldGame = (FrameLayout) findViewById(R.id.btnOldGame);
        btnMoney = (FrameLayout) findViewById(R.id.btnMoney);
        //btnUserAlbumShow.setBackgroundResource(R.drawable.main_bg1);
        //btnTakeAndUpload.setBackgroundResource(R.drawable.main_bg2);
        //btnGotoGame.setBackgroundResource(R.drawable.main_bg3);
        //btnMyPhoto.setBackgroundResource(R.drawable.main_bg4);
        //btnOldGame.setBackgroundResource(R.drawable.main_bg5);
        //btnMoney.setBackgroundResource(R.drawable.main_bg6);

        //_textView = (TextView) findViewById(R.id.textView);

        btnUserAlbumShow.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, UserAlbumListAllActivity.class);
                startActivity(intent);
                //finish();
            }
        });

        btnGotoGame.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, UserAlbumGameActivity.class);
                startActivity(intent);
                //finish();
            }
        });

        btnMyPhoto.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, UserAlbumListOfMineActivity.class);
                startActivity(intent);
            }
        });

        btnTakeAndUpload.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, UserAlbumCreateActivity.class);
                startActivity(intent);
            }
        });

        TextView txtUserOp = (TextView) findViewById(R.id.titlebar_btnUserOp);
        boolean userIsLogined = super.UserCheckIsLogined(MainActivity.this);
        if(userIsLogined){
            //显示会员图像



        }else{
            txtUserOp.setText(R.string.titlebar_userinfo_login);
            txtUserOp.setEnabled(true);
            txtUserOp.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //确认上传作品
                    Intent intent = new Intent(MainActivity.this, UserLoginActivity.class);
                    startActivityForResult(intent, 1);
                }
            });
        }

        String httpUrl = getString(R.string.config_default_intro_pics_url);
        LoadImagesHandler loadImagesHandler = new LoadImagesHandler();
        DocumentNewsData documentNewsData = new DocumentNewsData(httpUrl,loadImagesHandler);
        documentNewsData.GetDataFromHttp();

    }

    private class LoadImagesHandler extends Handler {
        @Override
        public void dispatchMessage(Message msg) {

            HttpClientStatus httpClientStatus = HttpClientStatus.values()[msg.what];

            switch(httpClientStatus){
                case START_GET:
                    //Toast.makeText(MainActivity.this, "开始下载", Toast.LENGTH_SHORT).show();

                    progressBar = new ProgressBar(MainActivity.this);
                    progressBar.setIndeterminate(true);
                    FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(50,50);
                    params.gravity = Gravity.CENTER;
                    progressBar.setLayoutParams(params);
                    viewFlipper.addView(progressBar);

                    break;

                case FINISH_GET:
                    //Toast.makeText(MainActivity.this, "下载成功", Toast.LENGTH_SHORT).show();

                    DocumentNewsCollections documentNewsCollections = (DocumentNewsCollections)msg.obj;
                    for(int i=0;i<documentNewsCollections.Count();i++){
                        DocumentNews documentNews = documentNewsCollections.Get(i);
                        if(documentNews != null){
                            //构建ImageButton对象加入到轮换图中

                            String titlePic = documentNews.getTitlePic();
                            String imageUrl = getString(R.string.config_site_url)+titlePic;
                            AsyncImageLoader asyncImageLoader = new AsyncImageLoader();

                            asyncImageLoader.loadDrawable(imageUrl, new AsyncImageLoader.ImageCallback() {
                                public void imageLoaded(Drawable imageDrawable, String imageUrl) {
                                    ImageButton imageButton = new ImageButton(MainActivity.this);
                                    LinearLayout.LayoutParams ibParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                                            LinearLayout.LayoutParams.MATCH_PARENT);
                                    imageButton.setLayoutParams(ibParams);
                                    imageButton.setImageDrawable(imageDrawable);
                                    viewFlipper.addView(imageButton);
                                    }
                                });


                        }
                    }
                    viewFlipper.removeView(progressBar);
                    break;

                case ERROR_GET:
                    Toast.makeText(MainActivity.this, "下载失败", Toast.LENGTH_SHORT).show();
                    break;

                default:
                    System.out.println("nothing to do");
                    break;
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


}