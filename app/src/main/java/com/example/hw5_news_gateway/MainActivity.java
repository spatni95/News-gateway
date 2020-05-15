package com.example.hw5_news_gateway;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Layout;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    private Menu opt_menu;
    private boolean flag = false;
    private ArrayList<NewsMenuBean> news_list = new ArrayList<>();
    private ArrayList<SingleArticleBean> news_article_list = new ArrayList<>();
    private HashMap<String, ArrayList<NewsMenuBean>> newsData = new HashMap<>();
    public static final String BROADCAST_TO_NEWS = "BROADCAST TO NEWS SERVICE";
    public static final String BROADCAST_FROM_NEWS = "BROADCAST FROM NEWS SERVICE";
    static final String MESSAGE = "MSG_DATA";
    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;
    private ActionBarDrawerToggle mDrawerToggle;
    private NewsReceiver newsReceiver;
    private final ArrayList<String> tempCopyList = new ArrayList<>();
    private MyPageAdapter pageAdapter;
    private List<Fragment> fragments;
    private ViewPager pager;
    private String color_code_list[] = {"#000000", "#f9d418", "#838fea", "#158c13", "#f9042d", "#6fbdf2", "#242b60", "#f435ce", "#3d1b1b", "#ef550e", "#3bef0e", "#0eefdc", "#0e55ef", "#ef0e91", "#330101", "#776767"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Intent intent = new Intent(this, NewsService.class);
        startService(intent);

        newsReceiver = new NewsReceiver();
        mDrawerLayout = findViewById(R.id.drawer_layout);
        mDrawerList = findViewById(R.id.drawer_list);
        mDrawerList.setOnItemClickListener(
                new ListView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        Log.d(TAG, "onItemClick: ");
                        pager.setBackground(null);
                        NewsMenuBean news = news_list.get(position);
                        Intent broadCastIntent = new Intent();
                        broadCastIntent.setAction(BROADCAST_TO_NEWS);
                        broadCastIntent.putExtra(MESSAGE, news);
                        sendBroadcast(broadCastIntent);
                        mDrawerLayout.closeDrawer(mDrawerList);
                    }
                }
        );
        mDrawerToggle = new ActionBarDrawerToggle( this, mDrawerLayout, R.string.open_drawer,  R.string.close_drawer );
        fragments = new ArrayList<>();
        pageAdapter = new MyPageAdapter(getSupportFragmentManager());
        pager = findViewById(R.id.container);
        pager.setAdapter(pageAdapter);
        if (newsData.isEmpty()) {
            new DownloadNews(this, "").execute();
        }

    }


    public void setSources(ArrayList<NewsMenuBean> listIn) {
        Log.d(TAG, "onCreate: restoreState setSources" + newsData.size() + " / " + news_list.size() + " /flag" + flag);
        if (!flag) {
            if (newsData.isEmpty()) {
                for (NewsMenuBean n : listIn) {
                    if (n.getCategory().trim().isEmpty()) {
                        n.setCategory("Unspecified");
                    }
                    if (!newsData.containsKey(n.getCategory())) {
                        newsData.put(n.getCategory(), new ArrayList<NewsMenuBean>());
                    }
                    newsData.get(n.getCategory()).add(n);
                }

                newsData.put("All", listIn);
                ArrayList<String> tempList = new ArrayList<>(newsData.keySet());
                Collections.sort(tempList);
                tempCopyList.addAll(tempList);
                for (String s : tempList) {
                    opt_menu.add(s);
                }
                if (opt_menu.size() != 0) {
                    for (int i = 0; i < opt_menu.size(); i++) {
                        MenuItem item = opt_menu.getItem(i);
                        SpannableString s = new SpannableString(item.getTitle());
                        s.setSpan(new ForegroundColorSpan(Color.parseColor(color_code_list[i])), 0, s.length(), 0);
                        item.setTitle(s);
                    }
                }
                news_list.addAll(listIn);
                mDrawerList.setAdapter(new ArrayAdapter<NewsMenuBean>(this, R.layout.side_menu, news_list) {
                    @Override
                    public View getView(int position, View convertView, ViewGroup parent) {
                        View view = super.getView(position, convertView, parent);
                        NewsMenuBean newsMenuBean = news_list.get(position);
                        TextView text = view.findViewById(R.id.text_view);
                        text.setText(newsMenuBean.toString());
                        text.setTextColor(Color.parseColor(newsMenuBean.getColor(tempCopyList.indexOf(newsMenuBean.getCategory()))));
                        return view;
                    }
                });

            } else {

                news_list.clear();
                news_list.addAll(listIn);
                mDrawerList.setAdapter(new ArrayAdapter<NewsMenuBean>(this, R.layout.side_menu, news_list) {
                    @Override
                    public View getView(int position, View convertView, ViewGroup parent) {
                        View view = super.getView(position, convertView, parent);
                        NewsMenuBean newsMenuBean = news_list.get(position);
                        TextView text = view.findViewById(R.id.text_view);
                        text.setText(newsMenuBean.toString());
                        text.setTextColor(Color.parseColor(newsMenuBean.getColor(tempCopyList.indexOf(newsMenuBean.getCategory()))));
                        return view;
                    }
                });
                ((ArrayAdapter) mDrawerList.getAdapter()).notifyDataSetChanged();
            }
            if (getSupportActionBar() != null) {
                getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                getSupportActionBar().setHomeButtonEnabled(true);
            }
        }

    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        Log.d(TAG, "onCreate: restoreState options");
        getMenuInflater().inflate(R.menu.main_menu, menu);
        opt_menu = menu;
        opt_menu.clear();
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        Log.d(TAG, "onCreate: restoreState Item" + " /" + flag);
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        setTitle(item.getTitle().toString());
        if (!item.getTitle().toString().trim().equalsIgnoreCase("All")) {
            Log.d(TAG, "onOptionsItemSelected: inside not all");
            new DownloadNews(this, item.getTitle().toString()).execute();
        } else {
            if (newsData != null) {
                Log.d(TAG, "onOptionsItemSelected: inside all");
                news_list.clear();
                news_list.addAll(newsData.get(item.getTitle().toString()));
                ((ArrayAdapter) mDrawerList.getAdapter()).notifyDataSetChanged();
            }
        }
        return super.onOptionsItemSelected(item);
    }

    private int count = 0;
    public class NewsReceiver extends BroadcastReceiver {
        private static final String TAG = "NewsReceiver";

        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent == null || intent.getAction() == null)
                return;
            switch (intent.getAction()) {
                case NewsService.BROADCAST_FROM_NEWS:
                    news_article_list.clear();
                    if (intent.hasExtra(NewsService.MSG_FROM_NS)) {
                        news_article_list.addAll((ArrayList<SingleArticleBean>) intent.getSerializableExtra(NewsService.MSG_FROM_NS));
                    }
                    MainActivity.this.reDoFragments();
                    Log.d(TAG, "onReceive: " + news_article_list.size());
                    break;
                default:
                    Log.d(TAG, "onReceive: Unexpected broadcast: " + intent.getAction());
            }
        }
    }

    public void reDoFragments() {

        Log.d(TAG, "reDoFragments: ");
        for (int i = 0; i < news_article_list.size(); i++) {
            if (news_article_list.get(i).getArticle_Name() != null) {
                setTitle(news_article_list.get(i).getArticle_Name());
                break;
            }
        }

        for (int i = 0; i < pageAdapter.getCount(); i++)
            pageAdapter.notifyChangeInPosition(i);

        fragments.clear();

        for (int i = 0; i < news_article_list.size(); i++) {
            fragments.add(NewsPiece.newInstance(news_article_list.get(i), i + 1, news_article_list.size()));
        }

        pageAdapter.notifyDataSetChanged();

        pager.setCurrentItem(0);
    }

    private class MyPageAdapter extends FragmentPagerAdapter {
        private static final String TAG = "MyPageAdapter";
        private long baseId = 0;
        public MyPageAdapter(FragmentManager fm) {
            super(fm);
        }
        @Override
        public int getItemPosition(@NonNull Object object) {
            return POSITION_NONE;
        }
        @Override
        public Fragment getItem(int position) {
            return fragments.get(position);
        }
        @Override
        public int getCount() {
            return fragments.size();
        }
        @Override
        public long getItemId(int position) {
            return baseId + position;
        }
        public void notifyChangeInPosition(int n) {
            baseId += getCount() + n;
        }
    }
    @Override
    public void onStop() {
        unregisterReceiver(newsReceiver);
        super.onStop();
    }
    @Override
    public void onResume() {
        IntentFilter filterNews = new IntentFilter(BROADCAST_FROM_NEWS);
        registerReceiver(newsReceiver, filterNews);
        super.onResume();
    }
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putSerializable("DRAWERLIST", news_list);
        outState.putSerializable("NEWSDATA", newsData);
        outState.putBoolean("FLAGV", true);
        outState.putSerializable("ARTICLELIST", news_article_list);
        outState.putString("TITLE", getTitle().toString());
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        HashMap<String, ArrayList<NewsMenuBean>> restoredHmap;
        restoredHmap = (HashMap<String, ArrayList<NewsMenuBean>>) savedInstanceState.getSerializable("NEWSDATA");
        newsData.putAll(restoredHmap);
        ArrayList<NewsMenuBean> restorednews_list;
        restorednews_list = (ArrayList<NewsMenuBean>) savedInstanceState.getSerializable("DRAWERLIST");
        news_list.addAll(restorednews_list);
        setOrientationChanges();
        ArrayList<SingleArticleBean> restoredArticleList;
        restoredArticleList = (ArrayList<SingleArticleBean>) savedInstanceState.getSerializable("ARTICLELIST");
        news_article_list.addAll(restoredArticleList);
        if (news_article_list.isEmpty())
            setTitle(savedInstanceState.getString("TITLE"));
        else
            reDoFragments();
    }

    public void setOrientationChanges() {
        ArrayList<String> tempList = new ArrayList<>(newsData.keySet());
        Collections.sort(tempList);
        tempCopyList.addAll(tempList);
        mDrawerList.setAdapter(new ArrayAdapter<NewsMenuBean>(this, R.layout.side_menu, news_list) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View view = super.getView(position, convertView, parent);
                NewsMenuBean newsMenuBean = news_list.get(position);
                TextView text = view.findViewById(R.id.text_view);
                text.setText(newsMenuBean.toString());
                text.setTextColor(Color.parseColor(newsMenuBean.getColor(tempCopyList.indexOf(newsMenuBean.getCategory()))));
                return view;
            }
        });
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeButtonEnabled(true);
        }
    }
}
