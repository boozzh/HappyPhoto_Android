package cn.happyz.happyphoto;

import android.content.Intent;
import android.graphics.Color;
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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;


import cn.happyz.happyphoto.DataProvider.DocumentNews.DocumentNews;
import cn.happyz.happyphoto.DataProvider.DocumentNews.DocumentNewsCollections;
import cn.happyz.happyphoto.DataProvider.DocumentNews.DocumentNewsData;
import cn.happyz.happyphoto.DataProvider.DocumentNews.DocumentNewsDataOperateType;
import cn.happyz.happyphoto.DataProvider.User.UserAlbumTypeCollections;
import cn.happyz.happyphoto.Gen.Activity.ActivityListGen;
import cn.happyz.happyphoto.Gen.Activity.ActivityListOfEndGen;
import cn.happyz.happyphoto.Gen.BaseGen;
import cn.happyz.happyphoto.Gen.User.UserAlbumCreateGen;
import cn.happyz.happyphoto.Gen.User.UserAlbumListAllGen;
import cn.happyz.happyphoto.Gen.User.UserAlbumListOfMineGen;
import cn.happyz.happyphoto.Gen.User.UserInfoGen;
import cn.happyz.happyphoto.Gen.User.UserLoginGen;
import cn.happyz.happyphoto.Gen.User.UserLoginRequestActivity;
import cn.happyz.happyphoto.Tools.AppAutoUpdate;
import cn.happyz.happyphoto.Tools.AsyncImageLoader;
import cn.happyz.happyphoto.Tools.FormatObject;
import cn.happyz.happyphoto.Tools.HttpClientStatus;
import cn.happyz.happyphoto.Tools.ToastObject;


public class DefaultGen extends BaseGen {

