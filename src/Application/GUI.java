package Application;

import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.rmi.AlreadyBoundException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.server.ServerNotActiveException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.AbstractAction;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
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
import javax.swing.text.DefaultCaret;

import com.sun.org.apache.xerces.internal.util.SynchronizedSymbolTable;

import Communication.TextMessage;
import GroupManagement.Client;
import GroupManagement.NameServer;

public class GUI implements Observer {

	private Client client;
	private JLabel userNameLabel;
	private JLabel IPLabel;
	private JLabel usersOnlineLabel;
	private JLabel groupsLabel;
	private JTextField userNameTextField;
	private JTextField portNrField;
	private JTextField randomMSGField;
	private JTextField dropMSGField;
	private JButton connectButton;
	private JButton clearButton;
	private JButton sendButton;
	private JButton createNewGroupButton;
	private JButton randomMSGButton;
	private JButton joinGroupButton;
	private JButton getMSGButton;
	private JButton dropMSGButton;
	private JCheckBox checkbox;
	private JCheckBox debugbox;
	private DefaultListModel groupList = new DefaultListModel();
	private DefaultListModel userList = new DefaultListModel();
	private JList listGroup = new JList(groupList);
	private JList listUser = new JList(userList);
	private JFrame frame = new JFrame();
	private static JTextArea chatArea;
	private JTextArea msgField;
	private JScrollPane scrollPane;
	private static String userName;
	private static int portNr;
	private ArrayList<String> listOfGroups = new ArrayList<String>();
	private ArrayList<String> listOfMembers = new ArrayList<String>();
	private HashMap<String, ArrayList<String>> mapOfGroups = new HashMap<String, ArrayList<String>>();
	private HashMap<String, String> leaders = new HashMap<String, String>();
	private boolean groupCreated;
	private boolean isLeader = false;
	private String leaderOfMyGroup;
	private String myGroupName = null;
	private String host;
	private boolean fancyPrinting1 = true;
	private boolean groupJoined;
	private static GUI gui;
	private Debugwindow dw;
	private int clientID;
	private int xCord = 700;
	private int yCord = 530;
	private JLabel lblHost;
	private JTextField hostField;
	private Timer timer;
	private TimerTask task;

	public static void main(String[] args) {
//		try {
//			new NameServer();
//		} catch (RemoteException | AlreadyBoundException ex) {
//			System.out.println("GUI: NameServer already running");
//		}
		SwingUtilities.invokeLater(new Runnable() {
		    public void run() {
		    	gui = new GUI();
	        }
	 	});
	}

	public GUI() {

		frame.getContentPane().setLayout(null);

		userNameLabel = new JLabel("Username");
		userNameLabel.setFont(new Font("Tahoma", Font.PLAIN, 12));
		userNameLabel.setBounds(28, 36, 73, 22);
		frame.getContentPane().add(userNameLabel);

		userNameTextField = new JTextField();
		userNameTextField.setBounds(99, 38, 125, 20);
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

		randomMSGButton = new JButton("Rdm MSG");
		randomMSGButton.setBounds(365, 35, 119, 23);
		frame.getContentPane().add(randomMSGButton);

		dropMSGButton = new JButton("Drop MSG");
		dropMSGButton.setBounds(365, 65, 119, 23);
		frame.getContentPane().add(dropMSGButton);

		getMSGButton = new JButton("Queue");
		getMSGButton.setBounds(365, 95, 119, 23);
		frame.getContentPane().add(getMSGButton);

		usersOnlineLabel = new JLabel("Online users");
		usersOnlineLabel.setFont(new Font("Tahoma", Font.PLAIN, 12));
		usersOnlineLabel.setBounds(557, 242, 73, 22);
		frame.getContentPane().add(usersOnlineLabel);

		listUser.setBounds(524, 275, 136, 200);
		frame.getContentPane().add(listUser);

		sendButton = new JButton("Send");
		sendButton.setBounds(384, 377, 100, 97);
		frame.getContentPane().add(sendButton);

		clearButton = new JButton("Clear");
		clearButton.setBounds(28, 95, 119, 23);
		frame.getContentPane().add(clearButton);

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
		randomMSGField.setBounds(275, 66, 60, 20);
		randomMSGField.setText("10");
		frame.getContentPane().add(randomMSGField);

		dropMSGField = new JTextField();
		dropMSGField.setColumns(10);
		dropMSGField.setBounds(275, 96, 60, 20);
		dropMSGField.setText("10");
		frame.getContentPane().add(dropMSGField);

		portNrField.setText("1111");

		IPLabel = new JLabel("Port");
		IPLabel.setFont(new Font("Tahoma", Font.PLAIN, 12));
		IPLabel.setBounds(234, 5, 30, 22);
		frame.getContentPane().add(IPLabel);

		chatArea = new JTextArea();
		chatArea.setWrapStyleWord(true);
		chatArea.setEditable(false);
		chatArea.setLineWrap(true);
		chatArea.setBounds(28, 130, 456, 240);
		scrollPane = new JScrollPane(chatArea);
		scrollPane.setBounds(28, 130, 456, 240);
		frame.getContentPane().add(scrollPane);

		msgField = new JTextArea();
		msgField.setWrapStyleWord(true);
		msgField.setLineWrap(true);
		msgField.setBounds(28, 377, 325, 97);
		msgField.setColumns(10);
		scrollPane = new JScrollPane(msgField);
		scrollPane.setBounds(28, 377, 325, 97);
		DefaultCaret caret = (DefaultCaret)chatArea.getCaret();
		caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);
		frame.getContentPane().add(scrollPane);

