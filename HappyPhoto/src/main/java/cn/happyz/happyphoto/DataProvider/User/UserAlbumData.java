package cn.happyz.happyphoto.DataProvider.User;

import android.os.Handler;
import android.os.Message;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

import cn.happyz.happyphoto.DataProvider.BaseData;
import cn.happyz.happyphoto.Tools.HttpClientStatus;
import cn.happyz.happyphoto.Tools.ThreadPoolUtils;

/**
 * Created by zcmzc on 13-12-13.
 */
public class UserAlbumData extends BaseData implements Runnable {
    private String HttpUrl = null;
    private Handler MyHandler = null;
    private UserAlbumDataOperateType _userAlbumDataOperateType = UserAlbumDataOperateType.Null;

    private UserAlbum _userAlbum;

    private int _pageIndex = 1;
    private int _pageSize = 12;

    public void setUserAlbum(UserAlbum userAlbum){
        _userAlbum = userAlbum;
    }

    public void setPageIndex(int pageIndex){
        _pageIndex = pageIndex;
    }
    public void setPageSize(int pageSize){
        _pageSize = pageSize;
    }



    public UserAlbumData(String httpUrl, Handler handler){
        HttpUrl = httpUrl;
        MyHandler = handler;
    }

    @Override
    public void run() {
        if(_userAlbumDataOperateType == UserAlbumDataOperateType.Create){
            this.Create();
        }else if(_userAlbumDataOperateType == UserAlbumDataOperateType.GetListOfAll){
            this.GetListOfAll();
        }else if(_userAlbumDataOperateType == UserAlbumDataOperateType.GetListOfType){
            this.GetListOfType();
        }else if(_userAlbumDataOperateType == UserAlbumDataOperateType.GetListOfMine){
            this.GetListOfMine();
        }
    }

    private void Create(){
        if(_userAlbum != null){
            Map<String,Object> params = new HashMap<String, Object>();
            if(_userAlbum.getSiteId() > 0){
                params.put("f_"+"SiteId",Integer.toString(_userAlbum.getSiteId()));
            }
            if(_userAlbum.getUserId() > 0){
                params.put("f_"+"UserId",Integer.toString(_userAlbum.getUserId()));
            }
            if(_userAlbum.getUserAlbumName() != null){
                params.put("f_"+"UserAlbumName",_userAlbum.getUserAlbumName());
            }
            if(_userAlbum.getUserAlbumIntro() != null){
                params.put("f_"+"UserAlbumIntro",_userAlbum.getUserAlbumIntro());
            }
            if(_userAlbum.getUserAlbumTypeId() > 0){
                params.put("f_"+"UserAlbumTypeId",Integer.toString(_userAlbum.getUserAlbumTypeId()));
            }
            try{
                boolean isBigData = false;
                String result = super.RunPost(HttpUrl, MyHandler,params,isBigData);
                Message msg = MyHandler.obtainMessage();
                msg.what = HttpClientStatus.FINISH_POST.ordinal();
                msg.obj = result;
                MyHandler.sendMessage(msg);
            } catch(UnsupportedEncodingException ex){

            }
        }
    }

    private void GetListOfAll(){
        HttpUrl += "&p=" + _pageIndex + "&ps=" + _pageSize;

        String result = super.RunGet(HttpUrl, MyHandler);
        if(result != null){
            try {
                UserAlbumCollections userAlbumCollections = new UserAlbumCollections();
                UserAlbum userAlbum = null;
                JSONObject jsonObject = new JSONObject(result).getJSONObject("useralbum");
                JSONArray jsonArray = jsonObject.getJSONArray("useralbumlist");

                for(int i=0;i<jsonArray.length();i++){
                    JSONObject jsonObject2 = (JSONObject)jsonArray.opt(i);
                    int userAlbumId = jsonObject2.getInt("UserAlbumID");
                    int siteId = jsonObject2.getInt("SiteID");
                    int userId = jsonObject2.getInt("UserID");
                    String userAlbumName = jsonObject2.getString("UserAlbumName");
                    int userAlbumTypeId = jsonObject2.getInt("UserAlbumTypeId");
                    String coverPicUrl = jsonObject2.getString("CoverPicUrl");

                    if(userAlbumId>0){
                        userAlbum = new UserAlbum(
                                siteId,
                                userId,
                                userAlbumName,
                                userAlbumTypeId
                        );
                        userAlbum.setCoverPicUrl(coverPicUrl);
                        userAlbum.setUserAlbumId(userAlbumId);
                    }
                    userAlbumCollections.add(userAlbum);
                }

                Message msg = MyHandler.obtainMessage();
                msg.what = HttpClientStatus.FINISH_GET.ordinal();
                msg.obj = userAlbumCollections;

                MyHandler.sendMessage(msg);
            } catch (Exception ex){
                ex.printStackTrace();
                MyHandler.sendEmptyMessage(HttpClientStatus.ERROR_GET.ordinal());
            }
        }
    }

