package com.example.quictest.logic;


import org.chromium.net.CronetException;
import org.chromium.net.UrlRequest;
import org.chromium.net.UrlResponseInfo;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.WritableByteChannel;

// 提供给ViewModel层继承的抽象类
abstract public class ReadToMemoryCronetCallback extends UrlRequest.Callback {
    private static final String TAG = "ReadToMemoryCronetCallback";
    private static final int BYTE_BUFFER_CAPACITY_BYTES = 64*1024;

    private final ByteArrayOutputStream bytesReceived = new ByteArrayOutputStream();
    private final WritableByteChannel receiveChannel = Channels.newChannel(bytesReceived);
    // 储存纳秒级时间
    private final long startTimeNanos;

    public ReadToMemoryCronetCallback() {
        startTimeNanos = System.nanoTime();
    }

    @Override
    public void onRedirectReceived(UrlRequest request, UrlResponseInfo info, String newLocationUrl) throws Exception {
        android.util.Log.i(TAG, "****** 重定向 ******");
        request.followRedirect();
    }

    @Override
    public void onResponseStarted(UrlRequest request, UrlResponseInfo info) throws Exception {
        long latencyNanos = System.nanoTime() - startTimeNanos;
        onConnected(request,info,latencyNanos);

        android.util.Log.i(TAG,
                "****** " +"使用协议：" + info.getNegotiatedProtocol());
        // http头受到后调用
        android.util.Log.i(TAG, "*** 收到Http头：*** " + info.getAllHeaders());

        // 读取http体
        request.read(ByteBuffer.allocateDirect(BYTE_BUFFER_CAPACITY_BYTES));
    }

    @Override
    public void onReadCompleted(UrlRequest request, UrlResponseInfo info, ByteBuffer byteBuffer) throws Exception {
//        android.util.Log.i(TAG, "****** 此包大小： ******" + byteBuffer);

        // 调整为读模式
        byteBuffer.flip();

        try {
            receiveChannel.write(byteBuffer);
        } catch (IOException e) {
            android.util.Log.i(TAG, "IOException during ByteBuffer read. Details: ", e);
        }

        byteBuffer.clear();

        // 继续读下一个包
        request.read(byteBuffer);
    }

    @Override
    public void onSucceeded(UrlRequest request, UrlResponseInfo info) {
        long latencyNanos = System.nanoTime() - startTimeNanos;

        android.util.Log.i(TAG,
                "****** 传输完成，总延迟为： " + latencyNanos + "ns" +
                        ". ");

        android.util.Log.i(TAG,
                "****** Http状态码：" + info.getHttpStatusCode()
                        + ", 共接收 " + info.getReceivedByteCount() + " 字节");

        byte[] bodyBytes = bytesReceived.toByteArray();
        onSucceeded(request,info,bodyBytes,latencyNanos);
    }

    @Override
    public void onFailed(UrlRequest request, UrlResponseInfo info, CronetException error) {

    }

    protected abstract void onConnected(UrlRequest request, UrlResponseInfo info, long latencyNanos);

    protected abstract void onSucceeded(
            UrlRequest request, UrlResponseInfo info, byte[] bodyBytes, long latencyNanos);


}

