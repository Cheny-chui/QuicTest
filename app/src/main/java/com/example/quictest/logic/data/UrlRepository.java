package com.example.quictest.logic.data;

public class UrlRepository {
    private static final String[] Urls = {
            "https://http3check.net/", // 用以测试0RTT
            "https://http3check.net/?host=www.google.com", // 用以测试download
    };

    public static int numberOfImages(){
        return Urls.length;
    }

    public static String getUrl(int position){
        return Urls[position];
    }
}
