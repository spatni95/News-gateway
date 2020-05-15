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
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class ArticleDownloader extends AsyncTask<String, Integer, String> {
    private static final String TAG = "NewsArticleDownloader";
    private NewsMenuBean newsMenuBean;
    private NewsService newsService;
    private static final String api_url = "https://newsapi.org/v2/everything?sources=";
    private static final String api_key = "&language=en&pageSize=100&apiKey=1e8e20da31c24b23ac9aee269c51ff0c";


    ArticleDownloader(NewsService newsService, NewsMenuBean newsMenuBean) {
        this.newsService = newsService;
        this.newsMenuBean = newsMenuBean;
    }

    @Override
    protected void onPostExecute(String s) {

        ArrayList<SingleArticleBean> news_article_list1 = parseJSON(s);
        if (news_article_list1 != null) {
            Log.d(TAG, "onPostExecute: " + news_article_list1.size());
            newsService.setArticles(news_article_list1);
        }
    }


    @Override
    protected String doInBackground(String... params) {
        String formedUrl = api_url + newsMenuBean.getId().trim() + api_key;
        Uri dataUri = Uri.parse(formedUrl);
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


    private ArrayList<SingleArticleBean> parseJSON(String s) {
        Log.d(TAG, "parseJSON: ");
        ArrayList<SingleArticleBean> news_article_list1 = new ArrayList<>();
        try {
            JSONObject jNewsSrc = new JSONObject(s);
            String source = null;
            String srcName = null;
            String status = jNewsSrc.getString("status");
            if (status.trim().equalsIgnoreCase("ok")) {
                String sources = jNewsSrc.getString("articles");
                if (sources != null) {
                    JSONArray jObjMain = new JSONArray(sources);
                    for (int i = 0; i < jObjMain.length(); i++) {
                        JSONObject jSource = (JSONObject) jObjMain.get(i);
                        if (source == null) {
                            source = jSource.getString("source");
                            JSONObject srcJson = new JSONObject(source);
                            srcName = srcJson.getString("name");
                        }
                        String author = jSource.getString("author");
                        String title = jSource.getString("title");
                        String description = jSource.getString("description");
                        String url = jSource.getString("url");
                        String urlToImage = jSource.getString("urlToImage");
                        String publishedAt = jSource.getString("publishedAt");
                        String publishedDate="";
                        if (!publishedAt.trim().isEmpty()) {
                            SimpleDateFormat srcFmt = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssXXX");
                            Date srcDate = srcFmt.parse(publishedAt);
                            SimpleDateFormat targetFormat = new SimpleDateFormat("MMM dd, yyyy HH:mm");
                            publishedDate = targetFormat.format(srcDate);
                        }
                        news_article_list1.add(new SingleArticleBean(srcName, author, title, description, url, urlToImage, publishedDate));
                    }
                }
            }
            return news_article_list1;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}

