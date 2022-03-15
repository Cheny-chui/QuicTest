package com.example.quictest.logic;

import android.app.Application;
import android.content.Context;

import org.chromium.net.CronetEngine;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class CronetApplication extends Application {
    private CronetEngine tcpEngine;
    private CronetEngine quicEngine;

    private ExecutorService tcpExecutorSevice;
    private ExecutorService quicExecutorSevice;

    @Override
    public void onCreate(){
        super.onCreate();
        tcpEngine = createTcpEngine(this);
        quicEngine = createQuicEngine(this);
        tcpExecutorSevice = Executors.newFixedThreadPool(8);
        quicExecutorSevice = Executors.newFixedThreadPool(8);
    }

    public ExecutorService getTcpExecutorService(){
        return tcpExecutorSevice;
    }
    public ExecutorService getQuicExecutorService(){
        return quicExecutorSevice;
    }

    public CronetEngine getTcpEngine(){
        return tcpEngine;
    }

    public CronetEngine getQuicEngine(){
        return quicEngine;
    }

    // engine不支持缓存、不支持Brotli压缩以充分测试性能
    private static CronetEngine createTcpEngine(Context context){
        return new CronetEngine.Builder(context)
                .enableHttp2(true)
                .enableQuic(false)
                .build();
    }
    private static CronetEngine createQuicEngine(Context context){

        return new CronetEngine.Builder(context)
                .setStoragePath(context.getFilesDir().getAbsolutePath())
                // 允许下一次自动使用Http3，并不保存http应答以控制变量
                .enableHttpCache(CronetEngine.Builder.HTTP_CACHE_DISK_NO_HTTP, 100 * 1024)
                .enableHttp2(true)
                .enableQuic(true)
                .addQuicHint("http3check.net", 443, 443)
                .addQuicHint("www.google.com.hk", 443, 443)
                .addQuicHint("www.facebook.com", 443, 443)
                .build();
    }

}
