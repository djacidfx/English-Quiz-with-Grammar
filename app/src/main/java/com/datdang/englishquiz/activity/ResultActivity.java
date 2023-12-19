package com.datdang.englishquiz.activity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.ayoubfletcher.consentsdk.ConsentSDK;
import com.datdang.englishquiz.Database.Databases;
import com.datdang.englishquiz.adapter.ResultAdapter;
import com.datdang.englishquiz.model.ResultModel;
import com.datdang.englishquiz.R;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Legandan on 27/02/2021.
 */

public class ResultActivity extends AppCompatActivity {
    ListView lvResult;
    int id;
    TextView tvImage;
    ImageButton imResult;
    ArrayList<ResultModel> resultModels;
    Databases databases;
    HashMap<Integer, String> map;
    int score=0;
    int lsd;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.result_main);
        databases = new Databases(this);
        databases.openDataBase();

        loadBanner();

        final InterstitialAd mInterstitialAd = new InterstitialAd(this);
        mInterstitialAd.setAdUnitId(getResources().getString(R.string.intrestial_ads));
        //  AdRequest adRequest = new AdRequest.Builder().build();
        mInterstitialAd.loadAd(ConsentSDK.getAdRequest(this));
        mInterstitialAd.setAdListener(new AdListener() {
            @Override
            public void onAdLoaded() {
                super.onAdLoaded();
                //   mInterstitialAd.show();
                if (mInterstitialAd.isLoaded()) {
                    mInterstitialAd.show();
                } else {
                    Log.d("TAG", "The interstitial wasn't loaded yet.");
                }
            }
        });

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        TextView toolbarTitle = findViewById(R.id.toolbar_title);
        toolbarTitle.setText("Your Test Result ");

        tvImage = findViewById(R.id.tv_result);

        id = getIntent().getIntExtra("ID",0);
        resultModels = databases.getResult(id);
        map = (HashMap<Integer, String>) getIntent().getSerializableExtra("KQ");
        lsd = getIntent().getIntExtra("lsd", 0);
        lvResult = findViewById(R.id.listResult);

        for (int i = 0 ; i < resultModels.size(); i++) {
            resultModels.get(i).setYourAnswer(map.get(i));
            if (resultModels.get(i).getKQ()) score++;
        }

        ResultAdapter resultAdapter = new ResultAdapter(this,R.layout.item_result,resultModels);
        if (lsd == 0) {
            databases.insertToDB(id,score);
        }
        else {
            int diem = getIntent().getIntExtra("score",0);
            if(score >diem) databases.updateLevelScore(lsd,id,score);
        }
        tvImage.setText("Correct: "+score+"/"+resultModels.size());
        lvResult.setAdapter(resultAdapter);
        imResult = findViewById(R.id.ig_result);
        imResult.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

    }

    // Load banner ads
    private void loadBanner() {
        AdView adView = findViewById(R.id.adView);
        // You have to pass the AdRequest from ConsentSDK.getAdRequest(this) because it handle the right way to load the ad
        adView.loadAd(ConsentSDK.getAdRequest(this));
    }

}
