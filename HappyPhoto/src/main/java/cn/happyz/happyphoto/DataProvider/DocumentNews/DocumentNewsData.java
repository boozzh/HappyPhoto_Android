package cn.happyz.happyphoto.DataProvider.DocumentNews;

import android.os.Handler;
import android.os.Message;

import org.json.JSONArray;
import org.json.JSONObject;

import cn.happyz.happyphoto.DataProvider.BaseData;
import cn.happyz.happyphoto.Tools.HttpClientStatus;
import cn.happyz.happyphoto.Tools.ThreadPoolUtils;

/**
 * Created by zcmzc on 13-11-18.
 */
public class DocumentNewsData extends BaseData implements Runnable {

    private String HttpUrl = null;
    private Handler MyHandler = null;
    private DocumentNewsDataOperateType MyOperateType;

    public DocumentNewsData(String httpUrl, Handler handler){
        HttpUrl = httpUrl;
        MyHandler = handler;
    }

    @Override
    public void run() {
        if(MyOperateType == DocumentNewsDataOperateType.GetList){
            String result = super.RunGet(HttpUrl, MyHandler);
            if(result != null){
                try {
                    DocumentNewsCollections documentNewsCollections = new DocumentNewsCollections();
                    DocumentNews documentNews;
                    JSONObject jsonObject = new JSONObject(result).getJSONObject("documentnews");
                    JSONArray jsonArray = jsonObject.getJSONArray("documentnewslist");

                    for(int i=0;i<jsonArray.length();i++){
                        JSONObject jsonObject2 = (JSONObject)jsonArray.opt(i);
                        documentNews = new DocumentNews(
                                jsonObject2.getInt("documentnewsid"),
                                jsonObject2.getString("documentnewstitle"),
                                jsonObject2.getString("titlepic")
                        );
                        documentNewsCollections.add(documentNews);
                    }

                    Message msg = MyHandler.obtainMessage();
                    msg.what = HttpClientStatus.FINISH_GET.ordinal();
                    msg.obj = documentNewsCollections;

                    MyHandler.sendMessage(msg);
                } catch (Exception ex){
                    ex.printStackTrace();
                    MyHandler.sendEmptyMessage(HttpClientStatus.ERROR_GET.ordinal());
                }
            }
        }

    }

    public void GetDataFromHttp(DocumentNewsDataOperateType documentNewsOperateType){
        this.MyOperateType = documentNewsOperateType;
        ThreadPoolUtils.execute(this);
    }
}
