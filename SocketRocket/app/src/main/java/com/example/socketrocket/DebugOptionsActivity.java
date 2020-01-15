package com.example.socketrocket;

import android.app.Activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.Toast;

import com.example.socketrocket.appengine.database.DatabaseConnection;
import com.example.socketrocket.appengine.networking.NetworkConnection;
import com.example.socketrocket.appengine.networking.NetworkController;
import com.example.socketrocket.appengine.networking.NetworkErrorType;
import com.example.socketrocket.appengine.networking.NetworkRequestDelegate;

import org.json.JSONObject;

public class DebugOptionsActivity extends Activity implements View.OnClickListener, NetworkRequestDelegate {

    private DatabaseConnection dbHandle;
    private int currentRequestId = NetworkRequestDelegate.INVALID_REQUEST_ID;
    private EditText textFieldUsername, textFieldEmail, textFieldPassword;

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
        this.findViewById(R.id.debug_button_populate_db).setOnClickListener(this);
        this.findViewById(R.id.debug_button_print_user_data).setOnClickListener(this);
        // netzwerk
        this.findViewById(R.id.debug_button_send_test_request).setOnClickListener(this);
        this.findViewById(R.id.debug_button_send_signup_request).setOnClickListener(this);
        this.findViewById(R.id.debug_button_send_login_request).setOnClickListener(this);
        this.findViewById(R.id.debug_button_send_userdata_request).setOnClickListener(this);
        this.findViewById(R.id.debug_button_send_highscores_request).setOnClickListener(this);
        // netzwerk - user template
        this.textFieldUsername = this.findViewById(R.id.debug_edittext_username);
        this.textFieldEmail = this.findViewById(R.id.debug_edittext_email);
        this.textFieldPassword = this.findViewById(R.id.debug_edittext_password);
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
            case R.id.debug_button_populate_db: this.onPopulateDBPressed(); break;
            case R.id.debug_button_print_user_data: this.onPrintUserDataPressed(); break;
            // netzwerk
            case R.id.debug_button_send_test_request: this.onSendTestRequestPressed(); break;
            case R.id.debug_button_send_signup_request: this.onSendSignupRequestPressed(); break;
            case R.id.debug_button_send_login_request: this.onSendLoginRequestPressed(); break;
            case R.id.debug_button_send_userdata_request: this.onSendUserDataRequestPressed(); break;
            case R.id.debug_button_send_highscores_request: this.onSendHighscoresRequestPressed(); break;
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
    }

    private void onDeleteDBPressed() {
        boolean success = this.dbHandle.deleteDatabase();
        String result = success ? "Deleted Database" : "File not found";
        System.out.println(result);
        Toast.makeText(this, result, Toast.LENGTH_LONG).show();
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

    private void onPopulateDBPressed() {
        NetworkConnection.sendLoadHighscoresRequest(this);
    }

    private void onPrintUserDataPressed() {

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
        this.showSendRequestPrompt("Test Anfrage senden?", networkTask);
    }

    private void onSendSignupRequestPressed() {
        final String username = this.textFieldUsername.getText().toString();
        final String email = this.textFieldEmail.getText().toString();
        final String password = this.textFieldPassword.getText().toString();
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
        this.showSendRequestPrompt(title, networkTask);
    }

    private void onSendLoginRequestPressed() {
        final String username = this.textFieldUsername.getText().toString();
        final String password = this.textFieldPassword.getText().toString();
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
        this.showSendRequestPrompt(title, networkTask);
    }

    private void onSendUserDataRequestPressed() {
        Runnable networkTask = new Runnable() {
            public void run() {
                currentRequestId = NetworkConnection.sendLoadUserDataRequest(DebugOptionsActivity.this);
                if(currentRequestId != NetworkRequestDelegate.INVALID_REQUEST_ID)
                    setNetworkButtonsLocked(true);
            }
        };
        String title = "Userdaten laden?";
        this.showSendRequestPrompt(title, networkTask);
    }

    private void onSendHighscoresRequestPressed() {
        Runnable networkTask = new Runnable() {
            public void run() {
                currentRequestId = NetworkConnection.sendLoadHighscoresRequest(DebugOptionsActivity.this);
                if(currentRequestId != NetworkRequestDelegate.INVALID_REQUEST_ID)
                    setNetworkButtonsLocked(true);
            }
        };
        String title = "Highscores laden?";
        this.showSendRequestPrompt(title, networkTask);
    }

    // Lock Buttons

    private void setNetworkButtonsLocked(boolean locked) {
        this.findViewById(R.id.debug_button_send_test_request).setEnabled(!locked);
        this.findViewById(R.id.debug_button_send_signup_request).setEnabled(!locked);
        this.findViewById(R.id.debug_button_send_login_request).setEnabled(!locked);
        this.findViewById(R.id.debug_button_send_userdata_request).setEnabled(!locked);
        this.findViewById(R.id.debug_button_send_highscores_request).setEnabled(!locked);
    }


    // MARK: - Netzwerk senden Alert

    private void showSendRequestPrompt(final String message, final Runnable onContinue) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setMessage(message);
        alertDialogBuilder.setPositiveButton("yes",
                new DialogInterface.OnClickListener() {
                    @Override public void onClick(DialogInterface arg0, int arg1) {
                        onContinue.run();
                    }
                }
        );
        alertDialogBuilder.setNegativeButton("No", null);
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
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
    public void didRecieveNetworkError(int requestId, NetworkErrorType errorType) {
        String result = "Network error: " + errorType.toString();
        System.out.println(result);
        Toast.makeText(this, result, Toast.LENGTH_LONG).show();
        this.setNetworkButtonsLocked(false);
        this.currentRequestId = NetworkRequestDelegate.INVALID_REQUEST_ID;
    }
}
