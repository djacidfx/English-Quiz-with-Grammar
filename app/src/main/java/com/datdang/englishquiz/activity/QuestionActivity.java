package com.datdang.englishquiz.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.ayoubfletcher.consentsdk.ConsentSDK;
import com.datdang.englishquiz.Constant;
import com.datdang.englishquiz.Database.Databases;
import com.datdang.englishquiz.dialog.EnglishDialog;
import com.datdang.englishquiz.model.OptionModel;
import com.datdang.englishquiz.model.QuestionModel;
import com.datdang.englishquiz.model.ResultModel;
import com.datdang.englishquiz.R;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Legandan on 27/02/2021.
 */


public class QuestionActivity extends AppCompatActivity implements EnglishDialog.EnglishDialogListener {
    final public static String KEY_TITLE = "TitleQuestionActivity";
    Button btnNext;
    Button btnPre;
    TextView tvPage;
    TextView tvQuestion;
    RadioGroup radioGroups;
    ArrayList<OptionModel> optionModels;
    ArrayList<ResultModel> correctA;
    private TextView ToolbarTitle;
    private Toolbar toolbar;
    private int counter = 0;
    int id;
    int level_score_id;
    Databases databases;
    HashMap<Integer, String> map = new HashMap<>();
    private InterstitialAd mInterstitialAd;
    int index = 0;
    int score = 0;
    EnglishDialog englishDialog;
    List<QuestionModel> listData = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.question_main);
        databases = new Databases(this);
        databases.openDataBase();

        setupToolbar();
      //  loadInter();
        loadBanner();

        //show mInterstitialAd
        MobileAds.initialize(this, getString(R.string.Admob_publisher_id));
        mInterstitialAd= new InterstitialAd(this);
        mInterstitialAd.setAdUnitId(getString(R.string.intrestial_ads));
        mInterstitialAd.loadAd(ConsentSDK.getAdRequest(this));

        correctA = new ArrayList<>();
        btnNext = findViewById(R.id.btnNext);
        btnPre = findViewById(R.id.btnPrevious);
        tvPage = findViewById(R.id.txtPage);
        tvQuestion = findViewById(R.id.txtQuestion);
        btnPre.setEnabled(false);
        radioGroups = findViewById(R.id.groupChoice);

        radioGroups.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                RadioButton rd = findViewById(radioGroup.getCheckedRadioButtonId());

                if (rd != null) {
                    map.put(index, rd.getText().toString());
                }
            }
        });


        id = getIntent().getIntExtra("ID", 0);
        level_score_id = getIntent().getIntExtra("level_score_id",0);

        prepareData(id);
        addEvent();
        correctA = databases.getResult(id);

        englishDialog = new EnglishDialog();
        englishDialog.setCancelable(false);
        englishDialog.setEnglishDialogListener(this);
    }

    private void setupToolbar() {
        if (Build.VERSION.SDK_INT <= 22 ) { getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN); }
        toolbar = findViewById(R.id.toolbar);
        ToolbarTitle = findViewById(R.id.toolbar_title);
        ToolbarTitle.setTextColor(getResources().getColor(R.color.white));
        setSupportActionBar(toolbar);
        ToolbarTitle.setText(getIntent().getStringExtra(KEY_TITLE));
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        toolbar.setTitleTextColor(0xFFFFFFFF);
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayShowTitleEnabled(false);
    }

    private void addEvent() {

        btnPre.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("ResourceAsColor")
            @Override
            public void onClick(View view) {
                Typeface typeface = Typeface.createFromAsset(getAssets(), "font1.otf");
                radioGroups.removeAllViews();
                index--;
               ShowAds();
                tvPage.setText((index + 1) + "/" + listData.size());
                QuestionModel q = listData.get(index);
                tvQuestion.setText(q.getContent());
                int qid = q.getId();
                ArrayList<OptionModel> optionModels = databases.getOption(qid);
                for (int i = 0; i < optionModels.size(); i++) {
                    OptionModel o = optionModels.get(i);
                    RadioButton rd = new RadioButton(QuestionActivity.this);
                    rd.setId(o.getId());
                    rd.setButtonTintList(ColorStateList.valueOf(Color.WHITE));
                    rd.setText(o.getContent());
                    rd.setTextSize(24);
                    rd.setTextColor(Color.WHITE);
                    rd.setTypeface(typeface);
                    if (o.getContent().equals(map.get(index))) rd.setChecked(true);
                    radioGroups.addView(rd);
                    ShowAds();
                }
                if (index == 0) {
                    btnPre.setEnabled(false);
                }

            }
        });

        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Typeface typeface = Typeface.createFromAsset(getAssets(), "font1.otf");
                if (index == listData.size() - 1) {
                    ShowAds();
                    englishDialog.show(getFragmentManager(), "TEST");


                } else {
                    radioGroups.removeAllViews();
                    index++;
                    ShowAds();
                    tvPage.setText((index + 1) + "/" + listData.size());
                    QuestionModel q = listData.get(index);
                    tvQuestion.setText(q.getContent());
                    int qid = q.getId();
                    ArrayList<OptionModel> optionModels = databases.getOption(qid);
                    for (int i = 0; i < optionModels.size(); i++) {
                        OptionModel o = optionModels.get(i);
                        RadioButton rd = new RadioButton(QuestionActivity.this);
                        rd.setId(o.getId());
                        rd.setButtonTintList(ColorStateList.valueOf(Color.WHITE));
                        rd.setText(o.getContent());
                        rd.setTextSize(24);
                        rd.setTextColor(Color.WHITE);
                        rd.setTypeface(typeface);
                        if (o.getContent().equals(map.get(index))) rd.setChecked(true);
                        radioGroups.addView(rd);

                    }

                }
                if (index != 0) {
                    btnPre.setEnabled(true);
                }


            }

        });
    }




    // Load banner ads
    private void loadBanner() {
        AdView adView = findViewById(R.id.adView);
        // You have to pass the AdRequest from ConsentSDK.getAdRequest(this) because it handle the right way to load the ad
        adView.loadAd(ConsentSDK.getAdRequest(this));
    }

    public void ShowAds(){
//        mInterstitialAd.loadAd(ConsentSDK.getAdRequest(getActivity()));
        counter++;
        if (counter==Integer.parseInt(String.valueOf(Constant.adShowQ))){

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


    private void prepareData(int id) {

        Typeface typeface = Typeface.createFromAsset(getAssets(), "font1.otf");
        listData = databases.getQuestion(id);
        QuestionModel q = listData.get(index);
        tvPage.setText((index + 1) + "/" + listData.size());
        int qid = q.getId();
        tvQuestion.setText(q.getContent());
        optionModels = databases.getOption(qid);

        for (int i = 0; i < optionModels.size(); i++) {
            OptionModel o = optionModels.get(i);
            RadioButton rd = new RadioButton(QuestionActivity.this);
            rd.setId(o.getId());
            rd.setButtonTintList(ColorStateList.valueOf(Color.WHITE));
            rd.setText(o.getContent());
            rd.setTextSize(24);
            rd.setTextColor(Color.WHITE);
            rd.setTypeface(typeface);
            radioGroups.addView(rd);
        }
        tvPage.setText((index + 1) + "/" + listData.size());
    }

    @Override
    public void onPosClickListener() {

        Intent intent = new Intent(QuestionActivity.this, ResultActivity.class);
        intent.putExtra("ID", id);
        intent.putExtra("KQ", map);
        intent.putExtra("lsd",level_score_id);
        intent.putExtra("score",getIntent().getIntExtra("score",0));
        Intent kqIntent = new Intent();
        for (int i = 0; i < correctA.size(); i++) {
            if (correctA.get(i).getCorrectAnswer().equals(map.get(i))) score++;
        }
//        System.out.println("Diem " + score);
        kqIntent.putExtra("score", score);
        setResult(RESULT_OK, kqIntent);
        finish();
        startActivity(intent);
    }

    @Override
    public void onNeClickListener() {
        englishDialog.dismiss();
    }


}