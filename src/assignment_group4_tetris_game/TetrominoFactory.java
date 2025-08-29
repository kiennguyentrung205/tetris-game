package assignment_group4_tetris_game;

import java.awt.Color;
import java.util.Random;

public class TetrominoFactory {

    public Tetromino createI() {
        int[][] shape = {{1, 1, 1, 1}};
        return new Tetromino(shape, Color.decode("#FFCC99"), 'I');
    }

    public Tetromino createO() {
        int[][] shape = {{1, 1}, {1, 1}};
        return new Tetromino(shape, Color.YELLOW, 'O');
    }

    public Tetromino createT() {
        int[][] shape = {{0, 1, 0}, {1, 1, 1}};
        return new Tetromino(shape, Color.MAGENTA, 'T');
    }

    public Tetromino createL() {
        int[][] shape = {{0, 0, 1}, {1, 1, 1}};
        return new Tetromino(shape, Color.ORANGE, 'L');
    }

    public Tetromino createJ() {
        int[][] shape = {{1, 0, 0}, {1, 1, 1}};
        return new Tetromino(shape, Color.decode("#8470FF"), 'J');
    }

    public Tetromino createS() {
        int[][] shape = {{0, 1, 1}, {1, 1, 0}};
        return new Tetromino(shape, Color.GREEN, 'S');
    }

    public Tetromino createZ() {
        int[][] shape = {{1, 1, 0}, {0, 1, 1}};
        return new Tetromino(shape, Color.RED, 'Z');
    }

    public Tetromino createRandomTetromino() {
        Random random = new Random();
        switch (random.nextInt(7)) {
            case 0:
                return createI();
            case 1:
                return createO();
            case 2:
                return createT();
            case 3:
                return createL();
            case 4:
                return createJ();
            case 5:
                return createS();
            case 6:
                return createZ();
            default:
                return createI();
        }
    }
}
