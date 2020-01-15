package com.example.socketrocket;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.example.socketrocket.appengine.BackgroundTaskHandler;
import com.example.socketrocket.appengine.database.DatabaseConnection;
import com.example.socketrocket.appengine.database.reflect.objects.Score;

public class HighscoresActivity extends Activity implements View.OnClickListener {

    private int titleHighlightColor;
    private int titleDefaultColor;
    private TextView title;
    private boolean isLoading = false;
    private Score[] scores = new Score[0];
    private DatabaseConnection dbHandle;


    // MARK: - Lifecycle

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_highscores);
        this.dbHandle = new DatabaseConnection(this);
        this.initViews();
    }

    private void initViews() {
        // buttons
        this.findViewById(R.id.button_highscores_prev).setOnClickListener(this);
        this.findViewById(R.id.button_highscores_next).setOnClickListener(this);
        // title
        this.title = this.findViewById(R.id.textView_highscores_title);
        this.titleHighlightColor = this.getResources().getColor(R.color.abc_search_url_text_normal);
        this.titleDefaultColor = this.title.getSolidColor();
        this.updateTitleView();
        this.title.setOnClickListener(this);
    }


    // MARK: - OnClickListener

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.button_highscores_prev: this.onNextButtonPressed(); break;
            case R.id.button_highscores_next: this.onPrevButtonPressed(); break;
            case R.id.textView_highscores_title: this.onTitlePressed(); break;
        }
    }


    // MARK: - Actions

    // MARK: Buttons

    private void onPrevButtonPressed() {

    }

    private void onNextButtonPressed() {

    }

    // MARK: Title

    private void onTitlePressed() {
        this.isLoading = true;
        this.updateTitleView();
        this.loadDataBackground();
    }

    private void updateTitleView() {
        this.title.setClickable(!this.isLoading);
        this.title.setTextColor(this.isLoading ? this.titleHighlightColor : 0xFFFFFFFF);
    }


    // MARK: - Data

    private void loadDataBackground() {
        new BackgroundTaskHandler(this) {
            public void inBackground() {
                // debug delay on data loading time
                try { Thread.sleep(2000); } catch (InterruptedException e) {}
                Score[] data = dbHandle.getAllScores();
                if (data != null) {
                    scores = data;
                } else {
                    scores = new Score[0];
                }
            }
            public void onMainThread() {
                isLoading = false;
                updateTitleView();
            }
        }.start();
    }

}
