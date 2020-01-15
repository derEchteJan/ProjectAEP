package com.example.socketrocket;

import android.app.Activity;
import android.os.Bundle;
import android.service.autofill.UserData;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import com.example.socketrocket.R;
import com.example.socketrocket.appengine.database.DatabaseConnection;
import com.example.socketrocket.appengine.database.reflect.objects.User;
import com.example.socketrocket.appengine.networking.NetworkConnection;
import com.example.socketrocket.appengine.networking.NetworkErrorType;
import com.example.socketrocket.appengine.networking.NetworkRequestDelegate;

import org.json.JSONObject;

public class RegisterActivity extends Activity implements View.OnClickListener, NetworkRequestDelegate {

    private EditText usernameInput, emailInput, passwordInput, confirmPasswordInput;
    private Button signUpButton;
    private int currentRequestId = NetworkRequestDelegate.INVALID_REQUEST_ID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_register);
        DatabaseConnection.sharedInstance().initDatabase(this);
        this.init();
    }

    private void init() {
        // TODO: Email komplett noch dazu + handling
        this.usernameInput = this.findViewById(R.id.register_editText_username);
        this.emailInput = this.findViewById(R.id.register_editText_email);
        this.passwordInput = this.findViewById(R.id.register_editText_password);
        this.confirmPasswordInput = this.findViewById(R.id.register_editText_confirm_password);
        this.signUpButton = this.findViewById(R.id.register_button_signUp);
        this.signUpButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if(v == this.signUpButton) {
            String username = this.usernameInput.getText().toString();
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
            } else {
                int requestId = NetworkConnection.sendRegistrationRequest(this, "email", username, password);
                if(requestId == NetworkRequestDelegate.INVALID_REQUEST_ID) {
                    this.currentRequestId = requestId;
                    this.showNetworkError(null);
                }
            }
        }
    }


    // MARK: - Input Check

    private boolean checkPasswords(String pw1, String pw2) {
        // TODO: erweitern
        // liefert wenn die passworteingabe pw1 und die wiederholte eingabe pw2 gültig und gleich sind
        return pw1.equals(pw2);
    }

    private boolean checkUsername(String username) {
        // TODO: erweitern
        // liefert ob der username im gültigen format und lang genug ist
        return username.length() > 3;
    }


    // MARK: - Input Feedback

    private void showInvalidPassword() {
        // TODO: umsetzen
    }

    private void showInvalidUsername() {
        // TODO: umsetzen
    }

    private void showNetworkError(NetworkErrorType errorType) {
        // TODO: umsetzen
        System.out.println("Netzwerkfehler! "+errorType.toString());
    }

    private void showInternalError() {
        // TODO: umsetzen
        System.out.println("Interner Fehler aufgetreten! ");
    }

    private void showSignUpSuccess() {
        // TODO: umsetzen
    }


    // MARK: - NetworkRequestDelegate

    @Override
    public void didRecieveNetworkResponse(int requestId, JSONObject data) {
        if(requestId == NetworkRequestDelegate.INVALID_REQUEST_ID || requestId != this.currentRequestId) {
            return; // falsche request id
        }
        // TODO: umsetzen
        // TODO: "token" vom json auslesen, in der DB Speichern
        System.out.println("erfolgreich registriert.");
        if(this.handleResponse(data)){
            this.showSignUpSuccess();
        } else {
            this.showInternalError();
        }
    }

    @Override
    public void didRecieveNetworkError(int requestId, NetworkErrorType errorType) {
        this.showNetworkError(errorType);
    }


    // MARK: - Response Handling

    private boolean handleResponse(JSONObject responseObject) {
        try {
            User newUser = new User();
            newUser.name = (String)responseObject.get("name");
            newUser.email = (String)responseObject.get("email");
            newUser.password = (String)responseObject.get("password");
            newUser.token = (String)responseObject.get("token");
            DatabaseConnection.sharedInstance().addUser(newUser);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            // TODO: wenn response ungültig ist
            return false;
        }
    }

}
