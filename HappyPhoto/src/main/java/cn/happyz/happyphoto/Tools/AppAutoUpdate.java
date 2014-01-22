package cn.happyz.happyphoto.Tools;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import java.io.File;

import cn.happyz.happyphoto.DataProvider.ClientApp.ClientApp;
import cn.happyz.happyphoto.DataProvider.ClientApp.ClientAppData;
import cn.happyz.happyphoto.DataProvider.ClientApp.ClientAppDataOperateType;
import cn.happyz.happyphoto.R;

/**
 * 应用程序自动更新类
 */
public class AppAutoUpdate {

    private String currentVersionName;
    private String newVersionName;
    private int currentVersionCode;
    private int newVersionCode;
    private Context _context;
    ClientApp clientApp;
    private ProgressDialog updateProgressDialog;
    private String appSavePath;

    public AppAutoUpdate(Context context){
        _context = context;
        GetCurrentVersion();
    }

    /**
     * 检查并更新主程序
     */
    public void CheckAndUpdate(){
        String httpUrl = _context.getString(R.string.config_client_app_get_new_url);
        ClientAppGetNewHandler clientAppGetNewHandler = new ClientAppGetNewHandler();
        ClientAppData clientAppData = new ClientAppData(httpUrl,clientAppGetNewHandler);
        clientAppData.GetDataFromHttp(ClientAppDataOperateType.GetNew);
    }

    private void GetCurrentVersion(){
        try {
            PackageInfo pInfo = _context.getPackageManager().getPackageInfo(
                    _context.getPackageName(), 0);
            currentVersionName = pInfo.versionName;
            currentVersionCode = pInfo.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            Log.e("update", e.getMessage());
            currentVersionName = "1.0.0000";
            currentVersionCode = 100000;
        }
    }

    private class ClientAppGetNewHandler extends Handler {
        @Override
        public void dispatchMessage(Message msg) {

            HttpClientStatus httpClientStatus = HttpClientStatus.values()[msg.what];

            switch(httpClientStatus){
                case START_GET:
                    break;

                case FINISH_GET:
                    clientApp = (ClientApp)msg.obj;
                    if(clientApp != null){
                        newVersionCode = clientApp.getVersionCode();
                        newVersionName = clientApp.getVersionName();

                        if(newVersionCode>currentVersionCode){
                            //有新版本，提示
                            DialogHelper.Confirm(_context,
                                    R.string.client_app_title,
                                    R.string.client_app_title,
                                    R.string.client_app_confirm_to_update,
                                    new ConfirmToUpdateListener(),
                                    R.string.client_app_cancel_to_update,
                                    new CancelToUpdateListener()
                            );
                        }
                    }

                    break;

                case ERROR_GET:
                    break;

                default:
                    break;
            }
        }
    }

    private class ConfirmToUpdateListener implements android.content.DialogInterface.OnClickListener{
        public void onClick(DialogInterface dialog, int which) {
            //开始更新
            String appFilePath = _context.getString(R.string.config_site_url) + "/" +
                    clientApp.getAppFilePath();


            appSavePath = Environment.getExternalStorageDirectory()
                    + _context.getString(R.string.config_client_app_file_save_path)
                    + clientApp.getVersionCode() + ".apk"
            ;


            ClientAppGetAppFileHandler clientAppGetAppFileHandler = new ClientAppGetAppFileHandler();
            ClientAppData clientAppData = new ClientAppData(appFilePath,clientAppGetAppFileHandler);
            clientAppData.setFileSavePath(appSavePath);
            clientAppData.GetDataFromHttp(ClientAppDataOperateType.GetAppFile);

        }
    }

    private class ClientAppGetAppFileHandler extends Handler {
        @Override
        public void dispatchMessage(Message msg) {

            HttpClientStatus httpClientStatus = HttpClientStatus.values()[msg.what];

            switch(httpClientStatus){
                case START_DOWNLOAD:
                    int max = 0;
                    if(msg.obj != null){
                        max = Integer.parseInt(msg.obj.toString());
                        //ToastObject.Show(_context, Integer.toString(max));
                    }
                    updateProgressDialog = new ProgressDialog(_context);
                    updateProgressDialog.setIndeterminate(false);
                    updateProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
                    updateProgressDialog.setMax(max);
                    updateProgressDialog.setProgress(0);
                    updateProgressDialog.show();
                    break;

                case RUNNING_DOWNLOAD:
                    if(msg.obj != null){
                        int progress = Integer.parseInt(msg.obj.toString());
                        if (updateProgressDialog != null
                                && updateProgressDialog.isShowing()) {
                            updateProgressDialog.setProgress(progress);
                        }
                    }
                    break;

                case FINISH_DOWNLOAD:
                    if (updateProgressDialog != null
                            && updateProgressDialog.isShowing()) {
                        updateProgressDialog.dismiss();
                    }
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.setDataAndType(
                            Uri.fromFile(new File(appSavePath)),
                            "application/vnd.android.package-archive");
                    _context.startActivity(intent);
                    break;

                case ERROR_DOWNLOAD:
                    break;

                default:
                    break;
            }
        }
    }

    private class CancelToUpdateListener implements android.content.DialogInterface.OnClickListener{
        public void onClick(DialogInterface dialog, int which) {
            dialog.cancel();
        }
    }
}
