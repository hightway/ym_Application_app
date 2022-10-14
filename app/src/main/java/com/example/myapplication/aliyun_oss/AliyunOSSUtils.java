package com.example.myapplication.aliyun_oss;

import static com.example.myapplication.aliyun_oss.MyOSSConfig.BUCKET_NAME;
import static com.nirvana.tools.core.ComponentSdkCore.getApplicationContext;

import android.content.Context;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import com.alibaba.sdk.android.oss.ClientConfiguration;
import com.alibaba.sdk.android.oss.ClientException;
import com.alibaba.sdk.android.oss.OSS;
import com.alibaba.sdk.android.oss.OSSClient;
import com.alibaba.sdk.android.oss.ServiceException;
import com.alibaba.sdk.android.oss.callback.OSSCompletedCallback;
import com.alibaba.sdk.android.oss.callback.OSSProgressCallback;
import com.alibaba.sdk.android.oss.common.OSSLog;
import com.alibaba.sdk.android.oss.common.auth.OSSCredentialProvider;
import com.alibaba.sdk.android.oss.common.auth.OSSPlainTextAKSKCredentialProvider;
import com.alibaba.sdk.android.oss.common.auth.OSSStsTokenCredentialProvider;
import com.alibaba.sdk.android.oss.internal.OSSAsyncTask;
import com.alibaba.sdk.android.oss.model.CompleteMultipartUploadResult;
import com.alibaba.sdk.android.oss.model.GetObjectRequest;
import com.alibaba.sdk.android.oss.model.GetObjectResult;
import com.alibaba.sdk.android.oss.model.MultipartUploadRequest;
import com.alibaba.sdk.android.oss.model.PutObjectRequest;
import com.alibaba.sdk.android.oss.model.PutObjectResult;
import com.alibaba.sdk.android.oss.model.ResumableUploadRequest;
import com.alibaba.sdk.android.oss.model.ResumableUploadResult;
import com.example.myapplication.MyApp;
import com.example.myapplication.http.UserConfig;
import com.zhy.http.okhttp.utils.L;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.Calendar;
import java.util.HashMap;

public class AliyunOSSUtils {

    private static AliyunOSSUtils mOSSUtils;
    private UploadListener listener;
    private Upload_ParListener listener_part;
    private static OSS oss;
    private static Context context;
    private HashMap hashMap = new HashMap<String, String>() {
        {
            put("callbackUrl", "https://api.orzwe.com/api/ossCallback");
            put("callbackHost", "api.orzwe.com");
            put("callbackBodyType", "application/x-www-form-urlencoded");
            //json
            //put("callbackBody", "{\"mimeType\":${mimeType},\"size\":${size},\"bucket\":${bucket},\"object\":${object},\"imageInfo.format\":${imageInfo.format},\"imageInfo.height\":${imageInfo.height},\"imageInfo.width\":${imageInfo.width}}");
            //x-www-form-urlencoded
            put("callbackBody", "bucket=${bucket}&object=${object}&size=${size}&mimeType=${mimeType}&imageInfo.height=${imageInfo.height}&imageInfo.width=${imageInfo.width}&imageInfo.format=${imageInfo.format}&my_var=${x:my_var}");

        }
    };

    /**
     * 单一实例
     */
    public static AliyunOSSUtils getInstance(Context c) {
        context = c;

        if (mOSSUtils == null) {
            synchronized (AliyunOSSUtils.class) {
                if (mOSSUtils == null) {
                    mOSSUtils = new AliyunOSSUtils();
                }

            }
        }
        return mOSSUtils;
    }

    public AliyunOSSUtils() {
        // 配置类如果不设置，会有默认配置。
        OSSCredentialProvider provider = new OSSStsTokenCredentialProvider(UserConfig.instance().AccessKeyId,
                UserConfig.instance().AccessKeySecret, UserConfig.instance().SecurityToken);

        ClientConfiguration conf = new ClientConfiguration();
        conf.setConnectionTimeout(15 * 1000); // 连接超时，默认15秒。
        conf.setSocketTimeout(15 * 1000); // socket超时，默认15秒。
        conf.setMaxConcurrentRequest(5); // 最大并发请求数，默认5个。
        conf.setMaxErrorRetry(2); // 失败后最大重试次数，默认2次。
        oss = new OSSClient(context.getApplicationContext(), MyOSSConfig.OSS_ENDPOINT, provider, conf);
    }

