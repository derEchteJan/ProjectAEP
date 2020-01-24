package com.example.socketrocket;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.socketrocket.appengine.database.DatabaseConnection;
import com.example.socketrocket.appengine.database.reflect.objects.User;
import com.example.socketrocket.appengine.networking.NetworkConnection;

public class AccountOverviewActivity extends Activity implements View.OnClickListener {

    private TextView usernameLabel, emailLabel, tokenLabel;
    private Button logoutButton, authoriszeButton;
    private DatabaseConnection dbHandle;
    private User currentUser;
    private boolean tokenShown = false;


    // MARK: - Life Cycle

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.setContentView(R.layout.activity_account_overview);
        this.dbHandle = new DatabaseConnection(this);
        this.initViews();
        this.loadCurrentUser();
        this.updateViews();
    }

    @Override
    protected void onResume() {
        super.onResume();
        this.loadCurrentUser();
        this.updateViews();
    }

    private void initViews() {
        this.usernameLabel = this.findViewById(R.id.account_overview_textview_username);
        this.emailLabel = this.findViewById(R.id.account_overview_textview_email);
        this.tokenLabel = this.findViewById(R.id.account_overview_textview_token);
        this.logoutButton = this.findViewById(R.id.account_overview_button_logout);
        this.authoriszeButton = this.findViewById(R.id.account_overview_button_authorize);
        this.logoutButton.setOnClickListener(this);
        this.authoriszeButton.setOnClickListener(this);
        this.tokenLabel.setOnClickListener(this);
        this.tokenLabel.setClickable(true);
    }

    private void updateViews() {
        if(this.currentUser == null) {
            Toast.makeText(this, "Fehler: kein User gefunden", Toast.LENGTH_LONG).show();
            return;
        }
        this.usernameLabel.setText(this.currentUser.name);
        this.emailLabel.setText(this.currentUser.email);
        this.tokenLabel.setText(this.tokenShown ? this.currentUser.token : "anzeigen");
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


    // MARK: - Buttons

    @Override
    public void onClick(View v) {
        if(v == this.authoriszeButton) {
            this.onAuthorizeButtonPressed();
        } else if (v == this.tokenLabel) {
            this.onTokenTitlePressed();
        } else if (v == this.logoutButton) {
            this.onLogoutButtonPressed();
        }
    }

    private void onAuthorizeButtonPressed() {
        Toast.makeText(this,"Nicht implementiert", Toast.LENGTH_LONG).show();
    }

    private void onTokenTitlePressed() {
        this.tokenShown ^= true;
        this.updateViews();
    }

    private void onLogoutButtonPressed() {
        Runnable logoutTask = new Runnable() {
            public void run() {
                dbHandle.deleteAllUsers();
                Toast.makeText(AccountOverviewActivity.this, "Abgemeldet", Toast.LENGTH_LONG).show();
                Intent intent = new Intent(AccountOverviewActivity.this, MenuActivity.class);
                startActivity(intent);
            }
        };
        AppUtils.showAskIfContinueAlert(this, "Abmelden?", logoutTask);
    }
}
