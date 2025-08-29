package assignment_group4_tetris_game;

import java.awt.Color;

public class Tetromino {

    private int[][] shape;
    private Color color;
    char type;
    int x;
    int y;

    public Tetromino() {
    }

    public Tetromino(int[][] shape, Color color, char type) {
        this.shape = shape;
        this.color = color;
        this.type = type;
        this.x = 0;
        this.y = 0;
    }

    public void moveDown() {
        y++;
    }

    public void moveLeft() {
        x--;
    }

    public void moveRight() {
        x++;
    }

    public void rotate() {
        if (type == 'O') {
            return;
        }

        if (type == 'I') {
            shape = normalizeTo4x4(shape);
            shape = rotateI();
        } else {
            shape = normalizeTo3x3(shape);
            shape = rotate3x3();
        }
    }

    private int[][] rotate3x3() {
        int size = 3;
        int[][] rotated = new int[size][size];

        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                if (i < shape.length && j < shape[0].length) {
                    rotated[j][size - 1 - i] = shape[i][j];
                }
            }
        }
        return rotated;
    }

    private int[][] normalizeTo3x3(int[][] shape) {
        int[][] normalized = new int[3][3];

        int shapeRows = shape.length;
        int shapeCols = shape[0].length;

        // Giới hạn kích thước tối đa là 3x3
        shapeRows = Math.min(shapeRows, 3);
        shapeCols = Math.min(shapeCols, 3);

        // Căn giữa khối trong ma trận 3x3
        int offsetX = (3 - shapeCols) / 2;
        int offsetY = (3 - shapeRows) / 2;

        for (int i = 0; i < shapeRows; i++) {
            for (int j = 0; j < shapeCols; j++) {
                if (i + offsetY < 3 && j + offsetX < 3) {
                    normalized[i + offsetY][j + offsetX] = shape[i][j];
                }
            }
        }
        return normalized;
    }

    private int[][] normalizeTo4x4(int[][] shape) {
        int[][] normalized = new int[4][4];

        for (int i = 0; i < shape.length; i++) {
            for (int j = 0; j < shape[i].length; j++) {
                if (i < 4 && j < 4) {
                    normalized[i][j] = shape[i][j];
                }
            }
        }
        return normalized;
    }

    private int[][] rotateI() {
        int[][] rotated = new int[4][4];

        if (shape[1][0] == 1) {
            for (int i = 0; i < 4; i++) {
                rotated[i][1] = 1;
            }
        } else {
            for (int i = 0; i < 4; i++) {
                rotated[1][i] = 1;
            }
        }

        return rotated;
    }

    public int[][] getShape() {
        return shape;
    }

    public Color getColor() {
        return color;
    }
}
