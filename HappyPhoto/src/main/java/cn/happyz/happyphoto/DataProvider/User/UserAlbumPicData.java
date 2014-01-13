package cn.happyz.happyphoto.DataProvider.User;

import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

import cn.happyz.happyphoto.DataProvider.BaseData;
import cn.happyz.happyphoto.R;
import cn.happyz.happyphoto.Tools.HttpClientStatus;
import cn.happyz.happyphoto.Tools.ThreadPoolUtils;

/**
 * Created by zcmzc on 13-12-13.
 */
public class UserAlbumPicData extends BaseData implements Runnable {
    private String _httpUrl = null;
    private Handler _handler = null;
    private UserAlbumPicDataOperateType _userAlbumPicDataOperateType = UserAlbumPicDataOperateType.Null;
    private UserAlbumPic _userAlbumPic;
    private UserAlbum _userAlbum;

    public UserAlbumPicData(String httpUrl, Handler handler){
        _httpUrl = httpUrl;
        _handler = handler;
    }

    public void setUserAlbumPic(UserAlbumPic userAlbumPic){
        _userAlbumPic = userAlbumPic;
    }
    public void setUserAlbum(UserAlbum userAlbum){
        _userAlbum = userAlbum;
    }

    @Override
    public void run() {
        if(_userAlbumPicDataOperateType == UserAlbumPicDataOperateType.Create){
            if(_userAlbumPic != null){

                int userAlbumId = _userAlbumPic.getUserAlbumId();
                int userId = _userAlbumPic.getUserId();
                String userAlbumPicUrl = _userAlbumPic.getUserAlbumPicUrl();
                int isCover = _userAlbumPic.getIsCover();

                Map<String,Object> params = new HashMap<String, Object>();

                if(userAlbumId> 0 ){
                    params.put("f_"+"UserAlbumId", Integer.toString(userAlbumId));
                }
                if(userId> 0 ){
                    params.put("f_"+"UserId", Integer.toString(userId));
                }
                if(isCover> 0 ){
                    params.put("f_"+"IsCover", Integer.toString(isCover));
                }
                if(userAlbumPicUrl != null){
                    params.put("file_"+"UserAlbumPicUrl", userAlbumPicUrl);
                }
                try{
                    boolean isBigData = true;
                    super.RunPost(_httpUrl,_handler,params,isBigData);
                    Message msg = _handler.obtainMessage();
                    msg.what = HttpClientStatus.FINISH_POST.ordinal();
                    msg.obj = _userAlbumPic;
                    _handler.sendMessage(msg);
                } catch(UnsupportedEncodingException ex){

                }
            }
        }else if(_userAlbumPicDataOperateType == UserAlbumPicDataOperateType.GetList){
            if(_userAlbum != null){
                int userAlbumId = _userAlbum.getUserAlbumId();
                if(userAlbumId>0){
                    _httpUrl += "&uaid=" + Integer.toString(userAlbumId);

                    String result = super.RunGet(_httpUrl, _handler);
                    if(result != null){
                        try {
                            UserAlbumPicCollections userAlbumPicCollections = new UserAlbumPicCollections();
                            UserAlbumPic userAlbumPic = null;
                            JSONObject jsonObject = new JSONObject(result).getJSONObject("useralbumpic");
                            JSONArray jsonArray = jsonObject.getJSONArray("useralbumpiclist");

                            for(int i=0;i<jsonArray.length();i++){
                                JSONObject jsonObject2 = (JSONObject)jsonArray.opt(i);
                                userAlbumId = jsonObject2.getInt("UserAlbumID");
                                int userAlbumPicId = jsonObject2.getInt("UserAlbumPicID");
                                String userAlbumPicTitle = jsonObject2.getString("UserAlbumPicTitle");
                                String userAlbumPicIntro = jsonObject2.getString("UserAlbumPicIntro");
                                String userAlbumPicUrl = jsonObject2.getString("UserAlbumPicUrl");
                                String userAlbumPicThumbnailUrl = jsonObject2.getString("UserAlbumPicThumbnailUrl");
                                String userAlbumPicCompressUrl = jsonObject2.getString("UserAlbumPicCompressUrl");
                                String userAlbumPicTempUrl = jsonObject2.getString("UserAlbumPicTempUrl");
                                int isCover = jsonObject2.getInt("IsCover");

                                if(userAlbumPicId>0){
                                    userAlbumPic = new UserAlbumPic(
                                            userAlbumPicId
                                    );
                                    userAlbumPic.setUserAlbumId(userAlbumId);
                                    userAlbumPic.setUserAlbumPicTitle(userAlbumPicTitle);
                                    userAlbumPic.setUserAlbumPicIntro(userAlbumPicIntro);
                                    userAlbumPic.setUserAlbumPicUrl(userAlbumPicUrl);
                                    userAlbumPic.setUserAlbumPicThumbnailUrl(userAlbumPicThumbnailUrl);
                                    userAlbumPic.setUserAlbumPicCompressUrl(userAlbumPicCompressUrl);
                                    userAlbumPic.setUserAlbumPicTempUrl(userAlbumPicTempUrl);
                                    userAlbumPic.setIsCover(isCover);
                                }
                                userAlbumPicCollections.add(userAlbumPic);
                            }

                            Message msg = _handler.obtainMessage();
                            msg.what = HttpClientStatus.FINISH_GET.ordinal();
                            msg.obj = userAlbumPicCollections;

                            _handler.sendMessage(msg);
                        } catch (Exception ex){
                            ex.printStackTrace();
                            _handler.sendEmptyMessage(HttpClientStatus.ERROR_GET.ordinal());
                        }

                    }
                }
            }
        }
    }

    /**
     * 执行HTTP请求
     * @param userAlbumPicDataOperateType
     */
    public void RequestFromHttp(UserAlbumPicDataOperateType userAlbumPicDataOperateType){
        this._userAlbumPicDataOperateType = userAlbumPicDataOperateType;
        ThreadPoolUtils.execute(this);
    }
}
