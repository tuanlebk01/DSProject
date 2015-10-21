package Application;

import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.InetAddress;
import java.rmi.AlreadyBoundException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.server.ServerNotActiveException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import Communication.TextMessage;
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
	private JTextField randomMSGField;
	private JButton connectButton;
	private JButton sendButton;
	private JButton createNewGroupButton;
	private JButton randomMSGButton;
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
	private HashMap<String, ArrayList<String>> mapOfGroups = new HashMap<String, ArrayList<String>>();
	private HashMap<String, String> leaders = new HashMap<String, String>();
	private int clientID;
	private boolean groupCreated;
	private boolean isLeader = false;
	private String leaderOfMyGroup;
	private String myGroupName = null;
	private boolean fancyPrinting1 = true;
	private boolean groupJoined;

	public static void main(String[] args) {
		try {
			new NameServer();
		} catch (RemoteException | AlreadyBoundException ex) {
			System.out.println("GUI: NameServer already running");
		}
		SwingUtilities.invokeLater(new Runnable() {
		    public void run() {
		    	new GUI();
	        }
	 	});
	}

	public GUI() {

		frame.getContentPane().setLayout(null);

		userNameLabel = new JLabel("Username");
		userNameLabel.setFont(new Font("Tahoma", Font.PLAIN, 12));
		userNameLabel.setBounds(28, 5, 73, 22);
		frame.getContentPane().add(userNameLabel);

		userNameTextField = new JTextField();
		userNameTextField.setBounds(91, 7, 125, 20);
		frame.getContentPane().add(userNameTextField);
		userNameTextField.setColumns(10);
		userNameTextField.setText("User1");

		createNewGroupButton = new JButton("Create group");
		createNewGroupButton.setBounds(524, 5, 135, 23);
		frame.getContentPane().add(createNewGroupButton);

		joinGroupButton = new JButton("Join group");
		joinGroupButton.setBounds(524, 35, 135, 23);
		frame.getContentPane().add(joinGroupButton);

		connectButton = new JButton("Connect");
		connectButton.setBounds(365, 5, 119, 23);
		frame.getContentPane().add(connectButton);

		randomMSGButton = new JButton("Test MSG");
		randomMSGButton.setBounds(365, 35, 119, 23);
		frame.getContentPane().add(randomMSGButton);

		usersOnlineLabel = new JLabel("Online users");
		usersOnlineLabel.setFont(new Font("Tahoma", Font.PLAIN, 12));
		usersOnlineLabel.setBounds(557, 242, 73, 22);
		frame.getContentPane().add(usersOnlineLabel);

		listUser.setBounds(524, 275, 136, 200);
		frame.getContentPane().add(listUser);

		sendButton = new JButton("Send");
		sendButton.setBounds(384, 377, 100, 97);
		frame.getContentPane().add(sendButton);

		listGroup.setBounds(524, 100, 136, 124);
		frame.getContentPane().add(listGroup);

		groupsLabel = new JLabel("Groups");
		groupsLabel.setFont(new Font("Tahoma", Font.PLAIN, 12));
		groupsLabel.setBounds(573, 65, 51, 22);
		frame.getContentPane().add(groupsLabel);

		portNrField = new JTextField();
		portNrField.setColumns(10);
		portNrField.setBounds(275, 7, 60, 20);
		frame.getContentPane().add(portNrField);

		randomMSGField = new JTextField();
		randomMSGField.setColumns(10);
		randomMSGField.setBounds(275, 37, 60, 20);
		randomMSGField.setText("10");
		frame.getContentPane().add(randomMSGField);

		portNrField.setText("1111");

		IPLabel = new JLabel("Port");
		IPLabel.setFont(new Font("Tahoma", Font.PLAIN, 12));
		IPLabel.setBounds(234, 5, 30, 22);
		frame.getContentPane().add(IPLabel);

		chatArea = new JTextArea();
		chatArea.setEditable(false);
		chatArea.setLineWrap(true);
		chatArea.setBounds(28, 100, 456, 265);
		frame.add(new JScrollPane(chatArea), BorderLayout.CENTER);
		frame.getContentPane().add(chatArea);

		msgField = new JTextArea();
		msgField.setBounds(28, 377, 325, 97);
		msgField.setColumns(10);
		frame.add(new JScrollPane(msgField), BorderLayout.CENTER);
		frame.getContentPane().add(msgField);

		createGroup();
		joinGroup();

		randomMSGButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent a) {
				sendTestMessage();
			}
		});

		sendButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent a) {
				sendMessage();
			}
		});

		connectButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent a) {
				connect(connectButton.getText());
			}
		});

		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setSize(700, 530);
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);
	}

	private void joinGroup() {
		listGroup.addListSelectionListener(new ListSelectionListener() {
			public void valueChanged(ListSelectionEvent evt) {
				boolean fancyPrinting2;
				if (evt.getValueIsAdjusting()) {
					final JList source = (JList) evt.getSource();
					try {
						Thread.sleep(100);
						mapOfGroups = client.getGroupsInfo();
						listOfMembers.clear();
						listOfMembers = mapOfGroups.get(source.getSelectedValue().toString());


						fancyPrinting2 = true;
						if (fancyPrinting2 && fancyPrinting1) {
							joinGroupButton.addActionListener(new ActionListener() {
								public void actionPerformed(ActionEvent a) {
										try {

											leaders = client.getGroupLeaders();
											String group = source.getSelectedValue().toString();
											String leader = leaders.get(group);


											leaderOfMyGroup = client.joinGroup(group, leader);
											myGroupName = group;

											groupJoined = client.isGroupJoined();

											if(groupJoined) {

												mapOfGroups = client.getGroupsInfo();
												listOfMembers = mapOfGroups.get(myGroupName);

												userList.clear();


												for (int i = 0; i < listOfMembers.size(); i++) {
													userList.add(i,listOfMembers.get(i));
												}

												JOptionPane.showMessageDialog(null,
														"Joined group: " + myGroupName);

												startThread();

											} else {

											JOptionPane.showMessageDialog(
													null, "Failed to join group, Username exists");

											}

									} catch (RemoteException
											| ServerNotActiveException
											| AlreadyBoundException
											| NotBoundException e) {
											JOptionPane.showMessageDialog(
											null, "Failed to join group, Exception error");
											e.printStackTrace();
										}
									}
								});
							fancyPrinting2 = false;
							fancyPrinting1 = false;
						}
					} catch (RemoteException | InterruptedException ex) {
						ex.printStackTrace();
					}
				}
			}
		});
	}

	private void createGroup() {
		createNewGroupButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent a) {

				String input = JOptionPane.showInputDialog(frame,
						"Enter group name");

				if (input != null && (input.length() > 1)) {

					try {
						System.out.println("GUI: Trying to create group");

						groupCreated = client.createGroup(input, userName);
						if (groupCreated) {

							myGroupName = input;
							isLeader = true;
							updateLists();

//							JOptionPane.showMessageDialog(null,
//									"Group created with name: " + input);
							System.out.println("GUI: Group created: " + input);
							startThread();

						} else {
//							JOptionPane.showMessageDialog(null,
//									"Group not created, Nameserver error");
							System.out.println("GUI: Failed to create group");
						}

					} catch (RemoteException | ServerNotActiveException
							| NotBoundException | AlreadyBoundException e) {
//						JOptionPane
//								.showMessageDialog(null, "Group not created, Exception error");
						 e.printStackTrace();
						System.out.println("GUI: Failed to create group, Exception error");
					}

				} else {
//					JOptionPane.showMessageDialog(null, "Group not created, wrong input, no error");
					System.out.println("GUI: Failed to create group, wrong input, no error");
				}
			}
		});
	}

	public void connect(String connect) {
		if (connectButton.getText().equals("Connect")) {
			if (userNameTextField.getText().length() < 1) {
				JOptionPane
						.showMessageDialog(frame, "You need to type a name.");
				return;
			} else {
				userName = userNameTextField.getText();
				System.out.println("GUI: Username: " + userName);
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
				clientID = client.connectToNameServer(userName, portNr);
				updateLists();

				System.out.println("GUI: Connected to nameserver");

			} catch (Exception ex) {
				 ex.printStackTrace();
				connectButton.setText("Connect");
				JOptionPane.showMessageDialog(frame,
						"Error, could not connect.");

			}
		} else {
			try {
				client.disconnect(myGroupName, userName);
			} catch (RemoteException e) {
				e.printStackTrace();
			}
			chatArea.setText("");
			userList.clear();
			groupList.clear();
			connectButton.setText("Connect");
		}
	}

	public void updateLists() {
		try {
			mapOfGroups = client.getGroupsInfo();
		} catch (RemoteException ex) {
			ex.printStackTrace();
		}

		listOfGroups.clear();
		listOfMembers.clear();

		Iterator it = mapOfGroups.entrySet().iterator();
		while (it.hasNext()) {
			Map.Entry pair = (Map.Entry) it.next();
			listOfGroups.add(pair.getKey().toString());
			listOfMembers.add(pair.getValue().toString());
//					System.out.println("GUI:");
//					System.out.println(pair.getKey() + " = " + pair.getValue());
			it.remove();
		}


		groupList.clear();
		for (int i = 0; i < listOfGroups.size(); i++) {
			groupList.add(i, listOfGroups.get(i));
		}

		userList.clear();
		for (int i = 0; i < listOfMembers.size(); i++) {
			userList.add(i,listOfMembers.get(i));
		}
	}

	private void startThread() {

        final Timer timer = new Timer(true);
        ArrayList<TextMessage> textMessages;

        TimerTask task = new TimerTask() {

            public void run() {
            	updateLists();
            	ArrayList<TextMessage> textMessages;
            	textMessages = client.getMessages();
            	for(int i = 0; i < textMessages.size(); i++) {
            		writeMsg(textMessages.get(i).getSenderUserName(), textMessages.get(i).getMessage());
            	}

            }
        };

        timer.schedule(task, 0, 250);

	}



	public void sendTestMessage() {
		if (connectButton.getText().equals("Connect")) {
			JOptionPane.showMessageDialog(frame, "You need to connect first.");
			return;
		}
		if(myGroupName != null) {
			int nrOftestMSG = Integer.parseInt(randomMSGField.getText());

			randomMSGField.setText("");

			try {
				client.broadcastTestMessages(nrOftestMSG);
			} catch (RemoteException e) {
				e.printStackTrace();
			}
		}
	}

	public void sendMessage() {
		if (connectButton.getText().equals("Connect")) {
			JOptionPane.showMessageDialog(frame, "You need to connect first.");
			return;
		}
		if(myGroupName != null) {
			String message = msgField.getText();
			message = "[" + userNameTextField.getText() + "] " + message;
			msgField.setText("");
			try {
				client.broadcastMessage(message);
			} catch (RemoteException | NotBoundException e) {
				e.printStackTrace();
			}
		}
	}

	public void writeMsg(String userName, String message) {
		if (!userName.equals(""))
			chatArea.append(userName + ": " + message + "\n");
		else {
			chatArea.append(message + "\n");
		}
	}

	public void setClientID(int clientID) {
		this.clientID = clientID;
	}

	public boolean isLeader() {
		return isLeader;
	}

	public void setLeader(boolean isLeader) {
		this.isLeader = isLeader;
	}
}