    /**
     * 上传单个文件
     *
     * @param
     * @param
     */
    //同步上传
    /*public String uploadFile(String name, String localPath) {
        // 构造上传请求。
        PutObjectRequest put = new PutObjectRequest(BUCKET_NAME, name, localPath);
        put.setProgressCallback(new OSSProgressCallback<PutObjectRequest>() {
            @Override
            public void onProgress(PutObjectRequest request, long currentSize, long totalSize) {
                Log.d("PutObject", "currentSize: " + currentSize + " totalSize: " + totalSize);

            }
        });

        try {
            // 开始同步上传
            PutObjectResult result = oss.putObject(put);

            Log.d("upload", "upload: result=" + result);
            // 得到一个外网可访问的地址
            String url = oss.presignPublicObjectURL(BUCKET_NAME, name);
//            if (listener != null)
//                listener.onUpLoadComplete(url);
            // 格式打印输出
            L.e("--------------同步上传：" + url + "-----------");
            Log.d("PublicObjectURL", String.format("PublicObjectURL:%s", url));
            return url;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }*/

    //异步上传
    public void upLoadMultipleFile(String file_name, String path) {

        String name = getNew_File_Name(file_name);

        // 构造上传请求。
        PutObjectRequest put = new PutObjectRequest(BUCKET_NAME, name, path);
        put.setCallbackParam(hashMap);

        // 异步上传时可以设置进度回调。
        put.setProgressCallback(new OSSProgressCallback<PutObjectRequest>() {
            @Override
            public void onProgress(PutObjectRequest request, long currentSize, long totalSize) {
                Log.d("PutObject", "currentSize: " + currentSize + " totalSize: " + totalSize);
                if (listener != null)
                    listener.onUpLoad_Pro(currentSize, totalSize);
            }
        });

        OSSAsyncTask task = oss.asyncPutObject(put, new OSSCompletedCallback<PutObjectRequest, PutObjectResult>() {
            @Override
            public void onSuccess(PutObjectRequest request, PutObjectResult result) {
                Log.d("PutObject", "UploadSuccess");
                Log.d("ETag", result.getETag());
                Log.d("RequestId", result.getRequestId());
                String url = oss.presignPublicObjectURL(BUCKET_NAME, name);

                L.e("图片地址--------------" + url + "-----------------");

                if (listener != null)
                    listener.onUpLoadComplete(url);
            }

            @Override
            public void onFailure(PutObjectRequest request, ClientException clientExcepion, ServiceException serviceException) {
                // 请求异常。
                if (clientExcepion != null) {
                    // 本地异常，如网络异常等。
                    clientExcepion.printStackTrace();
                }
                if (serviceException != null) {
                    // 服务异常。
                    Log.e("ErrorCode", serviceException.getErrorCode());
                    Log.e("RequestId", serviceException.getRequestId());
                    Log.e("HostId", serviceException.getHostId());
                    Log.e("RawMessage", serviceException.getRawMessage());
                }
            }
        });

        // task.cancel(); // 可以取消任务。
        // task.waitUntilFinished(); // 等待上传完成。
    }


    /**
     *  异步分片上传
     * */
    public void MultipartUpload(String file_name, String localFilepath) {

        String objectName = getNew_File_Name(file_name);

        MultipartUploadRequest request = new MultipartUploadRequest(BUCKET_NAME, objectName, localFilepath);
        request.setPartSize(1024 * 1024);
        request.setCallbackParam(hashMap);
        request.setProgressCallback(new OSSProgressCallback<MultipartUploadRequest>() {
            @Override
            public void onProgress(MultipartUploadRequest request, long currentSize, long totalSize) {
                OSSLog.logDebug("[testMultipartUpload] - " + currentSize + " " + totalSize, false);
                if (listener_part != null)
                    listener_part.onUpLoad_PartComplete(currentSize, totalSize);
            }
        });

        OSSAsyncTask task = oss.asyncMultipartUpload(request, new OSSCompletedCallback<MultipartUploadRequest, CompleteMultipartUploadResult>() {
            @Override
            public void onSuccess(MultipartUploadRequest request, CompleteMultipartUploadResult result) {
                OSSLog.logInfo(result.getServerCallbackReturnBody());
            }

            @Override
            public void onFailure(MultipartUploadRequest request, ClientException clientException, ServiceException serviceException) {
                OSSLog.logError(serviceException.getRawMessage());
            }
        });

        //Thread.sleep(100);
        // 取消分片上传。
        //task.cancel();
        task.waitUntilFinished();
    }



