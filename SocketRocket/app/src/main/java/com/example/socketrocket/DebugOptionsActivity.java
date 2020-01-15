package com.example.socketrocket;

import android.app.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import com.example.socketrocket.appengine.database.DatabaseConnection;
import com.example.socketrocket.appengine.database.DatabaseController;

public class DebugOptionsActivity extends Activity implements View.OnClickListener {

    private DatabaseConnection dbHandle;

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
        this.findViewById(R.id.button_main_menu).setOnClickListener(this);
        this.findViewById(R.id.button_reinit_db).setOnClickListener(this);
        this.findViewById(R.id.button_delete_db).setOnClickListener(this);
        this.findViewById(R.id.button_info_db).setOnClickListener(this);
        this.findViewById(R.id.button_populate_db).setOnClickListener(this);
        this.findViewById(R.id.switch_debug_mode).setOnClickListener(this);
    }


    // MARK: - OnClickListener

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.button_main_menu: this.onMainMenuPressed(); break;
            case R.id.button_reinit_db: this.onReinitDBPressed(); break;
            case R.id.button_delete_db: this.onDeleteDBPressed(); break;
            case R.id.button_info_db: this.onDBInfoPressed(); break;
            //case R.id.button_populate_db: this.onPopulateDBPressed(); break;
            default:
                Toast.makeText(this, "Action not implemented", Toast.LENGTH_LONG).show();
                break;
        }
    }

    // MARK: - Buttons

    private void onMainMenuPressed() {
        this.finish();
    }

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

    }
}
