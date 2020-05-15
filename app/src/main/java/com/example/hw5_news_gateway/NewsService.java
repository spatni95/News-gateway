package com.example.hw5_news_gateway;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.util.Log;

import java.util.ArrayList;

public class NewsService extends Service {

    private static final String TAG = "NewsService";
    private boolean running = true;
    private ServiceReceiver serviceReceiver;
    private ArrayList<SingleArticleBean> articleList = new ArrayList<>();
    public static final String BROADCAST_TO_NEWS = "BROADCAST TO NEWS SERVICE";
    public static final String BROADCAST_FROM_NEWS = "BROADCAST FROM NEWS SERVICE";
    public static final String MSG_FROM_NS = "MSG_FROM_NS";

    public NewsService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        serviceReceiver = new ServiceReceiver();
        IntentFilter filter = new IntentFilter(BROADCAST_TO_NEWS);
        registerReceiver(serviceReceiver, filter);

        new Thread(new Runnable() {
            @Override
            public void run() {

                while (running) {
                    try {
                        if (articleList.isEmpty()) {
                            Thread.sleep(250);
                        }
                        else {
                            sendBroadcastToMain();
                            articleList.clear();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }


                }
                Log.d(TAG, "run: Ending loop");
            }
        }).start();
        return Service.START_STICKY;
    }

    private void sendBroadcastToMain() {
        Log.d(TAG, "sendBroadcastToMain: " + articleList.size());

        //Creating Intent for MainActivity
        Log.d(TAG, "run: Intent");
        Intent broadCastIntent = new Intent();
        broadCastIntent.setAction(BROADCAST_FROM_NEWS);
        broadCastIntent.putExtra(MSG_FROM_NS, articleList);
        sendBroadcast(broadCastIntent);

    }

    @Override
    public void onDestroy() {
        unregisterReceiver(serviceReceiver);
        running = false;
        super.onDestroy();
    }

    public void setArticles(ArrayList<SingleArticleBean> listIn) {
        Log.d(TAG, "setArticles: " + listIn.size());
        articleList.clear();
        articleList.addAll(listIn);
    }

    public class ServiceReceiver extends BroadcastReceiver {

        private static final String TAG = "ServiceReceiver";

        @Override
        public void onReceive(Context context, Intent intent) {

            if (intent == null || intent.getAction() == null)
                return;
            switch (intent.getAction()) {
                case MainActivity.BROADCAST_TO_NEWS:
                    NewsMenuBean newsMenuBean_main = null;
                    if (intent.hasExtra(MainActivity.MESSAGE)) {
                        newsMenuBean_main = (NewsMenuBean) intent.getSerializableExtra(MainActivity.MESSAGE);
                        new ArticleDownloader(NewsService.this, newsMenuBean_main).execute();
                    }
                    Log.d(TAG, "onReceive: " + newsMenuBean_main.getName());
                    break;
                default:
                    Log.d(TAG, "onReceive: Unexpected broadcast: " + intent.getAction());
            }
        }

    }
}

