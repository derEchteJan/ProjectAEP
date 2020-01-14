package com.example.socketrocket;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.socketrocket.fragments.LoginFragment;

public class MenuActivity extends AppCompatActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_menu);

        float size = new Button(this).getTextSize();

        //txvheader = (TextView)this.findViewById(R.id.txvHeader);
        Button btnStart = (Button)this.findViewById(R.id.btnStart);
        Button btnHigh = (Button)this.findViewById(R.id.btnHigh);
        Button btnLogin = (Button)this.findViewById(R.id.btnLogin);
        Button btnDebug = (Button)this.findViewById(R.id.btnDebug);


        btnStart.setOnClickListener(this);
        btnHigh.setOnClickListener(this);
        btnLogin.setOnClickListener(this);
        btnDebug.setOnClickListener(this);
        //new TestDelegate().runTest();
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()) {
            case R.id.btnStart:
                goToGameActivity();
                break;
            case R.id.btnHigh:
                goToHighscores();
                break;
            case R.id.btnLogin:
                goToLogin();
                break;
            case R.id.btnDebug:
                goToDebugOptions();
                break;
            default:
                System.out.println("Unknown button pressed in " + this.getClass().getSimpleName() + ", id was: " + v.getId());
                break;
        }
    }

    public void goToGameActivity(){
        Intent intent = new Intent(this, GameActivity.class);
        startActivity(intent);
        //this.setContentView(new GamePanel(this));
        //new TestDelegate().runTest(this);

    }

    public void goToHighscores() {
        // TODO: Jan
        System.out.println("goToHighscores");
        return;
    }

    public void goToLogin() {
        // TODO: Chris
        Intent intent = new Intent(this, LoginFragment.class);
        startActivity(intent);
    }

    public void goToDebugOptions() {
        Intent intent = new Intent(this, DebugOptionsActivity.class);
        startActivity(intent);
    }
}
