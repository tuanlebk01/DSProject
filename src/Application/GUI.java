package Application;


import javax.swing.DefaultListModel;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextField;

import java.awt.BorderLayout;
import java.awt.Font;

import javax.swing.JButton;

import java.awt.List;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.util.Vector;

import javax.swing.JTextArea;
import javax.swing.JList;
import javax.swing.JLayeredPane;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.event.FocusListener;

import javax.swing.JTextPane;

import GroupManagement.Client;


public class GUI {

	private Client client;
	private JLabel userNameLabel;
	private JLabel IPLabel;
	private JLabel usersOnlineLabel;
	private JLabel groupsLabel;
	private JTextField userNameTextField;
	private JTextField portNrField;
	private JButton connectButton;
	private JButton sendButton;
	private JButton createNewGroupButton;
	private JList userList = new JList();
	private List groupList;
	private JFrame frame = new JFrame();
	private static JTextArea chatArea;
	private JTextArea msgField;
	private static String userName;
	private static int portNr;

	public static void main(String[] args) {
		new GUI();
	}

	public GUI() {

		frame.getContentPane().setLayout(null);

		userNameLabel = new JLabel("Username");
		userNameLabel.setFont(new Font("Tahoma", Font.PLAIN, 12));
		userNameLabel.setBounds(28, 25, 73, 22);
		frame.getContentPane().add(userNameLabel);

		userNameTextField = new JTextField();
		userNameTextField.setBounds(91, 27, 125, 20);
		frame.getContentPane().add(userNameTextField);
		userNameTextField.setColumns(10);
		userNameTextField.setText("User1");

		createNewGroupButton = new JButton("Create group");
		createNewGroupButton.setBounds(524, 26, 135, 23);
		frame.getContentPane().add(createNewGroupButton);

		connectButton = new JButton("Connect");
		connectButton.setBounds(365, 26, 119, 23);
		frame.getContentPane().add(connectButton);

		usersOnlineLabel = new JLabel("Online users");
		usersOnlineLabel.setFont(new Font("Tahoma", Font.PLAIN, 12));
		usersOnlineLabel.setBounds(557, 242, 73, 22);
		frame.getContentPane().add(usersOnlineLabel);

		userList = new JList();
		userList.setBounds(524, 275, 136, 200);
		frame.getContentPane().add(userList);

		sendButton = new JButton("Send");
		sendButton.setBounds(384, 377, 104, 97);
		frame.getContentPane().add(sendButton);

		groupList = new List();
		groupList.setBounds(524, 100, 136, 124);
		frame.getContentPane().add(groupList);

		groupsLabel = new JLabel("Groups");
		groupsLabel.setFont(new Font("Tahoma", Font.PLAIN, 12));
		groupsLabel.setBounds(573, 65, 51, 22);
		frame.getContentPane().add(groupsLabel);

		portNrField = new JTextField();
		portNrField.setColumns(10);
		portNrField.setBounds(275, 27, 60, 20);
		frame.getContentPane().add(portNrField);

		portNrField.setText("1111");

		IPLabel = new JLabel("Port");
		IPLabel.setFont(new Font("Tahoma", Font.PLAIN, 12));
		IPLabel.setBounds(234, 25, 30, 22);
		frame.getContentPane().add(IPLabel);

		chatArea = new JTextArea();
		chatArea.setEditable(false);
		chatArea.setLineWrap(true);
		chatArea.setBounds(28, 76, 456, 294);
		frame.add(new JScrollPane(chatArea), BorderLayout.CENTER);
   		frame.getContentPane().add(chatArea);

   		msgField = new JTextArea();
   		msgField.setBounds(28, 377, 350, 97);
   		msgField.setColumns(10);
   		frame.add(new JScrollPane(msgField), BorderLayout.CENTER);
   		frame.getContentPane().add(msgField);


   		createNewGroupButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent a) {

				String input = JOptionPane.showInputDialog(frame, "Enter group name");

				if(input != null && (input.length() > 1)) {

					try {
						client.createGroup(input, userName);
					} catch (RemoteException e) {
						e.printStackTrace();
					}
					JOptionPane.showMessageDialog(null, "Group created with name: " + input);
					System.out.println(input);

				} else {

					JOptionPane.showMessageDialog(null, "Group not created");
				}


			}
		});

		sendButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent a) {
				sendMessage();
			}
		});

		connectButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent a) {
				connect();
			}
		});

		frame.setSize(700, 530);
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);
	}

	public void connect() {

		if (connectButton.getText().equals("Connect")) {
			if (userNameTextField.getText().length() < 1) {
				JOptionPane
				.showMessageDialog(frame, "You need to type a name.");
				return;
			} else {
				userName = userNameTextField.getText();
				System.out.println("username: " + userName);
			}

			if (portNrField.getText().length() < 1) {
				JOptionPane.showMessageDialog(frame, "You need to type a port.");
				return;
			} else {
				portNr = Integer.parseInt(portNrField.getText());
			}

			try {

				client = new Client();
				connectButton.setText("Disconnect");
				System.out.println("connecting to nameserver");
				client.connectToNameServer(portNr);

			} catch (Exception ex) {
				ex.printStackTrace();
				System.out.println("Error, could not connect.");
				JOptionPane.showMessageDialog(frame, "Error, could not connect.");

			}
		} else {
			updateUsers(null);
			connectButton.setText("Connect");
		}
	}

	public void sendMessage() {
		if (connectButton.getText().equals("Connect")) {
			JOptionPane.showMessageDialog(frame, "You need to connect first.");
			return;
		}
		String message = msgField.getText();
		message = "[" + userNameTextField.getText() + "] " + message;
		msgField.setText("");
	}

	public static void writeMsg(String message) {
		if (!userName.equals(""))
			chatArea.append(userName + ": " + message + "\n");
		else chatArea.append(message + "\n");
	}

	private void updateUsers(Vector connected) {

		DefaultListModel listModel = new DefaultListModel();

		if (connected != null)
			for (int i = 0; i < connected.size(); i++) {
				try {
					String temp = "asd";//((Client) connected.get(i)).getName();
					listModel.addElement(temp);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		userList.setModel(listModel);

	}
}