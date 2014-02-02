package cn.happyz.happyphoto.DataProvider.Activity;

import android.os.Handler;
import android.os.Message;

import org.json.JSONArray;
import org.json.JSONObject;

import cn.happyz.happyphoto.DataProvider.BaseData;
import cn.happyz.happyphoto.Tools.HttpClientStatus;
import cn.happyz.happyphoto.Tools.ThreadPoolUtils;

/**
 * Created by zcmzc on 14-1-19.
 */
public class ActivityAlbumData extends BaseData implements Runnable {
    private String HttpUrl = null;
    private Handler MyHandler = null;
    private ActivityAlbumDataOperateType MyOperateType;

    public ActivityAlbumData(String httpUrl, Handler handler){
        HttpUrl = httpUrl;
        MyHandler = handler;
    }

    @Override
    public void run() {
        if(MyOperateType == ActivityAlbumDataOperateType.Create){
            String result = super.RunGet(HttpUrl, MyHandler);
            if(result != null){
                try {
                    int createResult = -1;
                    JSONObject jsonObject = new JSONObject(result).getJSONObject("activity_album");
                    JSONArray jsonArray = jsonObject.getJSONArray("activity_album_create_result");

                    for(int i=0;i<jsonArray.length();i++){
                        JSONObject jsonObject2 = (JSONObject)jsonArray.opt(i);
                        createResult = jsonObject2.getInt("create_result");
                    }

                    Message msg = MyHandler.obtainMessage();
                    msg.what = HttpClientStatus.FINISH_GET.ordinal();
                    msg.obj = createResult;

                    MyHandler.sendMessage(msg);
                } catch (Exception ex){
                    ex.printStackTrace();
                    MyHandler.sendEmptyMessage(HttpClientStatus.ERROR_GET.ordinal());
                }
            }
        }else if(MyOperateType == ActivityAlbumDataOperateType.Delete){
            String result = super.RunGet(HttpUrl, MyHandler);
            if(result != null){
                try {
                    int createResult = -1;
                    JSONObject jsonObject = new JSONObject(result).getJSONObject("activity_album");
                    JSONArray jsonArray = jsonObject.getJSONArray("activity_album_delete_result");

                    for(int i=0;i<jsonArray.length();i++){
                        JSONObject jsonObject2 = (JSONObject)jsonArray.opt(i);
                        createResult = jsonObject2.getInt("delete_result");
                    }

                    Message msg = MyHandler.obtainMessage();
                    msg.what = HttpClientStatus.FINISH_GET.ordinal();
                    msg.obj = createResult;

                    MyHandler.sendMessage(msg);
                } catch (Exception ex){
                    ex.printStackTrace();
                    MyHandler.sendEmptyMessage(HttpClientStatus.ERROR_GET.ordinal());
                }
            }
        }
    }

    public void GetDataFromHttp(ActivityAlbumDataOperateType activityAlbumDataOperateType){
        this.MyOperateType = activityAlbumDataOperateType;
        ThreadPoolUtils.execute(this);
    }
}