    /**
     *  异步断点上传
     * */
    public void ResumableUpload(String file_name, String localFilepath) {

        String objectName = getNew_File_Name(file_name);

        String recordDirectory = Environment.getExternalStorageDirectory().getAbsolutePath() + "/oss_record/";
        File recordDir = new File(recordDirectory);

        // 确保断点记录的保存路径已存在，如果不存在则新建断点记录的保存路径。
        if (!recordDir.exists()) {
            recordDir.mkdirs();
        }

        // 创建断点上传请求，并指定断点记录文件的保存路径，保存路径为断点记录文件的绝对路径。
        ResumableUploadRequest request = new ResumableUploadRequest(BUCKET_NAME, objectName, localFilepath, recordDirectory);
        request.setCallbackParam(hashMap);
        // 设置上传过程回调。
        request.setProgressCallback(new OSSProgressCallback<ResumableUploadRequest>() {
            @Override
            public void onProgress(ResumableUploadRequest request, long currentSize, long totalSize) {
                Log.d("resumableUpload", "currentSize: " + currentSize + " totalSize: " + totalSize);
            }
        });


        OSSAsyncTask resumableTask = oss.asyncResumableUpload(request, new OSSCompletedCallback<ResumableUploadRequest, ResumableUploadResult>() {
            @Override
            public void onSuccess(ResumableUploadRequest request, ResumableUploadResult result) {
                Log.d("resumableUpload", "success!");
            }

            @Override
            public void onFailure(ResumableUploadRequest request, ClientException clientExcepion, ServiceException serviceException) {
                // 异常处理。
            }
        });

        // 等待完成断点上传任务。
        resumableTask.waitUntilFinished();
    }




    /**
     * 下载文件
     *
     * @param name
     */
    public void downLoadFile(String name) {
        // 构造下载文件请求。
        GetObjectRequest get = new GetObjectRequest(BUCKET_NAME, name);
        OSSAsyncTask task = oss.asyncGetObject(get, new OSSCompletedCallback<GetObjectRequest, GetObjectResult>() {
            @Override
            public void onSuccess(GetObjectRequest request, GetObjectResult result) {
                // 请求成功。
                Log.d("asyncGetObject", "DownloadSuccess");
                Log.d("Content-Length", "" + result.getContentLength());

                InputStream inputStream = result.getObjectContent();
                byte[] buffer = new byte[2048];
                int len;

                try {
                    while ((len = inputStream.read(buffer)) != -1) {
                        // 您可以在此处编写代码来处理下载的数据。

                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            @Override
            // GetObject请求成功，将返回GetObjectResult，其持有一个输入流的实例。返回的输入流，请自行处理。
            public void onFailure(GetObjectRequest request, ClientException clientExcepion, ServiceException serviceException) {
                // 请求异常。
                if (clientExcepion != null) {
                    // 本地异常，如网络异常等。
                    clientExcepion.printStackTrace();
                }
                if (serviceException != null) {
                    // 服务异常。
                    Log.e("ErrorCode", serviceException.getErrorCode());
                    Log.e("RequestId", serviceException.getRequestId());
                    Log.e("HostId", serviceException.getHostId());
                    Log.e("RawMessage", serviceException.getRawMessage());
                }
            }
        });
        // task.cancel(); // 可以取消任务。
        // task.waitUntilFinished(); // 等待任务完成。
    }


    public void setUpLoadListener(UploadListener listener) {
        this.listener = listener;
    }
    public interface UploadListener {
        void onUpLoadComplete(String url);
        void onUpLoad_Pro(long part, long total);
    }


    public void setUpLoad_PartListener(Upload_ParListener listener) {
        this.listener_part = listener;
    }
    public interface Upload_ParListener {
        void onUpLoad_PartComplete(long part, long total);
    }






    private String getNew_File_Name(String file_name) {
        if(has_Chinese(file_name)){
            //转码
            String code = StrtoGBK(file_name);
            //拼接日期作为上级目录
            return getDate()+"/"+code;
        }else{
            return getDate()+"/"+file_name;
        }
    }

    // 判断一个字符串是否含有中文
    public boolean has_Chinese(String str) {
        if (str == null)
            return false;
        for (char c : str.toCharArray()) {
            if (is_Chinese(c))
                // 有一个中文字符就返回
                return true;
        }
        return false;
    }

    public boolean is_Chinese(char c) {
        // 根据字节码判断
        return c >= 0x4E00 && c <= 0x9FA5;
    }

    public String StrtoGBK(String str){
        try {
            String strGBK = URLEncoder.encode(str, "GBK");
            //Log.e("=====strGBK", strGBK);
            String strUTF8 = URLDecoder.decode(strGBK, "UTF-8");
            //Log.e("=====strUTF8", strUTF8);
            return strUTF8;
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return "";
        }
    }

    public String getDate(){
        Calendar c = Calendar.getInstance();
        int day = c.get(Calendar.DAY_OF_MONTH);
        int month = c.get(Calendar.MONTH);
        int year = c.get(Calendar.YEAR);
        String date = String.valueOf(year)+String.valueOf(month+1)+String.valueOf(day);
        return date;
    }


}

