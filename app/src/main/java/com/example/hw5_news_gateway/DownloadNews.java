package com.example.hw5_news_gateway;

import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class DownloadNews extends AsyncTask<String, Integer, String> {
    private static final String TAG = "NewsSourceDownloader";
    private MainActivity mainActivity;
    private static final String api_url = "https://newsapi.org/v2/sources?language=en&country=us&category=";
    private static final String api_key = "&apiKey=d49b2ce2ba0a45519affc8b6caf3907e";
    private String category;

    DownloadNews(MainActivity ma, String category) {
        mainActivity = ma;
        this.category = category;
    }

    @Override
    protected void onPostExecute(String s) {

        ArrayList<NewsMenuBean> news_list = parseJSON(s);

        if (news_list != null) {
            Log.d(TAG, "onPostExecute: " + news_list.size());
            mainActivity.setSources(news_list);
        }
    }


    @Override
    protected String doInBackground(String... params) {
        String finalUrl = api_url + category + api_key;
        Log.d(TAG, "doInBackground: final URL" + finalUrl);
        Uri dataUri = Uri.parse(finalUrl);
        String urlToUse = dataUri.toString();

        StringBuilder sb = new StringBuilder();
        try {
            URL url = new URL(urlToUse);

            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            InputStream is = conn.getInputStream();
            BufferedReader reader = new BufferedReader((new InputStreamReader(is)));

            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line).append('\n');
            }


        } catch (Exception e) {
            Log.d(TAG, "doInBackground: Exception " + e);
            e.printStackTrace();
            return null;
        }
        return sb.toString();
    }


    private ArrayList<NewsMenuBean> parseJSON(String s) {
        Log.d(TAG, "parseJSON: ");
        ArrayList<NewsMenuBean> news_list= new ArrayList<>();
        try {
            JSONObject jNewsSrc = new JSONObject(s);
            String status = jNewsSrc.getString("status");
            if (status.trim().equalsIgnoreCase("ok")) {
                String sources = jNewsSrc.getString("sources");
                if (sources != null) {
                    JSONArray jObjMain = new JSONArray(sources);
                    for (int i = 0; i < jObjMain.length(); i++) {
                        JSONObject jSource = (JSONObject) jObjMain.get(i);
                        String id = jSource.getString("id");
                        String name = jSource.getString("name");
                        String url = jSource.getString("url");
                        String category = jSource.getString("category");
                        news_list.add(new NewsMenuBean(id, name, url, category));
                    }
                }
            }
            return news_list;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

}
