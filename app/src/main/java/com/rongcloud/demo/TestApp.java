package com.rongcloud.demo;

import android.app.Application;
import android.net.http.SslCertificate;
import android.util.Log;

import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import io.rong.calllib.RongCallClient;
import io.rong.common.utils.SSLUtils;
import io.rong.imkit.RongIM;
import io.rong.imkit.config.FeatureConfig;
import io.rong.imkit.config.RongConfigCenter;
import io.rong.imlib.RongCoreClient;
import io.rong.imlib.model.InitOption;

public class TestApp extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
       // setSSL();
        InitOption initOption = new InitOption.Builder().build();
        // 私有 navi地址是必须设置的，注意这个地址不能设置错了，一般是有im-nav 这样的字段
        //initOption.setNaviServer("https://xxxx/fcs-im-nav");
        // 文件服务器地址，非必须，根据您的配置设置是否配置
       // initOption.setFileServer("https://xxxx/fcs-im-file");
        // 初始化 必须调用，注意参数都是必填
        RongIM.init(this, "p5tvi9dspl334",initOption);
        //如果只用到了imlib，RongCoreClient.init;
      // RongCoreClient.init(this, "pgyu6aty8i3xu",initOption);
    }



    private void setSSL() {
        SSLContext mySSLContext = getSslContext();
        if (mySSLContext != null) {
            // 设置 SDK 内部的上传下载支持自签证书
            SSLUtils.setSSLContext(mySSLContext);
            // 并且把 Glide 设置成支持自签证书（glide 内部是 HttpsURLConnection）
            // SDK 内置的图片预览用的是 Glide
            HttpsURLConnection.setDefaultSSLSocketFactory(mySSLContext.getSocketFactory());
        }
        HostnameVerifier hostnameVerifier = getHostnameVerifier();
        if (hostnameVerifier != null) {
            // 设置 SDK 内部的上传下载支持自签证书
            SSLUtils.setHostnameVerifier(hostnameVerifier);
            // 并且把 Glide 设置成支持自签证书（glide 内部是 HttpsURLConnection）
            // SDK 内置的图片预览用的是 Glide
            HttpsURLConnection.setDefaultHostnameVerifier(hostnameVerifier);
        }
        // 设置合并转发消息(WebView)支持自签证书
        RongConfigCenter.featureConfig()
                .setSSLInterceptor(
                        new FeatureConfig.SSLInterceptor() {
                            @Override
                            public boolean check(SslCertificate sslCertificate) {
                                return true;
                            }
                        });

        RongCallClient.setPreconnectEnabled(false);
    }

    private SSLContext getSslContext() {
        TrustManager tm[] = {
                new X509TrustManager() {
                    @Override
                    public void checkClientTrusted(X509Certificate[] chain, String authType)
                            throws CertificateException {
                        Log.d("checkClientTrusted", "authType:" + authType);
                    }

                    @Override
                    public void checkServerTrusted(X509Certificate[] chain, String authType)
                            throws CertificateException {
                        Log.d("checkServerTrusted", "authType:" + authType);
                    }

                    @Override
                    public X509Certificate[] getAcceptedIssuers() {
                        return new X509Certificate[0];
                    }
                }
        };

        SSLContext mySSLContext = null;
        try {
            mySSLContext = SSLContext.getInstance("TLS");
            mySSLContext.init(null, tm, null);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return mySSLContext;
    }

    private HostnameVerifier getHostnameVerifier() {
        HostnameVerifier hostnameVerifier =
                new HostnameVerifier() {
                    @Override
                    public boolean verify(String hostname, SSLSession session) {
                        return true;
                    }
                };
        return hostnameVerifier;
    }
}
