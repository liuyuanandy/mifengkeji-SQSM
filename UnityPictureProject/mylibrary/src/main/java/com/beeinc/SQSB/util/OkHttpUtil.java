package com.beeinc.SQSB.util;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class OkHttpUtil {
    public static void  downLoadFile(final String url, final String filePath, final DownloadCallBack callBack){
        new Thread(){
            public void run(){
//                OkHttpClient client = new OkHttpClient();//创建OkHttpClient对象。
                OkHttpClient okHttpClient = new OkHttpClient.Builder()
                        .connectTimeout(20, TimeUnit.SECONDS)
                        .build();
                Request request = new Request.Builder()//创建Request 对象。
                        .url(url)
                        .build();
                okHttpClient.newCall(request).enqueue(new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        callBack.failed();
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {

                        ResponseBody responseBody = null;
                        BufferedInputStream bis = null;
                        FileOutputStream fos = null;
                        try {
                            if (call.isCanceled()) {
                                return;
                            }
                            if (response.isSuccessful()) {
                                responseBody = response.body();
                                long total = responseBody.contentLength();
                                bis = new BufferedInputStream(responseBody.byteStream());
                                File file = new File(filePath);
                                fos = new FileOutputStream(file);
                                byte[] bytes = new byte[1024 * 8];
                                int len;
                                long current = 0;
                                while ((len = bis.read(bytes)) != -1) {
                                    fos.write(bytes, 0, len);
                                    fos.flush();
                                    current += len;
                                    //计算进度
                                    int progress = (int) (100 * current / total);
                                }
                                if(callBack!=null){
                                    callBack.success(filePath);
                                }

                            } else {
                                if(callBack!=null){
                                    callBack.failed();
                                }
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            if(callBack!=null){
                                callBack.failed();
                            }
                        } finally {
                            if (null != responseBody) {
                                responseBody.close();
                            }
                            if(bis!=null){
                                bis.close();
                            }
                            if(fos!=null){
                                fos.close();
                            }
                        }

//                        try{
//                            InputStream is = response.body().byteStream();//从服务器得到输入流对象
//                            long sum = 0;
//                            File file = new File(filePath);
//                            FileOutputStream fos = new FileOutputStream(file);
//                            byte[] buf = new byte[1024*8];
//                            int len = 0;
//                            while ((len = is.read(buf)) != -1){
//                                fos.write(buf, 0, len);
//                            }
//                            fos.flush();
//                            callBack.success(filePath);
//                        }catch (Exception e){
//                            callBack.failed();
//                        }
                    }
                });//回调方法的使用与get异步请求相同，此时略。
            }
        }.start();

    }

    public interface DownloadCallBack{
        public void success(String filePath);
        public void failed();
    }

}
