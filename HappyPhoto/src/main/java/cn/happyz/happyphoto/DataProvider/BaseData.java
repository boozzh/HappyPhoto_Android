package cn.happyz.happyphoto.DataProvider;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.ContentBody;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import cn.happyz.happyphoto.Tools.HttpClientHelper;
import cn.happyz.happyphoto.Tools.HttpClientHelperForBigData;
import cn.happyz.happyphoto.Tools.HttpClientStatus;

/**
 * Created by zcmzc on 13-11-18.
 */
public class BaseData {

    /**
     * 运行HTTP GET操作
     *
     * @param httpUrl 请求网址
     * @param handler 发送结果消息的句柄
     * @return
     */
    public String RunGet(String httpUrl, Handler handler) {

        HttpGet httpGet = new HttpGet(httpUrl);

        //为这个HttpGet设置一些特定的属性，别的属性沿用HttpClient
        HttpParams params = new BasicHttpParams();
        HttpConnectionParams.setConnectionTimeout(params, 60000);
        httpGet.setParams(params);

        handler.sendEmptyMessage(HttpClientStatus.START_GET.ordinal());

        try {

            HttpResponse httpResponse = HttpClientHelper.GetHttpClient().execute(httpGet);

            byte[] bytes = EntityUtils.toByteArray(httpResponse.getEntity());
            //在大多数情况下，这个下载下来的是XML或者Json。应该解析完组装成对象再放置到Message中。
            //这里简单起见，直接变成字符串打印了
            String result = new String(bytes);
            return result;

        } catch (Exception ex) {
            ex.printStackTrace();
            handler.sendEmptyMessage(HttpClientStatus.ERROR_GET.ordinal());
            return null;
        }
    }

    /**
     * 运行HTTP Post操作
     *
     * @param httpUrl   请求网址
     * @param handler   发送结果消息的句柄
     * @param isBigData 是否是大数据操作
     * @return
     */
    public String RunPost(String httpUrl, Handler handler, Map<String, Object> params, boolean isBigData) throws UnsupportedEncodingException {
        if(httpUrl == null){
            return null;
        }
        HttpPost httpPost = new HttpPost(httpUrl);

        //为这个HttpGet设置一些特定的属性，别的属性沿用HttpClient
        HttpParams httpParams = new BasicHttpParams();
        httpParams.setParameter("charset", HTTP.UTF_8);
        HttpConnectionParams.setConnectionTimeout(httpParams, 60000);
        httpPost.setParams(httpParams);
        httpPost.addHeader("charset", HTTP.UTF_8);

        //封装数据

        handler.sendEmptyMessage(HttpClientStatus.START_POST.ordinal());

        MultipartEntityBuilder multipartEntityBuilder = MultipartEntityBuilder.create();
        multipartEntityBuilder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);
        Set<Map.Entry<String, Object>> set = params.entrySet();
        for (Iterator<Map.Entry<String, Object>> it = set.iterator(); it.hasNext(); ) {
            Map.Entry<String, Object> entry = it.next();
            ContentBody contentBody = null;
            if (entry.getKey().toString().startsWith("file_")) {
                File file = new File(entry.getValue().toString());
                contentBody = new FileBody(file);
            } else {
                //String content = entry.getValue().toString();

                String content = URLEncoder.encode(entry.getValue().toString(), "UTF-8");
                contentBody = new StringBody(content, ContentType.MULTIPART_FORM_DATA);
            }

            multipartEntityBuilder.addPart(entry.getKey(), contentBody);
            //System.out.println(entry.getKey() + "--->" + entry.getValue());
        }

        httpPost.setEntity(multipartEntityBuilder.build());

        try {
            //执行
            HttpResponse httpResponse = null;
            if (isBigData) {
                httpResponse = HttpClientHelperForBigData.GetHttpClient().execute(httpPost);
            } else {
                httpResponse = HttpClientHelper.GetHttpClient().execute(httpPost);
            }

            byte[] bytes = EntityUtils.toByteArray(httpResponse.getEntity());
            String result = new String(bytes); //结果字符串
            return result;

        } catch (Exception ex) {
            //ex.printStackTrace();
            Log.e("ZZZ", "Exception: " + Log.getStackTraceString(ex));
            //Log.e("camera_error",ex.getStackTrace().toString());
            handler.sendEmptyMessage(HttpClientStatus.ERROR_POST.ordinal());
            return null;
        } finally {
        }
    }

    /**
     * 从HTTP网址中下载文件
     * @param httpUrl
     * @param handler
     * @param fileSavePath
     */
    public void GetFileFromUrl(String httpUrl, Handler handler, String fileSavePath){

        if(httpUrl == null || fileSavePath == null){
            return;
        }



        HttpGet httpGet = new HttpGet(httpUrl);

        try {
            //执行
            HttpResponse httpResponse = HttpClientHelperForBigData.GetHttpClient().execute(httpGet);

            HttpEntity httpEntity = httpResponse.getEntity();

            long length = httpEntity.getContentLength();
            Message msg = handler.obtainMessage();
            msg.what = HttpClientStatus.START_DOWNLOAD.ordinal();
            msg.obj = length;
            handler.sendMessage(msg);
            //handler.sendEmptyMessage(HttpClientStatus.START_DOWNLOAD.ordinal());
            InputStream inputStream = httpEntity.getContent();

            File file = new File(fileSavePath);
            if(file.exists())
            {
                file.delete();
            }

            FileOutputStream fileOutputStream = new FileOutputStream(file);
            int count = 0;
            byte buffer[] = new byte[512];

            do{
                int numberOfRead = inputStream.read(buffer);
                count += numberOfRead;
                //int progress =(int)(((float)count / length) * 100);
                //handler.sendEmptyMessage(progress);
                //updateHandler.sendMessage(updateHandler.obtainMessage(UPDATE_DOWNLOADING));
                if(numberOfRead <= 0){
                    handler.sendEmptyMessage(HttpClientStatus.FINISH_DOWNLOAD.ordinal());
                    break;
                }else{
                    Message msg2 = handler.obtainMessage();
                    msg2.what = HttpClientStatus.RUNNING_DOWNLOAD.ordinal();
                    msg2.obj = count;
                    handler.sendMessage(msg2);
                }
                fileOutputStream.write(buffer, 0, numberOfRead);
            }while (true);
            fileOutputStream.close();
            inputStream.close();
        } catch (Exception ex) {
            //ex.printStackTrace();
            Log.e("ZZZ", "Exception: " + Log.getStackTraceString(ex));
            //Log.e("camera_error",ex.getStackTrace().toString());
            handler.sendEmptyMessage(HttpClientStatus.ERROR_DOWNLOAD.ordinal());
        } finally {
        }
    }
}
