package cn.happyz.happyphoto.DataProvider.ClientApp;

import android.os.Handler;
import android.os.Message;

import org.json.JSONArray;
import org.json.JSONObject;

import cn.happyz.happyphoto.DataProvider.BaseData;
import cn.happyz.happyphoto.Tools.HttpClientStatus;
import cn.happyz.happyphoto.Tools.ThreadPoolUtils;

/**
 * 客户端程序数据处理类
 */
public class ClientAppData extends BaseData implements Runnable {

    private String HttpUrl = null;
    private Handler MyHandler = null;


    private String FileSavePath;
    private ClientAppDataOperateType MyOperateType;
    private int ClientAppType;

    public void setClientAppType(int clientAppType) {
        ClientAppType = clientAppType;
    }

    public void setFileSavePath(String fileSavePath) {
        FileSavePath = fileSavePath;
    }

    public ClientAppData(String httpUrl, Handler handler) {
        HttpUrl = httpUrl;
        MyHandler = handler;
    }

    @Override
    public void run() {
        if (MyOperateType == ClientAppDataOperateType.GetNew) {
            String result = super.RunGet(HttpUrl, MyHandler);
            if (result != null) {
                try {
                    ClientApp clientApp = null;
                    JSONObject jsonObject = new JSONObject(result).getJSONObject("client_app");
                    JSONArray jsonArray = jsonObject.getJSONArray("get_new");

                    if (jsonArray.length() == 1) {
                        JSONObject jsonObject2 = (JSONObject) jsonArray.opt(0);
                        clientApp = new ClientApp(
                                jsonObject2.getInt("ClientAppId"),
                                jsonObject2.getInt("ClientAppType"),
                                jsonObject2.getString("ClientAppName"),
                                jsonObject2.getString("CreateDate"),
                                jsonObject2.getString("UpdateDate"),
                                jsonObject2.getInt("VersionCode"),
                                jsonObject2.getString("VersionName"),
                                jsonObject2.getString("AppFilePath")
                        );
                    }

                    Message msg = MyHandler.obtainMessage();
                    msg.what = HttpClientStatus.FINISH_GET.ordinal();
                    msg.obj = clientApp;

                    MyHandler.sendMessage(msg);
                } catch (Exception ex) {
                    ex.printStackTrace();
                    MyHandler.sendEmptyMessage(HttpClientStatus.ERROR_GET.ordinal());
                }
            }
        } else if (MyOperateType == ClientAppDataOperateType.GetAppFile) {
            if(FileSavePath == null){
                return;
            }
            super.GetFileFromUrl(HttpUrl, MyHandler, FileSavePath);
        }
    }

    public void GetDataFromHttp(ClientAppDataOperateType clientAppDataOperateType) {
        this.MyOperateType = clientAppDataOperateType;
        ThreadPoolUtils.execute(this);
    }
}
