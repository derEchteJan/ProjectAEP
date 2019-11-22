package com.example.socketrocket.fragments;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import androidx.fragment.app.FragmentActivity;

import com.example.socketrocket.R;

public class HighscoresFragment extends FragmentActivity implements View.OnClickListener {

    private int titleHighlightColor;
    private int titleDefaultColor;
    private TextView title;

    // MARK: - Lifecycle

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.fragment_highscores);
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
        this.setTitleActive(false);
        this.title.setOnClickListener(this);
    }


    // MARK: - OnClickListener

    @Override
    public void onClick(View v) {

    }


    // MARK: - Actions

    // MARK: Buttons

    private void onPrevButtonPressed() {

    }

    private void onNextButtonPressed() {

    }

    // MARK: Title

    private void onTitlePressed() {

    }

    private void setTitleActive(boolean active) {
        this.title.setClickable(!active);
        this.title.setTextColor(active ? this.titleHighlightColor : this.titleDefaultColor);
    }

}