		checkbox = new JCheckBox(new CheckboxAction("Ordered"));
		checkbox.setSelected(true);
		checkbox.setBounds(182, 64, 85, 25);
		frame.getContentPane().add(checkbox);

		debugbox = new JCheckBox(new CheckboxAction2("Enable debug window"));
		debugbox.setSelected(false);
		debugbox.setBounds(30, 64, 150, 25);
		frame.getContentPane().add(debugbox);

		lblHost = new JLabel("Host");
		lblHost.setFont(new Font("Tahoma", Font.PLAIN, 12));
		lblHost.setBounds(28, 4, 73, 22);
		frame.getContentPane().add(lblHost);

		hostField = new JTextField();
		hostField.setText("localhost");
		hostField.setColumns(10);
		hostField.setBounds(99, 6, 125, 20);
		frame.getContentPane().add(hostField);

		createGroup();
		joinGroup();

		dropMSGButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent a) {
				sendDropTestMessage();
			}
		});

		clearButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent a) {
				chatArea.setText("");
			}
		});

		getMSGButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent a) {
				getQueue();
			}
		});

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
		frame.setSize(xCord, yCord);
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);
	}

	class CheckboxAction extends AbstractAction {
	    public CheckboxAction(String text) {
	        super(text);
	    }

	    @Override
	    public void actionPerformed(ActionEvent e) {
	        JCheckBox cbLog = (JCheckBox) e.getSource();
	        if (connectButton.getText().equals("Disconnect")) {
	        	if (cbLog.isSelected()) {
	            	client.setOrdered(true);
	        	} else {
	        		client.setOrdered(false);
	        	}
	        }
	    }
	}

	class CheckboxAction2 extends AbstractAction {
	    public CheckboxAction2(String text) {
	        super(text);
	    }

	    @Override
	    public void actionPerformed(ActionEvent e) {
	        JCheckBox cbLog2 = (JCheckBox) e.getSource();
	        if (cbLog2.isSelected()) {
	        	if(connectButton.getText().equals("Disconnect")) {
	        		if(dw == null) {
	        			dw = new Debugwindow(client);
	        		}
	        	}
	        }
	    }
	}

	private void joinGroup() {
		listGroup.addListSelectionListener(new ListSelectionListener() {
			public void valueChanged(ListSelectionEvent evt) {
				boolean fancyPrinting2;
				if (evt.getValueIsAdjusting()) {
					final JList source = (JList) evt.getSource();
					try {
						Thread.sleep(100);
						mapOfGroups = client.askNSforGroupsInfo();
						listOfMembers.clear();
						listOfMembers = mapOfGroups.get(source.getSelectedValue().toString());


						fancyPrinting2 = true;
						if (fancyPrinting2 && fancyPrinting1) {
							joinGroupButton.addActionListener(new ActionListener() {
								public void actionPerformed(ActionEvent a) {
										try {

											leaders = client.getGroupLeaders();
											String group = "###";
											if(source.getSelectedValue() != null) {
												group = source.getSelectedValue().toString();
											}

											String leader = leaders.get(group);

											if(group.equals("###")) {
												JOptionPane.showMessageDialog(frame, "Select group to join.");
												return;
												} else if (group.endsWith("<---")) {
													String tmp = group.substring(0, group.length() - 5);
												if (myGroupName.equals(tmp) || leaderOfMyGroup.equals(userName)) {
													JOptionPane.showMessageDialog(frame, "Can't join your own group.");
													return;
												}
											}

											if(listOfMembers.contains(userName)) {
												JOptionPane.showMessageDialog(frame, "Username already exists.");
												return;
											}

											leaderOfMyGroup = client.joinGroup(group, leader);
											myGroupName = group;
											groupJoined = client.isGroupJoined();

											if(groupJoined) {

												mapOfGroups = client.getGroupsInfo();

												JOptionPane.showMessageDialog(null,
														"Joined group: " + myGroupName);

												memberJoined();
												client.addObserver(gui);
												updateLists();
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
					if(leaderOfMyGroup != null) {
						if(leaderOfMyGroup.equals(userName) || myGroupName != null) {
							JOptionPane.showMessageDialog(frame, "Can't create new group when already in a group, disconnect first.");
							return;
						}
					}
					try {
						groupCreated = client.createGroup(input, userName);

						if (groupCreated) {

							myGroupName = input;
							leaderOfMyGroup = userName;
							isLeader = true;

							groupList.clear();
							userList.clear();

							mapOfGroups = client.askNSforGroupsInfo();

							Iterator it = mapOfGroups.entrySet().iterator();
							while (it.hasNext()) {
								Map.Entry pair = (Map.Entry) it.next();
								if(!listOfGroups.contains(pair.getKey().toString())) {
									listOfGroups.add(pair.getKey().toString());
								}
							}

							listOfMembers = client.getListOfClientsInMyGroup();

							for (int i = 0; i < listOfGroups.size(); i++) {
								if(!groupList.contains(listOfGroups)) {
									groupList.add(i, listOfGroups.get(i));
								}
							}

							for (int i = 0; i < listOfMembers.size(); i++) {
								if( listOfMembers.get(i).equals(leaderOfMyGroup)) {
									userList.add(i, listOfMembers.get(i) + " : <--- Leader");
								} else {
									userList.add(i, listOfMembers.get(i));
								}
							}

//							JOptionPane.showMessageDialog(null,
//									"Group created with name: " + input);
							client.updateGroupList(userName);
							client.addObserver(gui);
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
				JOptionPane.showMessageDialog(frame, "You need to type a name.");
				return;
			} else {
				userName = userNameTextField.getText();
				host = hostField.getText();
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
				clientID = client.connectToNameServer(userName, host, portNr);

				mapOfGroups = client.askNSforGroupsInfo();
				listOfGroups.clear();
				listOfMembers.clear();

				//this list is weird...

				Iterator it = mapOfGroups.entrySet().iterator();
				while (it.hasNext()) {
					Map.Entry pair = (Map.Entry) it.next();
					listOfGroups.add(pair.getKey().toString());
					listOfMembers.add(pair.getValue().toString());
					it.remove();
				}

				for (int i = 0; i < listOfGroups.size(); i++) {
					groupList.add(i, listOfGroups.get(i));
				}

				for (int i = 0; i < listOfMembers.size(); i++) {
					if( listOfMembers.get(i).equals(leaderOfMyGroup)) {
						userList.add(i, listOfMembers.get(i) + " : L");
					} else {
						userList.add(i, listOfMembers.get(i));
					}
				}

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
			} catch (RemoteException | NotBoundException e) {
				e.printStackTrace();
			}
			connectButton.setText("Connect");
			if(timer != null) {
				timer.cancel();
				timer.purge();
			}
			if(task != null) {
				task.cancel();
			}
	        myGroupName = null;
	        leaderOfMyGroup = null;
			groupList.clear();
			userList.clear();
			chatArea.setText("");
			msgField.setText("");
			client = null;
		}
	}

	public void memberJoined() {
		try {
			client.updateGroupList(userName);
			client.notifyOthers();
		} catch (RemoteException | NotBoundException ex) {
			ex.printStackTrace();
		}
	}

	public void update(Observable obj, Object arg) {
		updateLists();
	}

	public void updateLists() {
		listOfGroups.clear();
		groupList.clear();
		userList.clear();

		try {
			listOfMembers = client.getListOfClientsInMyGroup();
		} catch (RemoteException ex) {
			ex.printStackTrace();
		}

		Iterator it = mapOfGroups.entrySet().iterator();
		while (it.hasNext()) {
			Map.Entry pair = (Map.Entry) it.next();
			listOfGroups.add(pair.getKey().toString());
		}

		for (int i = 0; i < listOfGroups.size(); i++) {
			if(listOfGroups.get(i).equals(myGroupName)) {
				groupList.add(i, listOfGroups.get(i) + " <---");
			} else {
				groupList.add(i, listOfGroups.get(i));
			}
		}

		leaderOfMyGroup = client.getMyLeader();

		for (int i = 0; i < listOfMembers.size(); i++) {
			if(listOfMembers.get(i).equals(leaderOfMyGroup)) {
				userList.add(i, listOfMembers.get(i) + " : <--- Leader");
			} else {
				userList.add(i, listOfMembers.get(i));
			}
		}
	}

	public void startThread() {

        timer = new Timer(true);
        ArrayList<TextMessage> textMessages;

        task = new TimerTask() {
            public void run() {
	           	ArrayList<TextMessage> textMessages;
	           	textMessages = client.getMessages();

	           	if(dw != null) {
   					dw.incommingQueue();
   					dw.holdbackQueue();
   					dw.acceptedQueue();
   				}

	           	if(!(textMessages == null)) {
	           		for(int i = 0; i < textMessages.size(); i++) {
	           			if(!(textMessages.get(i) == null)){
	           				writeMsg(textMessages.get(i).getSenderUserName(), textMessages.get(i).getMessage());
	            		}
	            	}
	            }
            }
        };
        timer.schedule(task, 0, 500);
	}

	public void getQueue() {
		if (connectButton.getText().equals("Connect")) {
			JOptionPane.showMessageDialog(frame, "You need to connect first.");
			return;
		}
		if(myGroupName != null) {
			ArrayList<TextMessage> queue = client.getMessagesInQueue();
			for(int i = 0; i < queue.size(); i++) {
				chatArea.append("Message in queue: " + queue.get(i).getSeqNr() + "\n");
			}
		}
	}

	public void sendDropTestMessage() {
		if (connectButton.getText().equals("Connect")) {
			JOptionPane.showMessageDialog(frame, "You need to connect first.");
			return;
		}
		if(myGroupName != null) {
			int nrOftestMSG = Integer.parseInt(dropMSGField.getText());

			try {
				client.broadcastTestMessages2(nrOftestMSG);
			} catch (RemoteException | NotBoundException e) {
				e.printStackTrace();
			}
		}
	}

	public void sendTestMessage() {
		if (connectButton.getText().equals("Connect")) {
			JOptionPane.showMessageDialog(frame, "You need to connect first.");
			return;
		}
		if(myGroupName != null) {
			int nrOftestMSG = Integer.parseInt(randomMSGField.getText());

			try {
				client.broadcastTestMessages(nrOftestMSG);
			} catch (RemoteException | NotBoundException e) {
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
			message = userNameTextField.getText() + ": " + message;
			msgField.setText("");
			try {
				client.broadcastMessage(message);
				if(dw != null) {
					dw.outgoingQueue();
				}
			} catch (RemoteException | NotBoundException e) {
				e.printStackTrace();
			}
		}
	}

	public void writeMsg(String userName, String message) {
			chatArea.append(message + "\n");
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