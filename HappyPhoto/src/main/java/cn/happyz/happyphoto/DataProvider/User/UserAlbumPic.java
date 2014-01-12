package cn.happyz.happyphoto.DataProvider.User;

/**
 * Created by zcmzc on 13-12-13.
 */
public class UserAlbumPic {

    private int _userAlbumPicId;
    private int _userAlbumId;
    private String _userAlbumPicTitle;
    private String _userAlbumPicIntro;
    private String _userAlbumPicUrl;
    private String _userAlbumPicThumbnailUrl;
    private String _userAlbumPicCompressUrl;
    private String _userAlbumPicTempUrl;
    private String _userAlbumPicCoverUrl;
    private int _isCover;
    private int _state;
    private int _userId;

    public UserAlbumPic(String userAlbumPicUrl){
        //_userAlbumId = userAlbumId;
        _userAlbumPicUrl = userAlbumPicUrl;
        //_userAlbumPicThumbnailUrl = userAlbumPicThumbnailUrl;
    }

    public UserAlbumPic(int userAlbumPicId){
        _userAlbumPicId = userAlbumPicId;
    }

    public int getUserAlbumPicId(){
        return _userAlbumPicId;
    }
    public void setUserAlbumPicId(int userAlbumPicId){
        _userAlbumPicId = userAlbumPicId;
    }

    public int getUserAlbumId(){
        return _userAlbumId;
    }
    public void setUserAlbumId(int userAlbumId){
        _userAlbumId = userAlbumId;
    }

    public int getUserId(){
        return _userId;
    }
    public void setUserId(int userId){
        _userId = userId;
    }

    public int getIsCover(){
        return _isCover;
    }
    public void setIsCover(int isCover){
        _isCover = isCover;
    }

    public String getUserAlbumPicTitle(){
        return _userAlbumPicTitle;
    }
    public void setUserAlbumPicTitle(String userAlbumPicTitle){
        _userAlbumPicTitle = userAlbumPicTitle;
    }

    public String getUserAlbumPicIntro(){
        return _userAlbumPicIntro;
    }
    public void setUserAlbumPicIntro(String userAlbumPicIntro){
        _userAlbumPicIntro = userAlbumPicIntro;
    }


    public String getUserAlbumPicUrl(){
        return _userAlbumPicUrl;
    }
    public void setUserAlbumPicUrl(String userAlbumPicUrl){
        _userAlbumPicUrl = userAlbumPicUrl;
    }

    public String getUserAlbumPicCompressUrl(){
        return _userAlbumPicCompressUrl;
    }
    public void setUserAlbumPicCompressUrl(String userAlbumPicCompressUrl){
        _userAlbumPicCompressUrl = userAlbumPicCompressUrl;
    }


    public String getUserAlbumPicTempUrl(){
        return _userAlbumPicTempUrl;
    }
    public void setUserAlbumPicTempUrl(String userAlbumPicTempUrl){
        _userAlbumPicTempUrl = userAlbumPicTempUrl;
    }

    public String getUserAlbumPicThumbnailUrl(){
        return _userAlbumPicThumbnailUrl;
    }
    public void setUserAlbumPicThumbnailUrl(String userAlbumPicThumbnailUrl){
        _userAlbumPicThumbnailUrl = userAlbumPicThumbnailUrl;
    }
}
