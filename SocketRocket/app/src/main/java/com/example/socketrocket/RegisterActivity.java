package com.example.socketrocket;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.socketrocket.appengine.database.DatabaseConnection;
import com.example.socketrocket.appengine.database.reflect.objects.User;
import com.example.socketrocket.appengine.networking.NetworkConnection;
import com.example.socketrocket.appengine.networking.NetworkError;
import com.example.socketrocket.appengine.networking.NetworkRequestDelegate;

import org.json.JSONObject;

public class RegisterActivity extends Activity implements View.OnClickListener, NetworkRequestDelegate {

    private EditText usernameInput, emailInput, passwordInput, confirmPasswordInput;
    private Button signUpButton;
    private int currentRequestId = NetworkRequestDelegate.INVALID_REQUEST_ID;
    private DatabaseConnection dbHandle;
    private User sentUser = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_register);
        this.dbHandle = new DatabaseConnection(this);
        this.init();
    }

    private void init() {
        this.usernameInput = this.findViewById(R.id.register_edittext_username);
        this.emailInput = this.findViewById(R.id.register_edittext_email);
        this.passwordInput = this.findViewById(R.id.register_edittext_password);
        this.confirmPasswordInput = this.findViewById(R.id.register_edittext_password_repeat);
        this.signUpButton = this.findViewById(R.id.register_button_signup);
        this.signUpButton.setOnClickListener(this);
        //this.usernameInput.setText("username");
        //this.emailInput.setText("user@example.com");
        //this.passwordInput.setText("username");
        //this.confirmPasswordInput.setText("username");
    }

    @Override
    public void onClick(View v) {
        if(v == this.signUpButton) {
            String username = this.usernameInput.getText().toString();
            String email = this.emailInput.getText().toString();
            String password = this.passwordInput.getText().toString();
            String repeatPassword = this.confirmPasswordInput.getText().toString();
            // Check Input
            if(!checkUsername(username)) return;
            if(!checkEmail(email)) return;
            if(!checkPasswords(password, repeatPassword)) return;
            // Start request
            password = AppUtils.md5(password);
            int requestId = NetworkConnection.sendRegistrationRequest(this, email, username, password);
            this.currentRequestId = requestId;
            if(requestId == NetworkRequestDelegate.INVALID_REQUEST_ID) {
                Toast.makeText(this, "Interner Fehler", Toast.LENGTH_LONG).show();
                return;
            }
            this.setNetworkButtonsLocked(true);
            this.sentUser = new User();
            this.sentUser.name = username;
            this.sentUser.email = email;
            this.sentUser.password = password;
        }
    }

    private void setNetworkButtonsLocked(boolean locked) {
        this.signUpButton.setEnabled(!locked);
        this.usernameInput.setEnabled(!locked);
    }

    // MARK: - Input Check

    private boolean checkUsername(String username) {
        boolean nameEmpty = username.isEmpty();
        boolean validLength = username.length() >= 4;
        if(nameEmpty || !validLength) {
            Toast.makeText(this, nameEmpty ? "Bitte Namen angeben" : "Name muss mind. 4 Zeichen lang sein", Toast.LENGTH_LONG).show();
            this.usernameInput.getText().clear();
            return false;
        }
        return true;
    }

    private boolean checkEmail(String email) {
        boolean mailEmpty = email.isEmpty();
        boolean matchesPattern = email.matches("[\\S]{1,50}@[\\S]{1,50}\\.[\\S]{1,3}");
        if(mailEmpty || !matchesPattern) {
            Toast.makeText(this, mailEmpty ? "Bitte E-Mail angeben" : "E-Mail Adresse ungültig", Toast.LENGTH_LONG).show();
            this.emailInput.getText().clear();
            return false;
        }
        return true;
    }

    private boolean checkPasswords(String pw1, String pw2) {
        boolean firstEmpty = pw1.isEmpty();
        boolean secondEmpty = pw2.isEmpty();
        boolean validLength = pw1.length() >= 4 && pw1.length() <= 40;
        boolean bothEqual = pw1.equals(pw2);
        if(firstEmpty || secondEmpty || !validLength || !bothEqual) {
            String title = "";
            if(firstEmpty) title = "Bitte Passwort eingeben";
            else if(secondEmpty) title = "Bitte Passwort wiederholen";
            else if(!validLength) title = "Passwort muss zw. 4 u. 40 Zeichen lang sein";
            else if(!bothEqual) title = "Die Passwörter stimmen nicht überein";
            Toast.makeText(this, title, Toast.LENGTH_LONG).show();
            this.passwordInput.getText().clear();
            this.confirmPasswordInput.getText().clear();
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
            User newUser = new User();
            newUser.name = this.sentUser.name;
            newUser.email = this.sentUser.email;
            newUser.password = this.sentUser.password;
            newUser.token = (String)json.get("token");
            this.dbHandle.deleteAllUsers();
            this.dbHandle.addUser(newUser);
            this.sentUser = null;
        } catch (Exception e) {
            Toast.makeText(this, "Der Server hat ungültige Daten gesendet!", Toast.LENGTH_LONG).show();
            if(AppUtils.DEBUG_MODE) {
                System.out.println("Error in RegisterActivity while processing User");
                e.printStackTrace();
            }
            return;
        }
        Toast.makeText(this, "Registierung erfolgreich", Toast.LENGTH_LONG).show();
        // back to main menu
        Intent intent = new Intent(this, MenuActivity.class);
        startActivity(intent);
    }

    @Override
    public void didRecieveNetworkError(int requestId, NetworkError error) {
        this.setNetworkButtonsLocked(false);
        this.sentUser = null;
        if(AppUtils.DEBUG_MODE) {
            System.out.println("Netzwerkfehler:\n" + error.toString());
        }
        if(error.statusCode == 409) {
            Toast.makeText(this, "Der Name ist bereits vergeben", Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(this, "Netzwerkfehler", Toast.LENGTH_LONG).show();
        }
    }

}
