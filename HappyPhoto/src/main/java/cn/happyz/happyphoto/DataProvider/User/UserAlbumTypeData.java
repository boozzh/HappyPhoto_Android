package cn.happyz.happyphoto.DataProvider.User;

import android.os.Handler;
import android.os.Message;

import org.json.JSONArray;
import org.json.JSONObject;

import cn.happyz.happyphoto.DataProvider.BaseData;
import cn.happyz.happyphoto.Tools.HttpClientStatus;
import cn.happyz.happyphoto.Tools.ThreadPoolUtils;

/**
 * Created by zcmzc on 13-12-21.
 */
public class UserAlbumTypeData extends BaseData implements Runnable {
    private String _httpUrl = null;
    private Handler _handler = null;

    public UserAlbumTypeData(String httpUrl, Handler handler){
        _httpUrl = httpUrl;
        _handler = handler;
    }

    @Override
    public void run() {
        String result = super.RunGet(_httpUrl, _handler);

        if(result != null){
            try {
                UserAlbumTypeCollections userAlbumTypeCollections = new UserAlbumTypeCollections();
                UserAlbumType userAlbumType = null;
                JSONObject jsonObject = new JSONObject(result).getJSONObject("useralbumtype");
                JSONArray jsonArray = jsonObject.getJSONArray("useralbumtypelist");

                for(int i=0;i<jsonArray.length();i++){
                    JSONObject jsonObject2 = (JSONObject)jsonArray.opt(i);
                    int userAlbumTypeId = jsonObject2.getInt("UserAlbumTypeId");
                    if(userAlbumTypeId>0){
                        userAlbumType = new UserAlbumType(
                                jsonObject2.getInt("UserAlbumTypeId"),
                                jsonObject2.getString("UserAlbumTypeName")
                        );
                    }
                    userAlbumTypeCollections.add(userAlbumType);
                }

                Message msg = _handler.obtainMessage();
                msg.what = HttpClientStatus.FINISH_GET.ordinal();
                msg.obj = userAlbumTypeCollections;

                _handler.sendMessage(msg);
            } catch (Exception ex){
                ex.printStackTrace();
                _handler.sendEmptyMessage(HttpClientStatus.ERROR_GET.ordinal());
            }

        }
    }

    public void RequestFromHttp(){
        ThreadPoolUtils.execute(this);
    }

    public UserAlbumTypeCollections Get(int siteId, int userAlbumTypeUpdated){

        UserAlbumTypeCollections userAlbumTypeCollections = new UserAlbumTypeCollections();
        //判断web端是否有变化
        if(userAlbumTypeUpdated == 0){//没有变化,从sqlite取数据集

        }else{//有变化，从web取数据集，并同步到sqlite
            this.RequestFromHttp();
        }




        return userAlbumTypeCollections;

    }
}