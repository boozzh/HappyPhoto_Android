package cn.happyz.happyphoto.Tools;

/**
 * HTTP执行状态
 */
public enum HttpClientStatus{
    /**
     * 开始通过GET方式获取网络数据
     */
    START_GET,
    /**
     * 完成通过GET方式获取网络数据
     */
    FINISH_GET,
    /**
     * 完成通过GET方式获取网络数据，但是没有数据
     */
    FINISH_GET_BUT_NO_DATA,
    /**
     * 完成通过GET方式获取网络数据，但是会员验证错误
     */
    FINISH_GET_BUT_USER_ERROR,
    /**
     * 通过GET方式获取网络数据出错
     */
    ERROR_GET,
    /**
     * 开始通过POST方式获取网络数据
     */
    START_POST,
    /**
     * 完成通过POST方式获取网络数据
     */
    FINISH_POST,
    /**
     * 完成通过POST方式获取网络数据，但是会员验证错误
     */
    FINISH_POST_BUT_USER_ERROR,
    /**
     * 通过POST方式获取网络数据出错
     */
    ERROR_POST,
    /**
     * 开始下载网络数据，二进制文件
     */
    START_DOWNLOAD,
    /**
     * 正在下载网络数据，二进制文件
     */
    RUNNING_DOWNLOAD,
    /**
     * 完成下载网络数据，二进制文件
     */
    FINISH_DOWNLOAD,
    /**
     * 正在下载网络数据，二进制文件，但是会员验证错误
     */
    FINISH_DOWNLOAD_BUT_USER_ERROR,
    /**
     * 下载网络数据出错，二进制文件
     */
    ERROR_DOWNLOAD
}
