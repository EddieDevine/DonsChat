package Client;

import javax.swing.*;
import java.awt.event.*;

public class LoginScreen extends JFrame {
    private JTextField userField;
    private JPasswordField passField;
    private JButton loginButton;
    private JLabel statusLabel;

    public LoginScreen() {
        setTitle("Login");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(300, 200);
        setLayout(null);
        setLocationRelativeTo(null);

        JLabel userLabel = new JLabel("Username:");
        userLabel.setBounds(30, 30, 80, 25);
        add(userLabel);

        userField = new JTextField();
        userField.setBounds(120, 30, 130, 25);
        add(userField);

        JLabel passLabel = new JLabel("Password:");
        passLabel.setBounds(30, 70, 80, 25);
        add(passLabel);

        passField = new JPasswordField();
        passField.setBounds(120, 70, 130, 25);
        add(passField);

        loginButton = new JButton("Login");
        loginButton.setBounds(100, 110, 80, 30);
        add(loginButton);

        statusLabel = new JLabel("");
        statusLabel.setBounds(30, 150, 240, 25);
        add(statusLabel);

        loginButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                // get user input
                String username = userField.getText();
                String password = new String(passField.getPassword());

                // login to server
                int response = API.getToken(username, password);

                if(response == 200){ //sucess
                    dispose(); //close login screen
                    new Chat(); //open chat screen
                }
                else if(response == 401){ //invalid login
                    JOptionPane.showMessageDialog(null, "Invalid Credentials");
                }
                else if(response == 500){ //server side error
                    JOptionPane.showMessageDialog(null, "Server Error");
                }
                else{
                    JOptionPane.showMessageDialog(null, "Error");
                }
            }
        });

        setVisible(true);
    }
}
