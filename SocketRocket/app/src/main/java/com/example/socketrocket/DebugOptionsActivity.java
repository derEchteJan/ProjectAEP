package com.example.socketrocket;

import android.app.Activity;

import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.Toast;

import com.example.socketrocket.appengine.database.DatabaseConnection;
import com.example.socketrocket.appengine.database.reflect.objects.Score;
import com.example.socketrocket.appengine.database.reflect.objects.Setting;
import com.example.socketrocket.appengine.database.reflect.objects.User;
import com.example.socketrocket.appengine.networking.NetworkConnection;
import com.example.socketrocket.appengine.networking.NetworkError;
import com.example.socketrocket.appengine.networking.NetworkRequestDelegate;

import org.json.JSONObject;

public class DebugOptionsActivity extends Activity implements View.OnClickListener, NetworkRequestDelegate {

    private DatabaseConnection dbHandle;
    private int currentRequestId = NetworkRequestDelegate.INVALID_REQUEST_ID;
    private EditText textFieldUsername, textFieldEmail, textFieldPassword, textFieldToken;

    // MARK: - Lifecycle

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.setContentView(R.layout.activity_debug_options);
        this.initViews();
        this.dbHandle = new DatabaseConnection(this);
    }


    // MARK: - Initialization

    private void initViews() {
        // navigation
        this.findViewById(R.id.debug_button_back).setOnClickListener(this);
        // datenbank
        this.findViewById(R.id.debug_button_reinit_db).setOnClickListener(this);
        this.findViewById(R.id.debug_button_delete_db).setOnClickListener(this);
        this.findViewById(R.id.debug_button_print_db_info).setOnClickListener(this);
        this.findViewById(R.id.debug_button_print_user_data).setOnClickListener(this);
        this.findViewById(R.id.debug_button_print_highscore_data).setOnClickListener(this);
        this.findViewById(R.id.debug_button_print_settings_data).setOnClickListener(this);
        this.findViewById(R.id.debug_button_populate_db).setOnClickListener(this);
        // netzwerk
        this.findViewById(R.id.debug_button_send_test_request).setOnClickListener(this);
        this.findViewById(R.id.debug_button_send_signup_request).setOnClickListener(this);
        this.findViewById(R.id.debug_button_send_login_request).setOnClickListener(this);
        this.findViewById(R.id.debug_button_send_my_highscores_request).setOnClickListener(this);
        this.findViewById(R.id.debug_button_send_top_highscores_request).setOnClickListener(this);
        // netzwerk - user template
        this.textFieldUsername = this.findViewById(R.id.debug_edittext_username);
        this.textFieldEmail = this.findViewById(R.id.debug_edittext_email);
        this.textFieldPassword = this.findViewById(R.id.debug_edittext_password);
        this.textFieldToken = this.findViewById(R.id.debug_edittext_token);
        this.textFieldUsername.setDefaultFocusHighlightEnabled(false);
    }


    // MARK: - OnClickListener

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            // navigation
            case R.id.debug_button_back: this.onMainMenuPressed(); break;
            // datenbank
            case R.id.debug_button_reinit_db: this.onReinitDBPressed(); break;
            case R.id.debug_button_delete_db: this.onDeleteDBPressed(); break;
            case R.id.debug_button_print_db_info: this.onDBInfoPressed(); break;
            case R.id.debug_button_print_user_data: this.onPrintUserDataPressed(); break;
            case R.id.debug_button_print_highscore_data: this.onPrintHighscoreDataPressed(); break;
            case R.id.debug_button_print_settings_data: this.onPrintSettingsDataPressed(); break;
            case R.id.debug_button_populate_db: this.onPopulateDBPressed(); break;
            // netzwerk
            case R.id.debug_button_send_test_request: this.onSendTestRequestPressed(); break;
            case R.id.debug_button_send_signup_request: this.onSendSignupRequestPressed(); break;
            case R.id.debug_button_send_login_request: this.onSendLoginRequestPressed(); break;
            case R.id.debug_button_send_my_highscores_request: this.onSendLoadMyHighscoresRequestPressed(); break;
            case R.id.debug_button_send_top_highscores_request: this.onSendLoadAllHighscoresRequest(); break;
            // sonstige
            default:
                Toast.makeText(this, "Action not implemented", Toast.LENGTH_LONG).show(); break;
        }
    }

    // MARK: - Button Actions

    // Navigation

    private void onMainMenuPressed() {
        this.finish();
    }

    // Datenbank

    private void onReinitDBPressed() {
        this.dbHandle = new DatabaseConnection(this);
        Toast.makeText(this, "Datenbank neu angelegt", Toast.LENGTH_LONG).show();
    }

    private void onDeleteDBPressed() {
        Runnable databaseTask = new Runnable() {
            public void run() {
                boolean success = dbHandle.deleteDatabase();
                String result = success ? "Deleted Database" : "Database could not be deleted";
                System.out.println(result);
                Toast.makeText(DebugOptionsActivity.this, result, Toast.LENGTH_LONG).show();
            }
        };
        AppUtils.showAskIfContinueAlert(this, "Delete Database?", databaseTask);
    }

    private void onDBInfoPressed() {
        String path = this.dbHandle.getDatabasePath();
        String result;
        if(path != null) {
            result = "Database: " + path + "\n";
            result += "File size: " + this.dbHandle.getDatabaseSize();
        } else {
            result = "Database: none";
        }
        System.out.println(result);
        Toast.makeText(this, result, Toast.LENGTH_LONG).show();
    }

    private void onPrintUserDataPressed() {
        Runnable databaseTask = new Runnable() {
            public void run() {
                User[] results = dbHandle.getAllUsers();
                String result;
                if (results.length > 0) {
                    result = "Users:";
                    for (User user : results) result += "\n" + user.toString();
                } else {
                    result = "Keine Users vorhanden";
                }
                Toast.makeText(DebugOptionsActivity.this, result, Toast.LENGTH_LONG).show();
            }
        };
        AppUtils.showAskIfContinueAlert(this, "Print all User Objects?", databaseTask);
    }

    private void onPrintHighscoreDataPressed() {
        Runnable databaseTask = new Runnable() {
            public void run() {
                Score[] results = dbHandle.getAllScores();
                String result;
                if(results.length > 0) {
                    result = "Scores:";
                    for(Score score: results) result += "\n" + score.toString();
                } else {
                    result = "Keine Scores vorhanden";
                }
                Toast.makeText(DebugOptionsActivity.this, result, Toast.LENGTH_LONG).show();
            }
        };
        AppUtils.showAskIfContinueAlert(this, "Print all User Objects?", databaseTask);
    }

    private void onPrintSettingsDataPressed() {
        Runnable databaseTask = new Runnable() {
            public void run() {
                Setting[] results = dbHandle.getAllSettings();
                String result;
                if(results.length > 0) {
                    result = "Settings:";
                    for(Setting setting: results) result += "\n" + setting.toString();
                } else {
                    result = "Keine Settings vorhanden";
                }
                Toast.makeText(DebugOptionsActivity.this, result, Toast.LENGTH_LONG).show();
            }
        };
        AppUtils.showAskIfContinueAlert(this, "Print all Setting Objects?", databaseTask);
    }

    private void onPopulateDBPressed() {
        // TODO: Datenbank besiedeln
    }


    // Netzwerk

    private void onSendTestRequestPressed() {
        Runnable networkTask = new Runnable() {
            public void run() {
                currentRequestId = NetworkConnection.sendTestRequest(DebugOptionsActivity.this);
                if(currentRequestId != NetworkRequestDelegate.INVALID_REQUEST_ID)
                    setNetworkButtonsLocked(true);
            }
        };
        AppUtils.showAskIfContinueAlert(this, "Test-Request senden?", networkTask);
    }

    private void onSendSignupRequestPressed() {
        final String username = this.textFieldUsername.getText().toString();
        final String email = this.textFieldEmail.getText().toString();
        final String password = AppUtils.md5(this.textFieldPassword.getText().toString()); // pw hashen
        Runnable networkTask = new Runnable() {
            public void run() {
                currentRequestId = NetworkConnection.sendSignUpRequest(DebugOptionsActivity.this, username, email, password);
                if(currentRequestId != NetworkRequestDelegate.INVALID_REQUEST_ID)
                    setNetworkButtonsLocked(true);
            }
        };
        String title = "Registrierung durchführen?";
        title += "\nusername: "+username+",";
        title += "\nemail: "+email+",";
        title += "\npasswort: "+password+"";
        AppUtils.showAskIfContinueAlert(this, title, networkTask);
    }

    private void onSendLoginRequestPressed() {
        final String username = this.textFieldUsername.getText().toString();
        final String password = AppUtils.md5(this.textFieldPassword.getText().toString()); // pw hashen
        Runnable networkTask = new Runnable() {
            public void run() {
                currentRequestId = NetworkConnection.sendLoginRequest(DebugOptionsActivity.this, username, password);
                if(currentRequestId != NetworkRequestDelegate.INVALID_REQUEST_ID)
                    setNetworkButtonsLocked(true);
            }
        };
        String title = "Login durchführen?";
        title += "\nusername: "+username+",";
        title += "\npasswort: "+password+"";
        AppUtils.showAskIfContinueAlert(this, title, networkTask);
    }

    private void onSendLoadMyHighscoresRequestPressed() {
        final String token = this.textFieldToken.getText().toString();
        Runnable networkTask = new Runnable() {
            public void run() {
                currentRequestId = NetworkConnection.sendLoadMyHighscoresRequest(DebugOptionsActivity.this, token);
                if(currentRequestId != NetworkRequestDelegate.INVALID_REQUEST_ID)
                    setNetworkButtonsLocked(true);
            }
        };
        String title = "Eigene Scores laden?";
        title += "\ntoken: "+token;
        AppUtils.showAskIfContinueAlert(this, title, networkTask);
    }

    private void onSendLoadAllHighscoresRequest() {
        Runnable networkTask = new Runnable() {
            public void run() {
                currentRequestId = NetworkConnection.sendLoadAllHighscoresRequest(DebugOptionsActivity.this);
                if(currentRequestId != NetworkRequestDelegate.INVALID_REQUEST_ID)
                    setNetworkButtonsLocked(true);
            }
        };
        String title = "Top-Scores laden?";
        AppUtils.showAskIfContinueAlert(this, title, networkTask);
    }

    // Lock Buttons

    private void setNetworkButtonsLocked(boolean locked) {
        this.findViewById(R.id.debug_button_send_test_request).setEnabled(!locked);
        this.findViewById(R.id.debug_button_send_signup_request).setEnabled(!locked);
        this.findViewById(R.id.debug_button_send_login_request).setEnabled(!locked);
        this.findViewById(R.id.debug_button_send_my_highscores_request).setEnabled(!locked);
        this.findViewById(R.id.debug_button_send_top_highscores_request).setEnabled(!locked);
    }


    // MARK: - interface NetworkRequestDelegate

    @Override
    public void didRecieveNetworkResponse(int requestId, JSONObject[] data) {
        String result;
        if(data.length > 0) {
            result = "Recieved:";
            for(JSONObject object: data) result += "\n" + object.toString();
        }
        else result = "Recieved empty response";
        System.out.println(result);
        Toast.makeText(this, result, Toast.LENGTH_LONG).show();
        this.setNetworkButtonsLocked(false);
        this.currentRequestId = NetworkRequestDelegate.INVALID_REQUEST_ID;
    }

    @Override
    public void didRecieveNetworkError(int requestId, NetworkError error) {
        String result = "Network error: " + error.toString();
        System.out.println(result);
        Toast.makeText(this, result, Toast.LENGTH_LONG).show();
        this.setNetworkButtonsLocked(false);
        this.currentRequestId = NetworkRequestDelegate.INVALID_REQUEST_ID;
    }
}
