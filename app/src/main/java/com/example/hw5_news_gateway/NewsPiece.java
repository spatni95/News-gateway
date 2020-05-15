package com.example.hw5_news_gateway;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.squareup.picasso.Picasso;
import java.util.Locale;

public class NewsPiece extends Fragment {
    private static final String TAG = "NewsFragment";
    public static final String NEWS_ARTICLE = "NEWS_ARTICLE";

    public NewsPiece() {
    }

    public static final NewsPiece newInstance(SingleArticleBean singleArticleBean, int index, int max) {
        NewsPiece f = new NewsPiece();
        Bundle bdl = new Bundle(1);
        bdl.putSerializable(NEWS_ARTICLE, singleArticleBean);
        bdl.putSerializable("INDEX", index);
        bdl.putSerializable("TOTAL_COUNT", max);
        f.setArguments(bdl);
        return f;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Get the string to display from the arguments bundle
        View v = inflater.inflate(R.layout.news_piece, container, false);

        final SingleArticleBean singleArticleBean = (SingleArticleBean) getArguments().getSerializable(NEWS_ARTICLE);
        int index = getArguments().getInt("INDEX");
        int total = getArguments().getInt("TOTAL_COUNT");

        Log.d(TAG, "onCreateView: Image" + singleArticleBean.getArticle_urlToImage());
        TextView headLineView = v.findViewById(R.id.headLineView);
        headLineView.setText(singleArticleBean.getArticle_title());
        headLineView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickToOpenBrowser(singleArticleBean.getArticle_url());
            }
        });

        TextView dateView = v.findViewById(R.id.dateView);
        if (!singleArticleBean.getArticle_publishedAt().equalsIgnoreCase("null")) {
            dateView.setText(singleArticleBean.getArticle_publishedAt());
        } else {
            dateView.setVisibility(View.GONE);
        }

        TextView authorView = v.findViewById(R.id.authorView);
        if (!singleArticleBean.getArticle_author().equalsIgnoreCase("null")) {
            authorView.setText(singleArticleBean.getArticle_author());
        } else {
            authorView.setVisibility(View.GONE);
        }

        if (!singleArticleBean.getArticle_description().equalsIgnoreCase("null")) {

            TextView descriptionView = v.findViewById(R.id.articleTextView);
            descriptionView.setMovementMethod(new ScrollingMovementMethod());
            descriptionView.setText(singleArticleBean.getArticle_description());
            descriptionView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    clickToOpenBrowser(singleArticleBean.getArticle_url());
                }
            });
        }

        TextView pageNum = v.findViewById(R.id.countView);
        pageNum.setText(String.format(Locale.US, "%d of %d", index, total));



        Picasso picasso = new Picasso.Builder(getContext()).build();
        ImageView imgView = v.findViewById(R.id.imgView);
        if (!singleArticleBean.getArticle_urlToImage().equalsIgnoreCase("null")) {
            try {
                picasso.load(singleArticleBean.getArticle_urlToImage().trim())
                        .error(R.drawable.no_image)
                        .placeholder(R.drawable.waiting)
                        .into(imgView);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        imgView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickToOpenBrowser(singleArticleBean.getArticle_url());
            }
        });

        return v;
    }

    public void clickToOpenBrowser(String name) {
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(name));
        startActivity(intent);
    }
}