    private void GetListOfType(){
        HttpUrl += "&p=" + _pageIndex + "&ps=" + _pageSize;

        String result = super.RunGet(HttpUrl, MyHandler);
        if(result != null){
            try {
                UserAlbumCollections userAlbumCollections = new UserAlbumCollections();
                UserAlbum userAlbum = null;
                JSONObject jsonObject = new JSONObject(result).getJSONObject("useralbum");
                JSONArray jsonArray = jsonObject.getJSONArray("useralbumlist");

                for(int i=0;i<jsonArray.length();i++){
                    JSONObject jsonObject2 = (JSONObject)jsonArray.opt(i);
                    int userAlbumId = jsonObject2.getInt("UserAlbumID");
                    int siteId = jsonObject2.getInt("SiteID");
                    int userId = jsonObject2.getInt("UserID");
                    String userAlbumName = jsonObject2.getString("UserAlbumName");
                    int userAlbumTypeId = jsonObject2.getInt("UserAlbumTypeId");
                    String coverPicUrl = jsonObject2.getString("CoverPicUrl");

                    if(userAlbumId>0){
                        userAlbum = new UserAlbum(
                                siteId,
                                userId,
                                userAlbumName,
                                userAlbumTypeId
                        );
                        userAlbum.setCoverPicUrl(coverPicUrl);
                        userAlbum.setUserAlbumId(userAlbumId);
                    }
                    userAlbumCollections.add(userAlbum);
                }

                Message msg = MyHandler.obtainMessage();
                msg.what = HttpClientStatus.FINISH_GET.ordinal();
                msg.obj = userAlbumCollections;

                MyHandler.sendMessage(msg);
            } catch (Exception ex){
                ex.printStackTrace();
                MyHandler.sendEmptyMessage(HttpClientStatus.ERROR_GET.ordinal());
            }

        }
    }

    private void GetListOfMine(){
        HttpUrl += "&p=" + _pageIndex + "&ps=" + _pageSize;

        String result = super.RunGet(HttpUrl, MyHandler);
        if(result != null){
            try {
                UserAlbumCollections userAlbumCollections = new UserAlbumCollections();
                UserAlbum userAlbum = null;
                JSONObject jsonObject = new JSONObject(result).getJSONObject("useralbum");
                JSONArray jsonArray = jsonObject.getJSONArray("useralbumlist");

                for(int i=0;i<jsonArray.length();i++){
                    JSONObject jsonObject2 = (JSONObject)jsonArray.opt(i);
                    int userAlbumId = jsonObject2.getInt("UserAlbumID");
                    int siteId = jsonObject2.getInt("SiteID");
                    int userId = jsonObject2.getInt("UserID");
                    String userAlbumName = jsonObject2.getString("UserAlbumName");
                    int userAlbumTypeId = jsonObject2.getInt("UserAlbumTypeId");
                    String coverPicUrl = jsonObject2.getString("CoverPicUrl");

                    if(userAlbumId>0){
                        userAlbum = new UserAlbum(
                                siteId,
                                userId,
                                userAlbumName,
                                userAlbumTypeId
                        );
                        userAlbum.setCoverPicUrl(coverPicUrl);
                        userAlbum.setUserAlbumId(userAlbumId);
                    }
                    userAlbumCollections.add(userAlbum);
                }

                Message msg = MyHandler.obtainMessage();
                msg.what = HttpClientStatus.FINISH_GET.ordinal();
                msg.obj = userAlbumCollections;

                MyHandler.sendMessage(msg);
            } catch (Exception ex){
                ex.printStackTrace();
                MyHandler.sendEmptyMessage(HttpClientStatus.ERROR_GET.ordinal());
            }

        }
    }

    /**
     * 执行HTTP请求
     * @param userAlbumDataOperateType
     */
    public void GetDataFromHttp(UserAlbumDataOperateType userAlbumDataOperateType){
        this._userAlbumDataOperateType = userAlbumDataOperateType;
        ThreadPoolUtils.execute(this);
    }

}
