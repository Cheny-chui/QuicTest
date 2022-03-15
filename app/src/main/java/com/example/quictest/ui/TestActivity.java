package com.example.quictest.ui;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.quictest.R;
import com.example.quictest.logic.CronetApplication;
import com.example.quictest.logic.ReadToMemoryCronetCallback;
import com.example.quictest.logic.data.UrlRepository;
import com.example.quictest.util.Chart;

import org.chromium.net.UrlRequest;
import org.chromium.net.UrlResponseInfo;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;

public class TestActivity extends AppCompatActivity {

    private static final String TAG = "RttActivity";

    private Chart chart;
    private final int ceiling = 16;

    private AtomicInteger h2Cnt = new AtomicInteger(0);
    private AtomicInteger h3Cnt = new AtomicInteger(0);

    private MainActivity.TestMode testMode;

    private int index = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);

        Intent intent = getIntent();
        testMode = (MainActivity.TestMode) intent.getSerializableExtra("testMode");

        if(testMode== MainActivity.TestMode.RTT){
            index = 0;
        }else{
            index = 1;
        }

        setUpToolbar();

        setUpButton();

        chart = new Chart(findViewById(R.id.chart));

        chart.buildChart();
    }

    private void setUpToolbar(){
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayShowTitleEnabled(false);
        ((TextView) toolbar.findViewById(R.id.toolbar_title)).setText(R.string.app_name);
    }

    private void setUpButton(){
        Button h2Button = findViewById(R.id.h2_start_button);
        Button h3Button = findViewById(R.id.h3_start_button);
        CronetApplication cronetApplication = (CronetApplication) getApplication();


        h2Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Thread thread = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        ReadToMemoryCronetCallback callback = new ReadToMemoryCronetCallback() {
                            @Override
                            protected void onConnected(UrlRequest request, UrlResponseInfo info, long latencyNanos) {

                            }

                            @Override
                            protected void onSucceeded(UrlRequest request, UrlResponseInfo info, byte[] bodyBytes, long latencyNanos) {
                                chart.addChartData(Chart.DataSetIndex.H2, latencyNanos);
                                h2Cnt.incrementAndGet();
                                if (((TextView) findViewById(R.id.h3_protocol)).getText() != info.getNegotiatedProtocol()) {
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            ((TextView) findViewById(R.id.h2_protocol)).setText(info.getNegotiatedProtocol());
                                        }
                                    });
                                }
                            }
                        };
                        // 建立 ceiling 次连接，对比时间.
                        for (int i = 0; i < ceiling; ++i) {
                            if(testMode== MainActivity.TestMode.RTT) {
                                callback = new ReadToMemoryCronetCallback() {
                                    @Override
                                    protected void onConnected(UrlRequest request, UrlResponseInfo info, long latencyNanos) {
                                        chart.addChartData(Chart.DataSetIndex.H2, latencyNanos);
                                        h2Cnt.incrementAndGet();
                                        if (((TextView) findViewById(R.id.h3_protocol)).getText() != info.getNegotiatedProtocol()) {
                                            runOnUiThread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    ((TextView) findViewById(R.id.h2_protocol)).setText(info.getNegotiatedProtocol());
                                                }
                                            });
                                        }
                                        request.cancel();
                                    }

                                    @Override
                                    protected void onSucceeded(UrlRequest request, UrlResponseInfo info, byte[] bodyBytes, long latencyNanos) {
                                    }

                                    @Override
                                    public void onCanceled(UrlRequest request, UrlResponseInfo info) {
                                        super.onCanceled(request, info);
                                    }
                                };
                            }
                            UrlRequest.Builder h2Builder = cronetApplication.getTcpEngine()
                                    .newUrlRequestBuilder(
                                            UrlRepository.getUrl(index),
                                            callback,
                                            cronetApplication.getTcpExecutorService()
                                    );
                            h2Builder.build().start();
                            android.util.Log.i(TAG, "h2连接：" + UrlRepository.getUrl(index));
                        }

                        while (h2Cnt.get() < ceiling) {
                            android.util.Log.i(TAG, "h2Cnt: " + h2Cnt.get() + " ceiling: " + ceiling);
                            try {
                                Thread.sleep(1000);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                        chart.update();
                    }
                });
                thread.start();
            }
        });

        h3Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Thread thread = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        ReadToMemoryCronetCallback callback = new ReadToMemoryCronetCallback() {
                            @Override
                            protected void onConnected(UrlRequest request, UrlResponseInfo info, long latencyNanos) {

                            }

                            @Override
                            protected void onSucceeded(UrlRequest request, UrlResponseInfo info, byte[] bodyBytes, long latencyNanos) {
                                chart.addChartData(Chart.DataSetIndex.H3, latencyNanos);
                                h3Cnt.incrementAndGet();
                                if (((TextView) findViewById(R.id.h3_protocol)).getText() != info.getNegotiatedProtocol()) {
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            ((TextView) findViewById(R.id.h3_protocol)).setText(info.getNegotiatedProtocol());
                                        }
                                    });
                                }
                            }
                        };
                        // 建立 ceiling 次连接，对比时间
                        for (int i = 0; i < ceiling; ++i) {
                            if(testMode== MainActivity.TestMode.RTT) {
                                callback = new ReadToMemoryCronetCallback() {
                                    @Override
                                    protected void onConnected(UrlRequest request, UrlResponseInfo info, long latencyNanos) {

                                        chart.addChartData(Chart.DataSetIndex.H3, latencyNanos);
                                        h3Cnt.incrementAndGet();
                                        if (((TextView) findViewById(R.id.h3_protocol)).getText() != info.getNegotiatedProtocol()) {
                                            runOnUiThread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    ((TextView) findViewById(R.id.h3_protocol)).setText(info.getNegotiatedProtocol());
                                                }
                                            });
                                        }
                                        request.cancel();

                                    }

                                    @Override
                                    protected void onSucceeded(UrlRequest request, UrlResponseInfo info, byte[] bodyBytes, long latencyNanos) {
                                    }

                                    @Override
                                    public void onCanceled(UrlRequest request, UrlResponseInfo info) {
                                        super.onCanceled(request, info);
                                    }
                                };
                            }
                            UrlRequest.Builder h3Builder = cronetApplication.getQuicEngine()
                                    .newUrlRequestBuilder(
                                            UrlRepository.getUrl(index),
                                            callback,
                                            cronetApplication.getQuicExecutorService()
                                    );
                            h3Builder.build().start();
                            android.util.Log.i(TAG, "h3连接：" + UrlRepository.getUrl(index));
                        }

                        while (h3Cnt.get() < ceiling) {
                            android.util.Log.i(TAG, "h3Cnt: " + h3Cnt.get() + " ceiling: " + ceiling);
                            try {
                                Thread.sleep(1000);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                        chart.update();
                    }
                });
                thread.start();
            }
        });

    }
}