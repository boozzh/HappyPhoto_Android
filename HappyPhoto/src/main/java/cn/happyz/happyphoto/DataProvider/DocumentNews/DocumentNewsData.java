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

    private String _httpUrl = null;
    private Handler _handler = null;

    public DocumentNewsData(String httpUrl, Handler handler){
        _httpUrl = httpUrl;
        _handler = handler;
    }

    @Override
    public void run() {
        String result = super.RunGet(_httpUrl, _handler);
        if(result != null){
            try {
                DocumentNewsCollections documentNewsCollections = new DocumentNewsCollections();
                DocumentNews documentNews = null;
                JSONObject jsonObject = new JSONObject(result).getJSONObject("documentnews");
                JSONArray jsonArray = jsonObject.getJSONArray("documentnewslist");

                for(int i=0;i<jsonArray.length();i++){
                    JSONObject jsonObject2 = (JSONObject)jsonArray.opt(i);
                    documentNews = new DocumentNews(
                            jsonObject2.getInt("documentnewsid"),
                            jsonObject2.getString("documentnewstitle"),
                            jsonObject2.getString("titlepic")
                    );
                    documentNewsCollections.Add(documentNews);
                }

                Message msg = _handler.obtainMessage();
                msg.what = HttpClientStatus.FINISH_GET.ordinal();
                msg.obj = documentNewsCollections;

                _handler.sendMessage(msg);
            } catch (Exception ex){
                ex.printStackTrace();
                _handler.sendEmptyMessage(HttpClientStatus.ERROR_GET.ordinal());
            }
        }
    }

    public void GetDataFromHttp(){
        ThreadPoolUtils.execute(this);
    }
}
