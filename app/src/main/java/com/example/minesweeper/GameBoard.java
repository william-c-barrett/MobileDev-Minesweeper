package com.example.minesweeper;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * GameBoard - Contains the game logic for Minesweeper
 * Handles mine generation, cell revealing, flagging, and win/lose conditions
 * 
 * Reference: This implementation is based on the classic Minesweeper game logic.
 * For reference, see: https://en.wikipedia.org/wiki/Minesweeper_(video_game)
 */
public class GameBoard {

    public enum CellState {
        COVERED,        // Cell is not revealed yet
        UNCOVERED,      // Cell has been revealed
        FLAGGED         // Cell is flagged as suspected mine
    }

    public static class Cell {
        boolean isMine;
        int adjacentMines;
        CellState state;

        public Cell() {
            this.isMine = false;
            this.adjacentMines = 0;
            this.state = CellState.COVERED;
        }
    }

    private Cell[][] board;
    private int rows;
    private int columns;
    private int totalMines;
    private int uncoveredCells;
    private boolean gameOver;
    private boolean gameWon;

    public GameBoard(int rows, int columns, int minesPercent) {
        this.rows = rows;
        this.columns = columns;
        this.totalMines = (rows * columns * minesPercent) / 100;
        this.uncoveredCells = 0;
        this.gameOver = false;
        this.gameWon = false;
        this.board = new Cell[rows][columns];

        initializeBoard();
        placeMines();
        calculateAdjacentMines();
    }

    private void initializeBoard() {
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < columns; j++) {
                board[i][j] = new Cell();
            }
        }
    }

    private void placeMines() {
        Random random = new Random();
        int minesPlaced = 0;

        while (minesPlaced < totalMines) {
            int row = random.nextInt(rows);
            int col = random.nextInt(columns);

            if (!board[row][col].isMine) {
                board[row][col].isMine = true;
                minesPlaced++;
            }
        }
    }

    private void calculateAdjacentMines() {
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < columns; j++) {
                if (!board[i][j].isMine) {
                    board[i][j].adjacentMines = countAdjacentMines(i, j);
                }
            }
        }
    }

    private int countAdjacentMines(int row, int col) {
        int count = 0;
        for (int i = -1; i <= 1; i++) {
            for (int j = -1; j <= 1; j++) {
                int newRow = row + i;
                int newCol = col + j;
                if (isValidPosition(newRow, newCol) && board[newRow][newCol].isMine) {
                    count++;
                }
            }
        }
        return count;
    }

    private boolean isValidPosition(int row, int col) {
        return row >= 0 && row < rows && col >= 0 && col < columns;
    }

    public boolean revealCell(int row, int col) {
        if (gameOver || gameWon) {
            return false;
        }

        Cell cell = board[row][col];

        // Cannot reveal flagged cells
        if (cell.state == CellState.FLAGGED) {
            return false;
        }

        // Already uncovered
        if (cell.state == CellState.UNCOVERED) {
            return false;
        }

        // Reveal the cell
        cell.state = CellState.UNCOVERED;
        uncoveredCells++;

        // Check if mine was hit
        if (cell.isMine) {
            gameOver = true;
            revealAllMines();
            return true; // Signal that game is lost
        }

        // If cell has 0 adjacent mines, reveal surrounding cells
        if (cell.adjacentMines == 0) {
            revealAdjacentCells(row, col);
        }

        // Check win condition
        checkWinCondition();

        return false; // Game continues
    }

    private void revealAdjacentCells(int row, int col) {
        for (int i = -1; i <= 1; i++) {
            for (int j = -1; j <= 1; j++) {
                int newRow = row + i;
                int newCol = col + j;

                if (isValidPosition(newRow, newCol)) {
                    Cell cell = board[newRow][newCol];
                    if (cell.state == CellState.COVERED && !cell.isMine) {
                        cell.state = CellState.UNCOVERED;
                        uncoveredCells++;

                        // Recursively reveal if this cell also has 0 adjacent mines
                        if (cell.adjacentMines == 0) {
                            revealAdjacentCells(newRow, newCol);
                        }
                    }
                }
            }
        }
    }

    public void toggleFlag(int row, int col) {
        if (gameOver || gameWon) {
            return;
        }

        Cell cell = board[row][col];

        // Can only flag/unflag covered cells
        if (cell.state == CellState.COVERED) {
            cell.state = CellState.FLAGGED;
        } else if (cell.state == CellState.FLAGGED) {
            cell.state = CellState.COVERED;
        }
    }

    private void checkWinCondition() {
        int totalCells = rows * columns;
        if (uncoveredCells == (totalCells - totalMines)) {
            gameWon = true;
            revealAllMines();
        }
    }

    private void revealAllMines() {
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < columns; j++) {
                if (board[i][j].isMine) {
                    board[i][j].state = CellState.UNCOVERED;
                }
            }
        }
    }

    public Cell getCell(int row, int col) {
        return board[row][col];
    }

    public int getRows() {
        return rows;
    }

    public int getColumns() {
        return columns;
    }

    public boolean isGameOver() {
        return gameOver;
    }

    public boolean isGameWon() {
        return gameWon;
    }

    public boolean isGameFinished() {
        return gameOver || gameWon;
    }
}

