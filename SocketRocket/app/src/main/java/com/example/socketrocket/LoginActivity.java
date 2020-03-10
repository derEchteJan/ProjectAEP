package com.example.socketrocket;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.socketrocket.appengine.database.DatabaseConnection;
import com.example.socketrocket.appengine.database.reflect.objects.User;
import com.example.socketrocket.appengine.networking.NetworkConnection;
import com.example.socketrocket.appengine.networking.NetworkError;
import com.example.socketrocket.appengine.networking.NetworkRequestDelegate;

import org.json.JSONObject;

public class LoginActivity extends Activity implements NetworkRequestDelegate, View.OnClickListener {

    private Button loginButton, goToRegiButton;
    private EditText usernameInputField, passwordInputField;
    private int currentRequestId = NetworkRequestDelegate.INVALID_REQUEST_ID;
    private DatabaseConnection dbHandle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_login);
        this.dbHandle = new DatabaseConnection(this);
        this.initViews();
    }

    private void initViews() {
        this.loginButton = this.findViewById(R.id.login_button_login);
        this.goToRegiButton = this.findViewById(R.id.login_button_goto_registration);
        this.usernameInputField = this.findViewById(R.id.login_edittext_username);
        this.passwordInputField = this.findViewById(R.id.login_edittext_password);
        this.loginButton.setOnClickListener(this);
        this.goToRegiButton.setOnClickListener(this);
    }


    // MARK: - Buttons

    @Override
    public void onClick(View v) {
        if(v == this.loginButton) {
            String username = this.usernameInputField.getText().toString();
            String password = this.passwordInputField.getText().toString();
            if(!checkUsername(username)) return;
            if(!checkPassword(password)) return;
            password = AppUtils.md5(password);
            int requestId = NetworkConnection.sendLoginRequest(this, username, password);
            if(requestId != NetworkRequestDelegate.INVALID_REQUEST_ID) {
                this.currentRequestId = requestId;
                this.setNetworkButtonsLocked(true);
            } else {
                Toast.makeText(this, "Interner Fehler", Toast.LENGTH_LONG).show();
                return;
            }
        } else if(v == this.goToRegiButton) {
            this.goToSignup();
        }
    }

    private void goToSignup() {
        Intent intent = new Intent(this, RegisterActivity.class);
        startActivity(intent);
    }

    private void setNetworkButtonsLocked(boolean locked) {
        this.loginButton.setEnabled(!locked);
        this.goToRegiButton.setEnabled(!locked);
    }


    // MARK: - Input Validation

    private boolean checkUsername(String username) {
        boolean validLength = username.length() >= 4;
        if(!validLength) {
            Toast.makeText(this, "Bitte gültigen Namen angeben", Toast.LENGTH_LONG).show();
            this.usernameInputField.getText().clear();
            return false;
        }
        return true;
    }

    private boolean checkPassword(String password) {
        boolean validLength = password.length() >= 4;
        if(!validLength) {
            Toast.makeText(this, "Bitte gültiges Passwort angeben", Toast.LENGTH_LONG).show();
            this.passwordInputField.getText().clear();
            return false;
        }
        return true;
    }


    // MARK: - NetworkRequestDelegate

    @Override
    public void didRecieveNetworkResponse(int requestId, JSONObject[] data) {
        // Input wieder freigeben
        this.setNetworkButtonsLocked(false);
        // Prüfen auf internen fehler
        if(requestId == NetworkRequestDelegate.INVALID_REQUEST_ID || requestId != this.currentRequestId || data.length < 1) {
            return;
        }
        // Neuen User parsen und in der DB Speichern
        try {
            JSONObject json = data[0];
            User parsedUser = new User();
            parsedUser.name = (String)json.get("name");
            parsedUser.email = (String)json.get("email");
            parsedUser.password = (String)json.get("password");
            parsedUser.token = (String)json.get("token");
            this.dbHandle.deleteAllUsers();
            this.dbHandle.addUser(parsedUser);
        } catch (Exception e) {
            Toast.makeText(this, "Der Server hat ungültige Daten gesendet!", Toast.LENGTH_LONG).show();
            if(AppUtils.DEBUG_MODE) {
                System.out.println("Error in LoginActivity while processing User");
                e.printStackTrace();
            }
            return;
        }
        Toast.makeText(this, "Login erfolgreich", Toast.LENGTH_LONG).show();
        // back to main menu
        Intent intent = new Intent(this, MenuActivity.class);
        startActivity(intent);
    }

    @Override
    public void didRecieveNetworkError(int requestId, NetworkError error) {
        if(requestId == this.currentRequestId) {
            this.currentRequestId = NetworkRequestDelegate.INVALID_REQUEST_ID;
            this.setNetworkButtonsLocked(false);
            if(AppUtils.DEBUG_MODE) {
                System.out.println("Login Error: "+error.toString());
            }
            if(error.type == NetworkError.Type.httpStatus && error.statusCode == 403) {
                // 403 -> credentials invalid
                Toast.makeText(this, "Zugangsdaten ungültig", Toast.LENGTH_LONG).show();
                this.usernameInputField.getText().clear();
                this.passwordInputField.getText().clear();
            } else {
                // else -> some kind of error then
                Toast.makeText(this, "Netzwerkfehler", Toast.LENGTH_LONG).show();
            }
        } else {
            return;
        }
    }
}

