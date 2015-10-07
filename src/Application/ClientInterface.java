package Application;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextField;
import java.awt.Font;
import javax.swing.JButton;
import java.awt.List;
import javax.swing.JTextArea;

public class ClientInterface extends JFrame {
    private static final long serialVersionUID = 2618660503597352500L;
    private JTextField userNameTextField;
    public ClientInterface() {
        getContentPane().setLayout(null);
        
        JLabel userNameLabel = new JLabel("Username");
        userNameLabel.setFont(new Font("Tahoma", Font.PLAIN, 12));
        userNameLabel.setBounds(28, 25, 73, 22);
        getContentPane().add(userNameLabel);
        
        userNameTextField = new JTextField();
        userNameTextField.setBounds(97, 26, 181, 20);
        getContentPane().add(userNameTextField);
        userNameTextField.setColumns(10);
        
        JButton connectButton = new JButton("Connect");
        connectButton.setBounds(288, 26, 89, 23);
        getContentPane().add(connectButton);
        
        JButton disconnectButton = new JButton("Disconnect");
        disconnectButton.setBounds(398, 26, 89, 23);
        getContentPane().add(disconnectButton);
        
        JLabel onlineUserLabel = new JLabel("Online users");
        onlineUserLabel.setFont(new Font("Tahoma", Font.PLAIN, 12));
        onlineUserLabel.setBounds(554, 25, 73, 22);
        getContentPane().add(onlineUserLabel);
        
        List userList = new List();
        userList.setBounds(524, 76, 136, 398);
        getContentPane().add(userList);
        
        JTextArea chatArea = new JTextArea();
        chatArea.setBounds(28, 76, 456, 290);
        getContentPane().add(chatArea);
        
        JTextArea messageArea = new JTextArea();
        messageArea.setBounds(28, 377, 346, 97);
        getContentPane().add(messageArea);
        
        JButton sendButton = new JButton("Send");
        sendButton.setBounds(384, 377, 104, 97);
        getContentPane().add(sendButton);
    }
}