    private ViewFlipper viewFlipper;
    private FrameLayout btnUserAlbumShow;
    private FrameLayout btnTakeAndUpload;
    private FrameLayout btnActivityList;
    private FrameLayout btnMyPhoto;
    private FrameLayout btnOldGame;
    private ImageButton ibtnUserInfo;
    private FrameLayout btnMoney;
    public static UserAlbumTypeCollections globalUserAlbumTypeCollections;

    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
        setContentView(R.layout.default_page);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.titlebar);

        viewFlipper = (ViewFlipper) findViewById(R.id.viewFlipper1);
        // 开启自动启动并设置时间间隔
        viewFlipper.setAutoStart(true);
        viewFlipper.setFlipInterval(4000);

        btnUserAlbumShow = (FrameLayout) findViewById(R.id.btnUserAlbumShow);
        btnTakeAndUpload = (FrameLayout) findViewById(R.id.btnTakeAndUpload);
        btnActivityList = (FrameLayout) findViewById(R.id.btnActivityList);
        btnMyPhoto = (FrameLayout) findViewById(R.id.btnMyPhoto);
        btnOldGame = (FrameLayout) findViewById(R.id.btnOldGame);
        btnMoney = (FrameLayout) findViewById(R.id.btnMoney);

        ibtnUserInfo = (ImageButton) findViewById(R.id.ibtnUserInfo);
        ibtnUserInfo.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(DefaultGen.this, UserInfoGen.class);
                startActivity(intent);
                finish();
            }
        });

        btnUserAlbumShow.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(DefaultGen.this, UserAlbumListAllGen.class);
                startActivity(intent);
                //finish();
            }
        });

        btnActivityList.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(DefaultGen.this, ActivityListGen.class);
                startActivity(intent);
                //finish();
            }
        });

        btnMyPhoto.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(DefaultGen.this, UserAlbumListOfMineGen.class);
                startActivity(intent);
            }
        });

        btnTakeAndUpload.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(DefaultGen.this, UserAlbumCreateGen.class);
                startActivity(intent);
            }
        });

        btnOldGame.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(DefaultGen.this, ActivityListOfEndGen.class);
                startActivity(intent);
                //finish();
            }
        });

        btnMoney.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(DefaultGen.this, UserInfoGen.class);
                startActivity(intent);
                finish();
            }
        });

        TextView txtUserOp = (TextView) findViewById(R.id.titlebar_btnUserOp);
        boolean userIsLogin = super.UserCheckIsLogined(DefaultGen.this);
        if (userIsLogin) {
            //显示会员图像

            ibtnUserInfo.setVisibility(View.VISIBLE);
            txtUserOp.setVisibility(View.INVISIBLE);

        } else {
            txtUserOp.setText(R.string.titlebar_userinfo_login);
            txtUserOp.setEnabled(true);
            txtUserOp.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //确认上传作品
                    finish();
                    BaseGen.userLoginRequestActivity = UserLoginRequestActivity.DefaultGen;
                    Intent intent = new Intent(DefaultGen.this, UserLoginGen.class);
                    startActivityForResult(intent, 1);
                }
            });
        }

        String httpUrl = getString(R.string.config_default_intro_pics_url);
        LoadImagesHandler loadImagesHandler = new LoadImagesHandler();
        DocumentNewsData documentNewsData = new DocumentNewsData(httpUrl, loadImagesHandler);
        documentNewsData.GetDataFromHttp(DocumentNewsDataOperateType.GetList);

        //初始化一些系统值

        int displayWidth = FormatObject.GetDisplayWidth(this);
        ((AppApplication)getApplication()).setDisplayWidth(displayWidth);



        //check update
        AppAutoUpdate appAutoUpdate = new AppAutoUpdate(this);
        appAutoUpdate.CheckAndUpdate();

    }

    private class LoadImagesHandler extends Handler {
        @Override
        public void dispatchMessage(Message msg) {

            HttpClientStatus httpClientStatus = HttpClientStatus.values()[msg.what];

            switch (httpClientStatus) {
                case START_GET:
                    //Toast.makeText(DefaultGen.this, "开始下载", Toast.LENGTH_SHORT).show();

                    progressBar = new ProgressBar(DefaultGen.this);
                    progressBar.setIndeterminate(true);
                    FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(50, 50);
                    params.gravity = Gravity.CENTER;
                    progressBar.setLayoutParams(params);
                    viewFlipper.addView(progressBar);

                    break;

                case FINISH_GET:
                    //Toast.makeText(DefaultGen.this, "下载成功", Toast.LENGTH_SHORT).show();

                    DocumentNewsCollections documentNewsCollections = (DocumentNewsCollections) msg.obj;
                    for (int i = 0; i < documentNewsCollections.size(); i++) {
                        DocumentNews documentNews = documentNewsCollections.get(i);
                        if (documentNews != null) {
                            //构建ImageButton对象加入到轮换图中

                            String titlePic = documentNews.getTitlePic();
                            String imageUrl = getString(R.string.config_site_url) + titlePic;
                            AsyncImageLoader asyncImageLoader = new AsyncImageLoader();

                            asyncImageLoader.loadDrawable(imageUrl, new AsyncImageLoader.ImageCallback() {
                                public void imageLoaded(Drawable imageDrawable, String imageUrl) {
                                    //ImageButton imageButton = new ImageButton(DefaultGen.this);
                                    //LinearLayout.LayoutParams ibParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                                    //        LinearLayout.LayoutParams.MATCH_PARENT);
                                    //imageButton.setLayoutParams(ibParams);
                                    //imageButton.setScaleType(ImageView.ScaleType.FIT_START);
                                    //imageButton.setImageDrawable(imageDrawable);
                                    //imageButton.setBackgroundColor(Color.parseColor("#111d49"));
                                    //viewFlipper.addView(imageButton);

                                    ImageView imageView = new ImageView(DefaultGen.this);
                                    imageView.setImageDrawable(imageDrawable);
                                    LinearLayout.LayoutParams imageViewParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                                                    LinearLayout.LayoutParams.MATCH_PARENT);
                                    imageView.setLayoutParams(imageViewParams);
                                    imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
                                    imageView.setBackgroundColor(Color.parseColor("#111d49"));
                                    viewFlipper.addView(imageView);
                                }
                            });


                        }
                    }
                    viewFlipper.removeView(progressBar);
                    break;

                case ERROR_GET:
                    Toast.makeText(DefaultGen.this, "下载失败", Toast.LENGTH_SHORT).show();
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
