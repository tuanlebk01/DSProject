package Application;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.AbstractAction;
import javax.swing.DefaultListModel;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JButton;
import javax.swing.JToggleButton;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import Communication.TextMessage;
import GroupManagement.Client;

public class Debugwindow {

	private JFrame frame = new JFrame();
	private DefaultListModel list1 = new DefaultListModel();
	private JList list_1 = new JList(list1);
	private DefaultListModel list2 = new DefaultListModel();
	private JList list_2 = new JList(list2);
	private DefaultListModel list3 = new DefaultListModel();
	private JList list_3 = new JList(list3);
	private DefaultListModel list4 = new DefaultListModel();
	private JList list_4 = new JList(list4);

	private JLabel label1;
	private JLabel label2;
	private JLabel label3;
	private JLabel label4;
	private JButton button1;
	private JButton button2;
	private JButton button3;
	private JButton button4;
	private JToggleButton toggleButton1;
	private JToggleButton toggleButton2;
	private JToggleButton toggleButton3;
	private JToggleButton toggleButton4;
	private JCheckBox checkbox;
	private JCheckBox checkbox2;
	private Client client;
	private ArrayList<TextMessage> outgoingList;
	private ArrayList<TextMessage> incommingList;
	private ArrayList<TextMessage> holdbackList;
	private ArrayList<TextMessage> acceptedList;
	private int selectedIndex = 999;
	private int selectedIndex2 = 999;

	public Debugwindow(Client client) {

		this.client = client;
		frame.getContentPane().setLayout(null);

		label1 = new JLabel("Outgoing Msg");
		label1.setBounds(23, 11, 120, 14);
		frame.getContentPane().add(label1);

		label2 = new JLabel("Incomming Msg");
		label2.setBounds(360, 11, 120, 14);
		frame.getContentPane().add(label2);

		label3 = new JLabel("Holdback queue");
		label3.setBounds(23, 256, 120, 14);
		frame.getContentPane().add(label3);

		label4 = new JLabel("");
		label4.setBounds(360, 256, 120, 14);
		frame.getContentPane().add(label4);

		list_1.setBounds(10, 36, 150, 200);
		frame.getContentPane().add(list_1);

		list_2.setBounds(340, 36, 150, 200);
		frame.getContentPane().add(list_2);

		list_3.setBounds(10, 281, 150, 200);
		frame.getContentPane().add(list_3);

		list_4.setBounds(340, 281, 150, 200);
		frame.getContentPane().add(list_4);

		button1 = new JButton("Forward msg");
		button1.setBounds(170, 50, 160, 23);
		frame.getContentPane().add(button1);

		button2 = new JButton("Release msg");
		button2.setBounds(170, 85, 160, 23);
		frame.getContentPane().add(button2);

		button3 = new JButton("Accept msg");
		button3.setBounds(170, 330, 160, 23);
		frame.getContentPane().add(button3);

		checkbox = new JCheckBox(new CheckboxAction("Outgoing msg"));
		checkbox.setSelected(true);
		checkbox.setBounds(180, 140, 140, 23);
		frame.getContentPane().add(checkbox);

		checkbox2 = new JCheckBox(new CheckboxAction2("Incomming msg"));
		checkbox2.setSelected(true);
		checkbox2.setBounds(180, 175, 140, 23);
		frame.getContentPane().add(checkbox2);

		frame.setSize(500, 530);
		frame.setLocation(1315, 255);
		frame.setVisible(true);

		client.setOutgoingMsg(true);
		client.setIncommingMsg(true);
		client.setTimeout();


		list_1.addListSelectionListener(new ListSelectionListener() {
			public void valueChanged(ListSelectionEvent evt) {
				if (evt.getValueIsAdjusting()) {
					final JList source = (JList) evt.getSource();
					button1.addActionListener(new ActionListener() {
						public void actionPerformed(ActionEvent a) {
							String msg = "###";
							if(source.getSelectedValue() != null) {
								msg = source.getSelectedValue().toString();
									int selectedIndex = source.getSelectedIndex();
									selectMessages(selectedIndex);
							}
						}
					});
				}
			}
		});

		list_2.addListSelectionListener(new ListSelectionListener() {
			public void valueChanged(ListSelectionEvent evt) {
				if (evt.getValueIsAdjusting()) {

					final JList source = (JList) evt.getSource();

					if(list_2.getSelectedIndex() != -1) {
						selectedIndex = list_2.getSelectedIndex();
					}

					button2.addActionListener(new ActionListener() {
						public void actionPerformed(ActionEvent a) {
							if(selectedIndex != 999) {
								releaseMessage(selectedIndex);
							}
						}
					});
				}
			}
		});

		list_3.addListSelectionListener(new ListSelectionListener() {
			public void valueChanged(ListSelectionEvent evt) {
				if (evt.getValueIsAdjusting()) {

					final JList source = (JList) evt.getSource();

					if(list_3.getSelectedIndex() != -1) {
						selectedIndex2 = list_3.getSelectedIndex();
					}

					button3.addActionListener(new ActionListener() {
						public void actionPerformed(ActionEvent a) {
							if(selectedIndex2 != 999) {
								acceptMessage(selectedIndex2);
							}
						}
					});
				}
			}
		});

		// To close debugwindow and keeping client alive
		frame.addWindowListener(new java.awt.event.WindowAdapter() {
		    @Override
		    public void windowClosing(java.awt.event.WindowEvent windowEvent) {
		    	frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		    }
		});

	}

