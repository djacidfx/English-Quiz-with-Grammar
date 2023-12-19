package com.datdang.englishquiz.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.ayoubfletcher.consentsdk.ConsentSDK;
import com.datdang.englishquiz.Constant;
import com.datdang.englishquiz.Database.Databases;
import com.datdang.englishquiz.adapter.LevelAdapter;
import com.datdang.englishquiz.model.CategoryModel;
import com.datdang.englishquiz.model.LevelModel;
import com.datdang.englishquiz.R;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;

import java.util.ArrayList;


/**
 * Created by Legandan on 27/02/2021.
 */

public class MainActivity extends AppCompatActivity {
    ListView lvCategory;
    ListView lvTopic;
    ArrayList<CategoryModel> catelogyModels;
    ArrayList<LevelModel> levelModels = new ArrayList<>();
    Databases databases;
    int selectedLevel = 1;
    LevelAdapter topicAdapter;
    int selectedCate = 1 ;
    private TextView ToolbarTitle;
    private Toolbar toolbar;
    private long exitTime = 0;

    private int counter = 0;
    private InterstitialAd mInterstitialAd;
    private AdView adView;
    Context context;
    private Databases db;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.quiz_activity);
        databases = new Databases(this);
        databases.openDataBase();

        //show mInterstitialAd
        MobileAds.initialize(this, getString(R.string.Admob_publisher_id));
        mInterstitialAd= new InterstitialAd(this);
        mInterstitialAd.setAdUnitId(getString(R.string.intrestial_ads));
        mInterstitialAd.loadAd(ConsentSDK.getAdRequest(this));

     //   addLevel();
        setupToolbar();

        levelModels = databases.getTopics(selectedCate);
        lvTopic = findViewById(R.id.list_viewTopic);
        topicAdapter = new LevelAdapter(this, R.layout.item_topic,levelModels);
        lvTopic.setAdapter(topicAdapter);

        lvTopic.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                mInterstitialAd.loadAd(ConsentSDK.getAdRequest(getApplicationContext()));

                Intent intent = new Intent(MainActivity.this, QuestionActivity.class);
                intent.putExtra(QuestionActivity.KEY_TITLE, String.valueOf(levelModels.get(i)));
                intent.putExtra("ID", levelModels.get(i).getId());
                intent.putExtra("level_score_id", levelModels.get(i).getLevelscore_id());
                intent.putExtra("score",levelModels.get(i).getScore());
                selectedLevel = i;
                startActivityForResult(intent,0);


                ShowAds();


            }
        });

      //  loadInter();

        AdView adView = findViewById(R.id.adView);
        // You have to pass the AdRequest from ConsentSDK.getAdRequest(this) because it handle the right way to load the ad
        adView.loadAd(ConsentSDK.getAdRequest(this));
    }



    private void setupToolbar() {
        if (Build.VERSION.SDK_INT <= 22 ) { getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN); }
        toolbar = findViewById(R.id.toolbar);
        ToolbarTitle = findViewById(R.id.toolbar_title);
        ToolbarTitle.setTextColor(getResources().getColor(R.color.white));
        setSupportActionBar(toolbar);

        ToolbarTitle.setText(R.string.app_name);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        toolbar.setTitleTextColor(0xFFFFFFFF);
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayShowTitleEnabled(false);
        actionBar.setElevation(0);
       actionBar.setElevation(0);

    }


    @Override
    public void onBackPressed() {

        super.onBackPressed();
    }

    @Override
    protected void onResume() {

        super.onResume();
    }


    private void addLevel() {


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 0) {
            if (resultCode == RESULT_OK) {
                int d = data.getIntExtra("score", 0);
                if (d > levelModels.get(selectedLevel).getScore()) {
                    levelModels.get(selectedLevel).setScore(d);
                    topicAdapter.notifyDataSetChanged();
                }
            }
        }
    }


    public void ShowAds(){
//        mInterstitialAd.loadAd(ConsentSDK.getAdRequest(getActivity()));
        counter++;
        if (counter==Integer.parseInt(String.valueOf(Constant.adShow))){

            loadInterstitial();

            counter=0;


        }else {


        }
    }

    // Load Interstitial
    private void loadInterstitial() {
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

    }

}
