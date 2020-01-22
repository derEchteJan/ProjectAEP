package com.example.socketrocket;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.service.autofill.UserData;
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

import org.json.JSONException;
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
        this.usernameInput = this.findViewById(R.id.register_editText_username);
        this.emailInput = this.findViewById(R.id.register_editText_email);
        this.passwordInput = this.findViewById(R.id.register_editText_password);
        this.confirmPasswordInput = this.findViewById(R.id.register_editText_confirm_password);
        this.signUpButton = this.findViewById(R.id.register_button_signUp);
        this.signUpButton.setOnClickListener(this);
        this.usernameInput.setText("username");
        this.emailInput.setText("user@example.com");
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
            if(!checkPasswords(password, repeatPassword)) {
                // passwort falsch -> textfelder leeren
                this.passwordInput.getText().clear();
                this.confirmPasswordInput.getText().clear();
                System.out.println("du mongo hast dich vertippt"); // debug, raus später
                this.showInvalidPassword(); // anzeige für falsches passwort aufrufen
            } else if(!this.checkUsername(username)) {
                // username falsch -> textfeld löschen, evtl noch rot unterstreichen?
                this.usernameInput.getText().clear();
                this.showInvalidUsername(); //Anzeige für Falschen Usernamen aufrufen
            } else if(!this.checkEmail(email)){
                // email enthält kein "@" --> textfeld wird gelöscht
                this.usernameInput.getText().clear();
                this.showInvalidEmail();
            }else {
                password = AppUtils.md5(password);
                int requestId = NetworkConnection.sendRegistrationRequest(this, email, username, password);
                this.currentRequestId = requestId;
                if(requestId == NetworkRequestDelegate.INVALID_REQUEST_ID) {
                    this.currentRequestId = requestId;
                    this.showNetworkError(null);
                }
                this.sentUser = new User();
                this.sentUser.name = username;
                this.sentUser.email = email;
                this.sentUser.password = password;
                this.setNetworkButtonsLocked(true);
            }
        }
    }


    // MARK: - Input Check

    private boolean checkPasswords(String pw1, String pw2) {
        // TODO: erweitern
        // liefert wenn die passworteingabe pw1 und die wiederholte eingabe pw2 gültig und gleich sind
        boolean patternMatch = pw1.matches("[a-z]{4,20}");
        return patternMatch && pw1.equals(pw2);
    }

    private boolean checkUsername(String username) {
        // TODO: erweitern
        // liefert ob der username im gültigen format und lang genug ist
        return username.length() > 3;
    }

    private boolean checkEmail(String email) {
        // TODO: erweitern
        // liefert ob der username im gültigen format und lang genug ist
        return email.contains("@");
    }

    // MARK: - Input Feedback

    private void showInvalidPassword() {
        // TODO: umsetzen
        Toast.makeText(this, "Invalid Password!", Toast.LENGTH_LONG).show();
    }

    private void showInvalidUsername() {
        // TODO: umsetzen
        Toast.makeText(this, "Invalid Username!", Toast.LENGTH_LONG).show();
    }

    private void showInvalidEmail(){
        // TODO: umsetzen
        Toast.makeText(this, "Invalid EMail, must contain @!", Toast.LENGTH_LONG).show();
    }

    private void showNetworkError(NetworkError error) {
        if(AppUtils.DEBUG_MODE) {
            System.out.println("Netzwerkfehler:\n" + error.toString());
        }
        if(error.statusCode == 409) {
            Toast.makeText(this, "Der Name ist bereits vergeben", Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(this, "Netzwerkfehler", Toast.LENGTH_LONG).show();
        }
    }

    private void showInternalError() {
        if (AppUtils.DEBUG_MODE) {
            System.out.println("Interner Fehler aufgetreten");
        }
        Toast.makeText(this, "Interner Fehler aufgetreten", Toast.LENGTH_LONG).show();
    }

    private void showSignUpSuccess() {
        Toast.makeText(this, "Registierung erfolgreich", Toast.LENGTH_LONG).show();
        // back to main menu
        Intent intent = new Intent(this, MenuActivity.class);
        startActivity(intent);
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
        this.showSignUpSuccess();
    }

    @Override
    public void didRecieveNetworkError(int requestId, NetworkError error) {
        this.setNetworkButtonsLocked(false);
        this.sentUser = null;
        this.showNetworkError(error);
    }


    // MARK: - Response Handling

    private void setNetworkButtonsLocked(boolean locked) {
        this.signUpButton.setEnabled(!locked);
        this.usernameInput.setEnabled(!locked);
    }

}
