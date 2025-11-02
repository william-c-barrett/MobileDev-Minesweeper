package com.example.minesweeper;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

/*
    board screen, allows for a single click to reveal a cell or a long click to mark with a flag:
 */
public class GameActivity extends AppCompatActivity {

    private GameBoard gameBoard;
    private FrameLayout[][] cellContainers;
    private TextView[][] cellViews;
    private ImageView[][] cellImages;
    private LinearLayout gameBoardLayout;
    private Button restartButton;
    private Button backButton;
    private int coveredColorResId;
    private int uncoveredColorResId;
    private int suspectedColorResId;
    private int mineColorResId;
    private int rows;
    private int columns;
    private int minesPercent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        // Load settings
        rows = SettingsActivity.getRows(this);
        columns = SettingsActivity.getColumns(this);
        minesPercent = SettingsActivity.getMinesPercent(this);

        // Load color resources
        loadColorResources();

        // Setup restart button
        restartButton = findViewById(R.id.restartButton);
        restartButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                restartGame();
            }
        });

        // Setup back button
        backButton = findViewById(R.id.backButton);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Navigate back to MainActivity
                Intent intent = new Intent(GameActivity.this, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
            }
        });

        // Create initial game board
        startNewGame();
    }

    private void startNewGame() {
        // Create game board
        gameBoard = new GameBoard(rows, columns, minesPercent);

        // Setup UI
        gameBoardLayout = findViewById(R.id.gameBoardLayout);
        setupGameBoard();

        // Hide restart button initially
        restartButton.setVisibility(View.GONE);
    }

    private void restartGame() {
        // Start a new game with the same settings
        startNewGame();
    }

    private void loadColorResources() {
        int coveredIndex = SettingsActivity.getCoveredColorIndex(this);
        int uncoveredIndex = SettingsActivity.getUncoveredColorIndex(this);
        int suspectedIndex = SettingsActivity.getSuspectedColorIndex(this);
        int mineIndex = SettingsActivity.getMineColorIndex(this);

        coveredColorResId = getColorResourceId("covered_color", coveredIndex, R.color.covered_color_1);
        uncoveredColorResId = getColorResourceId("uncovered_color", uncoveredIndex, R.color.uncovered_color_1);
        suspectedColorResId = getColorResourceId("suspected_color", suspectedIndex, R.color.suspected_color_1);
        mineColorResId = getColorResourceId("mine_color", mineIndex, R.color.mine_color_1);
    }

    private int getColorResourceId(String colorType, int index, int defaultResId) {
        String resourceName = colorType + "_" + index;
        int resId = getResources().getIdentifier(resourceName, "color", getPackageName());
        // If resource not found, return default
        if (resId == 0) {
            return defaultResId;
        }
        return resId;
    }

    private void setupGameBoard() {
        int rows = gameBoard.getRows();
        int columns = gameBoard.getColumns();
        cellViews = new TextView[rows][columns];
        cellImages = new ImageView[rows][columns];
        cellContainers = new FrameLayout[rows][columns];

        // Calculate cell size based on screen
        int screenWidth = getResources().getDisplayMetrics().widthPixels;
        int cellSize = Math.min((screenWidth - 32) / columns, 80); // Max 80dp per cell

        gameBoardLayout.removeAllViews();

        for (int i = 0; i < rows; i++) {
            LinearLayout rowLayout = new LinearLayout(this);
            rowLayout.setOrientation(LinearLayout.HORIZONTAL);
            rowLayout.setGravity(Gravity.CENTER);

            for (int j = 0; j < columns; j++) {
                FrameLayout container = createCellContainer(i, j, cellSize);
                rowLayout.addView(container);
                cellContainers[i][j] = container;
            }

            gameBoardLayout.addView(rowLayout);
        }

        updateBoardDisplay();
    }

    private FrameLayout createCellContainer(final int row, final int col, int size) {
        // Create container
        FrameLayout container = new FrameLayout(this);
        FrameLayout.LayoutParams containerParams = new FrameLayout.LayoutParams(size, size);
        containerParams.setMargins(2, 2, 2, 2);
        container.setLayoutParams(containerParams);
        
        // Create TextView for background and text
        TextView cellView = new TextView(this);
        FrameLayout.LayoutParams textParams = new FrameLayout.LayoutParams(
            FrameLayout.LayoutParams.MATCH_PARENT,
            FrameLayout.LayoutParams.MATCH_PARENT
        );
        cellView.setLayoutParams(textParams);
        cellView.setGravity(Gravity.CENTER);
        cellView.setTextSize(16);
        cellView.setTextColor(Color.BLACK);
        setCellBackground(cellView, getColorResource(coveredColorResId));
        
        // Create ImageView for flag/bomb images
        ImageView imageView = new ImageView(this);
        int imageSize = (int) (size * 0.85); // 85% of cell size for bigger images
        FrameLayout.LayoutParams imageParams = new FrameLayout.LayoutParams(imageSize, imageSize);
        imageParams.gravity = Gravity.CENTER;
        imageView.setLayoutParams(imageParams);
        imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
        imageView.setVisibility(View.GONE); // Hidden by default
        
        container.addView(cellView);
        container.addView(imageView);
        
        cellViews[row][col] = cellView;
        cellImages[row][col] = imageView;

        // Tap listener - reveal cell
        container.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!gameBoard.isGameFinished()) {
                    boolean gameLost = gameBoard.revealCell(row, col);
                    updateBoardDisplay();
                    
                    if (gameLost) {
                        showToast(getString(R.string.game_lose));
                        // Show restart button when game is lost
                        restartButton.setVisibility(View.VISIBLE);
                    } else {
                        checkGameStatus();
                    }
                }
            }
        });

        // Long press listener for flag
        container.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (!gameBoard.isGameFinished()) {
                    gameBoard.toggleFlag(row, col);
                    updateBoardDisplay();
                    return true;
                }
                return false;
            }
        });

        return container;
    }

    private void updateBoardDisplay() {
        int rows = gameBoard.getRows();
        int columns = gameBoard.getColumns();

        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < columns; j++) {
                GameBoard.Cell cell = gameBoard.getCell(i, j);
                TextView cellView = cellViews[i][j];
                ImageView imageView = cellImages[i][j];

                if (cell.state == GameBoard.CellState.COVERED) {
                    setCellBackground(cellView, getColorResource(coveredColorResId));
                    cellView.setText("");
                    imageView.setVisibility(View.GONE);
                } else if (cell.state == GameBoard.CellState.FLAGGED) {
                    setCellBackground(cellView, getColorResource(suspectedColorResId));
                    cellView.setText("");
                    // Show flag image - centered and scaled
                    imageView.setImageResource(R.drawable.flag);
                    imageView.setVisibility(View.VISIBLE);
                } else if (cell.state == GameBoard.CellState.UNCOVERED) {
                    if (cell.isMine) {
                        setCellBackground(cellView, getColorResource(mineColorResId));
                        cellView.setText("");
                        // Show bomb image - centered and scaled
                        imageView.setImageResource(R.drawable.bomba);
                        imageView.setVisibility(View.VISIBLE);
                    } else {
                        setCellBackground(cellView, getColorResource(uncoveredColorResId));
                        imageView.setVisibility(View.GONE);
                        if (cell.adjacentMines > 0) {
                            cellView.setText(String.valueOf(cell.adjacentMines));
                        } else {
                            cellView.setText("");
                        }
                    }
                }
            }
        }
    }

    private void setCellBackground(TextView cellView, int color) {
        GradientDrawable drawable = new GradientDrawable();
        drawable.setShape(GradientDrawable.RECTANGLE);
        drawable.setColor(color);
        drawable.setCornerRadius(8f); // Rounded corners with 8dp radius
        cellView.setBackground(drawable);
    }

    private void checkGameStatus() {
        if (gameBoard.isGameWon()) {
            showToast(getString(R.string.game_win));
            // Show restart button when game is won
            restartButton.setVisibility(View.VISIBLE);
        }
    }

    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }

    private int getColorResource(int colorResId) {
        return ContextCompat.getColor(this, colorResId);
    }
    
}

