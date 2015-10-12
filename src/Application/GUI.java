package Application;


import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.List;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.rmi.RemoteException;
import java.rmi.server.ServerNotActiveException;
import java.util.ArrayList;

import javax.swing.DefaultListCellRenderer;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

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
	private DefaultListModel groupList = new DefaultListModel();
	private DefaultListModel userList = new DefaultListModel();
	private JList listGroup = new JList(groupList);
	private JList listUser = new JList(userList);
	private JFrame frame = new JFrame();
	private static JTextArea chatArea;
	private JTextArea msgField;
	private static String userName;
	private static int portNr;
	private ArrayList <String> listOfGroups = new ArrayList <String> ();

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

		listUser.setBounds(524, 275, 136, 200);
		frame.getContentPane().add(listUser);

		sendButton = new JButton("Send");
		sendButton.setBounds(384, 377, 104, 97);
		frame.getContentPane().add(sendButton);

		listGroup.setBounds(524, 100, 136, 124);
		frame.getContentPane().add(listGroup);

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
					} catch (RemoteException | ServerNotActiveException e) {
						JOptionPane.showMessageDialog(null, "Group not created");
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
				System.out.println("Trying to connect to nameserver");
				client.connectToNameServer(userName, portNr);
				listOfGroups = client.getGroupList();

				for(int i = 0; i < listOfGroups.size(); i++) {
					System.out.println(listOfGroups.get(i));
					groupList.add(i, listOfGroups.get(i));
				}

				System.out.println("Connected");

			} catch (Exception ex) {
//				ex.printStackTrace();
				System.out.println("Error, could not connect.");
				JOptionPane.showMessageDialog(frame, "Error, could not connect.");

			}
		} else {
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

	//https://docs.oracle.com/javase/tutorial/uiswing/events/listselectionlistener.html

	class SharedListSelectionHandler implements ListSelectionListener {

	    public void valueChanged(ListSelectionEvent e) {

	    	System.out.println("E rätt");

	    	ListSelectionModel lsm = (ListSelectionModel)e.getSource();

	        int firstIndex = e.getFirstIndex();
	        int lastIndex = e.getLastIndex();
	        boolean isAdjusting = e.getValueIsAdjusting();
	        chatArea.append("Event for indexes "
	                      + firstIndex + " - " + lastIndex
	                      + "; isAdjusting is " + isAdjusting
	                      + "; selected indexes:");

	        if (lsm.isSelectionEmpty()) {
	        	chatArea.append(" <none>");
	        } else {
	            // Find out which indexes are selected.
	            int minIndex = lsm.getMinSelectionIndex();
	            int maxIndex = lsm.getMaxSelectionIndex();
	            for (int i = minIndex; i <= maxIndex; i++) {
	                if (lsm.isSelectedIndex(i)) {
	                	chatArea.append(" " + i);
	                }
	            }
	        }
	        chatArea.append("\n");
	    }
	}

}