	class CheckboxAction extends AbstractAction {
	    public CheckboxAction(String text) {
	        super(text);
	    }

	    @Override
	    public void actionPerformed(ActionEvent e) {
	        JCheckBox cbLog = (JCheckBox) e.getSource();

	        	if (cbLog.isSelected()) {
	            	client.setOutgoingMsg(true);
	        	} else {
	        		client.setOutgoingMsg(false);
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
	        		client.setIncommingMsg(true);
	        	} else {
	        		client.setIncommingMsg(false);
	        	}
	    }
	}

	public void outgoingQueue() {

		list1.clear();
		outgoingList = client.getOutgoingMsgQueue();

		for(int i = 0; i < client.getOutgoingMsgQueue().size(); i++) {
			list1.add(i, client.getOutgoingMsgQueue().get(i).getSeqNr() + " : " + client.getOutgoingMsgQueue().get(i).getMessage());
		}
	}

	public void incommingQueue() {

		list2.clear();
		incommingList = client.getIncommingMsgQueue();

		for(int i = 0; i < client.getIncommingMsgQueue().size(); i++) {
				list2.add(i, client.getIncommingMsgQueue().get(i).getSeqNr() + " : " + client.getIncommingMsgQueue().get(i).getMessage());
		}
	}

	public void holdbackQueue() {

		list3.clear();
		holdbackList = client.getHoldbackQueue();

		for(int i = 0; i < client.getHoldbackQueue().size(); i++) {
			list3.add(i, client.getHoldbackQueue().get(i).getSeqNr() + " : " + client.getHoldbackQueue().get(i).getMessage());
		}
	}

	public void acceptedQueue() {

		if(list4.size() > 1) {
			list4.clear();
			acceptedList = client.getMessages();
		}

		for(int i = 0; i < client.getMessages().size(); i++) {
			list4.add(i, client.getMessages().get(i).getMessage());
		}
	}

	public void selectMessages(int selectedIndex) {

		if(client != null) {
			client.forwardMsg(outgoingList.get(selectedIndex));
			outgoingQueue();
		}
	}

	public void releaseMessage(int selectedIndex) {

		if(client != null) {
			if(incommingList.size() >= 0) {
				if(!(selectedIndex >= incommingList.size())) {
					client.releaseMsg(incommingList.get(selectedIndex));
					selectedIndex = 999;
					incommingQueue();
				}
			}
		}
	}

	public void acceptMessage(int selectedIndex) {

		if(client != null) {
			if(holdbackList.size() >= 0) {
				if(!(selectedIndex >= holdbackList.size())) {
					client.acceptMsg(holdbackList.get(selectedIndex));
					selectedIndex = 999;
					holdbackQueue();
				}
			}
		}
	}
}
