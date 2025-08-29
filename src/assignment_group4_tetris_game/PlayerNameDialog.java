package assignment_group4_tetris_game;

import javax.swing.*;
import java.awt.*;

public class PlayerNameDialog extends JDialog {

    private JTextField txtName;
    private String playerName;
    private boolean confirmed = false;

    public PlayerNameDialog(Frame parent) {
        super(parent, "Player Name", true);
        setLayout(null);
        getContentPane().setBackground(Color.decode("#FFA54F"));
        setSize(300, 150);
        setLocationRelativeTo(parent);

        JLabel lblName = new JLabel("Name:");
        lblName.setBounds(20, 3, 50, 25);
        lblName.setSize(80, 60);
        lblName.setFont(new Font("Arial", Font.BOLD, 14));
        lblName.setForeground(Color.decode("#8B4513"));
        add(lblName);

        txtName = new JTextField();
        txtName.setBounds(80, 20, 180, 30);
        txtName.setBackground(Color.decode("#FFE7BA"));
        txtName.setForeground(Color.decode("#8B4513"));
        add(txtName);

        JButton btnOk = new JButton("OK");
        btnOk.setBounds(80, 60, 80, 25);
        btnOk.setBackground(Color.decode("#FFE4C4"));
        btnOk.setForeground(Color.BLACK);
        add(btnOk);
        btnOk.addActionListener(e -> {
            String input = txtName.getText().trim();
            if (input.isEmpty()) {
                // Nếu ô nhập trống, hiển thị dialog lỗi tùy chỉnh
                showCustomErrorDialog("Please enter player name!");
            } else {
                playerName = input;
                confirmed = true;
                setVisible(false);
            }
        });

        JButton btnCancel = new JButton("Cancel");
        btnCancel.setBounds(180, 60, 80, 25);
        btnCancel.setBackground(Color.decode("#FFE4C4"));
        btnCancel.setForeground(Color.BLACK);
        add(btnCancel);
        btnCancel.addActionListener(e -> {
            confirmed = false;
            setVisible(false);
        });
    }

    public String getPlayerName() {
        return playerName;
    }

    public boolean isConfirmed() {
        return confirmed;
    }

    private void showCustomErrorDialog(String message) {
        JDialog dialog = new JDialog(this, "Sorry", true);
        dialog.setLayout(null);
        dialog.getContentPane().setBackground(Color.decode("#FFA54F"));
        dialog.setSize(300, 200);
        dialog.setLocationRelativeTo(this);

        JLabel label = new JLabel(message, SwingConstants.CENTER);
        label.setForeground(Color.decode("#8B4513"));
        label.setFont(new Font("Arial", Font.BOLD, 18));
        label.setBounds(10, 10, 260, 50);
        dialog.add(label);

        // Nếu có icon tùy chỉnh, hãy đảm bảo đường dẫn đúng và icon nằm trong classpath.
        // Ví dụ: icon được đặt trong thư mục resources/img/logo.png và được đóng gói trong jar.
        Icon myErrorIcon = null;
        try {
            myErrorIcon = new ImageIcon(getClass().getResource("/img/icon.png"));
        } catch (Exception ex) {
            // Nếu không tìm thấy icon, có thể bỏ qua
            System.err.println("Icon not found: " + ex.getMessage());
        }

        if (myErrorIcon != null) {
            JLabel iconLabel = new JLabel(myErrorIcon);
            // Đặt icon ở vị trí mong muốn, ví dụ bên trái dưới label thông báo.
            iconLabel.setBounds(115, 55, myErrorIcon.getIconWidth(), myErrorIcon.getIconHeight());
            dialog.add(iconLabel);
        }

        JButton btnOk = new JButton("OK");
        btnOk.setBounds(90, 110, 100, 30);
        btnOk.setBackground(Color.decode("#FFE4C4"));
        btnOk.setForeground(Color.BLACK);
        btnOk.addActionListener(e -> dialog.dispose());
        dialog.add(btnOk);

        dialog.setVisible(true);
    }
}
