package assignment_group4_tetris_game;

import java.io.*;
import java.util.*;

public class RankingUser {

    private final String fileName = "ranking.txt";

    // Lớp ScoreEntry lưu cặp (tên, điểm)
    public static class ScoreEntry {

        private String playerName;
        private int score;

        public ScoreEntry(String playerName, int score) {
            this.playerName = playerName;
            this.score = score;
        }

        public String getPlayerName() {
            return playerName;
        }

        public int getScore() {
            return score;
        }
    }

    // Phương thức lấy tất cả dữ liệu (tên, điểm) từ file
    public List<ScoreEntry> getAllScores() {
        List<ScoreEntry> list = new ArrayList<>();
        File file = new File(fileName);
        if (file.exists()) {
            try (BufferedReader br = new BufferedReader(new FileReader(file))) {
                String line;
                while ((line = br.readLine()) != null) {
                    String[] parts = line.split(":");
                    if (parts.length == 2) {
                        String name = parts[0];
                        int scr = Integer.parseInt(parts[1]);
                        list.add(new ScoreEntry(name, scr));
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return list;
    }

    // Phương thức thêm điểm mới vào file
    public void addNewScore(String playerName, int score) {
        try (FileWriter fw = new FileWriter(fileName, true)) {
            fw.write(playerName + ":" + score + "\n");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Phương thức thêm hoặc cập nhật điểm của người chơi, đảm bảo sắp xếp giảm dần
    public void addOrUpdateScore(String playerName, int score) {
        List<ScoreEntry> ranking = getAllScores();
        boolean found = false;

        // Cập nhật điểm nếu người chơi đã có trong danh sách
        for (ScoreEntry entry : ranking) {
            if (entry.getPlayerName().equals(playerName)) {
                entry.score = Math.max(entry.getScore(), score); // Giữ điểm cao nhất
                found = true;
                break;
            }
        }

        // Nếu không tìm thấy, thêm người chơi mới vào danh sách
        if (!found) {
            ranking.add(new ScoreEntry(playerName, score));
        }

        // Sắp xếp giảm dần theo điểm
        ranking.sort((a, b) -> Integer.compare(b.getScore(), a.getScore()));

        // Ghi lại danh sách đã sắp xếp vào file
        try (FileWriter fw = new FileWriter(fileName)) {
            for (ScoreEntry entry : ranking) {
                fw.write(entry.getPlayerName() + ":" + entry.getScore() + "\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Phương thức lấy danh sách xếp hạng, sắp xếp giảm dần theo điểm
    public List<ScoreEntry> getRanking() {
        List<ScoreEntry> ranking = getAllScores();

        // Sắp xếp giảm dần theo điểm
        ranking.sort((a, b) -> Integer.compare(b.getScore(), a.getScore()));

        return ranking;
    }
}
