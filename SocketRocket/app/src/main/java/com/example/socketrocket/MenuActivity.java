package com.example.socketrocket;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.socketrocket.appengine.database.DatabaseConnection;
import com.example.socketrocket.appengine.database.reflect.objects.User;
import com.example.socketrocket.appengine.networking.NetworkError;
import com.example.socketrocket.appengine.networking.NetworkRequestDelegate;

import org.json.JSONObject;

public class MenuActivity extends AppCompatActivity implements NetworkRequestDelegate, View.OnClickListener {

    private Button playButton, accountButton, highscoresButton, debugObtionsButton;
    private TextView accountButtonOverlayText;
    private DatabaseConnection dbHandle;
    private User currentUser;
    private AppController appController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_menu);
        this.dbHandle = new DatabaseConnection(this);
        this.appController = new AppController(this);
        this.initViews();
        this.loadCurrentUser();
        this.updateViews();
        this.appController.loadDataOnAppstart();
    }

    @Override
    protected void onResume() {
        super.onResume();
        this.dbHandle = new DatabaseConnection(this);
        this.loadCurrentUser();
        this.updateViews();
    }

    private void initViews() {
        this.playButton = this.findViewById(R.id.menu_button_play);
        this.accountButton = this.findViewById(R.id.menu_button_account);
        this.debugObtionsButton = this.findViewById(R.id.menu_button_debug_options);
        this.highscoresButton = this.findViewById(R.id.menu_button_scores);
        this.accountButtonOverlayText = this.findViewById(R.id.menu_textview_account_overlay);
        this.playButton.setOnClickListener(this);
        this.accountButton.setOnClickListener(this);
        this.highscoresButton.setOnClickListener(this);
        this.accountButton.setSingleLine();
        this.accountButton.setEllipsize(TextUtils.TruncateAt.END);
    }

    private void updateViews() {
        if(this.currentUser != null) {
            this.accountButton.setText(this.currentUser.name);
            this.accountButtonOverlayText.setVisibility(View.VISIBLE);
        }
        else {
            this.accountButton.setText("Anmelden");
            this.accountButtonOverlayText.setVisibility(View.INVISIBLE);
        }
        if(AppUtils.DEBUG_MODE) {
            this.debugObtionsButton.setOnClickListener(this);
            this.debugObtionsButton.setVisibility(View.VISIBLE);
        }
        this.findViewById(R.id.menu_linearlayout_buttoncontainer_1).forceLayout();
        this.accountButton.forceLayout();
    }


    // MARK: - Buttons

    @Override
    public void onClick(View v) {
        if(v == this.playButton) {
            this.onPlayButtonPressed();
        } else if(v == this.accountButton) {
            this.onAccountButtonPressed();
        } else if(v == this.debugObtionsButton) {
            this.onDebugOptionsButtonPressed();
        } else if(v == this.highscoresButton){
            this.onHighscoresButtonPressed();
        }
    }

    public void onPlayButtonPressed(){
        if(this.currentUser == null) {
            String title = "Wenn Du das Spiel ohne Benutzeraccount startest, werden deine Bestleistungen nicht geteilt. Fortfahren?";
            AppUtils.showAskIfContinueAlert(this, title, new Runnable() {
                public void run() {
                    Intent intent = new Intent(MenuActivity.this, GameActivity.class);
                    startActivity(intent);
                }
            });
        } else {
            Intent intent = new Intent(this, GameActivity.class);
            startActivity(intent);
        }
    }

    public void onHighscoresButtonPressed() {
        Intent intent = new Intent(this, HighscoresActivity.class);
        this.startActivity(intent);
    }

    public void onAccountButtonPressed() {
        if(this.currentUser != null) {
            Intent intent = new Intent(this, AccountOverviewActivity.class);
            this.startActivity(intent);
        } else {
            // Wenn kein User vorhanden -> goto login / regi
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
        }
    }

    public void onDebugOptionsButtonPressed() {
        // zu den Debug Options (Debug Mode only)
        if(AppUtils.DEBUG_MODE == false) return;
        Intent intent = new Intent(this, DebugOptionsActivity.class);
        startActivity(intent);
    }


    // MARK: - User Data

    private void loadCurrentUser() {
        this.currentUser = null;
        try {
            User[] usersInDB = this.dbHandle.getAllUsers();
            if(usersInDB.length == 1) {
                this.currentUser = usersInDB[0];
            }
        } catch (Exception e) {
            // TODO: DB Fehler behandeln
        }
    }


    // MARK: - NetworkRequestDelegate

    @Override
    public void didRecieveNetworkResponse(int requestId, JSONObject[] data) {
        this.appController.didRecieveNetworkResponse(requestId, data);
    }

    @Override
    public void didRecieveNetworkError(int requestId, NetworkError error) {
        this.appController.didRecieveNetworkError(requestId, error);
    }
}
