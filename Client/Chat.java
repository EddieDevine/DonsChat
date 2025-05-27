package Client;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.Vector;

public class Chat extends JFrame {
    private JTextArea chatArea;
    private JTextField messageField;
    private JButton sendButton;
    private JTextField searchField;
    private JList<String> userList;
    private DefaultListModel<String> listModel;

    public Chat() {
        setTitle("DonsChat");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 600); // Wider for left panel + chat
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // === LEFT PANEL ===
        JPanel leftPanel = new JPanel(new BorderLayout());
        searchField = new JTextField();
        listModel = new DefaultListModel<>();
        userList = new JList<>(listModel);

        // event listener for user list
        userList.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) { // ensures the event fires once per click
                String selectedUser = userList.getSelectedValue();
                if (selectedUser != null) {
                    // Clear chat area
                    chatArea.setText("");

                    String[] chats = API.getChats(selectedUser);

                    for (String chat : chats) {
                        chatArea.append(chat + "\n");

                        file.write(selectedUser, chat);
                    }
                }
            }
        });

        JScrollPane userScrollPane = new JScrollPane(userList);

        // add users
        addUsers(listModel);

        // Search filter
        searchField.addKeyListener(new KeyAdapter() {
            public void keyReleased(KeyEvent e) {
                String filter = searchField.getText().toLowerCase();
                DefaultListModel<String> filtered = new DefaultListModel<>();
                for (int i = 0; i < listModel.size(); i++) {
                    String user = listModel.getElementAt(i);
                    if (user.toLowerCase().contains(filter)) {
                        filtered.addElement(user);
                    }
                }
                userList.setModel(filtered);
            }
        });

        leftPanel.add(searchField, BorderLayout.NORTH);
        leftPanel.add(userScrollPane, BorderLayout.CENTER);
        leftPanel.setPreferredSize(new Dimension(200, 0));
        add(leftPanel, BorderLayout.WEST);

        // === CENTER CHAT AREA ===
        chatArea = new JTextArea();
        chatArea.setEditable(false);
        JScrollPane chatScrollPane = new JScrollPane(chatArea);
        add(chatScrollPane, BorderLayout.CENTER);

        // === BOTTOM PANEL ===
        JPanel bottomPanel = new JPanel(new BorderLayout());
        messageField = new JTextField();
        sendButton = new JButton("Send");

        bottomPanel.add(messageField, BorderLayout.CENTER);
        bottomPanel.add(sendButton, BorderLayout.EAST);
        add(bottomPanel, BorderLayout.SOUTH);

        // Send message logic
        sendButton.addActionListener(e -> sendMessage());
        messageField.addActionListener(e -> sendMessage());

        setVisible(true);
    }

    private void sendMessage() {
        String message = messageField.getText().trim();
        if (!message.isEmpty()) { // make sure message isn't empty
            chatArea.append(message + "\n");
            messageField.setText("");

            API.sendChat(userList.getSelectedValue(), message);
            file.write("me", message);
        }
    }

    // get users from server and add them to the page
    private void addUsers(DefaultListModel<String> inListModel) {
        String[] users = API.getUsers();

        for (String user : users) {
            listModel.addElement(user);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(Chat::new);
    }
}
