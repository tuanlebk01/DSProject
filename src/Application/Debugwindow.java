package Application;

import java.awt.Button;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.DefaultListModel;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JButton;
import javax.swing.JToggleButton;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

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
	
	private Client client;
	
	public Debugwindow(Client client) {
		
		this.client = client;
		frame.getContentPane().setLayout(null);
		
		label1 = new JLabel("Message queue");
		label1.setBounds(23, 11, 102, 14);
		frame.getContentPane().add(label1);
		
		label2 = new JLabel("Holdback queue");
		label2.setBounds(307, 11, 102, 14);
		frame.getContentPane().add(label2);
		
		label3 = new JLabel("Blocked queue");
		label3.setBounds(23, 256, 91, 14);
		frame.getContentPane().add(label3);
		
		label4 = new JLabel("List 4");
		label4.setBounds(333, 256, 46, 14);
		frame.getContentPane().add(label4);
		
		list_1.setBounds(10, 36, 136, 200);
		frame.getContentPane().add(list_1);
		
		list_2.setBounds(288, 36, 136, 200);
		frame.getContentPane().add(list_2);
		
		list_3.setBounds(10, 281, 136, 200);
		frame.getContentPane().add(list_3);
		
		list_4.setBounds(288, 281, 136, 200);
		frame.getContentPane().add(list_4);
		
		button1 = new JButton("Select msg");
		button1.setBounds(167, 33, 102, 23);
		frame.getContentPane().add(button1);
		
		button2 = new JButton("Select msg");
		button2.setBounds(167, 67, 102, 23);
		frame.getContentPane().add(button2);
		
		button3 = new JButton("Select msg");
		button3.setBounds(167, 278, 102, 23);
		frame.getContentPane().add(button3);
		
		button4 = new JButton("Select msg");
		button4.setBounds(167, 311, 102, 23);
		frame.getContentPane().add(button4);
		
		toggleButton1 = new JToggleButton("Toggle Block 1");
		toggleButton1.setBounds(156, 143, 121, 23);
		frame.getContentPane().add(toggleButton1);
		
		toggleButton2 = new JToggleButton("Toggle Block 2");
		toggleButton2.setBounds(156, 177, 121, 23);
		frame.getContentPane().add(toggleButton2);
		
		toggleButton3 = new JToggleButton("Toggle Block 3");
		toggleButton3.setBounds(156, 392, 121, 23);
		frame.getContentPane().add(toggleButton3);
		
		toggleButton4 = new JToggleButton("Toggle Block 4");
		toggleButton4.setBounds(156, 426, 121, 23);
		frame.getContentPane().add(toggleButton4);
		
		
		frame.setSize(450, 530);
		frame.setLocation(1315, 255);
		frame.setVisible(true);
		
		// To close debugwindow and keeping client alive
		frame.addWindowListener(new java.awt.event.WindowAdapter() {
		    @Override
		    public void windowClosing(java.awt.event.WindowEvent windowEvent) {
		    	frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		    }
		});
		
	}
	
	public void messageQueue(String message) {
		
		list1.add(list1.getSize(), message);

		list_1.addListSelectionListener(new ListSelectionListener() {
			public void valueChanged(ListSelectionEvent evt) {
				if (evt.getValueIsAdjusting()) {
					final JList source = (JList) evt.getSource();
					button1.addActionListener(new ActionListener() {
						public void actionPerformed(ActionEvent a) {
							String msg = "###";
							if(source.getSelectedValue() != null) {
								msg = source.getSelectedValue().toString();
								System.out.println("msg: " + msg);
								holdbackQueue(msg);
								int selectedIndex = list_1.getSelectedIndex();
								list1.remove(selectedIndex);
							}
						}
					});
				}
			}
		});
	}
	
	public void holdbackQueue(String message) {
		list2.add(list2.getSize(), message);
	}
	
	public void blockQueue() {
	}
	
	public void addTolist4() {
	}
}
