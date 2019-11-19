package com.example.socketrocket;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.socketrocket.gameengine.GamePanel;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.game_main);

        float size = new Button(this).getTextSize();
        Toast.makeText(this, "Textgröße " + size, Toast.LENGTH_LONG).show();

        //txvheader = (TextView)this.findViewById(R.id.txvHeader);
        Button btnStart = (Button)this.findViewById(R.id.btnStart);

        btnStart.setOnClickListener(this);
        //new TestDelegate().runTest();
    }

    @Override
    public void onClick(View v) {
        openGameActivity();
    }

    public void openGameActivity(){
        Intent intent = new Intent(this, GameActivity.class);
        startActivity(intent);
        //this.setContentView(new GamePanel(this));
        //new TestDelegate().runTest(this);

    }
}
