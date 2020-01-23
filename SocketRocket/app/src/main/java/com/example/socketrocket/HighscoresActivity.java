package com.example.socketrocket;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.RelativeLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.example.socketrocket.appengine.BackgroundTaskHandler;
import com.example.socketrocket.appengine.database.DatabaseConnection;
import com.example.socketrocket.appengine.database.reflect.objects.Score;
import com.example.socketrocket.appengine.networking.NetworkConnection;
import com.example.socketrocket.appengine.networking.NetworkError;
import com.example.socketrocket.appengine.networking.NetworkRequestDelegate;

import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class HighscoresActivity extends Activity implements View.OnClickListener, NetworkRequestDelegate {

    private int TITLE_COLOR_DEFAULT, TITLE_COLOR_LOCKED, TITLE_COLOR_LOADING;

    private TableLayout table;
    private TextView titleLabel, titleAnnotationLabel;
    private Button myScoresButton, topScoresButton;
    private boolean isLoading = false;
    private boolean hasUser = false;
    private int currentTabIndex = 0;
    private Score[] myScores, topScores;
    private DatabaseConnection dbHandle;


    // MARK: - Lifecycle

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_highscores);
        this.dbHandle = new DatabaseConnection(this);
        this.hasUser = this.dbHandle.getAllUsers().length > 0;
        this.initViews();
    }

    private void initViews() {
        // colors
        this.TITLE_COLOR_DEFAULT = 0xFFFFFFFF;
        this.TITLE_COLOR_LOCKED = 0xC0B0B0B0;
        this.TITLE_COLOR_LOADING = super.getResources().getColor(R.color.abc_search_url_text_normal);
        // title
        this.titleLabel = super.findViewById(R.id.highscores_textview_title);
        this.titleAnnotationLabel = super.findViewById(R.id.highscores_textview_title_annotation);
        this.titleLabel.setOnClickListener(this);
        // buttons
        this.myScoresButton = this.findViewById(R.id.highscores_button_switch_left);
        this.topScoresButton = this.findViewById(R.id.highscores_button_switch_right);
        this.myScoresButton.setOnClickListener(this);
        this.topScoresButton.setOnClickListener(this);
        ColorStateList stateList = new ColorStateList(new int[][] {new int[] { android.R.attr.state_enabled}, new int[] {-android.R.attr.state_enabled}}, new int[] {0xFF404040, TITLE_COLOR_LOADING, 0, 0});
        this.myScoresButton.setBackgroundTintList(stateList);
        this.topScoresButton.setBackgroundTintList(stateList);
        this.updateButtons();
        // table
        this.table = this.findViewById(R.id.highscores_tablelayout_maintable);
        // load data
        this.loadInitialData();
        this.reloadTableView();
        System.out.println(table);
    }


    // MARK: - OnClickListener

    @Override
    public void onClick(View v) {
        if(v == this.myScoresButton) {
            this.onMyScoresButtonPressed();
        } else if(v == this.topScoresButton) {
            this.onTopScoresButtonPressed();
        } else if(v == this.titleLabel) {
            this.onTitleLabelPressed();
        }
    }

    // Button Actions

    private void onTitleLabelPressed() {
        /*guard*/ if(this.isLoading || this.currentTabIndex != 1) return;
        this.loadTopScores();
    }

    private void onMyScoresButtonPressed() {
        if(this.currentTabIndex != 0) {
            this.currentTabIndex = 0;
            this.updateButtons();
            this.reloadTableView();
        }
    }

    private void onTopScoresButtonPressed() {
        if(this.currentTabIndex != 1) {
            if(this.hasUser) {
                this.currentTabIndex = 1;
                this.updateButtons();
                this.reloadTableView();
            } else {
                this.showLoginRequirementDialog();
            }
        }
    }

    private void showLoginRequirementDialog() {
        Runnable onContinueToLoginTask = new Runnable() {
            public void run() {
                Intent intent = new Intent(HighscoresActivity.this, LoginActivity.class);
                HighscoresActivity.this.startActivity(intent);
            }
        };
        AppUtils.showAskIfContinueAlert(this, "Um die globalen Bestleistungen zu sehen müssen Sie eingeloggt sein. Jetzt Account Erstellen?", onContinueToLoginTask);
    }

    private void updateButtons() {
        this.myScoresButton.setTextColor(this.currentTabIndex == 0 ? TITLE_COLOR_DEFAULT : TITLE_COLOR_LOADING);
        this.topScoresButton.setTextColor(this.currentTabIndex == 0 ? (this.hasUser ? TITLE_COLOR_LOADING : TITLE_COLOR_LOCKED) : TITLE_COLOR_DEFAULT);
        this.myScoresButton.setEnabled(this.currentTabIndex != 0);
        this.topScoresButton.setEnabled(this.currentTabIndex != 1);
        this.titleLabel.setClickable(!this.isLoading && this.currentTabIndex == 1);
        this.titleAnnotationLabel.setVisibility((!isLoading && this.currentTabIndex == 1)? View.VISIBLE : View.GONE);
    }


    // MARK: - TableView

    private void reloadTableView() {
        this.table.removeAllViews();
        Score[] data = this.currentTabIndex == 0 ? this.myScores : this.topScores;
        if(data == null) {
            if(this.currentTabIndex == 0) {
                this.table.addView(new IndicatorRow(this, IndicatorRow.Type.DB_ERROR));
                //this.addErrorIndicator();
            } else {
                if(this.isLoading)
                    this.table.addView(new IndicatorRow(this, IndicatorRow.Type.IS_LOADING));
                else
                    this.table.addView(new IndicatorRow(this, IndicatorRow.Type.NETW_ERROR));
            }
        } else {
            if (data.length > 0) {
                for (int rowIndex = 0; rowIndex < data.length; rowIndex++) {
                    Score rowDataObeject = data[rowIndex];
                    ScoreRow newRow = new ScoreRow(this, this.currentTabIndex == 0 ? ScoreRow.Type.MY : ScoreRow.Type.TOP);
                    newRow.setRequiredData(rowDataObeject, rowIndex + 1);
                    this.table.addView(newRow);
                }
            } else {
                this.table.addView(new IndicatorRow(this, IndicatorRow.Type.EMPTY_SET));
            }
        }
        this.table.requestLayout();
    }


    // MARK: - Data Loading

    private void setLoadingState(boolean isLoading) {
        this.isLoading = isLoading;
        this.titleLabel.setClickable(!this.isLoading && this.currentTabIndex == 1);
        this.titleLabel.setTextColor(this.isLoading ? TITLE_COLOR_LOADING : TITLE_COLOR_DEFAULT);
        this.titleAnnotationLabel.setVisibility((!isLoading && this.currentTabIndex == 1)? View.VISIBLE : View.GONE);
    }

    private void loadInitialData() {
        this.loadMyScores();
        // todo: evtl vorhandene daten vom AppController bevorzugen
        if(this.hasUser) {
            this.loadTopScores();
        }
    }

    private void loadMyScores() {
        this.myScores = null;
        try {
            this.myScores = this.dbHandle.getAllScores();
        } catch (Exception e) {
            this.myScores = null;
            return;
        }
    }

    private void loadTopScores() {
        this.setLoadingState(true);
        if(this.currentTabIndex == 1) {
            this.table.removeAllViews();
            this.table.addView(new IndicatorRow(this, IndicatorRow.Type.IS_LOADING));
            this.table.requestLayout();
        }
        NetworkConnection.sendLoadAllHighscoresRequest(this);
    }


    // MARK: - NetworkRequestDelegate

    @Override
    public void didRecieveNetworkResponse(int requestId, JSONObject[] data) {
        this.setLoadingState(false);
        Score[] parsedResults = parseNetworkResults(data);
        this.topScores = parsedResults;
        if(this.currentTabIndex == 1) {
            this.reloadTableView();
        }
    }

    @Override
    public void didRecieveNetworkError(int requestId, NetworkError error) {
        this.setLoadingState(false);
        this.topScores = null;
        this.reloadTableView();
    }

    private Score[] parseNetworkResults(JSONObject[] data) {
        Score[] results = new Score[data.length];
        try {
            for(int i = 0; i < results.length; i++) {
                JSONObject object = data[i];
                Score element = new Score();
                element.user_name = (String)object.get("user_name");
                element.timestamp = Long.parseLong((String)object.get("timestamp"));
                element.amount = Long.parseLong((String)object.get("amount"));
                results[i] = element;
            }
        }catch (Exception e) {
            return null;
        }
        return results;
    }


    // MARK: - Custom TableRow class

    private static class ScoreRow extends TableRow {

        private enum Type {
            MY, TOP
        }

        private final int DEFAULT_TEXT_COLOR = 0xFFC0C0C0, DEFAULT_BACKGROUND_COLOR_DARK = 0xFF404040, DEFAULT_BACKGROUND_COLOR_LIGHT = 0xFF505050;
        private final float TITLE_TEXT_SIZE = 18.0f, TIMESTAMP_TEXT_SIZE = 13.0f;

        private Score dataObject;
        private TextView titleLabel, nameLabel, timestampLabel;
        private Type type;

        public ScoreRow(Context context, Type type) {
            super(context);
            this.type = type;
            this.initViews(context);
        }

        private void initViews(Context context) {
            // Layout this
            TableRow.LayoutParams layoutParams = new TableRow.LayoutParams(LayoutParams.MATCH_PARENT);
            this.setLayoutParams(layoutParams);

            // Subviews
            RelativeLayout containerFrame = new RelativeLayout(context);
            this.titleLabel = new TextView(context);
            this.timestampLabel = new TextView(context);
            this.nameLabel = new TextView(context);

            // Text style
            this.titleLabel.setTextSize(TITLE_TEXT_SIZE);
            this.timestampLabel.setTextSize(TIMESTAMP_TEXT_SIZE);
            this.nameLabel.setTextSize(TIMESTAMP_TEXT_SIZE);
            this.titleLabel.setTextColor(DEFAULT_TEXT_COLOR);
            this.timestampLabel.setTextColor(DEFAULT_TEXT_COLOR);
            this.nameLabel.setTextColor(DEFAULT_TEXT_COLOR);
            this.nameLabel.setSingleLine();
            nameLabel.setEllipsize(TextUtils.TruncateAt.END);

            // Layout container subview
            TableRow.LayoutParams containerLayout = new TableRow.LayoutParams(
                    TableRow.LayoutParams.MATCH_PARENT,
                    LayoutParams.MATCH_PARENT);
            containerLayout.weight = 1;
            containerFrame.setLayoutParams(containerLayout);

            // Layout label subviews
            RelativeLayout.LayoutParams titleLayout = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
            titleLayout.addRule(RelativeLayout.ALIGN_PARENT_LEFT, RelativeLayout.TRUE);
            titleLayout.leftMargin = 10;
            if(this.type == Type.MY) {
                titleLayout.topMargin = 10;
                titleLayout.bottomMargin = 10;
            }
            RelativeLayout.LayoutParams timestampLayout = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
            timestampLayout.addRule(RelativeLayout.ALIGN_PARENT_RIGHT, RelativeLayout.TRUE);
            timestampLayout.addRule(RelativeLayout.ALIGN_PARENT_TOP, RelativeLayout.TRUE);
            timestampLayout.topMargin = 15;
            timestampLayout.rightMargin = 10;
            RelativeLayout.LayoutParams nameLayout = new RelativeLayout.LayoutParams(800, RelativeLayout.LayoutParams.WRAP_CONTENT);
            nameLayout.addRule(RelativeLayout.ALIGN_PARENT_LEFT, RelativeLayout.TRUE);
            nameLayout.topMargin = 90;
            nameLayout.leftMargin = 130;
            nameLayout.bottomMargin = 20;

            // Add subviews
            containerFrame.addView(this.titleLabel, titleLayout);
            if(this.type == Type.TOP) containerFrame.addView(this.nameLabel, nameLayout);
            containerFrame.addView(this.timestampLabel, timestampLayout);
            this.addView(containerFrame);
        }

        private void updateViews(int rank) {
            String dateString = new SimpleDateFormat("dd.MM.yyyy", Locale.GERMAN).format(new Date(this.dataObject.timestamp));
            String daytimeString = new SimpleDateFormat("hh:mm", Locale.GERMAN).format(new Date(this.dataObject.timestamp));
            String title = rank + ".   " + this.dataObject.amount + " Punkte";
            String timestamp = "[am " + dateString + " um " + daytimeString + " uhr]";
            this.titleLabel.setText(title);
            this.timestampLabel.setText(timestamp);
            this.nameLabel.setText(dataObject.user_name);
            this.setBackgroundColor((rank&1)==0 ? DEFAULT_BACKGROUND_COLOR_DARK : DEFAULT_BACKGROUND_COLOR_LIGHT);
            this.requestLayout();
        }


        // MARK: - External

        public void setRequiredData(Score dataObject, int rank) {
            this.dataObject = dataObject;
            this.updateViews(rank);
        }

    }

    private static class IndicatorRow extends TableRow {

        private enum Type {
            EMPTY_SET, DB_ERROR, NETW_ERROR, IS_LOADING
        }

        private final int TITLE_COLOR_ERROR = 0xFFB03030, TITLE_COLOR_NORMAL = 0xFFC0C0C0;
        private final float TITLE_TEXT_SIZE = 18.0f;

        private TextView titleLabel;
        private Type type;

        public IndicatorRow(Context context, Type type) {
            super(context);
            this.type = type;
            this.initViews(context);
        }

        private void initViews(Context context) {
            // subviews
            this.titleLabel = new TextView(context);
            RelativeLayout containerFrame = new RelativeLayout(context);
            // container layout
            TableRow.LayoutParams containerLayout = new TableRow.LayoutParams(
                    TableRow.LayoutParams.MATCH_PARENT,
                    LayoutParams.MATCH_PARENT);
            containerLayout.weight = 1;
            // title text style
            this.titleLabel.setTextColor((this.type == Type.DB_ERROR || this.type == Type.NETW_ERROR) ? TITLE_COLOR_ERROR : TITLE_COLOR_NORMAL);
            this.titleLabel.setTextSize(TITLE_TEXT_SIZE);
            switch(this.type) {
                case EMPTY_SET: this.titleLabel.setText("Noch keine vorhanden <3"); break;
                case DB_ERROR: this.titleLabel.setText("Datenbankfehler, bitte App neustarten"); break;
                case NETW_ERROR: this.titleLabel.setText("Netzwerkfehler"); break;
                case IS_LOADING: this.titleLabel.setText("laden.."); break;
            }
            // title layout
            RelativeLayout.LayoutParams titleLayout = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
            titleLayout.addRule(RelativeLayout.ALIGN_PARENT_LEFT, RelativeLayout.TRUE);
            titleLayout.leftMargin = 60;
            // add subviews
            containerFrame.addView(this.titleLabel, titleLayout);
            this.addView(containerFrame, containerLayout);
            // done
            this.requestLayout();
        }
    }
}
