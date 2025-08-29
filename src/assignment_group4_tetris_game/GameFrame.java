package assignment_group4_tetris_game;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import javax.swing.JPanel;
import javax.swing.Timer;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import javafx.embed.swing.JFXPanel;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javax.swing.ImageIcon;

/**
 *
 * @author
 */
public class GameFrame extends javax.swing.JFrame {

    private String playerName;
    private int nextScoreThreshold = 200;
    private MediaPlayer mediaPlayer;
    private boolean musicOn = true;
    private final int COLS = 16;
    private final int ROWS = 27;
    private final int cellSize = 36;
    private int[][] grid;
    private int score;
    JPanel grids, nextShape;
    private Tetromino currentTetromino;
    private TetrominoFactory factory;
    private Timer timer;
    private Image backgroundImage;
    private Tetromino nextTetromino;
    private boolean paused = false;
    private boolean gameOver = false;
    private int highScore = 0;
    private final String highScoreFile = "highscore.txt";
    private long startTime = 0L;
    private long pauseStartTime = 0L;
    private long totalPausedTime = 0L;
    private long lastSpeedIncreaseGameTime = 0L;

    public void init() {
        getContentPane().setBackground(Color.decode("#B0E2FF"));
        grids = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), null);
                drawGrid(g);
            }
        };
        grids.setBackground(Color.decode("#F8F8FF"));
        grids.setPreferredSize(new java.awt.Dimension(580, 974));
        grids.setSize(580, 974);
        pnlGrids.add(grids);
        grid = new int[ROWS][COLS];
        factory = new TetrominoFactory();
        currentTetromino = factory.createRandomTetromino();
        currentTetromino.x = (COLS - currentTetromino.getShape()[0].length) / 2;
        currentTetromino.y = 0;
        nextTetromino = factory.createRandomTetromino();
        loadHighScore();
        nextShape = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                if (nextTetromino != null) {
                    int nextCellSize = 40;
                    g.setColor(nextTetromino.getColor());
                    int[][] shape = nextTetromino.getShape();
                    int offsetX = (this.getWidth() - shape[0].length * nextCellSize) / 2;
                    int offsetY = (this.getHeight() - shape.length * nextCellSize) / 2;
                    for (int i = 0; i < shape.length; i++) {
                        for (int j = 0; j < shape[i].length; j++) {
                            if (shape[i][j] == 1) {
                                g.fillRect(offsetX + j * nextCellSize, offsetY + i * nextCellSize, nextCellSize, nextCellSize);
                                g.setColor(Color.BLACK);
                                g.drawRect(offsetX + j * nextCellSize, offsetY + i * nextCellSize, nextCellSize, nextCellSize);
                                g.setColor(nextTetromino.getColor());
                            }
                        }
                    }
                }
            }
        };
        nextShape.setBackground(Color.decode("#CAE1FF"));
        pnlNextShape.setLayout(new java.awt.BorderLayout());
        pnlNextShape.add(nextShape, java.awt.BorderLayout.CENTER);
        pnlNextShape.setPreferredSize(new java.awt.Dimension(282, 275));
        pnlOperation = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                ImageIcon icon = new ImageIcon(getClass().getResource("/img/frame.png"));
                if (icon != null) {
                    g.drawImage(icon.getImage(), 0, 0, getWidth(), getHeight(), this);
                } else {
                    System.out.println("Không tìm thấy ảnh: /img/frame.png");
                }
            }
        };
        lblGamePaused.setVisible(false);
        lblGameOver.setVisible(false);
        // Bắt đầu game
        startGame();
    }

    public void drawGrid(Graphics g) {
        //Vẽ các ô đã rơi xuống
        for (int i = 0; i < ROWS; i++) {
            for (int j = 0; j < COLS; j++) {
                if (grid[i][j] != 0) {
                    g.setColor(Color.decode("#A9A9A9"));
                    g.fillRect(j * cellSize, i * cellSize, cellSize, cellSize);
                    g.setColor(Color.BLACK);
                    g.drawRect(j * cellSize, i * cellSize, cellSize, cellSize);
                }
            }
        }

        // Vẽ khối hiện tại
        if (currentTetromino != null) {
            int[][] shape = currentTetromino.getShape();
            int tetX = currentTetromino.x;
            int tetY = currentTetromino.y;
            for (int i = 0; i < shape.length; i++) {
                for (int j = 0; j < shape[i].length; j++) {
                    if (shape[i][j] == 1) {
                        // Tô màu khối
                        g.setColor(currentTetromino.getColor());
                        g.fillRect((tetX + j) * cellSize, (tetY + i) * cellSize, cellSize, cellSize);
                        // Vẽ viền khối
                        g.setColor(Color.BLACK);
                        g.drawRect((tetX + j) * cellSize, (tetY + i) * cellSize, cellSize, cellSize);
                    }
                }
            }
        }
        // Vẽ lưới
        if (score < 600) {
            g.setColor(Color.DARK_GRAY);
            for (int i = 0; i < ROWS; i++) {
                for (int j = 0; j < COLS; j++) {
                    g.drawRect(j * cellSize, i * cellSize, cellSize, cellSize);
                }
            }
        }
    }

    private boolean canMove(Tetromino tet, int newX, int newY) {
        int[][] shape = tet.getShape();
        for (int i = 0; i < shape.length; i++) {
            for (int j = 0; j < shape[i].length; j++) {
                if (shape[i][j] == 1) {
                    int gridX = newX + j;
                    int gridY = newY + i;
                    if (gridX < 0 || gridX >= COLS || gridY < 0 || gridY >= ROWS) {
                        return false;
                    }
                    if (grid[gridY][gridX] != 0) {
                        return false;
                    }
                }
            }
        }
        return true;
    }

    private void placeTetromino(Tetromino tet) {
        int[][] shape = tet.getShape();
        for (int i = 0; i < shape.length; i++) {
            for (int j = 0; j < shape[i].length; j++) {
                if (shape[i][j] == 1) {
                    int gridX = tet.x + j;
                    int gridY = tet.y + i;
                    if (gridY >= 0 && gridY < ROWS && gridX >= 0 && gridX < COLS) {
                        grid[gridY][gridX] = 1;
                    }
                }
            }
        }
    }

    private void clearLines() {
        int linesCleared = 0;
        for (int r = ROWS - 1; r >= 0; r--) {
            boolean full = true;
            for (int c = 0; c < COLS; c++) {
                if (grid[r][c] == 0) {
                    full = false;
                    break;
                }
            }
            if (full) {
                removeLine(r);
                linesCleared++;
                r++;
            }
        }
        if (linesCleared > 0) {
            score += calculateScore(linesCleared);
            lblScore.setText(String.valueOf(score));
            updateHighScore();
        }
    }

    private int calculateScore(int linesCleared) {
        switch (linesCleared) {
            case 1:
                return 100;
            case 2:
                return 300;
            case 3:
                return 500;
            case 4:
                return 800;
            default:
                return linesCleared * 100;
        }
    }

    private void removeLine(int line) {
        for (int r = line; r > 0; r--) {
            for (int c = 0; c < COLS; c++) {
                grid[r][c] = grid[r - 1][c];
            }
        }
        for (int c = 0; c < COLS; c++) {
            grid[0][c] = 0;
        }
    }

    public void startGame() {
        startTime = System.currentTimeMillis();
        totalPausedTime = 0L;
        lastSpeedIncreaseGameTime = 0L;

        timer = new Timer(500, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Xử lý di chuyển khối
                if (canMove(currentTetromino, currentTetromino.x, currentTetromino.y + 1)) {
                    currentTetromino.moveDown();
                } else {
                    placeTetromino(currentTetromino);
                    clearLines();
                    currentTetromino = nextTetromino;
                    currentTetromino.x = (COLS - currentTetromino.getShape()[0].length) / 2;
                    currentTetromino.y = 0;
                    nextTetromino = factory.createRandomTetromino();
                    if (!canMove(currentTetromino, currentTetromino.x, currentTetromino.y)) {
                        timer.stop();
                        gameOver = true;
                        lblGameOver.setVisible(true);
                        btnPaused.setEnabled(false);
                        btnPlayAgain.setVisible(true);
                        RankingUser ru = new RankingUser();
                        ru.addOrUpdateScore(playerName, score);
                    }
                }

                if (score >= nextScoreThreshold) {
                    int newDelay = (int) (timer.getDelay() * 0.7);
                    timer.setDelay(newDelay);
                    nextScoreThreshold += 200;
                }

                repaint();
                pnlNextShape.repaint();
                long now = System.currentTimeMillis();
                long gameElapsed = now - startTime - totalPausedTime;
                lblTime.setText(formatTime(gameElapsed));
            }
        });
        timer.start();
    }

    private String formatTime(long millis) {
        long seconds = millis / 1000;
        long minutes = seconds / 60;
        long hours = minutes / 60;
        seconds %= 60;
        minutes %= 60;
        return String.format("%02d:%02d:%02d", hours, minutes, seconds);
    }

    private void loadHighScore() {
        try {
            File file = new File(highScoreFile);
            if (file.exists()) {
                BufferedReader br = new BufferedReader(new FileReader(file));
                String line = br.readLine();
                if (line != null) {
                    highScore = Integer.parseInt(line.trim());
                }
                br.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        lblHS.setText(String.valueOf(highScore));
    }

    // Phương thức ghi điểm cao nhất vào file
    private void saveHighScore() {
        try {
            FileWriter fw = new FileWriter(highScoreFile);
            fw.write(String.valueOf(highScore));
            fw.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Phương thức kiểm tra và cập nhật điểm cao nhất
    private void updateHighScore() {
        if (score > highScore) {
            highScore = score;
            lblHS.setText(String.valueOf(highScore));
            saveHighScore();
        }
    }

    public GameFrame(String playerName) {
        this.playerName = playerName;
        initComponents();
        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        this.setLocationRelativeTo(null);
        this.setResizable(false);
        ImageIcon icon = new ImageIcon(getClass().getResource("/img/logo.png"));
        this.setIconImage(icon.getImage());
        btnPlayAgain.setVisible(false);
        init();
        new JFXPanel();
        try {
            String musicPath = getClass().getResource("/sound/sound.mp3").toExternalForm();
            Media media = new Media(musicPath);
            mediaPlayer = new MediaPlayer(media);
            mediaPlayer.setCycleCount(MediaPlayer.INDEFINITE);
            mediaPlayer.play();
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Không tải được file nhạc!");
        }
        this.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent evt) {
                Key(evt);
            }
        });

        this.setFocusable(true);
    }

    public GameFrame() {
        initComponents();
        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        this.setLocationRelativeTo(null);
        this.setResizable(false);
        ImageIcon icon = new ImageIcon(getClass().getResource("/img/logo.png"));
        this.setIconImage(icon.getImage());
        btnPlayAgain.setVisible(false);
        init();
        new JFXPanel();
        try {
            String musicPath = getClass().getResource("/sound/sound.mp3").toExternalForm();
            Media media = new Media(musicPath);
            mediaPlayer = new MediaPlayer(media);
            mediaPlayer.setCycleCount(MediaPlayer.INDEFINITE);
            mediaPlayer.play();
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Không tải được file nhạc!");
        }
        this.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent evt) {
                Key(evt);
            }
        });

        this.setFocusable(true);
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        pnlGrids = new javax.swing.JPanel();
        lblGamePaused = new javax.swing.JLabel();
        lblGameOver = new javax.swing.JLabel();
        btnPlayAgain = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        pnlNextShape = new javax.swing.JPanel();
        lblTime = new javax.swing.JLabel();
        lblScore = new javax.swing.JLabel();
        pnlOperation = new javax.swing.JPanel();
        btnPaused = new javax.swing.JButton();
        btnExit = new javax.swing.JButton();
        lblHighestScore = new javax.swing.JLabel();
        lblHS = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("CapyTetris Game");
        setBackground(new java.awt.Color(0, 0, 0));

        pnlGrids.setPreferredSize(new java.awt.Dimension(578, 974));
        pnlGrids.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                Key(evt);
            }
        });

        lblGamePaused.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/Resume.png"))); // NOI18N

        lblGameOver.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/GameOver.png"))); // NOI18N

        btnPlayAgain.setBackground(new java.awt.Color(191, 239, 255));
        btnPlayAgain.setFont(new java.awt.Font("Showcard Gothic", 1, 18)); // NOI18N
        btnPlayAgain.setForeground(new java.awt.Color(255, 102, 102));
        btnPlayAgain.setText("PLAY AGAIN");
        btnPlayAgain.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnPlayAgainActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout pnlGridsLayout = new javax.swing.GroupLayout(pnlGrids);
        pnlGrids.setLayout(pnlGridsLayout);
        pnlGridsLayout.setHorizontalGroup(
            pnlGridsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlGridsLayout.createSequentialGroup()
                .addContainerGap(69, Short.MAX_VALUE)
                .addGroup(pnlGridsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pnlGridsLayout.createSequentialGroup()
                        .addGroup(pnlGridsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addComponent(lblGameOver, javax.swing.GroupLayout.DEFAULT_SIZE, 146, Short.MAX_VALUE)
                            .addComponent(btnPlayAgain, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addGap(205, 205, 205))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pnlGridsLayout.createSequentialGroup()
                        .addComponent(lblGamePaused)
                        .addGap(64, 64, 64))))
        );
        pnlGridsLayout.setVerticalGroup(
            pnlGridsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pnlGridsLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(lblGameOver)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnPlayAgain, javax.swing.GroupLayout.PREFERRED_SIZE, 59, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(30, 30, 30)
                .addComponent(lblGamePaused, javax.swing.GroupLayout.PREFERRED_SIZE, 211, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(269, 269, 269))
        );

        jLabel1.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        jLabel1.setForeground(new java.awt.Color(102, 102, 102));
        jLabel1.setText("TIME:");

        jLabel2.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        jLabel2.setForeground(new java.awt.Color(255, 51, 51));
        jLabel2.setText("SCORE:");

        jLabel3.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/title2.png"))); // NOI18N

        pnlNextShape.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(51, 0, 204)));

        javax.swing.GroupLayout pnlNextShapeLayout = new javax.swing.GroupLayout(pnlNextShape);
        pnlNextShape.setLayout(pnlNextShapeLayout);
        pnlNextShapeLayout.setHorizontalGroup(
            pnlNextShapeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 285, Short.MAX_VALUE)
        );
        pnlNextShapeLayout.setVerticalGroup(
            pnlNextShapeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 275, Short.MAX_VALUE)
        );

        lblTime.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        lblTime.setForeground(new java.awt.Color(102, 102, 102));
        lblTime.setText("00:00:00");

        lblScore.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        lblScore.setForeground(new java.awt.Color(255, 51, 51));
        lblScore.setText("0");

        btnPaused.setBackground(new java.awt.Color(255, 153, 153));
        btnPaused.setFont(new java.awt.Font("Showcard Gothic", 1, 18)); // NOI18N
        btnPaused.setForeground(new java.awt.Color(255, 255, 255));
        btnPaused.setText("PAUSE");
        btnPaused.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnPausedActionPerformed(evt);
            }
        });

        btnExit.setBackground(new java.awt.Color(255, 153, 153));
        btnExit.setFont(new java.awt.Font("Showcard Gothic", 1, 18)); // NOI18N
        btnExit.setForeground(new java.awt.Color(255, 255, 255));
        btnExit.setText("EXIT");
        btnExit.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnExitActionPerformed(evt);
            }
        });

        pnlOperation = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                // Tải ảnh từ resource
                ImageIcon icon = new ImageIcon(getClass().getResource("/img/frame.png"));
                if (icon != null) {
                    // Vẽ ảnh phủ toàn bộ panel
                    g.drawImage(icon.getImage(), 0, 0, getWidth(), getHeight(), this);
                } else {
                    System.out.println("Không tìm thấy ảnh: /img/frame.png");
                }
            }
        };
        pnlOperation.setBackground(Color.decode("#B0E2FF"));

        javax.swing.GroupLayout pnlOperationLayout = new javax.swing.GroupLayout(pnlOperation);
        pnlOperation.setLayout(pnlOperationLayout);
        pnlOperationLayout.setHorizontalGroup(
            pnlOperationLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlOperationLayout.createSequentialGroup()
                .addGap(92, 92, 92)
                .addGroup(pnlOperationLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(btnExit, javax.swing.GroupLayout.PREFERRED_SIZE, 98, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnPaused, javax.swing.GroupLayout.PREFERRED_SIZE, 98, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        pnlOperationLayout.setVerticalGroup(
            pnlOperationLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlOperationLayout.createSequentialGroup()
                .addGap(26, 26, 26)
                .addComponent(btnPaused, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(btnExit)
                .addContainerGap(31, Short.MAX_VALUE))
        );

        lblHighestScore.setFont(new java.awt.Font("Showcard Gothic", 1, 18)); // NOI18N
        lblHighestScore.setForeground(new java.awt.Color(255, 255, 255));
        lblHighestScore.setText("Highest score:");

        lblHS.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        lblHS.setForeground(new java.awt.Color(255, 255, 255));
        lblHS.setText("0");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(pnlGrids, javax.swing.GroupLayout.DEFAULT_SIZE, 567, Short.MAX_VALUE)
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addComponent(jLabel3)
                        .addGap(25, 25, 25))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addGroup(layout.createSequentialGroup()
                                    .addComponent(jLabel1)
                                    .addGap(140, 140, 140)
                                    .addComponent(lblTime))
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                    .addGroup(layout.createSequentialGroup()
                                        .addComponent(jLabel2)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .addComponent(lblScore))
                                    .addGroup(layout.createSequentialGroup()
                                        .addComponent(lblHighestScore)
                                        .addGap(39, 39, 39)
                                        .addComponent(lblHS, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                .addComponent(pnlNextShape, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(pnlOperation, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                        .addGap(83, 83, 83))))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(pnlGrids, javax.swing.GroupLayout.DEFAULT_SIZE, 990, Short.MAX_VALUE)
                .addGap(5, 5, 5))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap(128, Short.MAX_VALUE)
                .addComponent(jLabel3)
                .addGap(57, 57, 57)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblHighestScore)
                    .addComponent(lblHS))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lblScore)
                    .addComponent(jLabel2))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(lblTime))
                .addGap(50, 50, 50)
                .addComponent(pnlNextShape, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(pnlOperation, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(58, 58, 58))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void Key(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_Key
        if (paused || gameOver) {
            return;
        }
        if (currentTetromino != null) {
            switch (evt.getKeyCode()) {
                case KeyEvent.VK_LEFT:
                    if (canMove(currentTetromino, currentTetromino.x - 1, currentTetromino.y)) {
                        currentTetromino.moveLeft();
                    }
                    break;
                case KeyEvent.VK_RIGHT:
                    if (canMove(currentTetromino, currentTetromino.x + 1, currentTetromino.y)) {
                        currentTetromino.moveRight();
                    }
                    break;
                case KeyEvent.VK_DOWN:
                    if (canMove(currentTetromino, currentTetromino.x, currentTetromino.y + 1)) {
                        currentTetromino.moveDown();
                    }
                    break;
                case KeyEvent.VK_UP:
                    Tetromino temp = new Tetromino(currentTetromino.getShape(), currentTetromino.getColor(), currentTetromino.type);
                    temp.x = currentTetromino.x;
                    temp.y = currentTetromino.y;
                    temp.rotate();
                    if (canMove(temp, temp.x, temp.y)) {
                        currentTetromino.rotate();
                    }
                    break;
            }
            repaint();
        }
    }//GEN-LAST:event_Key

    private void btnPausedActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnPausedActionPerformed
        if (gameOver) {
            return;
        }
        if (timer != null && timer.isRunning()) {
            timer.stop();
            pauseStartTime = System.currentTimeMillis();
            paused = true;
            lblGamePaused.setVisible(true);
        } else {
            timer.start();
            paused = false;
            lblGamePaused.setVisible(false);
            totalPausedTime += (System.currentTimeMillis() - pauseStartTime);
        }
        GameFrame.this.requestFocusInWindow();
    }//GEN-LAST:event_btnPausedActionPerformed

    private void btnExitActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnExitActionPerformed
        // TODO add your handling code here:
        if (mediaPlayer != null) {
            mediaPlayer.stop();
        }
        MainFrame mainFrame = new MainFrame();
        mainFrame.setVisible(true);
        this.dispose();
    }//GEN-LAST:event_btnExitActionPerformed

    private void btnPlayAgainActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnPlayAgainActionPerformed
        // TODO add your handling code here:
        score = 0;
        lblScore.setText("0");
        grid = new int[ROWS][COLS];
        gameOver = false;
        paused = false;
        lblGameOver.setVisible(false);
        lblGamePaused.setVisible(false);
        btnPaused.setEnabled(true);
        btnPlayAgain.setVisible(false);
        factory = new TetrominoFactory();
        currentTetromino = factory.createRandomTetromino();
        currentTetromino.x = (COLS - currentTetromino.getShape()[0].length) / 2;
        currentTetromino.y = 0;
        nextTetromino = factory.createRandomTetromino();
        startTime = System.currentTimeMillis();
        startGame();
        repaint();
    }//GEN-LAST:event_btnPlayAgainActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(GameFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(GameFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(GameFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(GameFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new GameFrame().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnExit;
    private javax.swing.JButton btnPaused;
    private javax.swing.JButton btnPlayAgain;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel lblGameOver;
    private javax.swing.JLabel lblGamePaused;
    private javax.swing.JLabel lblHS;
    private javax.swing.JLabel lblHighestScore;
    private javax.swing.JLabel lblScore;
    private javax.swing.JLabel lblTime;
    private javax.swing.JPanel pnlGrids;
    private javax.swing.JPanel pnlNextShape;
    private javax.swing.JPanel pnlOperation;
    // End of variables declaration//GEN-END:variables
}
