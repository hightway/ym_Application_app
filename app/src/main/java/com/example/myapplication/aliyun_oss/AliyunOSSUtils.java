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
     * ????????????
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
        // ????????????????????????????????????????????????
        OSSCredentialProvider provider = new OSSStsTokenCredentialProvider(UserConfig.instance().AccessKeyId,
                UserConfig.instance().AccessKeySecret, UserConfig.instance().SecurityToken);

        ClientConfiguration conf = new ClientConfiguration();
        conf.setConnectionTimeout(15 * 1000); // ?????????????????????15??????
        conf.setSocketTimeout(15 * 1000); // socket???????????????15??????
        conf.setMaxConcurrentRequest(5); // ??????????????????????????????5??????
        conf.setMaxErrorRetry(2); // ????????????????????????????????????2??????
        oss = new OSSClient(context.getApplicationContext(), MyOSSConfig.OSS_ENDPOINT, provider, conf);
    }

    /**
     * ??????????????????
     *
     * @param
     * @param
     */
    //????????????
    /*public String uploadFile(String name, String localPath) {
        // ?????????????????????
        PutObjectRequest put = new PutObjectRequest(BUCKET_NAME, name, localPath);
        put.setProgressCallback(new OSSProgressCallback<PutObjectRequest>() {
            @Override
            public void onProgress(PutObjectRequest request, long currentSize, long totalSize) {
                Log.d("PutObject", "currentSize: " + currentSize + " totalSize: " + totalSize);

            }
        });

        try {
            // ??????????????????
            PutObjectResult result = oss.putObject(put);

            Log.d("upload", "upload: result=" + result);
            // ????????????????????????????????????
            String url = oss.presignPublicObjectURL(BUCKET_NAME, name);
//            if (listener != null)
//                listener.onUpLoadComplete(url);
            // ??????????????????
            L.e("--------------???????????????" + url + "-----------");
            Log.d("PublicObjectURL", String.format("PublicObjectURL:%s", url));
            return url;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }*/

    //????????????
    public void upLoadMultipleFile(String file_name, String path) {

        String name = getNew_File_Name(file_name);

        // ?????????????????????
        PutObjectRequest put = new PutObjectRequest(BUCKET_NAME, name, path);
        put.setCallbackParam(hashMap);

        // ??????????????????????????????????????????
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

                L.e("????????????--------------" + url + "-----------------");

                if (listener != null)
                    listener.onUpLoadComplete(url);
            }

            @Override
            public void onFailure(PutObjectRequest request, ClientException clientExcepion, ServiceException serviceException) {
                // ???????????????
                if (clientExcepion != null) {
                    // ????????????????????????????????????
                    clientExcepion.printStackTrace();
                }
                if (serviceException != null) {
                    // ???????????????
                    Log.e("ErrorCode", serviceException.getErrorCode());
                    Log.e("RequestId", serviceException.getRequestId());
                    Log.e("HostId", serviceException.getHostId());
                    Log.e("RawMessage", serviceException.getRawMessage());
                }
            }
        });

        // task.cancel(); // ?????????????????????
        // task.waitUntilFinished(); // ?????????????????????
    }


    /**
     *  ??????????????????
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
        // ?????????????????????
        //task.cancel();
        task.waitUntilFinished();
    }



    /**
     *  ??????????????????
     * */
    public void ResumableUpload(String file_name, String localFilepath) {

        String objectName = getNew_File_Name(file_name);

        String recordDirectory = Environment.getExternalStorageDirectory().getAbsolutePath() + "/oss_record/";
        File recordDir = new File(recordDirectory);

        // ???????????????????????????????????????????????????????????????????????????????????????????????????
        if (!recordDir.exists()) {
            recordDir.mkdirs();
        }

        // ???????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????
        ResumableUploadRequest request = new ResumableUploadRequest(BUCKET_NAME, objectName, localFilepath, recordDirectory);
        request.setCallbackParam(hashMap);
        // ???????????????????????????
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
                // ???????????????
            }
        });

        // ?????????????????????????????????
        resumableTask.waitUntilFinished();
    }




    /**
     * ????????????
     *
     * @param name
     */
    public void downLoadFile(String name) {
        // ???????????????????????????
        GetObjectRequest get = new GetObjectRequest(BUCKET_NAME, name);
        OSSAsyncTask task = oss.asyncGetObject(get, new OSSCompletedCallback<GetObjectRequest, GetObjectResult>() {
            @Override
            public void onSuccess(GetObjectRequest request, GetObjectResult result) {
                // ???????????????
                Log.d("asyncGetObject", "DownloadSuccess");
                Log.d("Content-Length", "" + result.getContentLength());

                InputStream inputStream = result.getObjectContent();
                byte[] buffer = new byte[2048];
                int len;

                try {
                    while ((len = inputStream.read(buffer)) != -1) {
                        // ?????????????????????????????????????????????????????????

                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            @Override
            // GetObject????????????????????????GetObjectResult??????????????????????????????????????????????????????????????????????????????
            public void onFailure(GetObjectRequest request, ClientException clientExcepion, ServiceException serviceException) {
                // ???????????????
                if (clientExcepion != null) {
                    // ????????????????????????????????????
                    clientExcepion.printStackTrace();
                }
                if (serviceException != null) {
                    // ???????????????
                    Log.e("ErrorCode", serviceException.getErrorCode());
                    Log.e("RequestId", serviceException.getRequestId());
                    Log.e("HostId", serviceException.getHostId());
                    Log.e("RawMessage", serviceException.getRawMessage());
                }
            }
        });
        // task.cancel(); // ?????????????????????
        // task.waitUntilFinished(); // ?????????????????????
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
            //??????
            String code = StrtoGBK(file_name);
            //??????????????????????????????
            return getDate()+"/"+code;
        }else{
            return getDate()+"/"+file_name;
        }
    }

    // ???????????????????????????????????????
    public boolean has_Chinese(String str) {
        if (str == null)
            return false;
        for (char c : str.toCharArray()) {
            if (is_Chinese(c))
                // ??????????????????????????????
                return true;
        }
        return false;
    }

    public boolean is_Chinese(char c) {
        // ?????????????????????
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

