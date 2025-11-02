package com.example.minesweeper;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;

/*
    Settings Screen
 */
public class SettingsActivity extends AppCompatActivity {

    private static final String PREFS_NAME = "MinesweeperSettings";
    private static final String KEY_ROWS = "rows";
    private static final String KEY_COLUMNS = "columns";
    private static final String KEY_MINES_PERCENT = "minesPercent";
    private static final String KEY_COVERED_COLOR = "coveredColor";
    private static final String KEY_UNCOVERED_COLOR = "uncoveredColor";
    private static final String KEY_SUSPECTED_COLOR = "suspectedColor";
    private static final String KEY_MINE_COLOR = "mineColor";

    private SharedPreferences sharedPreferences;

    private RadioGroup rowsRadioGroup;
    private RadioGroup columnsRadioGroup;
    private RadioGroup minesRadioGroup;
    private RadioGroup coveredColorRadioGroup;
    private RadioGroup uncoveredColorRadioGroup;
    private RadioGroup suspectedColorRadioGroup;
    private RadioGroup mineColorRadioGroup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        sharedPreferences = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);

        initializeViews();
        loadSettings();

        Button saveButton = findViewById(R.id.saveButton);
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveSettings();
                finish();
            }
        });
    }

    private void initializeViews() {
        rowsRadioGroup = findViewById(R.id.rowsRadioGroup);
        columnsRadioGroup = findViewById(R.id.columnsRadioGroup);
        minesRadioGroup = findViewById(R.id.minesRadioGroup);
        coveredColorRadioGroup = findViewById(R.id.coveredColorRadioGroup);
        uncoveredColorRadioGroup = findViewById(R.id.uncoveredColorRadioGroup);
        suspectedColorRadioGroup = findViewById(R.id.suspectedColorRadioGroup);
        mineColorRadioGroup = findViewById(R.id.mineColorRadioGroup);
        
        // Setup color swatches
        setupColorSwatches(coveredColorRadioGroup, "covered_color");
        setupColorSwatches(uncoveredColorRadioGroup, "uncovered_color");
        setupColorSwatches(suspectedColorRadioGroup, "suspected_color");
        setupColorSwatches(mineColorRadioGroup, "mine_color");
    }
    
    private void setupColorSwatches(RadioGroup radioGroup, String colorType) {
        for (int i = 1; i <= 5; i++) {
            String resourceName = colorType + "_" + i;
            int colorResId = getResources().getIdentifier(resourceName, "color", getPackageName());
            int color = ContextCompat.getColor(this, colorResId);
            
            // Set IDs based on color type
            int radioButtonId;
            if (colorType.equals("covered_color")) {
                switch(i) {
                    case 1: radioButtonId = R.id.coveredColor1; break;
                    case 2: radioButtonId = R.id.coveredColor2; break;
                    case 3: radioButtonId = R.id.coveredColor3; break;
                    case 4: radioButtonId = R.id.coveredColor4; break;
                    case 5: radioButtonId = R.id.coveredColor5; break;
                    default: radioButtonId = R.id.coveredColor1; break;
                }
            } else if (colorType.equals("uncovered_color")) {
                switch(i) {
                    case 1: radioButtonId = R.id.uncoveredColor1; break;
                    case 2: radioButtonId = R.id.uncoveredColor2; break;
                    case 3: radioButtonId = R.id.uncoveredColor3; break;
                    case 4: radioButtonId = R.id.uncoveredColor4; break;
                    case 5: radioButtonId = R.id.uncoveredColor5; break;
                    default: radioButtonId = R.id.uncoveredColor1; break;
                }
            } else if (colorType.equals("suspected_color")) {
                switch(i) {
                    case 1: radioButtonId = R.id.suspectedColor1; break;
                    case 2: radioButtonId = R.id.suspectedColor2; break;
                    case 3: radioButtonId = R.id.suspectedColor3; break;
                    case 4: radioButtonId = R.id.suspectedColor4; break;
                    case 5: radioButtonId = R.id.suspectedColor5; break;
                    default: radioButtonId = R.id.suspectedColor1; break;
                }
            } else {
                switch(i) {
                    case 1: radioButtonId = R.id.mineColor1; break;
                    case 2: radioButtonId = R.id.mineColor2; break;
                    case 3: radioButtonId = R.id.mineColor3; break;
                    case 4: radioButtonId = R.id.mineColor4; break;
                    case 5: radioButtonId = R.id.mineColor5; break;
                    default: radioButtonId = R.id.mineColor1; break;
                }
            }
            
            // Create radio button to make it look like a color swatch
            RadioButton radioButton = new RadioButton(this);
            radioButton.setId(radioButtonId);
            radioButton.setButtonDrawable(android.R.color.transparent);
            
            // Set the background as the color swatch
            GradientDrawable drawable = new GradientDrawable();
            drawable.setShape(GradientDrawable.RECTANGLE);
            drawable.setColor(color);
            drawable.setCornerRadius(12f);
            drawable.setStroke(3, Color.GRAY);
            radioButton.setBackground(drawable);
            
            // Set size
            int size = (int) (56 * getResources().getDisplayMetrics().density); // 56dp
            RadioGroup.LayoutParams params = new RadioGroup.LayoutParams(size, size);
            params.setMargins(4, 4, 4, 4);
            radioButton.setLayoutParams(params);
            
            // Remove text and padding
            radioButton.setText("");
            radioButton.setPadding(0, 0, 0, 0);
            
            // Make clickable
            radioButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    updateSwatchSelection(radioGroup, radioButtonId);
                }
            });
            
            // Add to radio group directly
            radioGroup.addView(radioButton);
        }
    }
    
    private void updateSwatchSelection(RadioGroup radioGroup, int selectedId) {
        // Update visual indication of selected swatch
        for (int i = 0; i < radioGroup.getChildCount(); i++) {
            View child = radioGroup.getChildAt(i);
            if (child instanceof RadioButton) {
                RadioButton radio = (RadioButton) child;
                GradientDrawable drawable = (GradientDrawable) radio.getBackground();
                if (radio.getId() == selectedId && radio.isChecked()) {
                    drawable.setStroke(5, Color.BLACK);
                } else {
                    drawable.setStroke(3, Color.GRAY);
                }
            }
        }
    }

    private void loadSettings() {
        // Load and set rows (default: 8)
        int rows = sharedPreferences.getInt(KEY_ROWS, 8);
        setRadioGroupSelection(rowsRadioGroup, getRowsId(rows));

        // Load and set columns (default: 8)
        int columns = sharedPreferences.getInt(KEY_COLUMNS, 8);
        setRadioGroupSelection(columnsRadioGroup, getColumnsId(columns));

        // Load and set mines percent (default: 15%)
        int minesPercent = sharedPreferences.getInt(KEY_MINES_PERCENT, 15);
        setRadioGroupSelection(minesRadioGroup, getMinesPercentId(minesPercent));

        // Load and set colors (default: 1 for all)
        int coveredColor = sharedPreferences.getInt(KEY_COVERED_COLOR, 1);
        int coveredColorId = getColorId(coveredColorRadioGroup, coveredColor);
        setRadioGroupSelection(coveredColorRadioGroup, coveredColorId);
        updateSwatchSelection(coveredColorRadioGroup, coveredColorId);

        int uncoveredColor = sharedPreferences.getInt(KEY_UNCOVERED_COLOR, 1);
        int uncoveredColorId = getColorId(uncoveredColorRadioGroup, uncoveredColor);
        setRadioGroupSelection(uncoveredColorRadioGroup, uncoveredColorId);
        updateSwatchSelection(uncoveredColorRadioGroup, uncoveredColorId);

        int suspectedColor = sharedPreferences.getInt(KEY_SUSPECTED_COLOR, 1);
        int suspectedColorId = getColorId(suspectedColorRadioGroup, suspectedColor);
        setRadioGroupSelection(suspectedColorRadioGroup, suspectedColorId);
        updateSwatchSelection(suspectedColorRadioGroup, suspectedColorId);

        int mineColor = sharedPreferences.getInt(KEY_MINE_COLOR, 1);
        int mineColorId = getColorId(mineColorRadioGroup, mineColor);
        setRadioGroupSelection(mineColorRadioGroup, mineColorId);
        updateSwatchSelection(mineColorRadioGroup, mineColorId);
    }

    private void saveSettings() {
        SharedPreferences.Editor editor = sharedPreferences.edit();

        // Save rows
        int rowsId = rowsRadioGroup.getCheckedRadioButtonId();
        editor.putInt(KEY_ROWS, getRowsValue(rowsId));

        // Save columns
        int columnsId = columnsRadioGroup.getCheckedRadioButtonId();
        editor.putInt(KEY_COLUMNS, getColumnsValue(columnsId));

        // Save mines percent
        int minesId = minesRadioGroup.getCheckedRadioButtonId();
        editor.putInt(KEY_MINES_PERCENT, getMinesPercentValue(minesId));

        // Save colors
        editor.putInt(KEY_COVERED_COLOR, getSelectedColorIndex(coveredColorRadioGroup));
        editor.putInt(KEY_UNCOVERED_COLOR, getSelectedColorIndex(uncoveredColorRadioGroup));
        editor.putInt(KEY_SUSPECTED_COLOR, getSelectedColorIndex(suspectedColorRadioGroup));
        editor.putInt(KEY_MINE_COLOR, getSelectedColorIndex(mineColorRadioGroup));

        editor.apply();
    }

    private void setRadioGroupSelection(RadioGroup group, int id) {
        if (id != -1) {
            group.check(id);
        }
    }

    private int getRowsId(int rows) {
        switch (rows) {
            case 5: return R.id.rows5;
            case 6: return R.id.rows6;
            case 7: return R.id.rows7;
            case 8: return R.id.rows8;
            case 9: return R.id.rows9;
            case 10: return R.id.rows10;
            default: return R.id.rows8;
        }
    }

    private int getRowsValue(int id) {
        if (id == R.id.rows5) return 5;
        if (id == R.id.rows6) return 6;
        if (id == R.id.rows7) return 7;
        if (id == R.id.rows8) return 8;
        if (id == R.id.rows9) return 9;
        if (id == R.id.rows10) return 10;
        return 8;
    }

    private int getColumnsId(int columns) {
        switch (columns) {
            case 5: return R.id.columns5;
            case 6: return R.id.columns6;
            case 7: return R.id.columns7;
            case 8: return R.id.columns8;
            case 9: return R.id.columns9;
            case 10: return R.id.columns10;
            default: return R.id.columns8;
        }
    }

    private int getColumnsValue(int id) {
        if (id == R.id.columns5) return 5;
        if (id == R.id.columns6) return 6;
        if (id == R.id.columns7) return 7;
        if (id == R.id.columns8) return 8;
        if (id == R.id.columns9) return 9;
        if (id == R.id.columns10) return 10;
        return 8;
    }

    private int getMinesPercentId(int percent) {
        switch (percent) {
            case 10: return R.id.mines10;
            case 15: return R.id.mines15;
            case 20: return R.id.mines20;
            default: return R.id.mines15;
        }
    }

    private int getMinesPercentValue(int id) {
        if (id == R.id.mines10) return 10;
        if (id == R.id.mines15) return 15;
        if (id == R.id.mines20) return 20;
        return 15;
    }

    private int getColorId(RadioGroup group, int colorIndex) {
        if (group == coveredColorRadioGroup) {
            switch (colorIndex) {
                case 1: return R.id.coveredColor1;
                case 2: return R.id.coveredColor2;
                case 3: return R.id.coveredColor3;
                case 4: return R.id.coveredColor4;
                case 5: return R.id.coveredColor5;
                default: return R.id.coveredColor1;
            }
        } else if (group == uncoveredColorRadioGroup) {
            switch (colorIndex) {
                case 1: return R.id.uncoveredColor1;
                case 2: return R.id.uncoveredColor2;
                case 3: return R.id.uncoveredColor3;
                case 4: return R.id.uncoveredColor4;
                case 5: return R.id.uncoveredColor5;
                default: return R.id.uncoveredColor1;
            }
        } else if (group == suspectedColorRadioGroup) {
            switch (colorIndex) {
                case 1: return R.id.suspectedColor1;
                case 2: return R.id.suspectedColor2;
                case 3: return R.id.suspectedColor3;
                case 4: return R.id.suspectedColor4;
                case 5: return R.id.suspectedColor5;
                default: return R.id.suspectedColor1;
            }
        } else if (group == mineColorRadioGroup) {
            switch (colorIndex) {
                case 1: return R.id.mineColor1;
                case 2: return R.id.mineColor2;
                case 3: return R.id.mineColor3;
                case 4: return R.id.mineColor4;
                case 5: return R.id.mineColor5;
                default: return R.id.mineColor1;
            }
        }
        return -1;
    }

    private int getSelectedColorIndex(RadioGroup group) {
        int checkedId = group.getCheckedRadioButtonId();
        if (group == coveredColorRadioGroup) {
            if (checkedId == R.id.coveredColor1) return 1;
            if (checkedId == R.id.coveredColor2) return 2;
            if (checkedId == R.id.coveredColor3) return 3;
            if (checkedId == R.id.coveredColor4) return 4;
            if (checkedId == R.id.coveredColor5) return 5;
        } else if (group == uncoveredColorRadioGroup) {
            if (checkedId == R.id.uncoveredColor1) return 1;
            if (checkedId == R.id.uncoveredColor2) return 2;
            if (checkedId == R.id.uncoveredColor3) return 3;
            if (checkedId == R.id.uncoveredColor4) return 4;
            if (checkedId == R.id.uncoveredColor5) return 5;
        } else if (group == suspectedColorRadioGroup) {
            if (checkedId == R.id.suspectedColor1) return 1;
            if (checkedId == R.id.suspectedColor2) return 2;
            if (checkedId == R.id.suspectedColor3) return 3;
            if (checkedId == R.id.suspectedColor4) return 4;
            if (checkedId == R.id.suspectedColor5) return 5;
        } else if (group == mineColorRadioGroup) {
            if (checkedId == R.id.mineColor1) return 1;
            if (checkedId == R.id.mineColor2) return 2;
            if (checkedId == R.id.mineColor3) return 3;
            if (checkedId == R.id.mineColor4) return 4;
            if (checkedId == R.id.mineColor5) return 5;
        }
        return 1;
    }

    // Static helper methods to access settings from other activities
    public static int getRows(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        return prefs.getInt(KEY_ROWS, 8);
    }

    public static int getColumns(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        return prefs.getInt(KEY_COLUMNS, 8);
    }

    public static int getMinesPercent(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        return prefs.getInt(KEY_MINES_PERCENT, 15);
    }

    public static int getCoveredColorIndex(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        return prefs.getInt(KEY_COVERED_COLOR, 1);
    }

    public static int getUncoveredColorIndex(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        return prefs.getInt(KEY_UNCOVERED_COLOR, 1);
    }

    public static int getSuspectedColorIndex(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        return prefs.getInt(KEY_SUSPECTED_COLOR, 1);
    }

    public static int getMineColorIndex(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        return prefs.getInt(KEY_MINE_COLOR, 1);
    }
}

