package com.example.app;

import android.app.Activity;
import android.util.Log;

import com.alibaba.fastjson.JSONObject;

import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Method;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;
import okio.ByteString;

public final class WebSocketClient extends WebSocketListener {

    private final String Tag = "WebSocketClient";

    public WebSocket webSocket;

    private Activity ownerActivity;


    //初始化WebSocket连接
    public void init(String url, Activity owner) {
        ownerActivity = owner;
        OkHttpClient client = new OkHttpClient.Builder()
                .readTimeout(0, TimeUnit.MILLISECONDS)
                .build();
        Request request = new Request.Builder()
                .url(url)
                .build();
        client.newWebSocket(request, this);
        client.dispatcher().executorService().shutdown();
    }

    public void sendMessage(String stringMessage) {
        if (webSocket == null) {
            throw new RuntimeException("连接断开");
        }
        webSocket.send(stringMessage);
    }

    public void sendMessage(ByteString byteStringMessage) {
        if (webSocket == null) {
            throw new RuntimeException("连接断开");
        }
        webSocket.send(byteStringMessage);
    }

    public void close() {
        webSocket.close(1000, "正常关闭!");
    }

    @Override
    public void onOpen(@NotNull WebSocket ws, Response response) {
        Log.i(Tag, "连接应答: " + response.message());
        webSocket = ws;
    }

    @Override
    public void onMessage(WebSocket webSocket, String text) {
        JSONObject jsonObject = JSONObject.parseObject(text);

        Log.i(Tag, "请求参数; \n" + text.replace(",", "\n"));

        WebSocketResponse webSocketResponse;
        try {
            webSocketResponse = JSONObject.toJavaObject(jsonObject, WebSocketResponse.class);
        } catch (Exception e) {
            throw new RuntimeException("Json格式不正确 text: " + text + "\nexception: " + e.getMessage());
        }

        if (!(ownerActivity instanceof MainActivity)) {
            return;
        }

        ((MainActivity) ownerActivity).webSocketResponse = webSocketResponse;
//        Runnable runnable = () -> {
//            try {
//                switch (webSocketResponse.type) {
//                    case 1:
//                        ((MainActivity) ownerActivity).textView.setBackgroundColor(ownerActivity.getColor(R.color.colorAccent));
//                        return;
//                    case 2:
//                        ((MainActivity) ownerActivity).textView.setBackgroundColor(ownerActivity.getColor(R.color.colorUser));
//                        return;
//                    case 3:
//                        ((MainActivity) ownerActivity).textView.setBackgroundColor(ownerActivity.getColor(R.color.colorPrimary));
//                }
//                ((MainActivity) ownerActivity).textView.setText(webSocketResponse.text);
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//
//        };

        ((MainActivity) ownerActivity).handler.post(((MainActivity) ownerActivity).runnable);

    }

    @Override
    public void onMessage(WebSocket webSocket, ByteString bytes) {
        System.out.println("MESSAGE: " + bytes.hex());
    }

    @Override
    public void onClosing(WebSocket webSocket, int code, String reason) {
        webSocket.close(1000, null);
        System.out.println("CLOSE: " + code + " " + reason);
    }

    @Override
    public void onFailure(WebSocket webSocket, Throwable t, Response response) {
        t.printStackTrace();
    }

}
