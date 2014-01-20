package cn.happyz.happyphoto.DataProvider.ClientApp;

/**
 * 客户端程序数据对象类
 */
public class ClientApp {

    private int ClientAppId;
    private int ClientAppType;
    private String ClientAppName;
    private String CreateDate;
    private String UpdateDate;
    private int VersionCode;
    private String VersionName;
    private String AppFilePath;

    public ClientApp(int clientAppId, int clientAppType, String clientAppName, String createDate, String updateDate, int versionCode, String versionName, String appFilePath) {
        ClientAppId = clientAppId;
        ClientAppType = clientAppType;
        ClientAppName = clientAppName;
        CreateDate = createDate;
        UpdateDate = updateDate;
        VersionCode = versionCode;
        VersionName = versionName;
        AppFilePath = appFilePath;
    }

    public int getClientAppId() {
        return ClientAppId;
    }

    public void setClientAppId(int clientAppId) {
        ClientAppId = clientAppId;
    }

    public int getClientAppType() {
        return ClientAppType;
    }

    public void setClientAppType(int clientAppType) {
        ClientAppType = clientAppType;
    }

    public String getClientAppName() {
        return ClientAppName;
    }

    public void setClientAppName(String clientAppName) {
        ClientAppName = clientAppName;
    }

    public String getCreateDate() {
        return CreateDate;
    }

    public void setCreateDate(String createDate) {
        CreateDate = createDate;
    }

    public String getUpdateDate() {
        return UpdateDate;
    }

    public void setUpdateDate(String updateDate) {
        UpdateDate = updateDate;
    }

    public int getVersionCode() {
        return VersionCode;
    }

    public void setVersionCode(int versionCode) {
        VersionCode = versionCode;
    }

    public String getVersionName() {
        return VersionName;
    }

    public void setVersionName(String versionName) {
        VersionName = versionName;
    }

    public String getAppFilePath() {
        return AppFilePath;
    }

    public void setAppFilePath(String appFilePath) {
        AppFilePath = appFilePath;
    }
}
