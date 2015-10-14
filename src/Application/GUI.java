package Application;

import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.rmi.AlreadyBoundException;
import java.rmi.RemoteException;
import java.rmi.server.ServerNotActiveException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import GroupManagement.Client;
import GroupManagement.NameServer;

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
	private JButton joinGroupButton;
	private DefaultListModel groupList = new DefaultListModel();
	private DefaultListModel userList = new DefaultListModel();
	private JList listGroup = new JList(groupList);
	private JList listUser = new JList(userList);
	private JFrame frame = new JFrame();
	private static JTextArea chatArea;
	private JTextArea msgField;
	private static String userName;
	private static int portNr;
	private ArrayList<String> listOfGroups = new ArrayList<String>();
	private ArrayList<String> listOfMembers = new ArrayList<String>();
	private HashMap<String, ArrayList<String>> groupMap = new HashMap<String, ArrayList<String>>();
	private int clientID;
	private boolean groupCreated;

	public static void main(String[] args) {
		try {
			new NameServer();
		} catch (RemoteException | AlreadyBoundException ex) {
			System.out.println("NameServer already running");
		}
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
		createNewGroupButton.setBounds(524, 0, 135, 23);
		frame.getContentPane().add(createNewGroupButton);

		joinGroupButton = new JButton("Join group");
		joinGroupButton.setBounds(524, 35, 135, 23);
		frame.getContentPane().add(joinGroupButton);

		connectButton = new JButton("Connect");
		connectButton.setBounds(365, 25, 119, 23);
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

				String input = JOptionPane.showInputDialog(frame,
						"Enter group name");

				if (input != null && (input.length() > 1)) {

					try {
						System.out.println("creating group");

						groupCreated = client.createGroup(input, userName);
						if (groupCreated) {

							groupMap = client.getGroups();

							listOfGroups.clear();
							listOfMembers.clear();

							Iterator it = groupMap.entrySet().iterator();
							while (it.hasNext()) {
								Map.Entry pair = (Map.Entry) it.next();
								listOfGroups.add(pair.getKey().toString());
								listOfMembers.add(pair.getValue().toString());
								System.out.println(pair.getKey() + " = " + pair.getValue());
								it.remove();
							}

							groupList.clear();

							for (int i = 0; i < listOfGroups.size(); i++) {
								groupList.add(i, listOfGroups.get(i));
							}

							JOptionPane.showMessageDialog(null,
									"Group created with name: " + input);

						} else {
							JOptionPane.showMessageDialog(null,
									"Group not created");
						}

					} catch (RemoteException | ServerNotActiveException e) {
						JOptionPane
								.showMessageDialog(null, "Group not created");
						e.printStackTrace();
					}

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

		listGroup.addListSelectionListener(new ListSelectionListener() {
			public void valueChanged(ListSelectionEvent evt) {

				if (evt.getValueIsAdjusting()) {
					final JList source = (JList) evt.getSource();
					try {
						groupMap = client.getGroups();

						listOfMembers.clear();
						listOfMembers = groupMap.get(source.getSelectedValue()
								.toString());

						userList.clear();

						for (int i = 0; i < listOfMembers.size(); i++) {
							userList.add(i, listOfMembers.get(i));
						}

						joinGroupButton.addActionListener(new ActionListener() {
							public void actionPerformed(ActionEvent a) {

								try {
									client.joinGroup(source.getSelectedValue()
											.toString());

								} catch (RemoteException
										| ServerNotActiveException e) {
									JOptionPane.showMessageDialog(null,
											"Select a group");
									// e.printStackTrace();
								}
							}
						});
					} catch (RemoteException ex) {
						ex.printStackTrace();
					}

				}
			}
		});

		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
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
				JOptionPane
						.showMessageDialog(frame, "You need to type a port.");
				return;
			} else {
				portNr = Integer.parseInt(portNrField.getText());
			}

			try {

				client = new Client();
				connectButton.setText("Disconnect");
				System.out.println("Trying to connect to nameserver");
				clientID = client.connectToNameServer(userName, portNr);

				groupMap = client.getGroups();

				listOfGroups.clear();
				listOfMembers.clear();

				Iterator it = groupMap.entrySet().iterator();
				while (it.hasNext()) {
					Map.Entry pair = (Map.Entry) it.next();
					listOfGroups.add(pair.getKey().toString());
					listOfMembers.add(pair.getValue().toString());
					System.out.println(pair.getKey() + " = " + pair.getValue());
					it.remove();
				}

				groupList.clear();

				for (int i = 0; i < listOfGroups.size(); i++) {
					groupList.add(i, listOfGroups.get(i));
				}

				System.out.println("Connected");

			} catch (Exception ex) {
				// ex.printStackTrace();
				// System.out.println("Error, could not connect.");
				JOptionPane.showMessageDialog(frame,
						"Error, could not connect.");

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
		else {
			chatArea.append(message + "\n");
		}
	}

	public void setClientID(int clientID) {
		this.clientID = clientID;
	}
}