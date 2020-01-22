package com.example.socketrocket;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;

import com.example.socketrocket.R;
import com.example.socketrocket.RegisterActivity;

public class LoginActivity extends Activity implements View.OnClickListener {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.fragment_login);


        float size = new Button(this).getTextSize();

        //txvheader = (TextView)this.findViewById(R.id.txvHeader);
        Button btnSignIn = (Button) this.findViewById(R.id.btnSignUp);

        btnSignIn.setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {
        switch(v.getId()) {
            case R.id.btnSignUp:
                goToSignIn();
                break;
            default:
                System.out.println("Unknown button pressed in " + this.getClass().getSimpleName() + ", id was: " + v.getId());
                break;
        }
    }

    public void goToSignIn() {
        Intent intent = new Intent(this, RegisterActivity.class);
        startActivity(intent);
    }

}

